package com.eerussianguy.firmalife.common.blocks;

import java.util.Locale;

public enum OvenType
{
    BRICK,
    RUSTIC,
    TILE,
    STONE
    ;

    private final String serializedName;

    OvenType()
    {
        serializedName = name().toLowerCase(Locale.ROOT);
    }

    public String getSerializedName()
    {
        return serializedName;
    }
}
