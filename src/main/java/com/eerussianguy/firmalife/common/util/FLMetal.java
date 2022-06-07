package com.eerussianguy.firmalife.common.util;

import java.util.Locale;

import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;

import net.minecraftforge.common.util.NonNullFunction;

import net.dries007.tfc.common.TFCItemGroup;
import net.dries007.tfc.util.Metal;

public enum FLMetal implements StringRepresentable
{
    STAINLESS_STEEL(0xFFD9FCFF, Rarity.EPIC, Metal.Tier.TIER_VI);

    private final String serializedName;
    private final Metal.Tier metalTier;
    private final Rarity rarity;
    private final int color;

    FLMetal(int color, Rarity rarity)
    {
        this(color, rarity, Metal.Tier.TIER_0);
    }

    FLMetal(int color, Rarity rarity, Metal.Tier metalTier)
    {
        this.serializedName = name().toLowerCase(Locale.ROOT);
        this.metalTier = metalTier;
        this.rarity = rarity;
        this.color = color;
    }

    @Override
    public String getSerializedName()
    {
        return serializedName;
    }

    public Metal.Tier getMetalTier()
    {
        return metalTier;
    }

    public Rarity getRarity()
    {
        return rarity;
    }

    public int getColor()
    {
        return color;
    }

    public enum ItemType
    {
        // Generic
        INGOT(metal -> new Item(new Item.Properties().tab(TFCItemGroup.METAL))),
        DOUBLE_INGOT(metal -> new Item(new Item.Properties().tab(TFCItemGroup.METAL))),
        SHEET(metal -> new Item(new Item.Properties().tab(TFCItemGroup.METAL))),
        DOUBLE_SHEET(metal -> new Item(new Item.Properties().tab(TFCItemGroup.METAL))),
        ROD(metal -> new Item(new Item.Properties().tab(TFCItemGroup.METAL)));

        private final NonNullFunction<FLMetal, Item> itemFactory;

        ItemType(NonNullFunction<FLMetal, Item> itemFactory)
        {
            this.itemFactory = itemFactory;
        }

        public Item create(FLMetal metal)
        {
            return itemFactory.apply(metal);
        }
    }
}
