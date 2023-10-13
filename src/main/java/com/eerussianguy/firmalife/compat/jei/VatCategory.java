package com.eerussianguy.firmalife.compat.jei;

import java.util.ArrayList;
import java.util.List;
import com.eerussianguy.firmalife.common.blocks.FLBlocks;
import com.eerussianguy.firmalife.common.recipes.VatRecipe;
import com.mojang.blaze3d.vertex.PoseStack;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.IRecipeSlotBuilder;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.compat.jei.JEIIntegration;
import net.dries007.tfc.compat.jei.category.BaseRecipeCategory;

public class VatCategory extends BaseRecipeCategory<VatRecipe>
{
    protected @Nullable IRecipeSlotBuilder inputFluidSlot;
    protected @Nullable IRecipeSlotBuilder inputItemSlot;
    protected @Nullable IRecipeSlotBuilder outputFluidSlot;
    protected @Nullable IRecipeSlotBuilder outputItemSlot;

    public VatCategory(RecipeType<VatRecipe> type, IGuiHelper helper)
    {
        super(type, helper, helper.createBlankDrawable(118, 26), new ItemStack(FLBlocks.VAT.get()));
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, VatRecipe recipe, IFocusGroup focuses)
    {
        inputFluidSlot = null;
        inputItemSlot = null;
        outputFluidSlot = null;
        outputItemSlot = null;

        final int[] positions = slotPositions(recipe);
        final List<FluidStack> inputFluid = collapse(recipe.getInputFluid());
        final List<ItemStack> inputItem = collapse(recipe.getInputItem());
        final FluidStack outputFluid = recipe.getOutputFluid();
        final List<ItemStack> outputItem = new ArrayList<>(collapse(inputItem, recipe.getOutputItem()));

        if (!recipe.getJarOutput().isEmpty())
        {
            outputItem.add(recipe.getJarOutput());
        }

        if (!inputFluid.isEmpty())
        {
            inputFluidSlot = builder.addSlot(RecipeIngredientRole.INPUT, inputItem.isEmpty() ? positions[1] : positions[0], 5);
            inputFluidSlot.addIngredients(JEIIntegration.FLUID_STACK, inputFluid);
            inputFluidSlot.setFluidRenderer(1, false, 16, 16);
            inputFluidSlot.setBackground(slot, -1, -1);
        }

        if (!inputItem.isEmpty())
        {
            inputItemSlot = builder.addSlot(RecipeIngredientRole.INPUT, positions[1], 5);
            inputItemSlot.addItemStacks(inputItem);
            inputItemSlot.setBackground(slot, -1, -1);
        }

        if (!outputFluid.isEmpty())
        {
            outputFluidSlot = builder.addSlot(RecipeIngredientRole.OUTPUT, positions[2], 5);
            outputFluidSlot.addIngredient(JEIIntegration.FLUID_STACK, outputFluid);
            outputFluidSlot.setFluidRenderer(1, false, 16, 16);
            outputFluidSlot.setBackground(slot, -1, -1);
        }

        if (!outputItem.isEmpty() && !outputItem.stream().allMatch(ItemStack::isEmpty))
        {
            outputItemSlot = builder.addSlot(RecipeIngredientRole.OUTPUT, outputFluid.isEmpty() ? positions[2] : positions[3], 5);
            outputItemSlot.addItemStacks(outputItem);
            outputItemSlot.setBackground(slot, -1, -1);
        }

        // Link inputs and outputs when focused
        // Only dependent if the item stack provider output internally depends on the input
        if (recipe.getOutputItem().dependsOnInput() && inputItemSlot != null && outputItemSlot != null)
        {
            builder.createFocusLink(inputItemSlot, outputItemSlot);
        }
    }

    @Override
    public void draw(VatRecipe recipe, IRecipeSlotsView recipeSlots, GuiGraphics stack, double mouseX, double mouseY)
    {
        final int arrowPosition = arrowPosition(recipe);
        arrow.draw(stack, arrowPosition, 5);
        arrowAnimated.draw(stack, arrowPosition, 5);
    }

    protected int[] slotPositions(VatRecipe recipe)
    {
        return new int[] {6, 26, 76, 96};
    }

    protected int arrowPosition(VatRecipe recipe)
    {
        return 48;
    }
}
