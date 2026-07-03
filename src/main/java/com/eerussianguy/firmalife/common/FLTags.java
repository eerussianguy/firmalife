package com.eerussianguy.firmalife.common;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluid;

import net.dries007.tfc.common.component.food.FoodTrait;
import net.dries007.tfc.common.component.food.FoodTraits;

@SuppressWarnings("unused")
public class FLTags
{
    public static class Blocks
    {
        public static final TagKey<Block> OVEN_BLOCKS = create("oven_blocks");
        public static final TagKey<Block> OVEN_INSULATION = create("oven_insulation");
        public static final TagKey<Block> PLANTERS = create("planters");
        public static final TagKey<Block> CHIMNEYS = create("chimneys");
        public static final TagKey<Block> GREENHOUSE = create("greenhouse");
        public static final TagKey<Block> GREENHOUSE_FULL_WALLS = create("greenhouse_full_walls");
        public static final TagKey<Block> GREENHOUSE_PANEL_WALLS = create("greenhouse_panel_walls");
        public static final TagKey<Block> GREENHOUSE_PANEL_ROOFS = create("greenhouse_panel_roofs");
        public static final TagKey<Block> ALWAYS_VALID_GREENHOUSE_WALL = create("always_valid_greenhouse_wall");
        public static final TagKey<Block> ALL_TREATED_WOOD_GREENHOUSE = create("all_treated_wood_greenhouse");
        public static final TagKey<Block> ALL_IRON_GREENHOUSE = create("all_iron_greenhouse");
        public static final TagKey<Block> IRON_GREENHOUSE = create("iron_greenhouse");
        public static final TagKey<Block> RUSTED_IRON_GREENHOUSE = create("rusted_iron_greenhouse");
        public static final TagKey<Block> ALL_COPPER_GREENHOUSE = create("all_copper_greenhouse");
        public static final TagKey<Block> STAINLESS_STEEL_GREENHOUSE = create("stainless_steel_greenhouse");
        public static final TagKey<Block> CELLAR_INSULATION = create("cellar_insulation");
        public static final TagKey<Block> BEE_RESTORATION_PLANTS = create("bee_restoration_plants");
        public static final TagKey<Block> BEE_RESTORATION_WATER_PLANTS = create("bee_restoration_water_plants");
        public static final TagKey<Block> BUZZING_LEAVES = create("buzzing_leaves");
        public static final TagKey<Block> PIPE_REPLACEABLE = create("pipe_replaceable");
        public static final TagKey<Block> GRAPE_STRINGS = create("grape_strings");
        public static final TagKey<Block> GRAPE_TRELLIS_POSTS_PLANT = create("grape_trellis_posts_plant");
        public static final TagKey<Block> FOOD_SHELVES = create("food_shelves");
        public static final TagKey<Block> HANGERS = create("hangers");
        public static final TagKey<Block> CHEESE_WHEELS = create("cheese_wheel");
        public static final TagKey<Block> JARBNETS = create("jarbnets");
        public static final TagKey<Block> KEGS = create("kegs");
        public static final TagKey<Block> STOMPING_BARRELS = create("stomping_barrels");
        public static final TagKey<Block> BARREL_PRESSES = create("barrel_presses");
        public static final TagKey<Block> WINE_SHELVES = create("wine_shelves");
        public static final TagKey<Block> CHROMITE = common("ores/chromite");
        public static final TagKey<Block> POOR_CHROMITE = create("ores/chromite/poor");
        public static final TagKey<Block> NORMAL_CHROMITE = create("ores/chromite/normal");
        public static final TagKey<Block> RICH_CHROMITE = create("ores/chromite/rich");
        public static final TagKey<Block> HERBS = create("herbs");

        private static TagKey<Block> create(String id)
        {
            return TagKey.create(Registries.BLOCK, FLHelpers.identifier(id));
        }

