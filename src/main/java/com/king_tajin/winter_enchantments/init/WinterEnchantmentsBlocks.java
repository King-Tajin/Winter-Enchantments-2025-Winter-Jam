package com.king_tajin.winter_enchantments.init;

import com.king_tajin.winter_enchantments.WinterEnchantments;
import com.king_tajin.winter_enchantments.blocks.IcicleBlock;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

public class WinterEnchantmentsBlocks {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(WinterEnchantments.MODID);

    public static final DeferredBlock<IcicleBlock> ICICLE = BLOCKS.register(
            "icicle",
            () -> new IcicleBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.ICE)
                    .sound(SoundType.GLASS)
                    .noOcclusion()
                    .strength(1.5F, 3.0F)
                    .dynamicShape()
                    .offsetType(BlockBehaviour.OffsetType.XZ)
                    .pushReaction(net.minecraft.world.level.material.PushReaction.DESTROY)
                    .noLootTable()
                    .setId(ResourceKey.create(Registries.BLOCK, Identifier.fromNamespaceAndPath(WinterEnchantments.MODID, "icicle")))
    ));
}