package com.eerussianguy.firmalife.init;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.item.ItemStack;

import net.minecraftforge.registries.IForgeRegistryEntry;

import net.dries007.tfc.api.capability.food.CapabilityFood;
import net.dries007.tfc.objects.inventory.ingredient.IIngredient;

public class PlanterRegistry extends IForgeRegistryEntry.Impl<PlanterRegistry>
{
    protected IIngredient<ItemStack> inputItem;
    protected ItemStack outputItem;
    private final int stages;

    @Nullable
    public static PlanterRegistry get(ItemStack item)
    {
        return RegistriesFL.PLANTER_QUAD.getValuesCollection().stream().filter(x -> x.isValidInput(item)).findFirst().orElse(null);
    }

    public static int getMaxStage(PlanterRegistry recipe)
    {
        return recipe.stages;
    }

    public PlanterRegistry(IIngredient<ItemStack> input, ItemStack output, int stages)
    {
        this.inputItem = input;
        this.outputItem = output;
        this.stages = stages;

        if (inputItem == null || outputItem == null)
        {
            throw new IllegalArgumentException("Sorry, but the planter needs inputs and outputs.");
        }
        if (stages < 1)
        {
            throw new IllegalArgumentException("Sorry, but crops need have to have stages.");
        }
    }

    @Nonnull
    public ItemStack getOutputItem(ItemStack stack)
    {
        return CapabilityFood.updateFoodFromPrevious(stack, outputItem.copy());
    }

    private boolean isValidInput(ItemStack inputItem)
    {
        return this.inputItem.test(inputItem);
    }
}
