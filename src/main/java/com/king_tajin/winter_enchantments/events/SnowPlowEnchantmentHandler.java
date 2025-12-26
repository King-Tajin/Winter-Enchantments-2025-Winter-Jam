package com.king_tajin.winter_enchantments.events;

import com.king_tajin.winter_enchantments.WinterEnchantments;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SnowLayerBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.level.BlockEvent;

import java.util.ArrayList;
import java.util.List;

public class SnowPlowEnchantmentHandler {

    private static final ResourceKey<Enchantment> SNOW_PLOW =
            ResourceKey.create(Registries.ENCHANTMENT,
                    Identifier.fromNamespaceAndPath(WinterEnchantments.MODID, "snow_plow"));

    private static class SnowBlockData {
        BlockPos pos;
        int layers;

        SnowBlockData(BlockPos pos, int layers) {
            this.pos = pos;
            this.layers = layers;
        }
    }

    public static void onBreakSpeed(PlayerEvent.BreakSpeed event) {
        Player player = event.getEntity();
        ItemStack tool = player.getMainHandItem();

        if (tool.isEmpty()) {
            return;
        }

        try {
            Holder<Enchantment> holder = player.level().registryAccess()
                    .lookupOrThrow(Registries.ENCHANTMENT)
                    .getOrThrow(SNOW_PLOW);

            int snowPlowLevel = tool.getEnchantmentLevel(holder);
            if (snowPlowLevel <= 0) {
                return;
            }
            BlockState state = event.getState();

            if (state.is(Blocks.SNOW) || state.is(Blocks.SNOW_BLOCK)) {
                float slownessFactor = 10.0f;
                float newSpeed = event.getOriginalSpeed() / slownessFactor;
                event.setNewSpeed(newSpeed);
            }
        } catch (Exception ignored) {}
    }

    public static void onLeftClickBlock(PlayerInteractEvent.LeftClickBlock event) {
        Player player = event.getEntity();
        ItemStack tool = player.getMainHandItem();

        if (tool.isEmpty()) {
            return;
        }

        try {
            Holder<Enchantment> holder = player.level().registryAccess()
                    .lookupOrThrow(Registries.ENCHANTMENT)
                    .getOrThrow(SNOW_PLOW);

            int level = tool.getEnchantmentLevel(holder);

            if (level <= 0) {
                return;
            }

            BlockState state = player.level().getBlockState(event.getPos());

            if (!state.is(Blocks.SNOW) && !state.is(Blocks.SNOW_BLOCK)) {
                event.setCanceled(true);
            }
        } catch (Exception ignored) {}
    }

    public static void onBlockBreak(BlockEvent.BreakEvent event) {
        Player player = event.getPlayer();
        ItemStack tool = player.getMainHandItem();

        if (tool.isEmpty()) {
            return;
        }

        try {
            Holder<Enchantment> holder = player.level().registryAccess()
                    .lookupOrThrow(Registries.ENCHANTMENT)
                    .getOrThrow(SNOW_PLOW);

            int level = tool.getEnchantmentLevel(holder);

            if (level <= 0) {
                return;
            }

            BlockState state = event.getState();
            BlockPos pos = event.getPos();

            if (state.is(Blocks.SNOW) || state.is(Blocks.SNOW_BLOCK)) {
                event.setCanceled(true);

                if (player.level() instanceof ServerLevel serverLevel) {
                    List<SnowBlockData> snowBlocks = new ArrayList<>();

                    int radius = 4;
                    int height = 5;

                    for (int y = 0; y < height; y++) {
                        for (int x = -radius; x <= radius; x++) {
                            for (int z = -radius; z <= radius; z++) {
                                if (x * x + z * z <= radius * radius) {
                                    BlockPos checkPos = pos.offset(x, y, z);
                                    BlockState checkState = serverLevel.getBlockState(checkPos);

                                    if (checkState.is(Blocks.SNOW)) {
                                        int layers = checkState.getValue(SnowLayerBlock.LAYERS);
                                        snowBlocks.add(new SnowBlockData(checkPos.immutable(), layers));
                                    } else if (checkState.is(Blocks.SNOW_BLOCK)) {
                                        snowBlocks.add(new SnowBlockData(checkPos.immutable(), 8));
                                    }
                                }
                            }
                        }
                    }

                    Vec3 playerLook = player.getLookAngle();
                    Vec3 perpendicular = new Vec3(-playerLook.z, 0, playerLook.x).normalize();

                    for (SnowBlockData snowData : snowBlocks) {
                        serverLevel.setBlock(snowData.pos, Blocks.AIR.defaultBlockState(), 3);
                    }

                    int movedBlocks = 0;

                    for (SnowBlockData snowData : snowBlocks) {
                        int forwardDistance = 2 + (int)(Math.random() * 2);
                        double lateralSpread = (Math.random() - 0.5) * 6;

                        Vec3 forwardVec = playerLook.scale(forwardDistance);
                        Vec3 lateralVec = perpendicular.scale(lateralSpread);
                        Vec3 totalOffset = forwardVec.add(lateralVec);

                        BlockPos targetPos = snowData.pos.offset(
                                (int)Math.round(totalOffset.x),
                                0,
                                (int)Math.round(totalOffset.z)
                        );

                        int remainingLayers = snowData.layers;

                        remainingLayers = tryPlaceSnowLayers(serverLevel, targetPos, remainingLayers);

                        if (remainingLayers > 0) {
                            BlockPos validPos = findClosestValidPosition(serverLevel, targetPos, remainingLayers);
                            if (validPos != null) {
                                remainingLayers = tryPlaceSnowLayers(serverLevel, validPos, remainingLayers);
                            }
                        }

                        if (remainingLayers > 0) {
                            remainingLayers = tryPlaceSnowLayers(serverLevel, snowData.pos, remainingLayers);
                        }

                        if (remainingLayers < snowData.layers) {
                            movedBlocks++;
                        }
                    }

                    if (movedBlocks > 0) {
                        int durabilityDamage = Math.max(1, movedBlocks / 16);
                        tool.hurtAndBreak(durabilityDamage, player, EquipmentSlot.MAINHAND);
                    }
                }
            }
        } catch (Exception ignored) {}
    }

