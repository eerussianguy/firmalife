package com.eerussianguy.firmalife.recipe;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.item.ItemStack;
import net.minecraftforge.registries.IForgeRegistryEntry;

import com.eerussianguy.firmalife.init.RegistriesFL;
import net.dries007.tfc.api.capability.food.CapabilityFood;
import net.dries007.tfc.objects.inventory.ingredient.IIngredient;

public class CrackingRecipe extends IForgeRegistryEntry.Impl<CrackingRecipe>
{
    protected IIngredient<ItemStack> inputItem;
    protected ItemStack outputItem;
    protected float chance;

    @Nullable
    public static CrackingRecipe get(ItemStack item)
    {
        return RegistriesFL.CRACKING.getValuesCollection().stream().filter(x -> x.isValidInput(item)).findFirst().orElse(null);
    }

    public CrackingRecipe(IIngredient<ItemStack> inputItem, ItemStack outputItem, float chance)
    {
        this.inputItem = inputItem;
        this.outputItem = outputItem;
        this.chance = chance;

        if (inputItem == null || outputItem == null)
            throw new IllegalArgumentException("Sorry, but you can't have cracking recipes that don't have an input and output");
        if (chance < 0 || chance > 1)
            throw new IllegalArgumentException("Sorry, the chance to drop must be between 0 and 1");
    }

    @Nonnull
    public int getChance()
    {
        return (int) (chance * 100);
    }

    public ItemStack getOutputItem(ItemStack stack) { return CapabilityFood.updateFoodFromPrevious(stack, outputItem.copy()); }

    private boolean isValidInput(ItemStack inputItem) { return this.inputItem.test(inputItem); }
}


