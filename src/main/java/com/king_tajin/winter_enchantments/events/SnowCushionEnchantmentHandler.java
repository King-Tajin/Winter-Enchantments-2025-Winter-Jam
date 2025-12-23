package com.king_tajin.winter_enchantments.events;

import com.king_tajin.winter_enchantments.WinterEnchantments;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SnowLayerBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.event.entity.living.LivingFallEvent;

public class SnowCushionEnchantmentHandler {
    private static final ResourceKey<Enchantment> SNOW_CUSHION = ResourceKey.create(
            Registries.ENCHANTMENT,
            Identifier.fromNamespaceAndPath(WinterEnchantments.MODID, "snow_cushion")
    );

    public static void onLivingFall(LivingFallEvent event) {
        if (!(event.getEntity() instanceof Player player)) {
            return;
        }

        ItemStack boots = player.getItemBySlot(EquipmentSlot.FEET);
        if (boots.isEmpty()) {
            return;
        }

        try {
            Holder<Enchantment> holder = player.level().registryAccess()
                    .lookupOrThrow(Registries.ENCHANTMENT)
                    .getOrThrow(SNOW_CUSHION);

            int level = EnchantmentHelper.getEnchantmentLevel(holder, player);

            if (level > 0) {
                BlockPos pos = player.blockPosition().below();
                BlockState state = player.level().getBlockState(pos);

                if (state.is(Blocks.SNOW_BLOCK)) {
                    event.setDistance(0);
                } else if (state.is(Blocks.SNOW)) {
                    int layers = state.getValue(SnowLayerBlock.LAYERS);
                    float reduction = switch (layers) {
                        case 1 -> 0.50f;
                        case 2 -> 0.70f;
                        case 3 -> 0.80f;
                        case 4 -> 0.90f;
                        case 5 -> 0.93f;
                        case 6 -> 0.96f;
                        case 7 -> 0.98f;
                        case 8 -> 1.00f;
                        default -> 0.0f;
                    };
                    event.setDistance(event.getDistance() * (1.0f - reduction));
                }
            }
        } catch (Exception ignored) {}
    }
}