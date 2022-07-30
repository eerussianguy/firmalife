package com.eerussianguy.firmalife.common.util;

import net.minecraft.world.food.FoodProperties;

public enum FLFruit
{
    PUMPKIN_CHUNKS;

    private final boolean meat, fast;

    FLFruit()
    {
        this(false, false);
    }

    FLFruit(boolean meat, boolean fast)
    {
        this.meat = meat;
        this.fast = fast;
    }

    public FoodProperties getFoodProperties()
    {
        FoodProperties.Builder builder = new FoodProperties.Builder();
        if (meat) builder.meat();
        if (fast) builder.fast();
        return builder.nutrition(4).saturationMod(0.3f).build();
    }
}