    private static BlockPos findClosestValidPosition(ServerLevel level, BlockPos center, int layersNeeded) {
        BlockPos closestPos = null;
        double closestDistance = Double.MAX_VALUE;

        for (int x = -5; x <= 5; x++) {
            for (int y = -5; y <= 5; y++) {
                for (int z = -5; z <= 5; z++) {
                    BlockPos checkPos = center.offset(x, y, z);
                    double distance = center.distSqr(checkPos);

                    if (distance < closestDistance) {
                        BlockState currentState = level.getBlockState(checkPos);
                        BlockState belowState = level.getBlockState(checkPos.below());

                        boolean canPlace = false;

                        if (currentState.is(Blocks.SNOW)) {
                            int currentLayers = currentState.getValue(SnowLayerBlock.LAYERS);
                            if (currentLayers + layersNeeded <= 8) {
                                canPlace = true;
                            }
                        } else if (currentState.isAir() && (belowState.isSolidRender() || belowState.is(Blocks.SNOW_BLOCK))) {
                            canPlace = true;
                        }

                        if (canPlace) {
                            closestPos = checkPos;
                            closestDistance = distance;
                        }
                    }
                }
            }
        }

        return closestPos;
    }

    private static int tryPlaceSnowLayers(ServerLevel level, BlockPos pos, int layersToAdd) {
        for (int yOffset = 0; yOffset >= -1; yOffset--) {
            BlockPos checkPos = pos.offset(0, yOffset, 0);
            int remaining = tryPlaceSnowLayersAtExactPos(level, checkPos, layersToAdd);
            if (remaining < layersToAdd) {
                return remaining;
            }
        }

        BlockPos checkPosUp = pos.above();
        return tryPlaceSnowLayersAtExactPos(level, checkPosUp, layersToAdd);
    }

    private static int tryPlaceSnowLayersAtExactPos(ServerLevel level, BlockPos pos, int layersToAdd) {
        BlockState currentState = level.getBlockState(pos);
        BlockState belowState = level.getBlockState(pos.below());

        if (currentState.is(Blocks.SNOW)) {
            int currentLayers = currentState.getValue(SnowLayerBlock.LAYERS);
            int availableSpace = 8 - currentLayers;
            int layersToPlace = Math.min(layersToAdd, availableSpace);

            if (layersToPlace > 0) {
                int newLayers = currentLayers + layersToPlace;
                level.setBlock(pos, currentState.setValue(SnowLayerBlock.LAYERS, newLayers), 3);
                return layersToAdd - layersToPlace;
            }
            return layersToAdd;
        } else if (currentState.isAir() && (belowState.isSolidRender() || belowState.is(Blocks.SNOW_BLOCK))) {
            int layersToPlace = Math.min(layersToAdd, 8);
            level.setBlock(pos, Blocks.SNOW.defaultBlockState().setValue(SnowLayerBlock.LAYERS, layersToPlace), 3);
            return layersToAdd - layersToPlace;
        }

        return layersToAdd;
    }
}