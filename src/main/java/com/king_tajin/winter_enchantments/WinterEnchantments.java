package com.king_tajin.winter_enchantments;

import com.king_tajin.winter_enchantments.events.*;
import com.king_tajin.winter_enchantments.init.WinterEnchantmentsTrades;
import com.king_tajin.winter_enchantments.init.WinterEnchantmentsVillagers;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(WinterEnchantments.MODID)
public class WinterEnchantments {
    public static final String MODID = "winter_enchantments";

    public static final Logger LOGGER =
            LogManager.getLogger(MODID);

    public WinterEnchantments(IEventBus modEventBus) {
        WinterEnchantmentsVillagers.POI_TYPES.register(modEventBus);
        WinterEnchantmentsVillagers.VILLAGER_PROFESSIONS.register(modEventBus);
        NeoForge.EVENT_BUS.addListener(SnowdriftEnchantmentHandler::onPlayerTick);
        NeoForge.EVENT_BUS.addListener(SnowdriftEnchantmentHandler::onLevelTick);
        NeoForge.EVENT_BUS.addListener(SnowRunnerEnchantmentHandler::onPlayerTick);
        NeoForge.EVENT_BUS.addListener(FrostResistanceHandler::onEntityTick);
        NeoForge.EVENT_BUS.addListener(FrostbiteEnchantmentHandler::onEntityDamage);
        NeoForge.EVENT_BUS.addListener(FrostbiteEnchantmentHandler::onEntityTick);
        NeoForge.EVENT_BUS.addListener(FrostbiteEnchantmentHandler::onEntityDeath);
        NeoForge.EVENT_BUS.addListener(WinterEnchantmentsTrades::onVillagerTrades);
    }

}
