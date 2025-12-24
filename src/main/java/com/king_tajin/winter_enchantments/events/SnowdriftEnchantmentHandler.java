package com.king_tajin.winter_enchantments.events;

import com.king_tajin.winter_enchantments.WinterEnchantments;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ChunkPos;
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
import net.neoforged.neoforge.event.level.LevelEvent;
import net.neoforged.neoforge.event.level.ChunkEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.Iterator;
import java.util.Random;

public class SnowdriftEnchantmentHandler {
    private static final ResourceKey<Enchantment> SNOWDRIFT = ResourceKey.create(
            Registries.ENCHANTMENT,
            Identifier.fromNamespaceAndPath(WinterEnchantments.MODID, "snowdrift")
    );

    private static final Map<BlockPos, Long> snowMeltTimes = new HashMap<>();
    private static final int MELT_TIME_TICKS = 350;
    private static final int MELT_TIME_VARIANCE = 50;
    private static final Random random = new Random();

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
                                BlockPos immutablePos = pos.immutable();
                                int meltDelay = MELT_TIME_TICKS + random.nextInt(MELT_TIME_VARIANCE * 2 + 1) - MELT_TIME_VARIANCE;
                                snowMeltTimes.put(immutablePos, currentTime + meltDelay);
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
        Iterator<Map.Entry<BlockPos, Long>> iterator = snowMeltTimes.entrySet().iterator();

        while (iterator.hasNext()) {
            Map.Entry<BlockPos, Long> entry = iterator.next();
            BlockPos pos = entry.getKey();
            long meltTime = entry.getValue();

            if (currentTime >= meltTime) {
                BlockState state = level.getBlockState(pos);
                if (state.is(Blocks.SNOW)) {
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
        Iterator<Map.Entry<BlockPos, Long>> iterator = snowMeltTimes.entrySet().iterator();

        while (iterator.hasNext()) {
            Map.Entry<BlockPos, Long> entry = iterator.next();
            BlockPos pos = entry.getKey();

            if (new ChunkPos(pos).equals(chunkPos)) {
                BlockState state = level.getBlockState(pos);
                if (state.is(Blocks.SNOW)) {
                    level.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
                }
                iterator.remove();
            }
        }
    }

    public static void onWorldUnload(LevelEvent.Unload event) {
        if (event.getLevel() instanceof ServerLevel) {
            snowMeltTimes.clear();
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