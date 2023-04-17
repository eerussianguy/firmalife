package com.eerussianguy.firmalife.common.blocks;

import java.util.Locale;

public enum OvenType
{
    BRICK,
    RUSTIC,
    TILE,
    STONE
    ;

    private final String trueName;
    private final String serializedName;

    OvenType()
    {
        serializedName = name().toLowerCase(Locale.ROOT) + "_";
        trueName = name().toLowerCase(Locale.ROOT);
    }

    public String getName()
    {
        return this == BRICK ? "" : serializedName;
    }

    public String getTrueName()
    {
        return trueName;
    }
}
