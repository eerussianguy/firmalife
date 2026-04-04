package com.eerussianguy.firmalife.recipes;

import com.eerussianguy.firmalife.common.FLTags;
import com.eerussianguy.firmalife.common.items.FLFoodTraits;
import com.eerussianguy.firmalife.common.recipes.SmokingRecipe;
import net.minecraft.world.item.crafting.Ingredient;

import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.component.food.FoodTraits;
import net.dries007.tfc.common.recipes.ingredients.AndIngredient;
import net.dries007.tfc.common.recipes.ingredients.HasTraitIngredient;
import net.dries007.tfc.common.recipes.ingredients.LacksTraitIngredient;
import net.dries007.tfc.common.recipes.ingredients.NotRottenIngredient;
import net.dries007.tfc.common.recipes.outputs.AddTraitModifier;
import net.dries007.tfc.common.recipes.outputs.CopyInputModifier;
import net.dries007.tfc.common.recipes.outputs.ItemStackProvider;

public interface SmokingRecipes extends Recipes
{
    default void smokingRecipes()
    {
        smoke(
            "smoke_meats",
            notRottenWithTrait(lacksTrait(Ingredient.of(TFCTags.Items.RAW_MEATS), FLFoodTraits.SMOKED), FoodTraits.BRINED),
            ItemStackProvider.of(CopyInputModifier.INSTANCE, AddTraitModifier.of(FLFoodTraits.SMOKED))
        );
        smoke(
            "smoke_cheeses",
            AndIngredient.of(Ingredient.of(FLTags.Items.CHEESES), NotRottenIngredient.INSTANCE, LacksTraitIngredient.of(FLFoodTraits.SMOKED)),
            ItemStackProvider.of(CopyInputModifier.INSTANCE, AddTraitModifier.of(FLFoodTraits.SMOKED))
        );
    }

    private void smoke(String name, Ingredient input, ItemStackProvider output)
    {
        add(name, new SmokingRecipe(input, output));
    }
}
