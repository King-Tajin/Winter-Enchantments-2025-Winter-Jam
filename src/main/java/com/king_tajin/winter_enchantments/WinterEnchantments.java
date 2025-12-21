package com.king_tajin.winter_enchantments;

import com.mojang.logging.LogUtils;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import org.slf4j.Logger;

@Mod(WinterEnchantments.MODID)
public class WinterEnchantments {
    public static final String MODID = "winter_enchantments";
    private static final Logger LOGGER = LogUtils.getLogger();

    public WinterEnchantments(IEventBus modEventBus, ModContainer modContainer) {
        modEventBus.addListener(this::commonSetup);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {

    }
}
