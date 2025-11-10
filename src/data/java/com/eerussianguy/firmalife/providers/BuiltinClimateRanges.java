package com.eerussianguy.firmalife.providers;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.UnaryOperator;
import com.eerussianguy.firmalife.common.blocks.plant.FLFruitBlocks;
import com.eerussianguy.firmalife.common.util.FLClimateRanges;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;

import net.dries007.tfc.TerraFirmaCraft;
import net.dries007.tfc.util.climate.ClimateRange;
import net.dries007.tfc.util.data.DataManager;

public class BuiltinClimateRanges extends DataManagerProvider<ClimateRange>
{
    public BuiltinClimateRanges(PackOutput output, CompletableFuture<HolderLookup.Provider> lookup)
    {
        super(ClimateRange.MANAGER, output, lookup, TerraFirmaCraft.MOD_ID);
    }

    @Override
    protected void addData(HolderLookup.Provider provider)
    {
        add(FLClimateRanges.FRUIT_TREES, FLFruitBlocks.Tree.COCOA, b -> b.hydration(44, 80).temperature(20, 35));
        add(FLClimateRanges.FRUIT_TREES, FLFruitBlocks.Tree.FIG, b -> b.hydration(25, 43).temperature(20, 35));
        add(FLClimateRanges.GRAPES, new ClimateRange.Builder().hydration(0, 100).temperature(0, 50).build());
        add(FLClimateRanges.STATIONARY_BUSHES, FLFruitBlocks.StationaryBush.PINEAPPLE, b -> b.hydration(40, 80).temperature(7, 24));
        add(FLClimateRanges.STATIONARY_BUSHES, FLFruitBlocks.StationaryBush.NIGHTSHADE, b -> b.hydration(50, 100).temperature(20, 32));
    }

    private <T> void add(Map<T, DataManager.Reference<ClimateRange>> map, T value, UnaryOperator<ClimateRange.Builder> builder)
    {
        add(map.get(value), builder.apply(new ClimateRange.Builder()).build());
    }
}
