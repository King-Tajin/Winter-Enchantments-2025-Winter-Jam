package com.king_tajin.winter_enchantments;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;

@Mod(WinterEnchantments.MODID)
public class WinterEnchantments {
    public static final String MODID = "winter_enchantments";

    public WinterEnchantments(IEventBus modEventBus) {
        modEventBus.addListener(this::commonSetup);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {

    }
}
