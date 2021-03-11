package com.eerussianguy.firmalife.compat.jei.wrapper;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;

import com.eerussianguy.firmalife.recipe.OvenRecipe;
import net.dries007.tfc.compat.jei.wrappers.SimpleRecipeWrapper;
import net.dries007.tfc.util.calendar.ICalendar;

public class OvenRecipeWrapper extends SimpleRecipeWrapper
{
    private final OvenRecipe recipe;

    public OvenRecipeWrapper(OvenRecipe recipeWrapper)
    {
        super(recipeWrapper);
        this.recipe = recipeWrapper;
    }

    @Override
    public void drawInfo(Minecraft minecraft, int recipeWidth, int recipeHeight, int mouseX, int mouseY)
    {
        float x = 60f;
        float y = 4f;
        String text = OvenRecipe.getDuration(recipe) / ICalendar.TICKS_IN_HOUR + " " + I18n.format("tooltip.firmalife.hours");
        x = x - minecraft.fontRenderer.getStringWidth(text) / 2.0f;
        minecraft.fontRenderer.drawString(text, x, y, 0xFFFFFF, false);
    }
}
