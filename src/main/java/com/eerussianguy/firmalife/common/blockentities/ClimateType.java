package com.eerussianguy.firmalife.common.blockentities;

public enum ClimateType
{
    GREENHOUSE,
    CELLAR,
    ;

    public static final ClimateType[] VALUES = values();

    public static ClimateType byId(int id)
    {
        return id >= VALUES.length ? GREENHOUSE : VALUES[id];
    }
}
