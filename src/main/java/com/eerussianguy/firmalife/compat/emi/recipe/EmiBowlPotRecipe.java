package com.eerussianguy.firmalife.compat.emi.recipe;

import java.util.Objects;
import dev.emi.emi.api.stack.EmiIngredient;
import net.minecraft.resources.ResourceLocation;

import com.eerussianguy.firmalife.common.recipes.BowlPotRecipe;
import net.dries007.tfc.compat.emi.EmiHelpers;
import net.dries007.tfc.compat.emi.recipe.EmiBasePotRecipe;

public class EmiBowlPotRecipe extends EmiBasePotRecipe<BowlPotRecipe>
{
    public EmiBowlPotRecipe(ResourceLocation id, BowlPotRecipe recipe)
    {
        super(id, recipe, 113, 80);
        inputs.addAll(groupSimilar(recipe.getItemIngredients(), EmiIngredient::of, Objects::equals));
        outputs.add(EmiHelpers.nonDecayStack(recipe.getResultItem(EmiHelpers.registryAccess())));
    }
}
