package com.king_tajin.winter_enchantments.events;

import com.king_tajin.winter_enchantments.WinterEnchantments;
import com.king_tajin.winter_enchantments.blocks.IcicleBlock;
import com.king_tajin.winter_enchantments.init.WinterEnchantmentsBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.FireworkRocketEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.block.PointedDripstoneBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DripstoneThickness;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.entity.EntityLeaveLevelEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

public class FrostedWingsEnchantmentHandler {

    private static final ResourceKey<Enchantment> FROSTED_WINGS = ResourceKey.create(
            Registries.ENCHANTMENT,
            Identifier.fromNamespaceAndPath(WinterEnchantments.MODID, "frosted_wings")
    );

    private static final Map<Integer, Vec3> trackedIcicles = new ConcurrentHashMap<>();
    private static final int DROP_DURATION = 60;
    private static final int CLEANUP_INTERVAL = 100;
    private static int cleanupCounter = 0;

    public static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES =
            DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, WinterEnchantments.MODID);

    public static final Supplier<AttachmentType<Integer>> BOOST_DURATION = ATTACHMENT_TYPES.register(
            "boost_duration",
            () -> AttachmentType.builder(() -> 0).build()
    );

    public static void onFireworkSpawn(EntityJoinLevelEvent event) {
        if (!(event.getEntity() instanceof FireworkRocketEntity firework)) {
            return;
        }

        if (event.getLevel().isClientSide()) {
            return;
        }

        Optional<? extends Player> nearestFlyingPlayer = event.getLevel().players().stream()
                .filter(p -> p.isFallFlying() && p.distanceToSqr(firework) < 25.0)
                .min(Comparator.comparingDouble(p -> p.distanceToSqr(firework)));

        if (nearestFlyingPlayer.isEmpty()) {
            return;
        }

        Player player = nearestFlyingPlayer.get();

        boolean hasBlockBelow = false;
        BlockPos playerPos = player.blockPosition();
        for (int y = 1; y <= 7; y++) {
            BlockState checkState = player.level().getBlockState(playerPos.below(y));
            if (!checkState.isAir()) {
                hasBlockBelow = true;
                break;
            }
        }

        if (hasBlockBelow) {
            return;
        }

        ItemStack chestplate = player.getItemBySlot(EquipmentSlot.CHEST);
        if (chestplate.isEmpty() || !chestplate.is(Items.ELYTRA)) {
            return;
        }

        try {
            Holder<Enchantment> holder = player.level().registryAccess()
                    .lookupOrThrow(Registries.ENCHANTMENT)
                    .getOrThrow(FROSTED_WINGS);

            int level = chestplate.getEnchantmentLevel(holder);

            if (level > 0) {
                player.setData(BOOST_DURATION, DROP_DURATION);

                if (event.getLevel() instanceof ServerLevel serverLevel) {
                    serverLevel.sendParticles(
                            ParticleTypes.CLOUD,
                            player.getX(),
                            player.getY() - 1.5,
                            player.getZ(),
                            10,
                            2,
                            0,
                            2,
                            0.02
                    );
                }
            }
        } catch (Exception ignored) {
        }
    }

    public static void onPlayerTick(PlayerTickEvent.Post event) {
        Player player = event.getEntity();

        if (player.level().isClientSide()) {
            return;
        }

        int duration = player.getData(BOOST_DURATION);

        if (duration > 0) {
            if (duration % 5 == 0) {
                dropIcicle(player);
            }

            duration--;
            player.setData(BOOST_DURATION, duration);
        }

        if (player.level() instanceof ServerLevel serverLevel) {
            spawnParticlesForTrackedIcicles(serverLevel);

            cleanupCounter++;
            if (cleanupCounter >= CLEANUP_INTERVAL) {
                cleanupDeadIcicles(serverLevel);
                cleanupCounter = 0;
            }
        }
    }

    public static void onPlayerLoggedOut(PlayerEvent.PlayerLoggedOutEvent event) {
        Player player = event.getEntity();
        if (player.hasData(BOOST_DURATION)) {
            player.removeData(BOOST_DURATION);
        }
    }

    public static void onEntityRemoved(EntityLeaveLevelEvent event) {
        if (event.getEntity() instanceof Player player) {
            if (player.hasData(BOOST_DURATION)) {
                player.removeData(BOOST_DURATION);
            }
        } else if (event.getEntity() instanceof FallingBlockEntity) {
            trackedIcicles.remove(event.getEntity().getId());
        }
    }

    private static void dropIcicle(Player player) {
        if (!(player.level() instanceof ServerLevel serverLevel)) {
            return;
        }

        int dropCount = 1 + serverLevel.getRandom().nextInt(2);

        for (int i = 0; i < dropCount; i++) {
            BlockPos spawnPos = player.blockPosition().below();

            BlockState icicleTip = WinterEnchantmentsBlocks.ICICLE.get().defaultBlockState()
                    .setValue(PointedDripstoneBlock.TIP_DIRECTION, net.minecraft.core.Direction.DOWN)
                    .setValue(PointedDripstoneBlock.THICKNESS, DripstoneThickness.TIP)
                    .setValue(IcicleBlock.ICICLE_TYPE, IcicleBlock.IcicleType.TIP);

            BlockState icicleBase = WinterEnchantmentsBlocks.ICICLE.get().defaultBlockState()
                    .setValue(PointedDripstoneBlock.TIP_DIRECTION, net.minecraft.core.Direction.DOWN)
                    .setValue(PointedDripstoneBlock.THICKNESS, DripstoneThickness.FRUSTUM)
                    .setValue(IcicleBlock.ICICLE_TYPE, IcicleBlock.IcicleType.BASE);

            double offsetX = (serverLevel.getRandom().nextDouble() - 0.5) * 0.3;
            double offsetZ = (serverLevel.getRandom().nextDouble() - 0.5) * 0.3;

            FallingBlockEntity fallingTip = FallingBlockEntity.fall(
                    serverLevel,
                    spawnPos,
                    icicleTip
            );
            fallingTip.setHurtsEntities(0.8f, 50);
            fallingTip.setDeltaMovement(fallingTip.getDeltaMovement().add(offsetX, 0, offsetZ));

            FallingBlockEntity fallingBase = FallingBlockEntity.fall(
                    serverLevel,
                    spawnPos,
                    icicleBase
            );
            fallingBase.setHurtsEntities(0.8f, 50);
            fallingBase.setDeltaMovement(fallingBase.getDeltaMovement().add(offsetX, 0, offsetZ));

            fallingBase.startRiding(fallingTip);

            trackedIcicles.put(fallingBase.getId(), fallingBase.position());
            trackedIcicles.put(fallingTip.getId(), fallingTip.position());

            serverLevel.playSeededSound(
                    null,
                    spawnPos.getX(),
                    spawnPos.getY(),
                    spawnPos.getZ(),
                    SoundEvents.TRIDENT_THROW,
                    SoundSource.PLAYERS,
                    0.4f,
                    1.2f,
                    serverLevel.getRandom().nextLong()
            );

            serverLevel.sendParticles(
                    ParticleTypes.CLOUD,
                    player.getX(),
                    player.getY() - 1.5,
                    player.getZ(),
                    10,
                    2,
                    0,
                    2,
                    0.02
            );
        }
    }

    private static void spawnParticlesForTrackedIcicles(ServerLevel level) {
        for (Map.Entry<Integer, Vec3> entry : trackedIcicles.entrySet()) {
            Entity entity = level.getEntity(entry.getKey());
            if (entity instanceof FallingBlockEntity fallingBlock) {
                if (level.getRandom().nextInt(2) == 0) {
                    level.sendParticles(
                            ParticleTypes.FISHING,
                            fallingBlock.getX(),
                            fallingBlock.getY(),
                            fallingBlock.getZ(),
                            2,
                            0,
                            0.2,
                            0,
                            0.05
                    );

                    level.sendParticles(
                            ParticleTypes.SOUL_FIRE_FLAME,
                            fallingBlock.getX(),
                            fallingBlock.getY(),
                            fallingBlock.getZ(),
                            1,
                            0.2,
                            0.5,
                            0.2,
                            0.05
                    );
                }

                if (fallingBlock.onGround()) {
                    trackedIcicles.remove(entry.getKey());
                    fallingBlock.discard();
                }
            }
        }
    }

    private static void cleanupDeadIcicles(ServerLevel level) {
        trackedIcicles.entrySet().removeIf(entry -> {
            Entity entity = level.getEntity(entry.getKey());
            return entity == null || entity.isRemoved() || !(entity instanceof FallingBlockEntity);
        });
    }
}