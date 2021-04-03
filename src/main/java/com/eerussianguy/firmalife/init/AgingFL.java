package com.eerussianguy.firmalife.init;

import net.minecraft.util.IStringSerializable;

import mcp.MethodsReturnNonnullByDefault;

@MethodsReturnNonnullByDefault
public enum AgingFL implements IStringSerializable
{
    // TODO: Aged foods should probably do something
    // TODO: Implement this as a FoodModifier
    // TODO: And maybe move this enum to StatePropertiesFL, ask russian
    FRESH("fresh", 0),
    AGED("aged", 4),
    VINTAGE("vintage", 8);

    private final int ID;
    private final String name;

    AgingFL(String name, int ID) {
        this.ID = ID;
        this.name = name;
    }

    @Override
    public String getName()
    {
        return this.name;
    }

    public int getID()
    {
        return this.ID;
    }
}
