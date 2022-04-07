package com.eerussianguy.firmalife.compat.jei;

import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import com.eerussianguy.firmalife.common.FLTags;
import com.eerussianguy.firmalife.common.blocks.FLBlocks;
import com.eerussianguy.firmalife.common.recipes.DryingRecipe;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.RecipeType;
import net.dries007.tfc.compat.jei.category.SimpleItemRecipeCategory;

public class DryingCategory extends SimpleItemRecipeCategory<DryingRecipe>
{
    public DryingCategory(RecipeType<DryingRecipe> type, IGuiHelper helper)
    {
        super(type, helper, new ItemStack(FLBlocks.DRYING_MAT.get()));
    }

    @Override
    protected TagKey<Item> getToolTag()
    {
        return FLTags.Items.USABLE_ON_OVEN; // todo: make this null
    }
}
