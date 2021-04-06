package com.eerussianguy.firmalife.init;

import net.minecraft.util.IStringSerializable;

import mcp.MethodsReturnNonnullByDefault;
import net.dries007.tfc.api.capability.food.FoodTrait;

@MethodsReturnNonnullByDefault
public enum AgingFL implements IStringSerializable
{
    FRESH("fresh", 0, FoodDataFL.FRESH),
    AGED("aged", 4, FoodDataFL.AGED),
    VINTAGE("vintage", 8, FoodDataFL.VINTAGE);

    private final int ID;
    private final String name;
    private final FoodTrait trait;

    AgingFL(String name, int ID, FoodTrait trait)
    {
        this.ID = ID;
        this.name = name;
        this.trait = trait;
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

    public FoodTrait getTrait()
    {
        return this.trait;
    }
}
