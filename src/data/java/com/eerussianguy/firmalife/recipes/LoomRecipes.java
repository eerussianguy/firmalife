package com.eerussianguy.firmalife.recipes;

import com.eerussianguy.firmalife.common.FLHelpers;
import com.eerussianguy.firmalife.common.items.FLItems;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.common.crafting.SizedIngredient;

import net.dries007.tfc.common.recipes.LoomRecipe;
import net.dries007.tfc.common.recipes.outputs.ItemStackProvider;

public interface LoomRecipes extends Recipes
{
    default void loomRecipes()
    {
        loom(
            SizedIngredient.of(FLItems.PINEAPPLE_FIBER, 16),
            FLItems.PINEAPPLE_LEATHER,
            16,
            FLHelpers.identifier("block/pineapple")
        );
    }

    private void loom(SizedIngredient input, ItemLike output, int steps, ResourceLocation texture)
    {
        add(new LoomRecipe(input, ItemStackProvider.of(output), steps, texture));
    }
}
