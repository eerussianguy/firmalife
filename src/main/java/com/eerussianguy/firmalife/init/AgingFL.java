package com.eerussianguy.firmalife.init;

import net.minecraft.util.IStringSerializable;
import net.minecraft.util.text.TextFormatting;

import mcp.MethodsReturnNonnullByDefault;
import net.dries007.tfc.api.capability.food.FoodTrait;

import static com.eerussianguy.firmalife.FirmaLife.MOD_ID;

@MethodsReturnNonnullByDefault
public enum AgingFL implements IStringSerializable
{
    FRESH("fresh", 0, FoodDataFL.FRESH, TextFormatting.GRAY),
    AGED("aged", 4, FoodDataFL.AGED, TextFormatting.DARK_RED),
    VINTAGE("vintage", 8, FoodDataFL.VINTAGE, TextFormatting.GOLD);

    private final int ID;
    private final String name;
    private final FoodTrait trait;
    private final TextFormatting format;

    AgingFL(String name, int ID, FoodTrait trait, TextFormatting format)
    {
        this.ID = ID;
        this.name = name;
        this.trait = trait;
        this.format = format;
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

    public TextFormatting getFormat()
    {
        return this.format;
    }

    public String getTranslationKey() {
        return "food_trait." + MOD_ID + "." + this.name;
    }
}
