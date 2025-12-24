package com.king_tajin.winter_enchantments.init;

import com.king_tajin.winter_enchantments.WinterEnchantments;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.item.trading.ItemCost;
import net.minecraft.world.item.trading.MerchantOffer;
import net.neoforged.neoforge.event.village.VillagerTradesEvent;

public class WinterEnchantmentsTrades {

    private static final ResourceKey<Enchantment> FROST_RESISTANCE =
            ResourceKey.create(Registries.ENCHANTMENT,
                    Identifier.fromNamespaceAndPath(WinterEnchantments.MODID, "frost_resistance"));

    private static final ResourceKey<Enchantment> SNOW_RUNNER =
            ResourceKey.create(Registries.ENCHANTMENT,
                    Identifier.fromNamespaceAndPath(WinterEnchantments.MODID, "snow_runner"));

    private static final ResourceKey<Enchantment> FROSTBITE =
            ResourceKey.create(Registries.ENCHANTMENT,
                    Identifier.fromNamespaceAndPath(WinterEnchantments.MODID, "frostbite"));

    private static final ResourceKey<Enchantment> ICE_CLAWS =
            ResourceKey.create(Registries.ENCHANTMENT,
                    Identifier.fromNamespaceAndPath(WinterEnchantments.MODID, "ice_claws"));

    private static final ResourceKey<Enchantment> FROST_WALKER =
            ResourceKey.create(Registries.ENCHANTMENT,
                    Identifier.fromNamespaceAndPath("minecraft", "frost_walker"));

    private static final ResourceKey<Enchantment> FROZEN_STABILITY =
            ResourceKey.create(Registries.ENCHANTMENT,
                    Identifier.fromNamespaceAndPath("minecraft", "frozen_stability"));

    private static final ResourceKey<Enchantment> SNOW_SHOES =
            ResourceKey.create(Registries.ENCHANTMENT,
                    Identifier.fromNamespaceAndPath("minecraft", "snow_shoes"));

    private static final ResourceKey<Enchantment> SNOWDRIFT =
            ResourceKey.create(Registries.ENCHANTMENT,
                    Identifier.fromNamespaceAndPath("minecraft", "snowdrift"));

    private static final ResourceKey<Enchantment> SNOW_CUSHION =
            ResourceKey.create(Registries.ENCHANTMENT,
                    Identifier.fromNamespaceAndPath("minecraft", "snow_cushion"));

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

            trades.get(1).add((trader, random, level) -> new MerchantOffer(
                    new ItemCost(Items.EMERALD, 5),
                    new ItemStack(Items.BOOK, 3),
                    12, 1, 0.05f
            ));

            trades.get(1).add((trader, random, level) -> new MerchantOffer(
                    new ItemCost(Items.RABBIT_HIDE, 9),
                    new ItemStack(Items.EMERALD, 1),
                    12, 1, 0.05f
            ));

            trades.get(2).add((trader, random, level) -> new MerchantOffer(
                    new ItemCost(Items.EMERALD, 3),
                    new ItemStack(Items.SNOW_BLOCK, 8),
                    12, 5, 0.05f
            ));

            trades.get(2).add((trader, random, level) -> new MerchantOffer(
                    new ItemCost(Items.EMERALD, 2),
                    new ItemStack(Items.ICE, 4),
                    12, 5, 0.05f
            ));

            trades.get(2).add((trader, random, level) -> new MerchantOffer(
                    new ItemCost(Items.EMERALD, 3),
                    new ItemStack(Items.LEATHER_BOOTS, 1),
                    12, 5, 0.05f
            ));

            trades.get(2).add((trader, random, level) -> new MerchantOffer(
                    new ItemCost(Items.SNOW_BLOCK, 4),
                    new ItemStack(Items.EMERALD, 1),
                    12, 5, 0.05f
            ));

            trades.get(2).add((trader, random, level) -> {
                ItemStack book = new ItemStack(Items.ENCHANTED_BOOK);
                addEnchantment(book, trader.registryAccess(), FROZEN_STABILITY, 1);
                return new MerchantOffer(
                        new ItemCost(Items.EMERALD, 12),
                        book,
                        12, 10, 0.2f
                );
            });

            trades.get(3).add((trader, random, level) -> new MerchantOffer(
                    new ItemCost(Items.EMERALD, 4),
                    new ItemStack(Items.PACKED_ICE, 4),
                    12, 10, 0.05f
            ));

            trades.get(3).add((trader, random, level) -> new MerchantOffer(
                    new ItemCost(Items.EMERALD, 6),
                    new ItemStack(Items.POWDER_SNOW_BUCKET, 1),
                    8, 10, 0.05f
            ));

            trades.get(3).add((trader, random, level) -> {
                ItemStack book = new ItemStack(Items.ENCHANTED_BOOK);
                addEnchantment(book, trader.registryAccess(), SNOW_SHOES, 1);
                return new MerchantOffer(
                        new ItemCost(Items.EMERALD, 12),
                        book,
                        12, 10, 0.2f
                );
            });

