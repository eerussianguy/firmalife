package com.eerussianguy.firmalife.common.util;

import java.util.Locale;

import net.minecraft.util.StringRepresentable;

public enum ExtraFluid implements StringRepresentable
{
    YEAST_STARTER(0xFFa79464),
    COCONUT_MILK(0xFFfcfae2),
    YAK_MILK(0xFFfcfaec),
    GOAT_MILK(0xFFf6f6eb),
    CURDLED_YAK_MILK(0xFFf9f4d6),
    CURDLED_GOAT_MILK(0xFFeeeed9),
    CREAM(0xFFFCF6C7),
    PINA_COLADA(0xFFE4C06A),
    CHOCOLATE(0xFF875633),
    SUGAR_WATER(0xFF99EEFF),
    FRUITY_FLUID(0xFFFF6619),
    MEAD(0xc79f28);

    private final String id;
    private final int color;

    ExtraFluid(int color)
    {
        this.id = name().toLowerCase(Locale.ROOT);
        this.color = color;
    }

    @Override
    public String getSerializedName()
    {
        return id;
    }

    public int getColor()
    {
        return color;
    }
}
