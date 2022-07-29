package com.eerussianguy.firmalife.common.recipes.data;

import net.minecraft.world.item.ItemStack;

import com.eerussianguy.firmalife.common.items.FLFoodTraits;
import net.dries007.tfc.common.capabilities.food.FoodCapability;

public enum PizzaModifier implements CustomFoodModifier<PizzaModifier>
{
    INSTANCE;

    @Override
    public ItemStack apply(ItemStack stack, ItemStack input)
    {
        ItemStack ret = CustomFoodModifier.super.apply(stack, input);
        FoodCapability.applyTrait(ret, FLFoodTraits.RAW);
        return ret;
    }

    @Override
    public float saturation()
    {
        return 1f;
    }

    @Override
    public float[] nutrients(float[] input)
    {
        input[G] = 1f;
        return input;
    }

    @Override
    public PizzaModifier instance()
    {
        return INSTANCE;
    }
}
