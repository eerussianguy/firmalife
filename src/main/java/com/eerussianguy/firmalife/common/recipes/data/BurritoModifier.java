package com.eerussianguy.firmalife.common.recipes.data;

public enum BurritoModifier implements CustomFoodModifier<BurritoModifier>
{
    INSTANCE;

    @Override
    public BurritoModifier instance()
    {
        return INSTANCE;
    }

    @Override
    public float saturation()
    {
        return 4f;
    }

    @Override
    public float[] nutrients(float[] input)
    {
        input[G] = 1f;
        return input;
    }
}
