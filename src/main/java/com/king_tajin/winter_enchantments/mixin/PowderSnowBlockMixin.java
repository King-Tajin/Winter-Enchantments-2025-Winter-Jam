package com.king_tajin.winter_enchantments.mixin;

import com.king_tajin.winter_enchantments.WinterEnchantments;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.block.PowderSnowBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PowderSnowBlock.class)
public class PowderSnowBlockMixin {

    @Inject(method = "canEntityWalkOnPowderSnow", at = @At("HEAD"), cancellable = true)
    private static void onCanEntityWalkOnPowderSnow(Entity entity, CallbackInfoReturnable<Boolean> cir) {
        if (entity instanceof LivingEntity living) {
            ItemStack boots = living.getItemBySlot(EquipmentSlot.FEET);

            if (!boots.isEmpty()) {
                ResourceKey<Enchantment> snowShoes = ResourceKey.create(
                        Registries.ENCHANTMENT,
                        Identifier.fromNamespaceAndPath(WinterEnchantments.MODID, "snow_shoes")
                );

                int level = boots.getEnchantmentLevel(
                        entity.level().holderOrThrow(snowShoes)
                );

                if (level > 0) {
                    cir.setReturnValue(true);
                }
            }
        }
    }
}