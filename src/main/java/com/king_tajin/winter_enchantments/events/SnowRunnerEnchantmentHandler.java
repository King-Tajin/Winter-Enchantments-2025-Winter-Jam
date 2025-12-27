package com.king_tajin.winter_enchantments.events;

import com.king_tajin.winter_enchantments.WinterEnchantments;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

import java.util.Set;

public class SnowRunnerEnchantmentHandler {
    private static final ResourceKey<Enchantment> SNOW_RUNNER = ResourceKey.create(
            Registries.ENCHANTMENT,
            Identifier.fromNamespaceAndPath(WinterEnchantments.MODID, "snow_runner")
    );

    private static final Identifier SPEED_MODIFIER_ID = Identifier.fromNamespaceAndPath(WinterEnchantments.MODID, "snow_runner_speed");

    private static final Set<Block> SPEED_BLOCKS = Set.of(
            Blocks.SNOW,
            Blocks.SNOW_BLOCK,
            Blocks.POWDER_SNOW,
            Blocks.ICE,
            Blocks.BLUE_ICE,
            Blocks.FROSTED_ICE,
            Blocks.PACKED_ICE
    );

    public static void onPlayerTick(PlayerTickEvent.Post event) {
        Player player = event.getEntity();

        if (player.level().isClientSide()) {
            return;
        }

        AttributeInstance movementSpeed = player.getAttribute(Attributes.MOVEMENT_SPEED);
        if (movementSpeed == null) {
            return;
        }

        boolean hasModifier = movementSpeed.getModifier(SPEED_MODIFIER_ID) != null;
        boolean shouldHaveSpeed = false;

        ItemStack boots = player.getItemBySlot(EquipmentSlot.FEET);
        if (!boots.isEmpty() && player.onGround()) {
            try {
                Holder<Enchantment> holder = player.level().registryAccess()
                        .lookupOrThrow(Registries.ENCHANTMENT)
                        .getOrThrow(SNOW_RUNNER);

                int level = boots.getEnchantmentLevel(holder);

                if (level > 0) {
                    BlockPos pos = player.blockPosition();
                    BlockState current = player.level().getBlockState(pos);
                    BlockState below = player.level().getBlockState(pos.below());

                    if (SPEED_BLOCKS.contains(current.getBlock()) ||
                            SPEED_BLOCKS.contains(below.getBlock())) {

                        shouldHaveSpeed = true;

                        if (!hasModifier) {
                            double speedBonus = getSpeedForLevel(level);
                            movementSpeed.addTransientModifier(new AttributeModifier(
                                    SPEED_MODIFIER_ID,
                                    speedBonus,
                                    AttributeModifier.Operation.ADD_VALUE
                            ));
                        }
                    }
                }
            } catch (Exception ignored) {}
        }

        if (!shouldHaveSpeed && hasModifier) {
            movementSpeed.removeModifier(SPEED_MODIFIER_ID);
        }
    }

    public static void onPlayerLogout(PlayerEvent.PlayerLoggedOutEvent event) {
        Player player = event.getEntity();
        AttributeInstance movementSpeed = player.getAttribute(Attributes.MOVEMENT_SPEED);
        if (movementSpeed != null) {
            movementSpeed.removeModifier(SPEED_MODIFIER_ID);
        }
    }

    private static double getSpeedForLevel(int level) {
        return switch (level) {
            case 1 -> 0.035;
            case 2 -> 0.045;
            case 3 -> 0.05;
            default -> 0.0;
        };
    }
}