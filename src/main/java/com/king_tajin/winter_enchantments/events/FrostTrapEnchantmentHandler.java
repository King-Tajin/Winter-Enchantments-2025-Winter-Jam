package com.king_tajin.winter_enchantments.events;

import com.king_tajin.winter_enchantments.WinterEnchantments;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.event.entity.ProjectileImpactEvent;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.tick.LevelTickEvent;
import net.neoforged.neoforge.event.level.LevelEvent;
import net.neoforged.neoforge.event.level.ChunkEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.Iterator;
import java.util.Random;

public class FrostTrapEnchantmentHandler {

    private static final ResourceKey<Enchantment> FROST_TRAP =
            ResourceKey.create(Registries.ENCHANTMENT,
                    Identifier.fromNamespaceAndPath(WinterEnchantments.MODID, "frost_trap"));

    private static final Map<UUID, Boolean> frostTrapArrows = new HashMap<>();
    private static final Map<BlockPos, Long> frostTrapBlocks = new HashMap<>();
    private static final int REMOVAL_TIME_TICKS = 900;
    private static final int REMOVAL_TIME_VARIANCE = 50;
    private static final Random random = new Random();

    public static void onArrowShoot(EntityJoinLevelEvent event) {
        Entity entity = event.getEntity();

        if (!(entity instanceof Projectile projectile)) {
            return;
        }

        if (event.getLevel().isClientSide()) {
            return;
        }

        Entity owner = projectile.getOwner();
        if (!(owner instanceof Player shooter)) {
            return;
        }

        ItemStack bow = shooter.getMainHandItem();
        if (bow.isEmpty()) {
            bow = shooter.getOffhandItem();
        }

        if (bow.isEmpty()) {
            return;
        }

        try {
            Holder<Enchantment> holder = shooter.level().registryAccess()
                    .lookupOrThrow(Registries.ENCHANTMENT)
                    .getOrThrow(FROST_TRAP);

            int level = bow.getEnchantmentLevel(holder);

            if (level > 0) {
                ServerLevel serverLevel = (ServerLevel) event.getLevel();
                Vec3 shooterPos = shooter.position().add(0, shooter.getEyeHeight(), 0);
                Vec3 direction = projectile.getDeltaMovement().normalize();

                serverLevel.playSound(
                        null,
                        shooter.blockPosition(),
                        SoundEvents.ENDER_DRAGON_FLAP,
                        SoundSource.PLAYERS,
                        0.8f,
                        1.5f
                );

                serverLevel.playSound(
                        null,
                        shooter.blockPosition(),
                        SoundEvents.PLAYER_HURT_FREEZE,
                        SoundSource.PLAYERS,
                        0.5f,
                        1.8f
                );

                for (int i = 6; i < 20; i++) {
                    Vec3 particlePos = shooterPos.add(direction.scale(i * 0.5));
                    serverLevel.sendParticles(
                            ParticleTypes.SONIC_BOOM,
                            particlePos.x,
                            particlePos.y,
                            particlePos.z,
                            1,
                            0, 0, 0,
                            0
                    );
                }

                for (int i = 1; i < 12; i++) {
                    double offsetX = (shooter.getRandom().nextDouble() - 0.5) * 1.5;
                    double offsetY = (shooter.getRandom().nextDouble() - 0.5) * 1.5;
                    double offsetZ = (shooter.getRandom().nextDouble() - 0.5) * 1.5;

                    serverLevel.sendParticles(
                            ParticleTypes.SNOWFLAKE,
                            shooterPos.x + offsetX,
                            shooterPos.y + offsetY,
                            shooterPos.z + offsetZ,
                            1,
                            0, 0.05, 0,
                            0.05
                    );
                }

                frostTrapArrows.put(projectile.getUUID(), true);
            }
        } catch (Exception ignored) {}
    }

