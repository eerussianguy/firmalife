package com.eerussianguy.firmalife.common.util;

import java.util.Locale;
import java.util.function.Supplier;

import com.eerussianguy.firmalife.common.blocks.FLBlocks;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.Tier;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.MapColor;
import net.minecraftforge.common.util.NonNullFunction;

import com.eerussianguy.firmalife.common.FLHelpers;
import net.dries007.tfc.common.TFCArmorMaterials;
import net.dries007.tfc.common.TFCTiers;
import net.dries007.tfc.common.blocks.TFCBlocks;
import net.dries007.tfc.util.Metal;
import net.dries007.tfc.util.registry.RegistryMetal;

public enum FLMetal implements RegistryMetal
{
    CHROMIUM(0xFFF5FEFF, MapColor.COLOR_LIGHT_GRAY),
    STAINLESS_STEEL(0xFFD9FCFF, MapColor.COLOR_LIGHT_GRAY);

    private final String serializedName;
    private final int color;
    private final MapColor mapColor;
    private final ResourceLocation sheet;

    FLMetal(int color, MapColor mapColor)
    {
        this.serializedName = name().toLowerCase(Locale.ROOT);
        this.color = color;
        this.mapColor = mapColor;
        this.sheet = FLHelpers.identifier("block/metal/full/" + serializedName);
    }

    public ResourceLocation getSheet()
    {
        return sheet;
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

    @Override
    public MapColor mapColor()
    {
        return mapColor;
    }

    @Override
    public Supplier<Block> getFullBlock()
    {
        return FLBlocks.METALS.get(this).get(Metal.BlockType.BLOCK);
    }

    public enum ItemType
    {
        // Generic
        INGOT(metal -> new Item(new Item.Properties())),
        DOUBLE_INGOT(metal -> new Item(new Item.Properties())),
        SHEET(metal -> new Item(new Item.Properties())),
        DOUBLE_SHEET(metal -> new Item(new Item.Properties())),
        ROD(metal -> new Item(new Item.Properties()));

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
