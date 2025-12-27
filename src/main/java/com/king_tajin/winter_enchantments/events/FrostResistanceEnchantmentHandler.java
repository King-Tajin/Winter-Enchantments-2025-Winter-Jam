package com.king_tajin.winter_enchantments.events;

import com.king_tajin.winter_enchantments.WinterEnchantments;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.neoforged.neoforge.event.entity.EntityLeaveLevelEvent;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.level.LevelEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class FrostResistanceEnchantmentHandler {

    private static final ResourceKey<Enchantment> FROST_RESISTANCE =
            ResourceKey.create(Registries.ENCHANTMENT,
                    Identifier.fromNamespaceAndPath(WinterEnchantments.MODID, "frost_resistance"));

    private static final Map<UUID, Integer> tickCounters = new HashMap<>();

    public static void onEntityTick(EntityTickEvent.Pre event) {
        if (!(event.getEntity() instanceof LivingEntity entity)) {
            return;
        }

        if (entity.level().isClientSide()) {
            return;
        }

        int ticksFrozen = entity.getTicksFrozen();
        if (ticksFrozen <= 0) {
            tickCounters.remove(entity.getUUID());
            return;
        }

        ItemStack chestplate = entity.getItemBySlot(EquipmentSlot.CHEST);
        if (chestplate.isEmpty()) {
            return;
        }

        Holder<Enchantment> enchantmentHolder = entity.level().registryAccess()
                .lookupOrThrow(Registries.ENCHANTMENT)
                .getOrThrow(FROST_RESISTANCE);

        int frostLevel = chestplate.getEnchantmentLevel(enchantmentHolder);

        if (frostLevel <= 0) {
            return;
        }

        UUID entityId = entity.getUUID();
        int counter = tickCounters.getOrDefault(entityId, 0) + 1;

        boolean shouldReduce = switch (frostLevel) {
            case 1 -> counter % 10 < 3;
            case 2 -> counter % 10 < 6;
            case 3 -> counter % 10 < 8;
            default -> false;
        };

        if (shouldReduce) {
            entity.setTicksFrozen(ticksFrozen - 1);
        }

        tickCounters.put(entityId, counter);
    }

    public static void onLivingDamage(LivingDamageEvent.Pre event) {
        LivingEntity entity = event.getEntity();

        if (entity.level().isClientSide()) {
            return;
        }

        if (!event.getSource().is(DamageTypes.FREEZE)) {
            return;
        }

        ItemStack chestplate = entity.getItemBySlot(EquipmentSlot.CHEST);
        if (chestplate.isEmpty()) {
            return;
        }

        try {
            Holder<Enchantment> enchantmentHolder = entity.level().registryAccess()
                    .lookupOrThrow(Registries.ENCHANTMENT)
                    .getOrThrow(FROST_RESISTANCE);

            int frostLevel = chestplate.getEnchantmentLevel(enchantmentHolder);

            if (frostLevel <= 0) {
                return;
            }

            double negateChance = frostLevel * 0.15;

            if (entity.level().getRandom().nextDouble() < negateChance) {
                event.getContainer().setNewDamage(0);
            }
        } catch (Exception ignored) {}
    }

    public static void onEntityRemoved(EntityLeaveLevelEvent event) {
        if (event.getEntity() instanceof LivingEntity) {
            tickCounters.remove(event.getEntity().getUUID());
        }
    }

    public static void onWorldUnload(LevelEvent.Unload event) {
        if (!event.getLevel().isClientSide()) {
            tickCounters.clear();
        }
    }
}