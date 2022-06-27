package com.eerussianguy.firmalife.compat.patchouli;

import net.minecraft.world.item.crafting.RecipeType;

import com.eerussianguy.firmalife.common.recipes.DryingRecipe;
import com.eerussianguy.firmalife.common.recipes.FLRecipeTypes;
import net.dries007.tfc.compat.patchouli.component.SimpleItemRecipeComponent;

public class DryingComponent extends SimpleItemRecipeComponent<DryingRecipe>
{
    @Override
    protected RecipeType<DryingRecipe> getRecipeType()
    {
        return FLRecipeTypes.DRYING.get();
    }
}
