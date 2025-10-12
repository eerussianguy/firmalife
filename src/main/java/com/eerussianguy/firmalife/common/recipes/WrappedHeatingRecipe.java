package com.eerussianguy.firmalife.common.recipes;

import java.util.function.Function;

import net.minecraft.world.item.ItemStack;

import net.dries007.tfc.common.recipes.HeatingRecipe;
import org.jetbrains.annotations.Nullable;

public record WrappedHeatingRecipe(Function<ItemStack, ItemStack> output, float temperature, int duration, boolean oven)
{
    @Nullable
    public static WrappedHeatingRecipe getRecipe(ItemStack stack)
    {
        OvenRecipe recipe = OvenRecipe.getRecipe(stack);
        if (recipe != null)
        {
            return of(recipe);
        }
        HeatingRecipe heatRecipe = HeatingRecipe.getRecipe(stack);
        return heatRecipe == null ? null : of(heatRecipe);
    }

    public static WrappedHeatingRecipe of(HeatingRecipe recipe)
    {
        return new WrappedHeatingRecipe(recipe::assembleItem, recipe.getTemperature(), 20 * 50, false);
    }

    public static WrappedHeatingRecipe of(OvenRecipe recipe)
    {
        return new WrappedHeatingRecipe(recipe::assembleItem, recipe.getTemperature(), recipe.getDuration(), true);
    }

    public boolean isValidTemperature(float temperature)
    {
        return temperature >= this.temperature;
    }

    public ItemStack assemble(ItemStack input)
    {
        return output.apply(input);
    }
}
