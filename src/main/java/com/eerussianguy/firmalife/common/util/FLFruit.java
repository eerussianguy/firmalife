package com.eerussianguy.firmalife.common.util;

import java.util.Locale;
import net.minecraft.world.food.FoodProperties;

public enum FLFruit
{
    FIG,
    PINEAPPLE;

    private final String name;
    private final boolean meat, fast;

    FLFruit()
    {
        this(false, false);
    }

    FLFruit(boolean meat, boolean fast)
    {
        this.meat = meat;
        this.fast = fast;
        this.name = name().toLowerCase(Locale.ROOT);
    }

    public String getSerializedName()
    {
        return name;
    }

    public FoodProperties getFoodProperties()
    {
        FoodProperties.Builder builder = new FoodProperties.Builder();
        if (meat) builder.meat();
        if (fast) builder.fast();
        return builder.nutrition(4).saturationMod(0.3f).build();
    }
}
