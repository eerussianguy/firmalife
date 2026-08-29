package com.eerussianguy.firmalife.compat.emi.recipe;

import java.util.Objects;
import dev.emi.emi.api.stack.EmiIngredient;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Ingredient;

import com.eerussianguy.firmalife.common.items.FLItems;
import com.eerussianguy.firmalife.common.recipes.StinkySoupRecipe;
import net.dries007.tfc.compat.emi.EmiHelpers;
import net.dries007.tfc.compat.emi.recipe.EmiBasePotRecipe;

public class EmiStinkySoupRecipe extends EmiBasePotRecipe<StinkySoupRecipe>
{
    public EmiStinkySoupRecipe(ResourceLocation id, StinkySoupRecipe recipe)
    {
        super(id, recipe, 113, 80);

        int ingredientCount = 0;
        for (Ingredient ingredient : recipe.getItemIngredients())
        {
            if (!ingredient.isEmpty())
            {
                ingredientCount++;
            }
        }
        inputs.addAll(groupSimilar(recipe.getItemIngredients(), EmiIngredient::of, Objects::equals));

        // Matches the serving count calculated in StinkySoupRecipe#getOutput
        final int servings = (int) (ingredientCount / 2f) + 1;
        outputs.add(EmiHelpers.nonDecayStack(FLItems.STINKY_SOUP.get().getDefaultInstance().copyWithCount(servings)));
    }
}
