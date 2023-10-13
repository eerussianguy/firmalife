package com.eerussianguy.firmalife.common.recipes;

import java.util.function.BiFunction;
import java.util.function.Function;

import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import net.dries007.tfc.common.recipes.HeatingRecipe;
import net.dries007.tfc.common.recipes.inventory.ItemStackInventory;
import org.jetbrains.annotations.Nullable;

public record WrappedHeatingRecipe(ResourceLocation id, BiFunction<ItemStackInventory, RegistryAccess, ItemStack> output, float temperature, int duration, boolean oven)
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
        return new WrappedHeatingRecipe(recipe.getId(), recipe::assemble, recipe.getTemperature(), 120, false);
    }

    public static WrappedHeatingRecipe of(OvenRecipe recipe)
    {
        return new WrappedHeatingRecipe(recipe.getId(), recipe::assemble, recipe.getTemperature(), recipe.getDuration(), true);
    }

    public boolean isValidTemperature(float temperature)
    {
        return temperature >= this.temperature;
    }

    public ItemStack assemble(ItemStackInventory inventory, RegistryAccess access)
    {
        return output.apply(inventory, access);
    }
}
