package com.eerussianguy.firmalife.common.util;

import java.util.Locale;

import net.minecraft.ChatFormatting;
import net.minecraft.util.StringRepresentable;

import com.eerussianguy.firmalife.common.items.FLFoodTraits;
import net.neoforged.neoforge.registries.DeferredHolder;

import net.dries007.tfc.common.component.food.FoodTrait;

public enum FoodAge implements StringRepresentable
{
    FRESH(0, ChatFormatting.GRAY, FLFoodTraits.FRESH),
    AGED(4, ChatFormatting.DARK_RED, FLFoodTraits.AGED),
    VINTAGE(8, ChatFormatting.GOLD, FLFoodTraits.VINTAGE);

    private final int id;
    private final String name;
    private final ChatFormatting format;
    private final DeferredHolder<FoodTrait, FoodTrait> trait;

    FoodAge(int id, ChatFormatting format, DeferredHolder<FoodTrait, FoodTrait> trait)
    {
        this.id = id;
        this.name = name().toLowerCase(Locale.ROOT);
        this.format = format;
        this.trait = trait;
    }

    @Override
    public String getSerializedName()
    {
        return name;
    }

    public int getId()
    {
        return this.id;
    }

    public ChatFormatting getFormat()
    {
        return this.format;
    }

    public DeferredHolder<FoodTrait, FoodTrait> getTrait()
    {
        return trait;
    }
}
