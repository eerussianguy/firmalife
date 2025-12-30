package com.eerussianguy.firmalife.recipes;

import com.eerussianguy.firmalife.common.blocks.FLBlocks;
import com.eerussianguy.firmalife.common.blocks.Herb;
import com.eerussianguy.firmalife.common.items.FLFood;
import com.eerussianguy.firmalife.common.items.Spice;
import com.eerussianguy.firmalife.common.util.FLFruit;
import net.minecraft.world.item.crafting.Ingredient;

import net.dries007.tfc.common.recipes.QuernRecipe;
import net.dries007.tfc.common.recipes.outputs.ItemStackProvider;

public interface QuernRecipes extends Recipes
{
    default void quernRecipes()
    {
        quern(notRotten(itemOf(FLFood.NIXTAMAL)), ItemStackProvider.of(itemOf(FLFood.MASA_FLOUR), 4));
        quern(notRotten(itemOf(FLFruit.RED_GRAPES)), copyFood(itemOf(FLFood.SMASHED_RED_GRAPES)));
        quern(notRotten(itemOf(FLFruit.WHITE_GRAPES)), copyFood(itemOf(FLFood.SMASHED_WHITE_GRAPES)));
        quern(notRotten(itemOf(FLFood.DEHYDRATED_SOYBEANS)), copyFood(itemOf(FLFood.SOYBEAN_PASTE)));
        quern(Ingredient.of(FLBlocks.HERBS.get(Herb.PIMENTO)), ItemStackProvider.of(itemOf(Spice.ALLSPICE)));
        quern(Ingredient.of(FLBlocks.HERBS.get(Herb.CARDAMOM)), ItemStackProvider.of(itemOf(Spice.GROUND_CARDAMOM)));
        quern(Ingredient.of(FLBlocks.HERBS.get(Herb.CUMIN)), ItemStackProvider.of(itemOf(Spice.GROUND_CUMIN)));
    }

    private void quern(Ingredient input, ItemStackProvider output)
    {
        add(new QuernRecipe(input, output));
    }
}
