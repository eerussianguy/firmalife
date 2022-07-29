package com.eerussianguy.firmalife.common.recipes.data;

import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;

import net.dries007.tfc.common.capabilities.food.FoodCapability;
import net.dries007.tfc.common.capabilities.food.FoodHandler;
import net.dries007.tfc.common.capabilities.food.Nutrient;
import net.dries007.tfc.common.recipes.RecipeHelpers;
import net.dries007.tfc.common.recipes.outputs.ItemStackModifier;

public interface ContainerAwareModifier<T extends ItemStackModifier> extends ItemStackModifier.SingleInstance<T>
{
    @Override
    default ItemStack apply(ItemStack stack, ItemStack input)
    {
        CraftingContainer inv = RecipeHelpers.getCraftingContainer();
        if (inv != null)
        {
            stack.getCapability(FoodCapability.CAPABILITY).ifPresent(food -> {
                if (food instanceof FoodHandler.Dynamic dynamic)
                {
                    initFoodStats(inv, dynamic);
                }
            });
            return stack;
        }
        return stack;
    }

    default float[] freshNutrients()
    {
        return new float[Nutrient.TOTAL];
    }

    void initFoodStats(CraftingContainer inv, FoodHandler.Dynamic dynamic);
}
