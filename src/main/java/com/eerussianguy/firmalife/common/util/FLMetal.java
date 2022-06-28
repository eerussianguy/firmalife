package com.eerussianguy.firmalife.common.util;

import java.util.Locale;

import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.Tier;

import net.minecraftforge.common.util.NonNullFunction;

import net.dries007.tfc.common.TFCArmorMaterial;
import net.dries007.tfc.common.TFCArmorMaterials;
import net.dries007.tfc.common.TFCItemGroup;
import net.dries007.tfc.common.TFCTiers;
import net.dries007.tfc.util.Metal;
import net.dries007.tfc.util.registry.RegistryMetal;

public enum FLMetal implements RegistryMetal
{
    CHROMIUM(0xFFF5FEFF),
    STAINLESS_STEEL(0xFFD9FCFF);

    private final String serializedName;
    private final int color;

    FLMetal(int color)
    {
        this.serializedName = name().toLowerCase(Locale.ROOT);
        this.color = color;
    }

    @Override
    public String getSerializedName()
    {
        return serializedName;
    }

    public int getColor()
    {
        return color;
    }

    public Rarity getRarity()
    {
        return Rarity.EPIC;
    }

    @Override
    public Tier toolTier()
    {
        return TFCTiers.STEEL;
    }

    @Override
    public ArmorMaterial armorTier()
    {
        return TFCArmorMaterials.RED_STEEL;
    }

    @Override
    public Metal.Tier metalTier()
    {
        return Metal.Tier.TIER_VI;
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