        private static TagKey<Block> common(String id)
        {
            return TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath("c", id));
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
        public static final TagKey<Item> BEEHIVE_FRAMES = create("beehive_frames");
        public static final TagKey<Item> BEE_BAIT = create("bee_bait");
        public static final TagKey<Item> FILLED_BEEHIVE_FRAMES = create("filled_beehive_frames");
        public static final TagKey<Item> EMPTY_WINE_BOTTLES = create("empty_wine_bottles");
        public static final TagKey<Item> WINE_BOTTLES = create("wine_bottles");
        public static final TagKey<Item> GRAPES = create("foods/grapes");
        public static final TagKey<Item> SMASHED_GRAPES = create("foods/smashed_grapes");
        public static final TagKey<Item> CAN_BE_PRESSED_LIKE_GRAPES = create("can_be_pressed_like_grapes");
        public static final TagKey<Item> COOKED_MEATS_AND_SUBSTITUTES = create("foods/cooked_meats_and_substitutes");
        public static final TagKey<Item> CHEESES = create("foods/cheeses");
        public static final TagKey<Item> AGED_CHEESES = create("foods/aged_cheeses");
        public static final TagKey<Item> RAW_EGGS = create("foods/raw_eggs");
        public static final TagKey<Item> FEEDS_YEAST = create("feeds_yeast");
        public static final TagKey<Item> WASHABLE_FOODS = create("foods/washable");
        public static final TagKey<Item> CHOCOLATE = create("foods/chocolate");
        public static final TagKey<Item> EGG_NOODLE_FLOUR = create("foods/egg_noodle_flour");
        public static final TagKey<Item> BREAD_SLICES = create("foods/bread_slices");
        public static final TagKey<Item> PIZZA_INGREDIENTS = create("foods/pizza_ingredients");
        public static final TagKey<Item> FLATBREADS = create("foods/flatbreads");
        public static final TagKey<Item> CHOCOLATE_BLENDS = create("foods/chocolate_blends");
        public static final TagKey<Item> FOOD_SHELVES = create("shelves");
        public static final TagKey<Item> HANGERS = create("hangers");
        public static final TagKey<Item> JARBNETS = create("jarbnets");
        public static final TagKey<Item> KEGS = create("kegs");
        public static final TagKey<Item> STOMPING_BARRELS = create("stomping_barrels");
        public static final TagKey<Item> BARREL_PRESSES = create("barrel_presses");
        public static final TagKey<Item> WINE_SHELVES = create("wine_shelves");
        public static final TagKey<Item> CHEESE_WHEELS = create("foods/cheese_wheels");
        public static final TagKey<Item> DYNAMIC_FOODS = create("foods/dynamic_foods");
        public static final TagKey<Item> FILLED_WINE_BOTTLES = create("foods/filled_wine_bottles");
        public static final TagKey<Item> HERBS = create("herbs");
        public static final TagKey<Item> COOKED_POULTRY = create("foods/cooked_poultry");

        private static TagKey<Item> create(String id)
        {
            return TagKey.create(Registries.ITEM, FLHelpers.identifier(id));
        }

    }

    public static class Fluids
    {
        public static final TagKey<Fluid> USABLE_IN_MIXING_BOWL = create("usable_in_mixing_bowl");
        public static final TagKey<Fluid> USABLE_IN_HOLLOW_SHELL = create("usable_in_hollow_shell");
        public static final TagKey<Fluid> USABLE_IN_WINE_GLASS = create("usable_in_wine_glass");
        public static final TagKey<Fluid> USABLE_IN_VAT = create("usable_in_vat");
        public static final TagKey<Fluid> WINE = create("wine");
        public static final TagKey<Fluid> MILKS = create("milks");
        public static final TagKey<Fluid> OILS = create("oils");

        private static TagKey<Fluid> create(String id)
        {
            return TagKey.create(Registries.FLUID, FLHelpers.identifier(id));
        }

    }

    public static class Traits
    {
        public static final TagKey<FoodTrait> WINE = create("wine");

        private static TagKey<FoodTrait> create(String id)
        {
            return TagKey.create(FoodTraits.KEY, FLHelpers.identifier(id));
        }
    }

    public static class Entities
    {
        public static final TagKey<EntityType<?>> DROPS_RENNET = create("drops_rennet");
        public static final TagKey<EntityType<?>> DROPS_MORE_RENNET = create("drops_more_rennet");

        private static TagKey<EntityType<?>> create(String id)
        {
            return TagKey.create(Registries.ENTITY_TYPE, FLHelpers.identifier(id));
        }
    }
}
