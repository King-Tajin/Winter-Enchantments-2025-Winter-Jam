package com.king_tajin.winter_enchantments.init;

import com.king_tajin.winter_enchantments.WinterEnchantments;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.trading.ItemCost;
import net.minecraft.world.item.trading.MerchantOffer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.village.VillagerTradesEvent;

@EventBusSubscriber(modid = WinterEnchantments.MODID)
public class WinterEnchantmentsTrades {

    @SubscribeEvent
    public static void onVillagerTrades(VillagerTradesEvent event) {
        if (event.getType().equals(WinterEnchantmentsVillagers.SNOW_SCRIBE.getKey())) {
            var trades = event.getTrades();

            trades.get(1).add((trader, random, level) -> new MerchantOffer(
                    new ItemCost(Items.EMERALD, 1),
                    new ItemStack(Items.PAPER, 24),
                    16, 2, 0.05f
            ));

            trades.get(1).add((trader, random, level) -> new MerchantOffer(
                    new ItemCost(Items.SNOWBALL, 16),
                    new ItemStack(Items.EMERALD, 1),
                    16, 1, 0.05f
            ));

            trades.get(2).add((trader, random, level) -> new MerchantOffer(
                    new ItemCost(Items.EMERALD, 3),
                    new ItemStack(Items.SNOW_BLOCK, 8),
                    12, 10, 0.05f
            ));
        }
    }
}