package com.eerussianguy.firmalife.recipes;

import com.eerussianguy.firmalife.common.FLHelpers;
import com.eerussianguy.firmalife.common.FLTags;
import com.eerussianguy.firmalife.common.blocks.FLBlocks;
import com.eerussianguy.firmalife.common.items.FLFood;
import com.eerussianguy.firmalife.common.items.FLFoodTraits;
import com.eerussianguy.firmalife.common.items.FLItems;
import com.eerussianguy.firmalife.common.recipes.data.EmptyPanModifier;
import com.eerussianguy.firmalife.common.util.ExtraFluid;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.common.NeoForgeMod;
import net.neoforged.neoforge.common.crafting.SizedIngredient;

import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.fluids.SimpleFluid;
import net.dries007.tfc.common.fluids.TFCFluids;
import net.dries007.tfc.common.recipes.BarrelRecipe;
import net.dries007.tfc.common.recipes.LoomRecipe;
import net.dries007.tfc.common.recipes.outputs.AddTraitModifier;
import net.dries007.tfc.common.recipes.outputs.CopyInputModifier;
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

    private void loom(SizedIngredient input, ItemLike output, int steps, ResourceLocation texture) {
        add(new LoomRecipe(input, ItemStackProvider.of(output), steps, texture));
    }
}
