package com.eerussianguy.firmalife.common.util;

import java.util.Locale;
import java.util.Map;
import java.util.function.Supplier;

import com.eerussianguy.firmalife.common.FLHelpers;
import com.eerussianguy.firmalife.common.blocks.plant.FLFruitBlocks;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.RegisteredDataManager;
import net.dries007.tfc.util.climate.ClimateRange;

public class FLClimateRanges
{
    public static final Map<FLFruitBlocks.Tree, Supplier<ClimateRange>> FRUIT_TREES = Helpers.mapOfKeys(FLFruitBlocks.Tree.class, tree -> register("plant/" + tree.name() + "_tree"));

    private static RegisteredDataManager.Entry<ClimateRange> register(String name)
    {
        return ClimateRange.MANAGER.register(FLHelpers.identifier(name.toLowerCase(Locale.ROOT)));
    }
}
