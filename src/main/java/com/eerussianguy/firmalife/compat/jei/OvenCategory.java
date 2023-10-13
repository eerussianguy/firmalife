package com.eerussianguy.firmalife.compat.jei;

import java.util.List;

import com.eerussianguy.firmalife.common.blocks.OvenType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.ItemStack;

import com.eerussianguy.firmalife.common.blocks.FLBlocks;
import com.eerussianguy.firmalife.common.recipes.OvenRecipe;
import com.mojang.blaze3d.vertex.PoseStack;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.IRecipeSlotBuilder;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import net.dries007.tfc.compat.jei.JEIIntegration;
import net.dries007.tfc.compat.jei.category.BaseRecipeCategory;
import net.dries007.tfc.config.TFCConfig;

public class OvenCategory extends BaseRecipeCategory<OvenRecipe>
{
    public OvenCategory(RecipeType<OvenRecipe> type, IGuiHelper helper)
    {
        super(type, helper, helper.createBlankDrawable(120, 38), new ItemStack(FLBlocks.CURED_OVEN_TOP.get(OvenType.BRICK).get()));
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, OvenRecipe recipe, IFocusGroup focuses)
    {
        IRecipeSlotBuilder inputSlot = builder.addSlot(RecipeIngredientRole.INPUT, 6, 17);
        IRecipeSlotBuilder outputSlot = builder.addSlot(RecipeIngredientRole.OUTPUT, 76, 17);

        final List<ItemStack> inputList = List.of(recipe.getIngredient().getItems());
        inputSlot.addIngredients(JEIIntegration.ITEM_STACK, inputList);
        inputSlot.setBackground(slot, -1,-1);

        outputSlot.addItemStacks(collapse(inputList, recipe.getResult()));
        outputSlot.setBackground(slot, -1, -1);

        builder.createFocusLink(inputSlot, outputSlot);
    }

    @Override
    public void draw(OvenRecipe recipe, IRecipeSlotsView recipeSlots, GuiGraphics stack, double mouseX, double mouseY)
    {
        fire.draw(stack, 48, 16);
        fireAnimated.draw(stack, 48, 16);

        MutableComponent color = TFCConfig.CLIENT.heatTooltipStyle.get().formatColored(recipe.getTemperature());
        if (color != null)
        {
            final Minecraft mc = Minecraft.getInstance();
            final Font font = mc.font;
            stack.drawString(font, color, 60 - font.width(color) / 2, 4, 0xFFFFFF, false);
        }
    }
}
