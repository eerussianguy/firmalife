package com.eerussianguy.firmalife.compat.jei;

import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import com.eerussianguy.firmalife.common.blocks.FLBlocks;
import com.eerussianguy.firmalife.common.recipes.SmokingRecipe;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.RecipeType;
import net.dries007.tfc.common.items.TFCItems;
import net.dries007.tfc.compat.jei.category.SimpleItemRecipeCategory;
import org.jetbrains.annotations.Nullable;

public class SmokingCategory extends SimpleItemRecipeCategory<SmokingRecipe>
{
    public SmokingCategory(RecipeType<SmokingRecipe> type, IGuiHelper helper)
    {
        super(type, helper, new ItemStack(TFCItems.WOOL_YARN.get()));
    }

    @Override
    @Nullable
    protected TagKey<Item> getToolTag()
    {
        return null;
    }
}
