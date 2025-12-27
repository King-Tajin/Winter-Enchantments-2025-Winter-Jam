package com.king_tajin.winter_enchantments.events;

import com.king_tajin.winter_enchantments.WinterEnchantments;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SnowHopEnchantmentHandler {

    private static final ResourceKey<Enchantment> SNOW_HOP = ResourceKey.create(
            Registries.ENCHANTMENT,
            Identifier.fromNamespaceAndPath(WinterEnchantments.MODID, "snow_hop")
    );

    private static final Identifier STEP_HEIGHT_MODIFIER_ID =
            Identifier.fromNamespaceAndPath(WinterEnchantments.MODID, "snow_hop_step_height");
    private static final Map<UUID, Boolean> playerStepModified = new HashMap<>();

    public static void onPlayerTick(PlayerTickEvent.Post event) {
        Player player = event.getEntity();

        if (player.level().isClientSide()) {
            return;
        }

        Holder<Enchantment> snowHopHolder = player.level().registryAccess()
                .lookupOrThrow(Registries.ENCHANTMENT)
                .getOrThrow(SNOW_HOP);

        ItemStack boots = player.getItemBySlot(EquipmentSlot.FEET);
        int enchantmentLevel = boots.getEnchantmentLevel(snowHopHolder);

        boolean isOnSnow = isStandingOnSnow(player);
        UUID playerId = player.getUUID();
        boolean currentlyModified = playerStepModified.getOrDefault(playerId, false);

        if (enchantmentLevel > 0 && isOnSnow) {
            if (!currentlyModified) {
                applyStepHeightBoost(player, enchantmentLevel);
                playerStepModified.put(playerId, true);
            }
        } else {
            if (currentlyModified) {
                removeStepHeightBoost(player);
                playerStepModified.put(playerId, false);
            }
        }
    }

    private static boolean isStandingOnSnow(Player player) {
        BlockState blockBelow = player.level().getBlockState(player.blockPosition().below());
        BlockState blockAt = player.level().getBlockState(player.blockPosition());

        return blockBelow.is(Blocks.SNOW_BLOCK) ||
                blockBelow.is(Blocks.SNOW) ||
                blockAt.is(Blocks.SNOW);
    }

    private static void applyStepHeightBoost(Player player, int level) {
        AttributeInstance stepHeight = player.getAttribute(Attributes.STEP_HEIGHT);
        if (stepHeight == null) {
            return;
        }

        stepHeight.removeModifier(STEP_HEIGHT_MODIFIER_ID);
        double boost = (level * 0.5) + 0.5;

        AttributeModifier modifier = new AttributeModifier(
                STEP_HEIGHT_MODIFIER_ID,
                boost,
                AttributeModifier.Operation.ADD_VALUE
        );

        stepHeight.addTransientModifier(modifier);
    }

    private static void removeStepHeightBoost(Player player) {
        AttributeInstance stepHeight = player.getAttribute(Attributes.STEP_HEIGHT);
        if (stepHeight != null) {
            stepHeight.removeModifier(STEP_HEIGHT_MODIFIER_ID);
        }
    }

    public static void onPlayerLogout(PlayerEvent.PlayerLoggedOutEvent event) {
        Player player = event.getEntity();
        removeStepHeightBoost(player);
        playerStepModified.remove(player.getUUID());
    }
}