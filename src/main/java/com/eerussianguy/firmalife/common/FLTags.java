package com.eerussianguy.firmalife.common;

import net.minecraft.core.Registry;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;

@SuppressWarnings("unused")
public class FLTags
{
    public static class Blocks
    {
        public static final TagKey<Block> OVEN_BLOCKS = create("oven_blocks");
        public static final TagKey<Block> OVEN_INSULATION = create("oven_insulation");
        public static final TagKey<Block> CHIMNEYS = create("chimneys");
        public static final TagKey<Block> GREENHOUSE = create("greenhouse");
        public static final TagKey<Block> ALL_TREATED_WOOD_GREENHOUSE = create("all_treated_wood_greenhouse");
        public static final TagKey<Block> ALL_IRON_GREENHOUSE = create("all_iron_greenhouse");
        public static final TagKey<Block> ALL_COPPER_GREENHOUSE = create("all_copper_greenhouse");
        public static final TagKey<Block> STAINLESS_STEEL_GREENHOUSE = create("stainless_steel_greenhouse");
        public static final TagKey<Block> CELLAR_INSULATION = create("cellar_insulation");
        public static final TagKey<Block> BEE_PLANTS = create("bee_plants");
        public static final TagKey<Block> BEE_RESTORATION_PLANTS = create("bee_restoration_plants");
        public static final TagKey<Block> BEE_RESTORATION_WATER_PLANTS = create("bee_restoration_water_plants");

        private static TagKey<Block> create(String id)
        {
            return TagKey.create(Registry.BLOCK_REGISTRY, FLHelpers.identifier(id));
        }
    }

    public static class Items
    {
        public static final TagKey<Item> USABLE_ON_OVEN = create("usable_on_oven");
        public static final TagKey<Item> SMOKING_FUEL = create("smoking_fuel");

        private static TagKey<Item> create(String id)
        {
            return TagKey.create(Registry.ITEM_REGISTRY, FLHelpers.identifier(id));
        }

    }

    public static class Biomes
    {

        private static TagKey<Biome> create(String id)
        {
            return TagKey.create(Registry.BIOME_REGISTRY, FLHelpers.identifier(id));
        }
    }
}
