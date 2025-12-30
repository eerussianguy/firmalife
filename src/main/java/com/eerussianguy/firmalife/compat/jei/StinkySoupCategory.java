package com.eerussianguy.firmalife.compat.jei;

import com.eerussianguy.firmalife.common.items.FLItems;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.IRecipeSlotBuilder;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.item.crafting.RecipeHolder;

import net.dries007.tfc.common.recipes.PotRecipe;
import net.dries007.tfc.compat.jei.category.PotRecipeCategory;

public class StinkySoupCategory extends PotRecipeCategory<PotRecipe>
{
    public StinkySoupCategory(RecipeType<RecipeHolder<PotRecipe>> type, IGuiHelper helper)
    {
        super(type, helper, 175, 50);
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, PotRecipe recipe, IFocusGroup focuses)
    {
        setInitialIngredients(builder, recipe);

        IRecipeSlotBuilder outputItem = builder.addSlot(RecipeIngredientRole.OUTPUT, 126, 6);
        outputItem.addItemStack(FLItems.STINKY_SOUP.get().getDefaultInstance());
        outputItem.setBackground(slot, -1, -1);
    }

    @Override
    public void draw(PotRecipe recipe, IRecipeSlotsView recipeSlots, GuiGraphics stack, double mouseX, double mouseY)
    {
        // fire
        fire.draw(stack, 27, 25);
        fireAnimated.draw(stack, 27, 25);
        // arrow
        arrow.draw(stack, 103, 26);
        arrowAnimated.draw(stack, 103, 26);
    }
}
