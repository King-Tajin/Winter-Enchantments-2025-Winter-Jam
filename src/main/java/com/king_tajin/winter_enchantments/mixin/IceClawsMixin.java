package com.king_tajin.winter_enchantments.mixin;

import com.king_tajin.winter_enchantments.WinterEnchantments;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.List;

@Mixin(LivingEntity.class)
public abstract class IceClawsMixin {

    @Shadow public abstract ItemStack getItemBySlot(EquipmentSlot slot);

    @Unique
    private static final ResourceKey<Enchantment> ICE_CLAWS = ResourceKey.create(
            Registries.ENCHANTMENT,
            Identifier.fromNamespaceAndPath(WinterEnchantments.MODID, "ice_claws")
    );

    @Unique
    private static final List<Block> ICE_BLOCKS = List.of(
            Blocks.ICE,
            Blocks.PACKED_ICE,
            Blocks.BLUE_ICE,
            Blocks.FROSTED_ICE
    );

    @Redirect(
            method = "travelInAir",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/block/state/BlockState;getFriction(Lnet/minecraft/world/level/LevelReader;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/entity/Entity;)F"
            )
    )
    private float redirectGetFriction(BlockState state, LevelReader level, BlockPos pos, Entity entity) {
        float originalFriction = state.getFriction(level, pos, entity);

        if (!(entity instanceof Player player)) {
            return originalFriction;
        }

        ItemStack boots = this.getItemBySlot(EquipmentSlot.FEET);
        if (boots.isEmpty()) {
            return originalFriction;
        }

        if (!ICE_BLOCKS.contains(state.getBlock())) {
            return originalFriction;
        }

        try {
            Holder<Enchantment> holder = entity.level().registryAccess()
                    .lookupOrThrow(Registries.ENCHANTMENT)
                    .getOrThrow(ICE_CLAWS);

            int level1 = EnchantmentHelper.getEnchantmentLevel(holder, player);


            if (level1 > 0) {
                return 0.6f;
            }
        } catch (Exception ignored) {}

        return originalFriction;
    }
}