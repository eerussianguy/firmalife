package com.eerussianguy.firmalife.recipes;

import java.util.List;

import com.eerussianguy.firmalife.common.blocks.FLFluids;
import com.eerussianguy.firmalife.common.util.FLMetal;

import net.dries007.tfc.common.fluids.TFCFluids;
import net.dries007.tfc.common.recipes.AlloyRecipe;
import net.dries007.tfc.util.AlloyRange;
import net.dries007.tfc.util.Metal;

public interface AlloyRecipes extends Recipes
{
    default void alloyRecipes()
    {
        add("stainless_steel", new AlloyRecipe(List.of(
            new AlloyRange(FLFluids.METALS.get(FLMetal.CHROMIUM).getSource(), 0.2, 0.3),
            new AlloyRange(TFCFluids.METALS.get(Metal.NICKEL).getSource(), 0.1, 0.2),
            new AlloyRange(TFCFluids.METALS.get(Metal.STEEL).getSource(), 0.6, 0.8)
        ), FLFluids.METALS.get(FLMetal.STAINLESS_STEEL).getSource()));
    }
}
