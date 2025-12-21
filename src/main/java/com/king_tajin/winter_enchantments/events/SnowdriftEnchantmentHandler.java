package com.king_tajin.winter_enchantments.events;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

public class SnowdriftEnchantmentHandler {
    private static final ResourceKey<Enchantment> SNOWDRIFT = ResourceKey.create(
            Registries.ENCHANTMENT,
            Identifier.fromNamespaceAndPath("winter_enchantments", "snowdrift")
    );

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
                        }
                    }
                }
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