package com.king_tajin.winter_enchantments.events;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.neoforge.event.tick.LevelTickEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.Iterator;

public class SnowdriftEnchantmentHandler {
    private static final ResourceKey<Enchantment> SNOWDRIFT = ResourceKey.create(
            Registries.ENCHANTMENT,
            Identifier.fromNamespaceAndPath("winter_enchantments", "snowdrift")
    );

    private static final Map<BlockPos, Long> placedSnow = new HashMap<>();
    private static final int MELT_TIME_TICKS = 1000;

    public static void onPlayerTick(PlayerTickEvent.Post event) {
        Player player = event.getEntity();
        Level level = player.level();

        if (level.isClientSide() || !player.onGround()) {
            return;
        }

        Holder<Enchantment> snowdriftHolder = level.registryAccess()
                .lookupOrThrow(Registries.ENCHANTMENT)
                .getOrThrow(SNOWDRIFT);

        int enchantmentLevel = EnchantmentHelper.getEnchantmentLevel(
                snowdriftHolder,
                player
        );

        if (enchantmentLevel <= 0) {
            return;
        }

        if (player.tickCount % 2 != 0) {
            return;
        }

        int radius = enchantmentLevel == 1 ? 0 : enchantmentLevel - 1;
        BlockPos centerPos = player.blockPosition();
        long currentTime = level.getGameTime();

        for (int x = -radius; x <= radius; x++) {
            for (int z = -radius; z <= radius; z++) {
                if (radius == 0 || x * x + z * z <= radius * radius) {
                    BlockPos pos = centerPos.offset(x, 0, z);
                    BlockPos belowPos = pos.below();
                    BlockState belowState = level.getBlockState(belowPos);
                    BlockState currentState = level.getBlockState(pos);

                    if (canPlaceSnow(level, pos, currentState, belowState)) {
                        if (currentState.isAir()) {
                            level.setBlock(pos, Blocks.SNOW.defaultBlockState(), 3);
                            boolean isColdBiome = level.getBiome(pos).value().coldEnoughToSnow(pos, level.getSeaLevel());
                            if (!isColdBiome) {
                                placedSnow.put(pos.immutable(), currentTime);
                            }
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
        Iterator<Map.Entry<BlockPos, Long>> iterator = placedSnow.entrySet().iterator();

        while (iterator.hasNext()) {
            Map.Entry<BlockPos, Long> entry = iterator.next();
            BlockPos pos = entry.getKey();
            long placedTime = entry.getValue();

            if (currentTime - placedTime > MELT_TIME_TICKS) {
                BlockState state = level.getBlockState(pos);
                if (state.is(Blocks.SNOW)) {
                    level.removeBlock(pos, false);
                }
                iterator.remove();
            }
        }
    }

    private static boolean canPlaceSnow(Level level, BlockPos pos, BlockState currentState, BlockState belowState) {
        if (!belowState.isSolidRender()) {
            return false;
        }

        if (!currentState.isAir() && !currentState.is(Blocks.SNOW)) {
            return false;
        }

        BlockState snowState = Blocks.SNOW.defaultBlockState();
        return snowState.canSurvive(level, pos);
    }
}