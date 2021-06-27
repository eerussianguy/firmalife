package com.eerussianguy.firmalife.recipe;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.item.ItemStack;
import net.minecraftforge.registries.IForgeRegistryEntry;

import com.eerussianguy.firmalife.init.RegistriesFL;
import net.dries007.tfc.api.capability.food.CapabilityFood;
import net.dries007.tfc.objects.inventory.ingredient.IIngredient;

public class PlanterRecipe extends IForgeRegistryEntry.Impl<PlanterRecipe>
{
    protected IIngredient<ItemStack> inputItem;
    protected ItemStack outputItem;
    private final int stages;
    private final boolean large;
    private final int tier;

    @Nullable
    public static PlanterRecipe get(ItemStack item)
    {
        return RegistriesFL.PLANTER_QUAD.getValuesCollection().stream().filter(x -> x.isValidInput(item)).findFirst().orElse(null);
    }

    public static int getMaxStage(PlanterRecipe recipe)
    {
        return recipe.stages;
    }

    public static int getTier(PlanterRecipe recipe)
    {
        return recipe.tier;
    }

    public PlanterRecipe(IIngredient<ItemStack> input, ItemStack output, int stages, boolean large)
    {
        this(input, output, stages, large, 0);
    }

    public PlanterRecipe(IIngredient<ItemStack> input, ItemStack output, int stages, boolean large, int tier)
    {
        this.inputItem = input;
        this.outputItem = output;
        this.stages = stages;
        this.large = large;
        this.tier = tier;

        if (inputItem == null || outputItem == null)
        {
            throw new IllegalArgumentException("Sorry, but the planter needs inputs and outputs.");
        }
        if (stages < 1)
        {
            throw new IllegalArgumentException("Sorry, but crops need have to have stages.");
        }
    }

    public boolean isLarge()
    {
        return large;
    }

    // Using this for HWYLA access
    @Nonnull
    public ItemStack getOutputItem() {
        return outputItem;
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

    @Nullable
    public static class PlantInfo
    {
        private final PlanterRecipe recipe;
        private final int stage;

        public PlantInfo(PlanterRecipe recipe, int stage)
        {
            this.recipe = recipe;
            this.stage = stage;
        }

        public PlanterRecipe getRecipe()
        {
            return recipe;
        }

        public int getStage()
        {
            return stage;
        }
    }
}
