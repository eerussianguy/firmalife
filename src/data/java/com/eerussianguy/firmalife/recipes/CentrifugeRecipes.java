package com.eerussianguy.firmalife.recipes;

import com.eerussianguy.firmalife.common.items.FLFood;
import com.eerussianguy.firmalife.common.items.FLItems;
import com.eerussianguy.firmalife.common.recipes.CentrifugeRecipe;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;

import net.dries007.tfc.common.recipes.outputs.ItemStackProvider;

public interface CentrifugeRecipes extends Recipes
{
    default void centrifugeRecipes()
    {
        add(new CentrifugeRecipe(Ingredient.of(FLItems.SCRAPED_BEEHIVE_FRAME), ItemStackProvider.of(FLItems.FOODS.get(FLFood.RAW_HONEY))));
        add(new CentrifugeRecipe(Ingredient.of(FLItems.SUGARED_BEEHIVE_FRAME), ItemStackProvider.of(Items.SUGAR)));
        add("wild_honeycomb", new CentrifugeRecipe(Ingredient.of(FLItems.WILD_HONEYCOMB), ItemStackProvider.of(FLItems.FOODS.get(FLFood.RAW_HONEY))));
    }
}
