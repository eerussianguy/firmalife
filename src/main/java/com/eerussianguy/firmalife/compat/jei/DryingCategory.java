package com.eerussianguy.firmalife.compat.jei;

import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import com.eerussianguy.firmalife.common.blocks.FLBlocks;
import com.eerussianguy.firmalife.common.recipes.DryingRecipe;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.RecipeType;
import net.dries007.tfc.compat.jei.category.SimpleItemRecipeCategory;

import net.minecraft.world.item.crafting.RecipeHolder;
import org.jetbrains.annotations.Nullable;

public class DryingCategory extends SimpleItemRecipeCategory<DryingRecipe>
{
    public DryingCategory(RecipeType<RecipeHolder<DryingRecipe>> type, IGuiHelper helper)
    {
        super(type, helper, new ItemStack(FLBlocks.DRYING_MAT.get()));
    }

    @Override
    @Nullable
    protected TagKey<Item> getToolTag()
    {
        return null;
    }
}
