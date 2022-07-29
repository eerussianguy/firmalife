package com.eerussianguy.firmalife.common.recipes.data;

import net.minecraft.world.item.ItemStack;

import net.dries007.tfc.common.capabilities.food.FoodCapability;
import net.dries007.tfc.common.capabilities.food.FoodHandler;
import net.dries007.tfc.common.recipes.outputs.ItemStackModifier;

public enum CopyDynamicFoodModifier implements ItemStackModifier.SingleInstance<CopyDynamicFoodModifier>
{
    INSTANCE;

    @Override
    public ItemStack apply(ItemStack stack, ItemStack input)
    {
        stack.getCapability(FoodCapability.CAPABILITY).ifPresent(cap -> {
            if (cap instanceof FoodHandler.Dynamic outHandler)
            {
                input.getCapability(FoodCapability.CAPABILITY).ifPresent(inputCap -> {
                    if (inputCap instanceof FoodHandler.Dynamic inHandler)
                    {
                        outHandler.setFood(inHandler.getData());
                        outHandler.setCreationDate(FoodCapability.getRoundedCreationDate());
                    }
                });
            }
        });
        return stack;
    }

    @Override
    public CopyDynamicFoodModifier instance()
    {
        return INSTANCE;
    }
}
