package com.eerussianguy.firmalife.common.util;

import java.util.Locale;

import net.minecraft.util.StringRepresentable;

public enum Carving implements StringRepresentable
{
    NONE,
    CIRCLE,
    CREEPER,
    AXE,
    HAMMER,
    PICKAXE,
    LEFT,
    RIGHT;

    private final String name;

    Carving()
    {
        this.name = name().toLowerCase(Locale.ROOT);
    }

    @Override
    public String getSerializedName()
    {
        return name;
    }
}
