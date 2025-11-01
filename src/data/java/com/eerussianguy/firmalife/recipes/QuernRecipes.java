package com.eerussianguy.firmalife.recipes;

import com.eerussianguy.firmalife.common.FLTags;
import com.eerussianguy.firmalife.common.blocks.FLBlocks;
import com.eerussianguy.firmalife.common.items.FLFood;
import com.eerussianguy.firmalife.common.items.FLFoodTraits;
import com.eerussianguy.firmalife.common.items.FLItems;
import com.eerussianguy.firmalife.common.recipes.data.EmptyPanModifier;
import com.eerussianguy.firmalife.common.util.ExtraFluid;
import com.eerussianguy.firmalife.common.util.FLFruit;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.common.NeoForgeMod;

import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.fluids.SimpleFluid;
import net.dries007.tfc.common.fluids.TFCFluids;
import net.dries007.tfc.common.recipes.BarrelRecipe;
import net.dries007.tfc.common.recipes.QuernRecipe;
import net.dries007.tfc.common.recipes.ingredients.AndIngredient;
import net.dries007.tfc.common.recipes.ingredients.NotRottenIngredient;
import net.dries007.tfc.common.recipes.outputs.AddTraitModifier;
import net.dries007.tfc.common.recipes.outputs.CopyFoodModifier;
import net.dries007.tfc.common.recipes.outputs.CopyInputModifier;
import net.dries007.tfc.common.recipes.outputs.ItemStackProvider;

public interface QuernRecipes extends Recipes
{
    default void quernRecipes()
    {
        quern(notRotten(itemOf(FLFood.NIXTAMAL)), ItemStackProvider.of(itemOf(FLFood.MASA_FLOUR), 4));
        quern(notRotten(itemOf(FLFruit.RED_GRAPES)), copyFood(itemOf(FLFood.SMASHED_RED_GRAPES)));
        quern(notRotten(itemOf(FLFruit.WHITE_GRAPES)), copyFood(itemOf(FLFood.SMASHED_WHITE_GRAPES)));
        quern(notRotten(itemOf(FLFood.DEHYDRATED_SOYBEANS)), copyFood(itemOf(FLFood.SOYBEAN_PASTE)));
    }

    private void quern(Ingredient input, ItemStackProvider output) {
        add(new QuernRecipe(input, output));
    }
}
