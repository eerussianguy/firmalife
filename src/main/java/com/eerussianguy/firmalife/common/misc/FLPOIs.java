package com.eerussianguy.firmalife.common.misc;

import com.eerussianguy.firmalife.FirmaLife;
import com.eerussianguy.firmalife.common.blocks.FLBlocks;
import com.google.common.collect.ImmutableSet;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class FLPOIs
{
    public static final DeferredRegister<PoiType> TYPES = DeferredRegister.create(BuiltInRegistries.POINT_OF_INTEREST_TYPE, FirmaLife.MOD_ID);

    public static final DeferredHolder<PoiType, PoiType> BEEHIVES = TYPES.register("beehives", () -> new PoiType(ImmutableSet.<BlockState>builder()
        .addAll(states(FLBlocks.BEEHIVE.get()))
        .addAll(states(FLBlocks.SKEP.get()))
        .build(),
        0, 1
    ));

    public static final DeferredHolder<PoiType, PoiType> CLIMATE_STATIONS = TYPES.register("climate_stations", () -> new PoiType(ImmutableSet.<BlockState>builder()
        .addAll(states(FLBlocks.CLIMATE_STATION.get()))
        .build(),
        0, 1
    ));

    private static Iterable<BlockState> states(Block block)
    {
        return block.getStateDefinition().getPossibleStates();
    }

}
