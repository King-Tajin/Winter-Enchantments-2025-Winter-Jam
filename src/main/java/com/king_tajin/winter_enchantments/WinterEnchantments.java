package com.king_tajin.winter_enchantments;

import com.king_tajin.winter_enchantments.events.*;
import com.king_tajin.winter_enchantments.init.*;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;

@Mod(WinterEnchantments.MODID)
public class WinterEnchantments {
    public static final String MODID = "winter_enchantments";

    public WinterEnchantments(IEventBus modEventBus) {
        WinterEnchantmentsVillagers.POI_TYPES.register(modEventBus);
        WinterEnchantmentsVillagers.VILLAGER_PROFESSIONS.register(modEventBus);
        FrostedWingsEnchantmentHandler.ATTACHMENT_TYPES.register(modEventBus);
        NeoForge.EVENT_BUS.addListener(SnowdriftEnchantmentHandler::onPlayerTick);
        NeoForge.EVENT_BUS.addListener(SnowdriftEnchantmentHandler::onLevelTick);
        NeoForge.EVENT_BUS.addListener(SnowdriftEnchantmentHandler::onChunkUnload);
        NeoForge.EVENT_BUS.addListener(SnowdriftEnchantmentHandler::onWorldUnload);
        NeoForge.EVENT_BUS.addListener(SnowRunnerEnchantmentHandler::onPlayerTick);
        NeoForge.EVENT_BUS.addListener(SnowRunnerEnchantmentHandler::onPlayerLogout);
        NeoForge.EVENT_BUS.addListener(FrostResistanceEnchantmentHandler::onEntityTick);
        NeoForge.EVENT_BUS.addListener(FrostResistanceEnchantmentHandler::onLivingDamage);
        NeoForge.EVENT_BUS.addListener(FrostResistanceEnchantmentHandler::onEntityRemoved);
        NeoForge.EVENT_BUS.addListener(FrostbiteEnchantmentHandler::onEntityDamage);
        NeoForge.EVENT_BUS.addListener(FrostbiteEnchantmentHandler::onEntityTick);
        NeoForge.EVENT_BUS.addListener(FrostbiteEnchantmentHandler::onEntityDeath);
        NeoForge.EVENT_BUS.addListener(FrostbiteEnchantmentHandler::onEntityRemoved);
        NeoForge.EVENT_BUS.addListener(WinterEnchantmentsTrades::onVillagerTrades);
        NeoForge.EVENT_BUS.addListener(FrozenStabilityEnchantmentHandler::onKnockback);
        NeoForge.EVENT_BUS.addListener(FrozenStabilityEnchantmentHandler::onPlayerTick);
        NeoForge.EVENT_BUS.addListener(SnowCushionEnchantmentHandler::onLivingFall);
        NeoForge.EVENT_BUS.addListener(FrostTrapEnchantmentHandler::onArrowShoot);
        NeoForge.EVENT_BUS.addListener(FrostTrapEnchantmentHandler::onProjectileHit);
        NeoForge.EVENT_BUS.addListener(FrostTrapEnchantmentHandler::onLevelTick);
        NeoForge.EVENT_BUS.addListener(FrostTrapEnchantmentHandler::onChunkUnload);
        NeoForge.EVENT_BUS.addListener(FrostTrapEnchantmentHandler::onWorldUnload);
        NeoForge.EVENT_BUS.addListener(SnowPlowEnchantmentHandler::onLeftClickBlock);
        NeoForge.EVENT_BUS.addListener(SnowPlowEnchantmentHandler::onBlockBreak);
        NeoForge.EVENT_BUS.addListener(SnowPlowEnchantmentHandler::onBreakSpeed);
        NeoForge.EVENT_BUS.addListener(SnowHopEnchantmentHandler::onPlayerTick);
        NeoForge.EVENT_BUS.addListener(SnowHopEnchantmentHandler::onPlayerLogout);
        NeoForge.EVENT_BUS.addListener(FrostedWingsEnchantmentHandler::onFireworkSpawn);
        NeoForge.EVENT_BUS.addListener(FrostedWingsEnchantmentHandler::onPlayerTick);
        NeoForge.EVENT_BUS.addListener(FrostedWingsEnchantmentHandler::onEntityRemoved);
        NeoForge.EVENT_BUS.addListener(FrostedWingsEnchantmentHandler::onPlayerLoggedOut);
    }
}
