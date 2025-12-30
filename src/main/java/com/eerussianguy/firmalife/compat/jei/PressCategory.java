package com.eerussianguy.firmalife.compat.jei;

import com.eerussianguy.firmalife.common.blocks.FLBlocks;
import com.eerussianguy.firmalife.common.recipes.PressRecipe;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.RecipeType;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.common.blocks.wood.Wood;
import net.dries007.tfc.compat.jei.category.SimpleItemRecipeCategory;

public class PressCategory extends SimpleItemRecipeCategory<PressRecipe>
{
    public PressCategory(RecipeType<RecipeHolder<PressRecipe>> type, IGuiHelper helper)
    {
        super(type, helper, new ItemStack(FLBlocks.BARREL_PRESSES.get(Wood.MANGROVE).get()));
    }

    @Override
    protected @Nullable TagKey<Item> getToolTag()
    {
        return null;
    }
}
