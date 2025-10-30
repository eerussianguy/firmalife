package com.eerussianguy.firmalife.common.util;

import java.util.Locale;
import java.util.Map;

import com.eerussianguy.firmalife.common.FLHelpers;
import com.eerussianguy.firmalife.common.blocks.plant.FLFruitBlocks;

import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.climate.ClimateRange;
import net.dries007.tfc.util.data.DataManager;

public class FLClimateRanges
{
    public static final Map<FLFruitBlocks.Tree, DataManager.Reference<ClimateRange>> FRUIT_TREES = Helpers.mapOf(FLFruitBlocks.Tree.class, tree -> register("plant/" + tree.name() + "_tree"));
    public static final Map<FLFruitBlocks.StationaryBush, DataManager.Reference<ClimateRange>> STATIONARY_BUSHES = Helpers.mapOf(FLFruitBlocks.StationaryBush.class, bush -> register("plant/" + bush.name() + "_bush"));
    public static final DataManager.Reference<ClimateRange> GRAPES = register("plant/grapes");

    private static DataManager.Reference<ClimateRange> register(String name)
    {
        return ClimateRange.MANAGER.getReference(FLHelpers.identifier(name.toLowerCase(Locale.ROOT)));
    }
}
