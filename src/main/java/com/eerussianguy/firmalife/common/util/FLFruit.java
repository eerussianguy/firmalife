package com.eerussianguy.firmalife.common.util;

import java.util.Locale;
import net.minecraft.world.food.FoodProperties;

public enum FLFruit
{
    FIG,
    PINEAPPLE,
    RED_GRAPES,
    WHITE_GRAPES
    ;

    private final String name;
    private final boolean fast;

    FLFruit()
    {
        this(false);
    }

    FLFruit(boolean fast)
    {
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
        if (fast) builder.fast();
        return builder.nutrition(4).saturationModifier(0.3f).build();
    }
}
