package com.eerussianguy.firmalife.recipes;

import com.eerussianguy.firmalife.common.FLTags;
import com.eerussianguy.firmalife.common.items.FLFoodTraits;
import com.eerussianguy.firmalife.common.recipes.SmokingRecipe;
import net.minecraft.world.item.crafting.Ingredient;

import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.component.food.FoodTraits;
import net.dries007.tfc.common.recipes.outputs.AddTraitModifier;
import net.dries007.tfc.common.recipes.outputs.CopyInputModifier;
import net.dries007.tfc.common.recipes.outputs.ItemStackProvider;

public interface SmokingRecipes extends Recipes
{
    default void smokingRecipes()
    {
        smoke(
            notRottenWithTrait(lacksTrait(Ingredient.of(TFCTags.Items.RAW_MEATS), FLFoodTraits.SMOKED), FoodTraits.BRINED),
            ItemStackProvider.of(CopyInputModifier.INSTANCE, AddTraitModifier.of(FLFoodTraits.SMOKED))
        );
        smoke(
            notRottenWithTrait(Ingredient.of(FLTags.Items.CHEESES), FLFoodTraits.SMOKED),
            ItemStackProvider.of(CopyInputModifier.INSTANCE, AddTraitModifier.of(FLFoodTraits.SMOKED))
        );
    }

    private void smoke(Ingredient input, ItemStackProvider output)
    {
        add(new SmokingRecipe(input, output));
    }
}
