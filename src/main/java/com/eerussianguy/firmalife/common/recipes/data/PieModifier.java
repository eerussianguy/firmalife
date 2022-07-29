package com.eerussianguy.firmalife.common.recipes.data;

import net.minecraft.world.item.ItemStack;

import com.eerussianguy.firmalife.common.items.FLFoodTraits;
import net.dries007.tfc.common.capabilities.food.FoodCapability;

public enum PieModifier implements CustomFoodModifier<PieModifier>
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
    public float water()
    {
        return 0.5f;
    }

    @Override
    public float[] nutrients(float[] input)
    {
        input[G] = 1f;
        input[D] = 0.5f;
        input[F] = 1.5f;
        return input;
    }

    @Override
    public PieModifier instance()
    {
        return INSTANCE;
    }
}
