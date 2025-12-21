package com.king_tajin.winter_enchantments.init;
import com.google.common.collect.ImmutableSet;
import com.king_tajin.winter_enchantments.WinterEnchantments;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.entity.npc.villager.VillagerProfession;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class WinterEnchantmentsVillagers {
    public static final DeferredRegister<PoiType> POI_TYPES =
            DeferredRegister.create(Registries.POINT_OF_INTEREST_TYPE, WinterEnchantments.MODID);

    public static final DeferredRegister<VillagerProfession> VILLAGER_PROFESSIONS =
            DeferredRegister.create(Registries.VILLAGER_PROFESSION, WinterEnchantments.MODID);

    public static final Supplier<PoiType> SNOW_SCRIBE_POI = POI_TYPES.register("snow_scribe_poi",
            () -> new PoiType(
                    ImmutableSet.copyOf(Blocks.ENCHANTING_TABLE.getStateDefinition().getPossibleStates()),
                    1, 1
            )
    );

    public static final DeferredHolder<VillagerProfession, VillagerProfession> SNOW_SCRIBE = VILLAGER_PROFESSIONS.register("snow_scribe",
            () -> new VillagerProfession(
                    Component.translatable("entity.minecraft.villager.snow_scribe"),
                    holder -> holder.value() == SNOW_SCRIBE_POI.get(),
                    holder -> holder.value() == SNOW_SCRIBE_POI.get(),
                    ImmutableSet.of(),
                    ImmutableSet.of(),
                    SoundEvents.VILLAGER_WORK_LIBRARIAN
            )
    );

    public static final ResourceKey<VillagerProfession> SNOW_SCRIBE_KEY =
            ResourceKey.create(Registries.VILLAGER_PROFESSION,
                    Identifier.fromNamespaceAndPath(WinterEnchantments.MODID, "snow_scribe"));
}