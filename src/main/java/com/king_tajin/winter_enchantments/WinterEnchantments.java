package com.king_tajin.winter_enchantments;

import com.king_tajin.winter_enchantments.init.WinterEnchantmentsVillagers;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;

@Mod(WinterEnchantments.MODID)
public class WinterEnchantments {
    public static final String MODID = "winter_enchantments";

    public WinterEnchantments(IEventBus modEventBus) {
        WinterEnchantmentsVillagers.POI_TYPES.register(modEventBus);
        WinterEnchantmentsVillagers.VILLAGER_PROFESSIONS.register(modEventBus);
    }

}
