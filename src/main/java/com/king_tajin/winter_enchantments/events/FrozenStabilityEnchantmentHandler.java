package com.king_tajin.winter_enchantments.events;

import com.king_tajin.winter_enchantments.WinterEnchantments;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.event.entity.living.LivingKnockBackEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

public class FrozenStabilityEnchantmentHandler {

    private static final ResourceKey<Enchantment> FROZEN_STABILITY =
            ResourceKey.create(Registries.ENCHANTMENT,
                    Identifier.fromNamespaceAndPath(WinterEnchantments.MODID, "frozen_stability"));

    public static void onKnockback(LivingKnockBackEvent event) {
        if (!(event.getEntity() instanceof Player player)) {
            return;
        }

        if (!player.isCrouching()) {
            return;
        }

        ItemStack leggings = player.getItemBySlot(EquipmentSlot.LEGS);
        if (leggings.isEmpty()) {
            return;
        }

        try {
            Holder<Enchantment> holder = player.level().registryAccess()
                    .lookupOrThrow(Registries.ENCHANTMENT)
                    .getOrThrow(FROZEN_STABILITY);

            int level = leggings.getEnchantmentLevel(holder);

            if (level > 0) {
                event.setCanceled(true);
            }
        } catch (Exception ignored) {}
    }

    public static void onPlayerTick(PlayerTickEvent.Post event) {
        Player player = event.getEntity();

        if (player.level().isClientSide()) {
            return;
        }

        if (!player.isCrouching()) {
            return;
        }

        ItemStack leggings = player.getItemBySlot(EquipmentSlot.LEGS);
        if (leggings.isEmpty()) {
            return;
        }

        try {
            Holder<Enchantment> holder = player.level().registryAccess()
                    .lookupOrThrow(Registries.ENCHANTMENT)
                    .getOrThrow(FROZEN_STABILITY);

            int level = leggings.getEnchantmentLevel(holder);

            if (level > 0 && player.tickCount % 4 == 0) {
                ServerLevel serverLevel = (ServerLevel) player.level();
                Vec3 pos = player.position();

                spawnParticle(serverLevel, pos, player, ParticleTypes.SNOWFLAKE, 2);

                if (player.getRandom().nextInt(3) == 0) {
                    spawnParticle(serverLevel, pos, player, ParticleTypes.ASH, 1);
                }

                if (player.getRandom().nextInt(4) == 0) {
                    spawnParticle(serverLevel, pos, player, ParticleTypes.ELECTRIC_SPARK, 1);
                }
            }
        } catch (Exception ignored) {}
    }

    private static void spawnParticle(ServerLevel level, Vec3 pos, Player player,
                                      net.minecraft.core.particles.ParticleOptions particle, int count) {
        for (int i = 0; i < count; i++) {
            double offsetX = (player.getRandom().nextDouble() - 0.5) * 1.2;
            double offsetY = 0.5 + player.getRandom().nextDouble();
            double offsetZ = (player.getRandom().nextDouble() - 0.5) * 1.2;

            level.sendParticles(
                    particle,
                    pos.x + offsetX,
                    pos.y + offsetY,
                    pos.z + offsetZ,
                    1,
                    0, 0.05, 0,
                    0.02
            );
        }
    }
}