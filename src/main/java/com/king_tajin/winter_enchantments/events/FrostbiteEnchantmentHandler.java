package com.king_tajin.winter_enchantments.events;

import com.king_tajin.winter_enchantments.WinterEnchantments;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.neoforged.neoforge.event.entity.EntityLeaveLevelEvent;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class FrostbiteEnchantmentHandler {

    private static final ResourceKey<Enchantment> FROSTBITE = ResourceKey.create(
            Registries.ENCHANTMENT,
            Identifier.fromNamespaceAndPath(WinterEnchantments.MODID, "frostbite")
    );

    private static final ResourceKey<Enchantment> FROST_RESISTANCE = ResourceKey.create(
            Registries.ENCHANTMENT,
            Identifier.fromNamespaceAndPath(WinterEnchantments.MODID, "frost_resistance")
    );

    private static final Map<UUID, Integer> freezingDuration = new HashMap<>();

    public static void onEntityDamage(LivingDamageEvent.Pre event) {
        if (!(event.getSource().getEntity() instanceof LivingEntity attacker)) {
            return;
        }

        if (attacker.level().isClientSide()) {
            return;
        }

        ItemStack weapon = attacker.getMainHandItem();
        if (weapon.isEmpty()) {
            return;
        }

        try {
            Holder<Enchantment> holder = attacker.level().registryAccess()
                    .lookupOrThrow(Registries.ENCHANTMENT)
                    .getOrThrow(FROSTBITE);

            int level = weapon.getEnchantmentLevel(holder);

            if (level > 0) {
                LivingEntity target = event.getEntity();
                UUID targetId = target.getUUID();
                int duration = 20 + (level * 50);
                freezingDuration.put(targetId, duration);
            }
        } catch (Exception ignored) {}
    }
    
    public static void onEntityTick(EntityTickEvent.Pre event) {
        if (!(event.getEntity() instanceof LivingEntity entity)) {
            return;
        }

        if (entity.level().isClientSide()) {
            return;
        }

        UUID entityId = entity.getUUID();
        Integer duration = freezingDuration.get(entityId);

        if (duration != null && duration > 0) {
            int resistanceLevel = 0;
            ItemStack chestplate = entity.getItemBySlot(EquipmentSlot.CHEST);
            if (!chestplate.isEmpty()) {
                try {
                    Holder<Enchantment> resistanceHolder = entity.level().registryAccess()
                            .lookupOrThrow(Registries.ENCHANTMENT)
                            .getOrThrow(FROST_RESISTANCE);
                    resistanceLevel = chestplate.getEnchantmentLevel(resistanceHolder);
                } catch (Exception ignored) {}
            }

            int newFreeze = getNewFreeze(entity, resistanceLevel);

            entity.setTicksFrozen(newFreeze);
            
            duration--;
            if (duration <= 0) {
                freezingDuration.remove(entityId);
            } else {
                freezingDuration.put(entityId, duration);
            }
        }
    }

    private static int getNewFreeze(LivingEntity entity, int resistanceLevel) {
        int freezeAmount = 3;
        //because the game removes 3 every tick

        int additionalFreeze = switch (resistanceLevel) {
            case 1 -> (entity.tickCount % 10 < 8) ? 1 : 0;
            case 2 -> (entity.tickCount % 10 < 6) ? 1 : 0;
            case 3 -> (entity.tickCount % 10 < 3) ? 1 : 0;
            default -> 1;
        };

        freezeAmount += additionalFreeze;

        int currentFreeze = entity.getTicksFrozen();
        int maxFreeze = entity.getTicksRequiredToFreeze() + 100;
        return Math.min(currentFreeze + freezeAmount, maxFreeze);
    }

    public static void onEntityDeath(LivingDeathEvent event) {
        freezingDuration.remove(event.getEntity().getUUID());
    }

    public static void onEntityRemoved(EntityLeaveLevelEvent event) {
        if (event.getEntity() instanceof LivingEntity) {
            freezingDuration.remove(event.getEntity().getUUID());
        }
    }
}