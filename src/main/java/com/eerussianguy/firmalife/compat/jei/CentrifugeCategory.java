package com.eerussianguy.firmalife.compat.jei;

import com.eerussianguy.firmalife.common.blocks.FLBlocks;
import com.eerussianguy.firmalife.common.recipes.CentrifugeRecipe;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.RecipeType;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.compat.jei.category.SimpleItemRecipeCategory;

public class CentrifugeCategory extends SimpleItemRecipeCategory<CentrifugeRecipe>
{
    public CentrifugeCategory(RecipeType<RecipeHolder<CentrifugeRecipe>> type, IGuiHelper helper)
    {
        super(type, helper, new ItemStack(FLBlocks.CENTRIFUGE.get()));
    }

    @Override
    @Nullable
    protected TagKey<Item> getToolTag()
    {
        return null;
    }
}
