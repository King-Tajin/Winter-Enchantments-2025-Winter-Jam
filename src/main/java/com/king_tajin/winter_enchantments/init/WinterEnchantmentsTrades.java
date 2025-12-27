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

    private static final ResourceKey<Enchantment> FROST_TRAP =
            ResourceKey.create(Registries.ENCHANTMENT,
                    Identifier.fromNamespaceAndPath(WinterEnchantments.MODID, "frost_trap"));

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
                    Identifier.fromNamespaceAndPath(WinterEnchantments.MODID, "frozen_stability"));

    private static final ResourceKey<Enchantment> SNOW_SHOES =
            ResourceKey.create(Registries.ENCHANTMENT,
                    Identifier.fromNamespaceAndPath(WinterEnchantments.MODID, "snow_shoes"));

    private static final ResourceKey<Enchantment> SNOWDRIFT =
            ResourceKey.create(Registries.ENCHANTMENT,
                    Identifier.fromNamespaceAndPath(WinterEnchantments.MODID, "snowdrift"));

    private static final ResourceKey<Enchantment> SNOW_CUSHION =
            ResourceKey.create(Registries.ENCHANTMENT,
                    Identifier.fromNamespaceAndPath(WinterEnchantments.MODID, "snow_cushion"));

    private static final ResourceKey<Enchantment> SNOW_PLOW =
            ResourceKey.create(Registries.ENCHANTMENT,
                    Identifier.fromNamespaceAndPath(WinterEnchantments.MODID, "snow_plow"));

    private static final ResourceKey<Enchantment> SNOW_HOP =
            ResourceKey.create(Registries.ENCHANTMENT,
                    Identifier.fromNamespaceAndPath(WinterEnchantments.MODID, "snow_hop"));

    public static void onVillagerTrades(VillagerTradesEvent event) {
        if (event.getType().equals(WinterEnchantmentsVillagers.SNOW_SCRIBE.getKey())) {
            var trades = event.getTrades();

            trades.get(1).add((trader, rand, level) -> new MerchantOffer(
                    new ItemCost(Items.PAPER, 24),
                    new ItemStack(Items.EMERALD, 1),
                    16, 2, 0.05f
            ));

            trades.get(1).add((trader, rand, level) -> new MerchantOffer(
                    new ItemCost(Items.SNOWBALL, 24),
                    new ItemStack(Items.EMERALD, 1),
                    16, 1, 0.05f
            ));

            trades.get(1).add((trader, rand, level) -> new MerchantOffer(
                    new ItemCost(Items.EMERALD, 1),
                    new ItemStack(Items.SNOW_BLOCK, 4),
                    16, 1, 0.05f
            ));

            trades.get(1).add((trader, rand, level) -> new MerchantOffer(
                    new ItemCost(Items.RABBIT_HIDE, 6),
                    new ItemStack(Items.EMERALD, 1),
                    16, 1, 0.05f
            ));

            trades.get(1).add((trader, rand, level) -> new MerchantOffer(
                    new ItemCost(Items.EMERALD, 3),
                    new ItemStack(Items.CARVED_PUMPKIN, 1),
                    12, 1, 0.05f
            ));

            trades.get(2).add((trader, rand, level) -> new MerchantOffer(
                    new ItemCost(Items.EMERALD, 2),
                    new ItemStack(Items.ICE, 6),
                    12, 5, 0.05f
            ));

            trades.get(2).add((trader, rand, level) -> new MerchantOffer(
                    new ItemCost(Items.EMERALD, 5),
                    new ItemStack(Items.LEATHER_BOOTS, 1),
                    12, 5, 0.05f
            ));

            trades.get(2).add((trader, rand, level) -> new MerchantOffer(
                    new ItemCost(Items.SNOW_BLOCK, 8),
                    new ItemStack(Items.EMERALD, 1),
                    12, 5, 0.05f
            ));

            trades.get(2).add((trader, rand, level) -> {
                ItemStack book = new ItemStack(Items.ENCHANTED_BOOK);
                addEnchantment(book, trader.registryAccess(), FROZEN_STABILITY, 1);
                return new MerchantOffer(
                        new ItemCost(Items.EMERALD, 6),
                        java.util.Optional.of(new ItemCost(Items.BOOK)),
                        book,
                        5, 10, 0.2f
                );
            });

            trades.get(2).add((trader, rand, level) -> {
                ItemStack book = new ItemStack(Items.ENCHANTED_BOOK);
                addEnchantment(book, trader.registryAccess(), ICE_CLAWS, 1);
                return new MerchantOffer(
                        new ItemCost(Items.EMERALD, 8),
                        java.util.Optional.of(new ItemCost(Items.BOOK)),
                        book,
                        5, 10, 0.2f
                );
            });

            trades.get(2).add((trader, rand, level) -> {
                ItemStack book = new ItemStack(Items.ENCHANTED_BOOK);
                addEnchantment(book, trader.registryAccess(), SNOW_PLOW, 1);
                return new MerchantOffer(
                        new ItemCost(Items.EMERALD, 10),
                        java.util.Optional.of(new ItemCost(Items.BOOK)),
                        book,
                        5, 10, 0.2f
                );
            });

            trades.get(3).add((trader, rand, level) -> new MerchantOffer(
                    new ItemCost(Items.EMERALD, 3),
                    new ItemStack(Items.PACKED_ICE, 4),
                    12, 10, 0.05f
            ));

            trades.get(3).add((trader, rand, level) -> new MerchantOffer(
                    new ItemCost(Items.EMERALD, 8),
                    new ItemStack(Items.POWDER_SNOW_BUCKET, 1),
                    8, 10, 0.05f
            ));

            trades.get(3).add((trader, rand, level) -> {
                ItemStack book = new ItemStack(Items.ENCHANTED_BOOK);
                addEnchantment(book, trader.registryAccess(), SNOW_SHOES, 1);
                return new MerchantOffer(
                        new ItemCost(Items.EMERALD, 7),
                        java.util.Optional.of(new ItemCost(Items.BOOK)),
                        book,
                        5, 10, 0.2f
                );
            });

            trades.get(3).add((trader, rand, level) -> new MerchantOffer(
                    new ItemCost(Items.EMERALD, 3),
                    new ItemStack(Items.SNOWBALL, 32),
                    16, 10, 0.05f
            ));

            trades.get(3).add((trader, rand, level) -> {
                ItemStack book = new ItemStack(Items.ENCHANTED_BOOK);
                addEnchantment(book, trader.registryAccess(), SNOW_CUSHION, 1);
                return new MerchantOffer(
                        new ItemCost(Items.EMERALD, 10),
                        java.util.Optional.of(new ItemCost(Items.BOOK)),
                        book,
                        5, 10, 0.2f
                );
            });

            trades.get(4).add((trader, rand, level) -> new MerchantOffer(
                    new ItemCost(Items.EMERALD, 8),
                    new ItemStack(Items.NAME_TAG, 1),
                    12, 10, 0.05f
            ));

            trades.get(4).add((trader, rand, level) -> new MerchantOffer(
                    new ItemCost(Items.EMERALD, 6),
                    new ItemStack(Items.BLUE_ICE, 2),
                    12, 15, 0.05f
            ));

            trades.get(4).add((net.minecraft.server.level.ServerLevel level, net.minecraft.world.entity.Entity trader, net.minecraft.util.RandomSource rand) -> {
                ItemStack book = new ItemStack(Items.ENCHANTED_BOOK);
                addEnchantment(book, level.registryAccess(), FROST_RESISTANCE, rand.nextInt(3) + 1);
                return new MerchantOffer(
                        new ItemCost(Items.EMERALD, 15),
                        java.util.Optional.of(new ItemCost(Items.BOOK)),
                        book,
                        5, 15, 0.2f
                );
            });

            trades.get(4).add((net.minecraft.server.level.ServerLevel level, net.minecraft.world.entity.Entity trader, net.minecraft.util.RandomSource rand) -> {
                ItemStack book = new ItemStack(Items.ENCHANTED_BOOK);
                addEnchantment(book, level.registryAccess(), SNOW_HOP, rand.nextInt(3) + 1);
                return new MerchantOffer(
                        new ItemCost(Items.EMERALD, 15),
                        java.util.Optional.of(new ItemCost(Items.BOOK)),
                        book,
                        5, 15, 0.2f
                );
            });

            trades.get(4).add((net.minecraft.server.level.ServerLevel level, net.minecraft.world.entity.Entity trader, net.minecraft.util.RandomSource rand) -> {
                ItemStack book = new ItemStack(Items.ENCHANTED_BOOK);
                addEnchantment(book, level.registryAccess(), FROST_WALKER, rand.nextInt(2) + 1);
                return new MerchantOffer(
                        new ItemCost(Items.EMERALD, 18),
                        java.util.Optional.of(new ItemCost(Items.BOOK)),
                        book,
                        5, 15, 0.2f
                );
            });

            trades.get(4).add((trader, rand, level) -> new MerchantOffer(
                    new ItemCost(Items.EMERALD, 10),
                    new ItemStack(Items.SNOW_GOLEM_SPAWN_EGG, 1),
                    6, 15, 0.05f
            ));

            trades.get(5).add((net.minecraft.server.level.ServerLevel level, net.minecraft.world.entity.Entity trader, net.minecraft.util.RandomSource rand) -> {
                ItemStack book = new ItemStack(Items.ENCHANTED_BOOK);
                addEnchantment(book, level.registryAccess(), FROSTBITE, rand.nextInt(3) + 1);
                return new MerchantOffer(
                        new ItemCost(Items.EMERALD, 20),
                        java.util.Optional.of(new ItemCost(Items.BOOK)),
                        book,
                        5, 30, 0.2f
                );
            });

            trades.get(5).add((net.minecraft.server.level.ServerLevel level, net.minecraft.world.entity.Entity trader, net.minecraft.util.RandomSource rand) -> {
                ItemStack book = new ItemStack(Items.ENCHANTED_BOOK);
                addEnchantment(book, level.registryAccess(), SNOWDRIFT, rand.nextInt(3) + 1);
                return new MerchantOffer(
                        new ItemCost(Items.EMERALD, 28),
                        java.util.Optional.of(new ItemCost(Items.BOOK)),
                        book,
                        5, 30, 0.2f
                );
            });

            trades.get(5).add((net.minecraft.server.level.ServerLevel level, net.minecraft.world.entity.Entity trader, net.minecraft.util.RandomSource rand) -> {
                ItemStack book = new ItemStack(Items.ENCHANTED_BOOK);
                addEnchantment(book, level.registryAccess(), SNOW_RUNNER, rand.nextInt(3) + 1);
                return new MerchantOffer(
                        new ItemCost(Items.EMERALD, 30),
                        java.util.Optional.of(new ItemCost(Items.BOOK)),
                        book,
                        5, 30, 0.2f
                );
            });

            trades.get(5).add((trader, rand, level) -> {
                ItemStack book = new ItemStack(Items.ENCHANTED_BOOK);
                addEnchantment(book, trader.registryAccess(), FROST_TRAP, 1);
                return new MerchantOffer(
                        new ItemCost(Items.EMERALD, 30),
                        java.util.Optional.of(new ItemCost(Items.BOOK)),
                        book,
                        5, 30, 0.2f
                );
            });

            trades.get(5).add((trader, rand, level) -> new MerchantOffer(
                    new ItemCost(Items.EMERALD, 10),
                    new ItemStack(Items.PACKED_ICE, 16),
                    12, 30, 0.05f
            ));

            trades.get(5).add((trader, rand, level) -> new MerchantOffer(
                    new ItemCost(Items.EMERALD, 6),
                    new ItemStack(Items.BLUE_ICE, 4),
                    8, 30, 0.05f
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
        } catch (Exception ignored) {}
    }
}