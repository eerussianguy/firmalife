package com.eerussianguy.firmalife.common.util;

import java.util.Locale;
import java.util.function.Function;

import com.eerussianguy.firmalife.common.blocks.FLBlocks;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.MapColor;

import com.eerussianguy.firmalife.common.FLHelpers;

import net.dries007.tfc.common.LevelTier;
import net.dries007.tfc.common.TFCTiers;
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

    @Override
    public LevelTier toolTier()
    {
        return TFCTiers.STEEL;
    }

    @Override
    public Holder<ArmorMaterial> armorMaterial()
    {
        return Metal.RED_STEEL.armorMaterial();
    }

    @Override
    public int armorDurability(ArmorItem.Type type)
    {
        return Metal.RED_STEEL.armorDurability(type);
    }

    @Override
    public Block getBlock(Metal.BlockType blockType)
    {
        return FLBlocks.METALS.get(this).get(Metal.BlockType.BLOCK).get();
    }

    @Override
    public MapColor mapColor()
    {
        return mapColor;
    }

    @Override
    public Rarity rarity()
    {
        return Rarity.EPIC;
    }

    @Override
    public float weatheringResistance()
    {
        return 0;
    }

    public enum ItemType
    {
        // Generic
        INGOT(metal -> new Item(new Item.Properties())),
        DOUBLE_INGOT(metal -> new Item(new Item.Properties())),
        SHEET(metal -> new Item(new Item.Properties())),
        DOUBLE_SHEET(metal -> new Item(new Item.Properties())),
        ROD(metal -> new Item(new Item.Properties()));

        private final Function<FLMetal, Item> itemFactory;

        ItemType(Function<FLMetal, Item> itemFactory)
        {
            this.itemFactory = itemFactory;
        }

        public Item create(FLMetal metal)
        {
            return itemFactory.apply(metal);
        }
    }
}
