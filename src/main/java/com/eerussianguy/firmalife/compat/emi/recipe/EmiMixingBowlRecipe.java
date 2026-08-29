package com.eerussianguy.firmalife.compat.emi.recipe;

import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.fluids.FluidStack;

import com.eerussianguy.firmalife.common.recipes.MixingBowlRecipe;
import com.eerussianguy.firmalife.compat.emi.FLEmiPlugin;
import net.dries007.tfc.compat.emi.EmiHelpers;
import net.dries007.tfc.compat.emi.recipe.BasicRecipe;

public class EmiMixingBowlRecipe extends BasicRecipe<MixingBowlRecipe>
{
    private static final int[] INPUT_X = {15, 5, 25, 5, 25};
    private static final int[] INPUT_Y = {5, 25, 25, 45, 45};

    private final int itemInputCount;
    private final boolean hasFluidInput;
    private final boolean hasItemOutput;

    public EmiMixingBowlRecipe(ResourceLocation id, MixingBowlRecipe recipe)
    {
        super(FLEmiPlugin.MIXING_BOWL, id, 110, 100);

        int count = 0;
        for (Ingredient ingredient : recipe.getItemIngredients())
        {
            if (!ingredient.isEmpty() && count < INPUT_X.length)
            {
                inputs.add(EmiIngredient.of(ingredient));
                count++;
            }
        }
        itemInputCount = count;

        hasFluidInput = recipe.getFluidIngredient().isPresent();
        recipe.getFluidIngredient().ifPresent(fluid -> inputs.add(EmiHelpers.toIngredient(fluid)));

        final ItemStack outputItem = recipe.getResultItem(EmiHelpers.registryAccess());
        hasItemOutput = !outputItem.isEmpty();
        if (hasItemOutput)
        {
            outputs.add(EmiHelpers.nonDecayStack(outputItem));
        }

        final FluidStack outputFluid = recipe.getDisplayFluid();
        if (!outputFluid.isEmpty())
        {
            outputs.add(EmiStack.of(outputFluid.getFluid(), outputFluid.getAmount()));
        }
    }

    @Override
    public void addWidgets(WidgetHolder widgets)
    {
        for (int i = 0; i < itemInputCount; i++)
        {
            widgets.addSlot(inputs.get(i), INPUT_X[i], INPUT_Y[i]);
        }
        if (hasFluidInput)
        {
            widgets.addSlot(inputs.get(itemInputCount), 15, 65);
        }

        widgets.addFillingArrow(47, 45, 3000);

        int index = 0;
        if (hasItemOutput)
        {
            widgets.addSlot(outputs.get(index++), 85, 25).recipeContext(this);
        }
        if (index < outputs.size())
        {
            widgets.addSlot(outputs.get(index), 85, 65).recipeContext(this);
        }
    }
}