    public static void onProjectileHit(ProjectileImpactEvent event) {
        Projectile projectile = event.getProjectile();

        if (!frostTrapArrows.containsKey(projectile.getUUID())) {
            return;
        }

        if (!(event.getRayTraceResult() instanceof net.minecraft.world.phys.EntityHitResult entityHit)) {
            return;
        }

        if (!(entityHit.getEntity() instanceof Player hitPlayer)) {
            return;
        }

        if (projectile.level().isClientSide()) {
            return;
        }

        ServerLevel serverLevel = (ServerLevel) projectile.level();
        BlockPos center = hitPlayer.blockPosition();

        serverLevel.playSound(
                null,
                center,
                SoundEvents.GLASS_BREAK,
                SoundSource.PLAYERS,
                2.5f,
                0.5f
        );

        serverLevel.playSound(
                null,
                center,
                SoundEvents.POWDER_SNOW_PLACE,
                SoundSource.PLAYERS,
                3.0f,
                0.7f
        );

        createFrostTrap(serverLevel, center);

        frostTrapArrows.remove(projectile.getUUID());
    }

    private static void createFrostTrap(ServerLevel level, BlockPos center) {
        long currentTime = level.getGameTime();

        for (int x = -5; x <= 5; x++) {
            for (int y = -5; y <= 5; y++) {
                for (int z = -5; z <= 5; z++) {
                    BlockPos pos = center.offset(x, y, z);
                    double distance = Math.sqrt(x * x + y * y + z * z);

                    if (distance <= 5) {
                        BlockState currentState = level.getBlockState(pos);

                        if (currentState.isAir() || currentState.canBeReplaced()) {
                            BlockPos immutablePos = pos.immutable();
                            int removalDelay = REMOVAL_TIME_TICKS + random.nextInt(REMOVAL_TIME_VARIANCE * 2 + 1) - REMOVAL_TIME_VARIANCE;

                            if (distance >= 5 - 2) {
                                level.setBlock(pos, Blocks.PACKED_ICE.defaultBlockState(), 3);
                            } else {
                                level.setBlock(pos, Blocks.POWDER_SNOW.defaultBlockState(), 3);
                            }

                            frostTrapBlocks.put(immutablePos, currentTime + removalDelay);
                        }
                    }
                }
            }
        }
    }

    public static void onLevelTick(LevelTickEvent.Post event) {
        if (!(event.getLevel() instanceof ServerLevel level)) {
            return;
        }

        long currentTime = level.getGameTime();
        Iterator<Map.Entry<BlockPos, Long>> iterator = frostTrapBlocks.entrySet().iterator();

        while (iterator.hasNext()) {
            Map.Entry<BlockPos, Long> entry = iterator.next();
            BlockPos pos = entry.getKey();
            long removalTime = entry.getValue();

            if (currentTime >= removalTime) {
                BlockState state = level.getBlockState(pos);
                if (state.is(Blocks.PACKED_ICE) || state.is(Blocks.POWDER_SNOW)) {
                    level.removeBlock(pos, false);
                }
                iterator.remove();
            }
        }
    }

    public static void onChunkUnload(ChunkEvent.Unload event) {
        if (!(event.getLevel() instanceof ServerLevel level)) {
            return;
        }

        ChunkPos chunkPos = event.getChunk().getPos();
        Iterator<Map.Entry<BlockPos, Long>> iterator = frostTrapBlocks.entrySet().iterator();

        while (iterator.hasNext()) {
            Map.Entry<BlockPos, Long> entry = iterator.next();
            BlockPos pos = entry.getKey();

            if (new ChunkPos(pos).equals(chunkPos)) {
                BlockState state = level.getBlockState(pos);
                if (state.is(Blocks.PACKED_ICE) || state.is(Blocks.POWDER_SNOW)) {
                    level.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
                }
                iterator.remove();
            }
        }
    }

    public static void onWorldUnload(LevelEvent.Unload event) {
        if (event.getLevel() instanceof ServerLevel) {
            frostTrapBlocks.clear();
            frostTrapArrows.clear();
        }
    }
}