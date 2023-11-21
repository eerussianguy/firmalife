package com.eerussianguy.firmalife.common;

import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluid;

@SuppressWarnings("unused")
public class FLTags
{
    public static class Blocks
    {
        public static final TagKey<Block> OVEN_BLOCKS = create("oven_blocks");
        public static final TagKey<Block> OVEN_INSULATION = create("oven_insulation");
        public static final TagKey<Block> CHIMNEYS = create("chimneys");
        public static final TagKey<Block> GREENHOUSE = create("greenhouse");
        public static final TagKey<Block> GREENHOUSE_FULL_WALLS = create("greenhouse_full_walls");
        public static final TagKey<Block> GREENHOUSE_PANEL_WALLS = create("greenhouse_panel_walls");
        public static final TagKey<Block> ALWAYS_VALID_GREENHOUSE_WALL = create("always_valid_greenhouse_wall");
        public static final TagKey<Block> ALL_TREATED_WOOD_GREENHOUSE = create("all_treated_wood_greenhouse");
        public static final TagKey<Block> ALL_IRON_GREENHOUSE = create("all_iron_greenhouse");
        public static final TagKey<Block> ALL_COPPER_GREENHOUSE = create("all_copper_greenhouse");
        public static final TagKey<Block> STAINLESS_STEEL_GREENHOUSE = create("stainless_steel_greenhouse");
        public static final TagKey<Block> CELLAR_INSULATION = create("cellar_insulation");
        public static final TagKey<Block> BEE_PLANTS = create("bee_plants");
        public static final TagKey<Block> BEE_RESTORATION_PLANTS = create("bee_restoration_plants");
        public static final TagKey<Block> BEE_RESTORATION_WATER_PLANTS = create("bee_restoration_water_plants");
        public static final TagKey<Block> BUTTERFLY_GRASS_MUTANTS = create("butterfly_grass_mutants");
        public static final TagKey<Block> BUZZING_LEAVES = create("buzzing_leaves");
        public static final TagKey<Block> PIPE_REPLACEABLE = create("pipe_replaceable");

        private static TagKey<Block> create(String id)
        {
            return TagKey.create(Registries.BLOCK, FLHelpers.identifier(id));
        }
    }

    public static class Items
    {
        public static final TagKey<Item> USABLE_ON_OVEN = create("usable_on_oven");
        public static final TagKey<Item> SMOKING_FUEL = create("smoking_fuel");
        public static final TagKey<Item> OVEN_FUEL = create("oven_fuel");
        public static final TagKey<Item> PUMPKIN_KNAPPING = create("pumpkin_knapping");
        public static final TagKey<Item> PIE_PANS = create("pie_pans");
        public static final TagKey<Item> CAN_BE_HUNG = create("can_be_hung");
        public static final TagKey<Item> USABLE_IN_STOVETOP_SOUP = create("usable_in_stovetop_soup");
        public static final TagKey<Item> BEEKEEPER_ARMOR = create("beekeeper_armor");

        private static TagKey<Item> create(String id)
        {
            return TagKey.create(Registries.ITEM, FLHelpers.identifier(id));
        }

    }

    public static class Fluids
    {
        public static final TagKey<Fluid> USABLE_IN_MIXING_BOWL = create("usable_in_mixing_bowl");
        public static final TagKey<Fluid> USABLE_IN_HOLLOW_SHELL = create("usable_in_hollow_shell");
        public static final TagKey<Fluid> USABLE_IN_VAT = create("usable_in_vat");

        private static TagKey<Fluid> create(String id)
        {
            return TagKey.create(Registries.FLUID, FLHelpers.identifier(id));
        }

    }
}
