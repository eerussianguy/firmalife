package com.eerussianguy.firmalife.recipe;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.IForgeRegistryEntry;

import com.eerussianguy.firmalife.init.RegistriesFL;
import net.dries007.tfc.Constants;
import net.dries007.tfc.objects.inventory.ingredient.IIngredient;

// For now this is unnecessary, but it sets the groundwork for harvesting Whey + other straining recipes
public class StrainingRecipe extends IForgeRegistryEntry.Impl<StrainingRecipe>
{
    protected IIngredient<FluidStack> inputFluid;
    protected ItemStack outputItem;
    protected FluidStack outputFluid;

    @Nullable
    public static StrainingRecipe get(FluidStack fluidStack)
    {
        return RegistriesFL.STRAINING.getValuesCollection().stream().filter(x -> x.isValidInput(fluidStack)).findFirst().orElse(null);
    }

    public StrainingRecipe(IIngredient<FluidStack> inputFluid, ItemStack outputItem, FluidStack outputFluid)
    {
        this.inputFluid = inputFluid;
        this.outputItem = outputItem;
        this.outputFluid = outputFluid;

        if (inputFluid == null || (outputItem == null && outputFluid == null))
            throw new IllegalArgumentException("Sorry, but you can't have straining recipes that don't have an input and output");

    }

    public int getDropAmount()
    {
        return Constants.RNG.nextInt(3) + 1;
    }

    public ItemStack getOutputItem()
    {
        if (outputItem != null)
            return new ItemStack(outputItem.getItem(), getDropAmount());
        return null;
    }

    @Nonnull
    public FluidStack getOutputFluid()
    {
        return outputFluid;
    }

    private boolean isValidInput(FluidStack inputFluid) { return this.inputFluid.test(inputFluid); }
}


