package com.king_tajin.winter_enchantments.init;

import com.google.common.collect.ImmutableSet;
import com.king_tajin.winter_enchantments.WinterEnchantments;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.entity.npc.villager.VillagerProfession;
import net.minecraft.world.item.trading.TradeSet;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.Set;

public class WinterEnchantmentsVillagers {
    public static final DeferredRegister<PoiType> POI_TYPES =
            DeferredRegister.create(Registries.POINT_OF_INTEREST_TYPE, WinterEnchantments.MODID);

    public static final DeferredRegister<VillagerProfession> VILLAGER_PROFESSIONS =
            DeferredRegister.create(Registries.VILLAGER_PROFESSION, WinterEnchantments.MODID);

    public static final DeferredHolder<PoiType, PoiType> SNOW_SCRIBE_POI = POI_TYPES.register("snow_scribe_poi",
            () -> {
                Set<BlockState> states = ImmutableSet.copyOf(Blocks.CHISELED_BOOKSHELF.getStateDefinition().getPossibleStates());
                return new PoiType(states, 1, 1);
            }
    );

    private static ResourceKey<TradeSet> tradeSet(String name) {
        return ResourceKey.create(Registries.TRADE_SET,
                Identifier.fromNamespaceAndPath(WinterEnchantments.MODID, name));
    }

    @SuppressWarnings("unused")
    public static final DeferredHolder<VillagerProfession, VillagerProfession> SNOW_SCRIBE = VILLAGER_PROFESSIONS.register("snow_scribe",
            () -> {
                var trades = new Int2ObjectOpenHashMap<ResourceKey<TradeSet>>();
                trades.put(1, tradeSet("snow_scribe_level_1"));
                trades.put(2, tradeSet("snow_scribe_level_2"));
                trades.put(3, tradeSet("snow_scribe_level_3"));
                trades.put(4, tradeSet("snow_scribe_level_4"));
                trades.put(5, tradeSet("snow_scribe_level_5"));
                return new VillagerProfession(
                        Component.translatable("entity.minecraft.villager.snow_scribe"),
                        holder -> holder.is(SNOW_SCRIBE_POI.getKey()),
                        holder -> holder.is(SNOW_SCRIBE_POI.getKey()),
                        ImmutableSet.of(),
                        ImmutableSet.of(),
                        SoundEvents.VILLAGER_WORK_LIBRARIAN,
                        trades
                );
            }
    );
}