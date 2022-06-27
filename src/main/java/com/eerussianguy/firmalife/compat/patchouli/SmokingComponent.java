package com.eerussianguy.firmalife.compat.patchouli;

import net.minecraft.world.item.crafting.RecipeType;

import com.eerussianguy.firmalife.common.recipes.FLRecipeTypes;
import com.eerussianguy.firmalife.common.recipes.SmokingRecipe;
import net.dries007.tfc.compat.patchouli.component.SimpleItemRecipeComponent;

public class SmokingComponent extends SimpleItemRecipeComponent<SmokingRecipe>
{
    @Override
    protected RecipeType<SmokingRecipe> getRecipeType()
    {
        return FLRecipeTypes.SMOKING.get();
    }
}
