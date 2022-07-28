package com.eerussianguy.firmalife.compat.jei;

import java.util.List;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

import net.minecraftforge.fluids.FluidStack;

import com.eerussianguy.firmalife.common.blocks.FLBlocks;
import com.eerussianguy.firmalife.common.recipes.MixingBowlRecipe;
import com.mojang.blaze3d.vertex.PoseStack;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.IRecipeSlotBuilder;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import net.dries007.tfc.compat.jei.category.BaseRecipeCategory;

public class MixingCategory extends BaseRecipeCategory<MixingBowlRecipe>
{
    private static final int[] INPUT_X = {15, 5, 25, 5, 25};
    private static final int[] INPUT_Y = {5, 25, 25, 45, 45};

    public MixingCategory(RecipeType<MixingBowlRecipe> type, IGuiHelper helper)
    {
        super(type, helper, helper.createBlankDrawable(110, 100), new ItemStack(FLBlocks.MIXING_BOWL.get()));
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, MixingBowlRecipe recipe, IFocusGroup focuses)
    {
        int i = 0;
        for (Ingredient ingredient : recipe.getItemIngredients())
        {
            if (!ingredient.isEmpty())
            {
                IRecipeSlotBuilder input = builder.addSlot(RecipeIngredientRole.INPUT, INPUT_X[i] + 1, INPUT_Y[i] + 1);
                input.addIngredients(ingredient);
                i++;
            }
        }

        final List<FluidStack> inputFluids = collapse(recipe.getFluidIngredient());
        if (!inputFluids.isEmpty())
        {
            IRecipeSlotBuilder fluidOutput = builder.addSlot(RecipeIngredientRole.INPUT, 16, 66);
            fluidOutput.addIngredients(VanillaTypes.FLUID, inputFluids);
            fluidOutput.setFluidRenderer(1, false, 16, 16);
        }

        if (!recipe.getResultItem().isEmpty())
        {
            IRecipeSlotBuilder output = builder.addSlot(RecipeIngredientRole.OUTPUT, 86, 26);
            output.addItemStack(recipe.getResultItem());
        }

        final FluidStack outputFluid = recipe.getDisplayFluid();
        if (!outputFluid.isEmpty())
        {
            IRecipeSlotBuilder fluidOutput = builder.addSlot(RecipeIngredientRole.OUTPUT, 16, 66);
            fluidOutput.addIngredient(VanillaTypes.FLUID, outputFluid);
            fluidOutput.setFluidRenderer(1, false, 16, 16);
        }
    }

    @Override
    public void draw(MixingBowlRecipe recipe, IRecipeSlotsView recipeSlots, PoseStack stack, double mouseX, double mouseY)
    {
        // Item Input
        drawFive(stack, INPUT_X, INPUT_Y);
        // Water Input
        slot.draw(stack, 15, 65);
        // fire
        fire.draw(stack, 47, 45);
        fireAnimated.draw(stack, 47, 45);
        // Item Output
        slot.draw(stack, 85, 25);
        // Water Output
        slot.draw(stack, 75, 65);
    }

    private void drawFive(PoseStack stack, int[] x, int[] y)
    {
        slot.draw(stack, x[0], y[0]);
        slot.draw(stack, x[1], y[1]);
        slot.draw(stack, x[2], y[2]);
        slot.draw(stack, x[3], y[3]);
        slot.draw(stack, x[4], y[4]);
    }
}