            trades.get(3).add((trader, random, level) -> {
                ItemStack book = new ItemStack(Items.ENCHANTED_BOOK);
                addEnchantment(book, trader.registryAccess(), SNOWDRIFT, 2);
                return new MerchantOffer(
                        new ItemCost(Items.EMERALD, 12),
                        book,
                        12, 10, 0.2f
                );
            });

            trades.get(3).add((trader, random, level) -> new MerchantOffer(
                    new ItemCost(Items.EMERALD, 5),
                    new ItemStack(Items.SNOWBALL, 32),
                    16, 10, 0.05f
            ));

            trades.get(4).add((trader, random, level) -> new MerchantOffer(
                    new ItemCost(Items.EMERALD, 8),
                    new ItemStack(Items.BLUE_ICE, 2),
                    12, 15, 0.05f
            ));

            trades.get(4).add((trader, random, level) -> {
                ItemStack book = new ItemStack(Items.ENCHANTED_BOOK);
                addEnchantment(book, trader.registryAccess(), FROST_RESISTANCE, 2);
                return new MerchantOffer(
                        new ItemCost(Items.EMERALD, 15),
                        book,
                        12, 15, 0.2f
                );
            });

            trades.get(4).add((trader, random, level) -> {
                ItemStack book = new ItemStack(Items.ENCHANTED_BOOK);
                addEnchantment(book, trader.registryAccess(), FROST_WALKER, 2);
                return new MerchantOffer(
                        new ItemCost(Items.EMERALD, 18),
                        book,
                        12, 15, 0.2f
                );
            });

            trades.get(4).add((trader, random, level) -> {
                ItemStack book = new ItemStack(Items.ENCHANTED_BOOK);
                addEnchantment(book, trader.registryAccess(), SNOW_CUSHION, 1);
                return new MerchantOffer(
                        new ItemCost(Items.EMERALD, 12),
                        book,
                        12, 10, 0.2f
                );
            });

            trades.get(4).add((trader, random, level) -> new MerchantOffer(
                    new ItemCost(Items.EMERALD, 7),
                    new ItemStack(Items.SNOW_GOLEM_SPAWN_EGG, 1),
                    6, 15, 0.05f
            ));

            trades.get(5).add((trader, random, level) -> {
                ItemStack book = new ItemStack(Items.ENCHANTED_BOOK);
                addEnchantment(book, trader.registryAccess(), FROSTBITE, 2);
                return new MerchantOffer(
                        new ItemCost(Items.EMERALD, 20),
                        book,
                        12, 30, 0.2f
                );
            });

            trades.get(5).add((trader, random, level) -> {
                ItemStack book = new ItemStack(Items.ENCHANTED_BOOK);
                addEnchantment(book, trader.registryAccess(), ICE_CLAWS, 1);
                return new MerchantOffer(
                        new ItemCost(Items.EMERALD, 22),
                        book,
                        12, 30, 0.2f
                );
            });

            trades.get(5).add((trader, random, level) -> {
                ItemStack book = new ItemStack(Items.ENCHANTED_BOOK);
                addEnchantment(book, trader.registryAccess(), SNOW_RUNNER, 2);
                return new MerchantOffer(
                        new ItemCost(Items.EMERALD, 25),
                        book,
                        12, 30, 0.2f
                );
            });

            trades.get(5).add((trader, random, level) -> {
                ItemStack book = new ItemStack(Items.ENCHANTED_BOOK);
                addEnchantment(book, trader.registryAccess(), FROST_RESISTANCE, 2);
                return new MerchantOffer(
                        new ItemCost(Items.EMERALD, 28),
                        book,
                        12, 30, 0.2f
                );
            });

            trades.get(5).add((trader, random, level) -> new MerchantOffer(
                    new ItemCost(Items.EMERALD, 12),
                    new ItemStack(Items.PACKED_ICE, 16),
                    12, 30, 0.05f
            ));
        }
    }

    private static void addEnchantment(ItemStack book, net.minecraft.core.RegistryAccess registryAccess, ResourceKey<Enchantment> enchantmentKey, int level) {
        try {
            Holder<Enchantment> enchantmentHolder = registryAccess
                    .lookupOrThrow(Registries.ENCHANTMENT)
                    .getOrThrow(enchantmentKey);

            ItemEnchantments.Mutable enchantments = new ItemEnchantments.Mutable(ItemEnchantments.EMPTY);
            enchantments.set(enchantmentHolder, level);
            book.set(DataComponents.STORED_ENCHANTMENTS, enchantments.toImmutable());
        } catch (Exception e) {
            WinterEnchantments.LOGGER.error("Failed to add enchantment to book", e);
        }
    }
}