package com.eerussianguy.firmalife.recipes;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import com.eerussianguy.firmalife.common.FLTags;
import com.eerussianguy.firmalife.common.blocks.FLBlocks;
import com.eerussianguy.firmalife.common.blocks.Herb;
import com.eerussianguy.firmalife.common.blocks.OvenType;
import com.eerussianguy.firmalife.common.blocks.greenhouse.Greenhouse;
import com.eerussianguy.firmalife.common.items.FLFood;
import com.eerussianguy.firmalife.common.items.FLFoodTraits;
import com.eerussianguy.firmalife.common.items.FLItems;
import com.eerussianguy.firmalife.common.items.Spice;
import com.eerussianguy.firmalife.common.recipes.data.AddPiePanModifier;
import com.eerussianguy.firmalife.common.util.ExtraFluid;
import com.eerussianguy.firmalife.common.util.FLFruit;
import com.eerussianguy.firmalife.common.util.FLMetal;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.common.Tags;

import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.blocks.DecorationBlockHolder;
import net.dries007.tfc.common.blocks.GroundcoverBlockType;
import net.dries007.tfc.common.blocks.TFCBlocks;
import net.dries007.tfc.common.blocks.plant.Plant;
import net.dries007.tfc.common.blocks.wood.Wood;
import net.dries007.tfc.common.component.food.FoodData;
import net.dries007.tfc.common.fluids.SimpleFluid;
import net.dries007.tfc.common.items.Food;
import net.dries007.tfc.common.items.Powder;
import net.dries007.tfc.common.items.TFCItems;
import net.dries007.tfc.common.recipes.ingredients.FluidContentIngredient;
import net.dries007.tfc.common.recipes.outputs.MealModifier;
import net.dries007.tfc.util.DataGenerationHelpers;
import net.dries007.tfc.util.Metal;

public interface CraftingRecipes extends Recipes
{
    default void craftingRecipes()
    {
        remove("crafting/pumpkin_pie");

        var brassRods = commonTagOf(Registries.ITEM, "rods/brass");
        var stainlessRods = commonTagOf(Registries.ITEM, "rods/stainless_steel");
        var wroughtRods = commonTagOf(Registries.ITEM, "rods/wrought_iron");
        var stainlessSheets = commonTagOf(Registries.ITEM, "rods/stainless_steel");
        var wroughtSheets = commonTagOf(Registries.ITEM, "sheets/wrought_iron");
        var anyBronzeSheets = commonTagOf(Registries.ITEM, "sheets/any_bronze");

        //Shaped
        recipe()
            .input('X', Items.BOWL)
            .input('Y', Tags.Items.RODS_WOODEN)
            .pattern("X", "Y")
            .shaped(FLItems.PEEL);
        recipe()
            .input('X', FLItems.TREATED_LUMBER)
            .input('Y', TFCItems.GLUE)
            .pattern("XYX", "YXY")
            .shaped(FLBlocks.MIXING_BOWL);
        recipe()
            .input('X', FLItems.FRUIT_LEAF)
            .pattern("XXX")
            .shaped(FLBlocks.DRYING_MAT);
        recipe()
            .input('S', stainlessRods)
            .input('G', Tags.Items.GLASS_BLOCKS)
            .input('M', FLBlocks.DRYING_MAT)
            .input('W', FLItems.TREATED_LUMBER)
            .pattern("SGS", " M ", "WWW")
            .shaped(FLBlocks.SOLAR_DRIER);
        recipe()
            .input('S', TFCItems.METAL_ITEMS.get(Metal.STEEL).get(Metal.ItemType.SAW_BLADE))
            .input('R', stainlessRods)
            .pattern("SR", " R")
            .shaped(FLBlocks.SWEEPER);
        recipe()
            .input('S', stainlessSheets)
            .input('R', stainlessRods)
            .input('B', TFCItems.BRASS_MECHANISMS)
            .pattern("SB", "RR", "RR")
            .shaped(FLBlocks.PICKER);
        recipe()
            .input('X', ItemTags.STONE_BRICKS)
            .input('Y', FLItems.BEESWAX)
            .pattern("XXX", "XYX", "XXX")
            .shaped(new ItemStack(FLBlocks.SEALED_BRICKS, 8));
        recipe()
            .input('X', FLBlocks.SEALED_BRICKS)
            .input('Y', brassRods)
            .pattern("YX", "YX", "YX")
            .shaped(FLBlocks.SEALED_DOOR);
        recipe()
            .input('X', Items.BRICK)
            .input('Y', TFCItems.COMPOST)
            .pattern("YY", "XX", "XX")
            .shaped(FLBlocks.QUAD_PLANTER);
        recipe()
            .input('X', Items.BRICK)
            .input('Y', TFCItems.COMPOST)
            .pattern("XYX", "XXX")
            .shaped(FLBlocks.LARGE_PLANTER);
        recipe()
            .input('X', Items.BRICK)
            .input('Y', TFCItems.COMPOST)
            .pattern("XXX", "XYX")
            .shaped(FLBlocks.HANGING_PLANTER);
        recipe()
            .input('X', Items.BRICK)
            .input('Y', TFCItems.COMPOST)
            .pattern("X X", "X X", "XYX")
            .shaped(FLBlocks.TRELLIS_PLANTER);
        recipe()
            .input('X', Items.BRICK)
            .input('Y', TFCItems.COMPOST)
            .pattern("X X", "XYX", "XXX")
            .shaped(FLBlocks.BONSAI_PLANTER);
        recipe()
            .input('X', TFCTags.Items.LUMBER)
            .input('Y', FLItems.BEESWAX)
            .pattern("XXX", "XYX", "XXX")
            .shaped(new ItemStack(FLItems.TREATED_LUMBER, 8));
        recipe()
            .input('Y', TFCItems.COMPOST)
            .input('X', FLItems.TREATED_LUMBER)
            .input('Z', wroughtSheets)
            .pattern("YY", "XX", "Z ")
            .shaped(new ItemStack(FLBlocks.HYDROPONIC_PLANTER, 2));
        recipe()
            .input('X', stainlessSheets)
            .input('Y', FLItems.BEESWAX)
            .pattern("X X", "YXY")
            .shaped(FLBlocks.VAT);
        recipe()
            .input('X', stainlessSheets)
            .input('Z', FLItems.TREATED_LUMBER)
            .pattern("X X", "ZZZ")
            .shaped(FLBlocks.JARRING_STATION);
        recipe()
            .input(FLBlocks.OVEN_BOTTOM)
            .input(Items.HOPPER)
            .shapeless(FLBlocks.OVEN_HOPPER);
        recipe()
            .input('X', TFCTags.Items.LUMBER)
            .pattern("X X", " X ", "X X")
            .shaped(FLItems.BEEHIVE_FRAME);
        recipe()
            .input(FLItems.BEEHIVE_FRAME)
            .input(FLItems.FOODS.get(FLFood.RAW_HONEY))
            .shapeless(FLItems.FILLED_BEEHIVE_FRAME);
        recipe()
            .input('X', TFCTags.Items.LUMBER)
            .input('Y', FLItems.BEEHIVE_FRAME)
            .input('Z', TFCBlocks.THATCH)
            .pattern("XYX", "XZX", "XYX")
            .shaped(FLBlocks.BEEHIVE);
        recipe()
            .input('X', TFCItems.STRAW)
            .input('Y', TFCBlocks.PLANTS.get(Plant.CATTAIL))
            .input('Z', Tags.Items.RODS_WOODEN)
            .pattern("XYX", "XZX", "XYX")
            .shaped(FLBlocks.SKEP);
        recipe()
            .input('X', wroughtSheets)
            .input('Y', TFCBlocks.COMPOSTER)
            .input('A', TFCTags.Items.AXLES)
            .input('G', TFCItems.GLUE)
            .pattern("XYX", "AGA")
            .shaped(FLBlocks.COMPOST_TUMBLER);
        recipe()
            .input('X', itemOf(Powder.SALT))
            .input('Y', itemOf(FLFood.YAK_CURD))
            .pattern("XXX", "YYY", "XXX")
            .shaped(FLBlocks.RAJYA_METOK_WHEEL);
        recipe()
            .input('X', itemOf(Powder.SALT))
            .input('Y', itemOf(FLFood.GOAT_CURD))
            .pattern("XXX", "YYY", "XXX")
            .shaped(FLBlocks.CHEVRE_WHEEL);
        recipe()
            .input('X', itemOf(Powder.SALT))
            .input('Y', itemOf(FLFood.MILK_CURD))
            .pattern("XXX", "YYY", "XXX")
            .shaped(FLBlocks.CHEDDAR_WHEEL);
        recipe()
            .input('X', TFCTags.Items.HIGH_QUALITY_CLOTH)
            .pattern("XX")
            .shaped(new ItemStack(FLItems.CHEESECLOTH, 8));
        recipe()
            .input('Y', Items.BLUE_STAINED_GLASS)
            .input('X', TFCItems.BRASS_MECHANISMS)
            .input('O', Tags.Items.DUSTS_REDSTONE)
            .input('B', ItemTags.PLANKS)
            .pattern("BXB", "OYO", "BXB")
            .shaped(FLBlocks.CLIMATE_STATION);
        recipe()
            .input('X', Tags.Items.SEEDS)
            .input('Y', TFCItems.COMPOST)
            .pattern(" X ", "XYX", " X ")
            .shaped(FLItems.SEED_BALL);
        recipe()
            .input('X', FLItems.TREATED_LUMBER)
            .pattern("XX", "XX")
            .shaped(FLBlocks.TREATED_WOOD);
        recipe()
            .input('X', itemOf(FLFood.RAW_EGG_NOODLES))
            .input('Y', itemOf(FLFood.TOMATO_SAUCE))
            .input('Z', FLTags.Items.COOKED_MEATS_AND_SUBSTITUTES)
            .input('Q', itemOf(Herb.OREGANO))
            .pattern("XQ", "YZ", "XQ")
            .shaped(itemOf(FLFood.RAW_LASAGNA));
        recipe()
            .input('Z', itemOf(Powder.SALT))
            .input('X', FLBlocks.SEALED_BRICKS)
            .input('Y', wroughtSheets)
            .pattern("ZZZ", "XYX")
            .shaped(FLBlocks.ASHTRAY);
        recipe()
            .input('X', FLBlocks.SEALED_BRICKS)
            .pattern("X X", "X X", "X X")
            .shaped(new ItemStack(FLBlocks.DARK_LADDER, 16));
        recipe()
            .input('X', FLBlocks.SEALED_BRICKS)
            .pattern("   ", "XXX", "XXX")
            .shaped(new ItemStack(FLBlocks.SEALED_WALL, 6));
        recipe()
            .input('Y', brassRods)
            .input('X', FLBlocks.SEALED_BRICKS)
            .pattern("YY", "XX", "XX")
            .shaped(FLBlocks.SEALED_TRAPDOOR);
        recipe()
            .input('S', anyBronzeSheets)
            .input('R', Tags.Items.DUSTS_REDSTONE)
            .input('B', TFCItems.BRASS_MECHANISMS)
            .input('Z', TFCTags.Items.AXLES)
            .pattern("SRS", "BBB", "ZZZ")
            .shaped(FLBlocks.PUMPING_STATION);
        recipe()
            .input('X', FLItems.TREATED_LUMBER)
            .pattern("X", "X", "X")
            .shaped(FLBlocks.GRAPE_TRELLIS_POST);
        recipe()
            .input('B', Blocks.BRICKS)
            .input('L', FluidContentIngredient.of(fluidOf(SimpleFluid.TANNIN), 1000))
            .pattern("BBB", "BLB", "BBB")
            .shaped(new ItemStack(FLBlocks.RUSTIC_BRICKS, 8));

        //Shapeless
        recipe()
            .input(Tags.Items.RODS_WOODEN)
            .input(TFCTags.Items.LUMBER)
            .inputIsPrimary(TFCTags.Items.TOOLS_KNIFE)
            .damageInputs()
            .shapeless(FLItems.SPOON);
        recipe()
            .input(FLItems.BEESWAX)
            .input(Tags.Items.STRINGS)
            .shapeless(new ItemStack(TFCBlocks.CANDLE, 4));
        recipe()
            .input(FluidContentIngredient.of(Fluids.WATER, 1000))
            .input(TFCItems.WOODEN_BUCKET)
            .input(TFCTags.Items.LUMBER)
            .shapeless(FLItems.WATERING_CAN);
        recipe()
            .input(Items.CLAY_BALL)
            .input(itemOf(Powder.WOOD_ASH))
            .input(itemOf(Powder.SALT))
            .input(itemOf(Food.BOILED_EGG))
            .shapeless(itemOf(FLFood.PICKLED_EGG));
        recipe()
            .input(itemOf(FLFood.TOAST))
            .input(itemOf(FLFood.BUTTER))
            .input(itemOf(Food.GARLIC))
            .shapeless(itemOf(FLFood.GARLIC_BREAD));
        recipe()
            .input(itemOf(FLFood.TORTILLA_CHIPS))
            .input(itemOf(FLFood.SALSA))
            .input(itemOf(FLFood.SHREDDED_CHEESE))
            .input(FLTags.Items.COOKED_MEATS_AND_SUBSTITUTES)
            .shapeless(itemOf(FLFood.NACHOS));
        recipe()
            .input(itemOf(FLFood.TOAST))
            .input(TFCTags.Items.PRESERVES)
            .shapeless(itemOf(FLFood.TOAST_WITH_JAM));
        recipe()
            .input(itemOf(FLFood.TOAST))
            .input(itemOf(FLFood.BUTTER))
            .shapeless(itemOf(FLFood.TOAST_WITH_BUTTER));
        recipe()
            .input(itemOf(Food.TOMATO))
            .input(itemOf(Powder.SALT))
            .input(itemOf(Food.GARLIC))
            .shapeless(new ItemStack(itemOf(FLFood.TOMATO_SAUCE_MIX), 5));
        recipe()
            .input(FLBlocks.TILES)
            .input(Items.BRICK)
            .shapeless(new ItemStack(FLItems.FINISHES.get(OvenType.TILE), 16));
        recipe()
            .input(FLBlocks.SEALED_BRICKS)
            .input(Items.BRICK)
            .shapeless(new ItemStack(FLItems.FINISHES.get(OvenType.STONE), 16));
        recipe()
            .input(FLBlocks.RUSTIC_BRICKS)
            .input(Items.BRICK)
            .shapeless(new ItemStack(FLItems.FINISHES.get(OvenType.RUSTIC), 16));
        recipe()
            .input(Items.CLAY_BALL)
            .input(Tags.Items.COBBLESTONES)
            .input(Items.BRICK)
            .shapeless(new ItemStack(FLBlocks.TILES, 16));
        recipe()
            .input(wroughtSheets)
            .input(FLItems.BEESWAX)
            .input(TFCTags.Items.WELDING_FLUX)
            .shapeless(FLItems.OVEN_INSULATION);
        recipe()
            .input(itemOf(FLFood.VANILLA_ICE_CREAM))
            .input(itemOf(FLFood.STRAWBERRY_ICE_CREAM))
            .input(itemOf(FLFood.CHOCOLATE_ICE_CREAM))
            .input(itemOf(FLFruit.PINEAPPLE))
            .input(itemOf(Food.CHERRY))
            .input(itemOf(Food.BANANA))
            .input(itemOf(Food.BANANA))
            .shapeless(itemOf(FLFood.BANANA_SPLIT));
        recipe()
            .input(itemOf(FLFood.VANILLA_ICE_CREAM))
            .input(itemOf(FLFood.CHOCOLATE_CHIP_COOKIE_DOUGH))
            .shapeless(itemOf(FLFood.COOKIE_DOUGH_ICE_CREAM));
        recipe()
            .input(FLItems.TREATED_LUMBER)
            .input(FLItems.TREATED_LUMBER)
            .input(Items.WHITE_DYE)
            .input(TFCItems.GLUE)
            .shapeless(FLBlocks.PLATE);
        recipe()
            .input(FLBlocks.COPPER_PIPE)
            .input(FLBlocks.COPPER_PIPE)
            .input(FLBlocks.COPPER_PIPE)
            .input(FLBlocks.COPPER_PIPE)
            .input(itemOf(Powder.WOOD_ASH))
            .shapeless(new ItemStack(FLBlocks.OXIDIZED_COPPER_PIPE, 4));
        recipe()
            .input(FLItems.STAINLESS_STEEL_JAR_LID)
            .input(TFCItems.EMPTY_JAR)
            .shapeless(FLItems.EMPTY_JAR_WITH_STAINLESS_STEEL_LID);
        recipe().useTool(TFCTags.Items.TOOLS_KNIFE, itemOf(Herb.BASIL), itemOf(Spice.BASIL_LEAVES));
        recipe()
            .inputIsPrimary(TFCTags.Items.TOOLS_KNIFE)
            .input(notRotten(FLTags.Items.CHEESES))
            .damageInputs()
            .shapeless(new ItemStack(itemOf(FLFood.SHREDDED_CHEESE), 4));
        recipe()
            .inputIsPrimary(TFCTags.Items.TOOLS_KNIFE)
            .input(notRotten(itemOf(Food.TOMATO)))
            .input(itemOf(Powder.SALT))
            .input(itemOf(Herb.CILANTRO))
            .damageInputs()
            .shapeless(new ItemStack(itemOf(FLFood.SALSA)));
        recipe()
            .inputIsPrimary(TFCTags.Items.TOOLS_HAMMER)
            .input(itemOf(Powder.SALT))
            .input(notRotten(itemOf(FLFood.TACO_SHELL)))
            .damageInputs()
            .shapeless(itemOf(FLFood.TORTILLA_CHIPS));
        recipe()
            .inputIsPrimary(TFCTags.Items.TOOLS_KNIFE)
            .input(notRottenWithTrait(itemOf(FLFruit.PINEAPPLE), FLFoodTraits.DRIED))
            .damageInputs()
            .shapeless(FLItems.PINEAPPLE_FIBER);
        recipe()
            .inputIsPrimary(TFCItems.SPINDLE)
            .input(FLItems.PINEAPPLE_FIBER)
            .damageInputs()
            .shapeless(new ItemStack(FLItems.PINEAPPLE_YARN, 8));
        recipe()
            .inputIsPrimary(TFCTags.Items.TOOLS_KNIFE)
            .input(itemOf(Powder.SALT))
            .input(notRottenWithTrait(itemOf(Food.PORK), FLFoodTraits.SMOKED))
            .damageInputs()
            .shapeless(new ItemStack(itemOf(FLFood.BACON), 4));
        recipe().useTool(TFCTags.Items.TOOLS_CHISEL, Blocks.BRICKS, FLBlocks.OVEN_COUNTERTOP.get(OvenType.BRICK));
        recipe().useTool(TFCTags.Items.TOOLS_CHISEL, FLBlocks.SEALED_BRICKS, FLBlocks.OVEN_COUNTERTOP.get(OvenType.STONE));
        recipe().useTool(TFCTags.Items.TOOLS_CHISEL, FLBlocks.RUSTIC_BRICKS, FLBlocks.OVEN_COUNTERTOP.get(OvenType.RUSTIC));
        recipe().useTool(TFCTags.Items.TOOLS_CHISEL, FLBlocks.TILES, FLBlocks.OVEN_COUNTERTOP.get(OvenType.TILE));
        recipe()
            .inputIsPrimary(TFCTags.Items.TOOLS_HAMMER)
            .input(TFCTags.Items.FIRED_VESSELS)
            .damageInputs()
            .shapeless(FLItems.POTTERY_SHERD);
        decorationRecipe(FLBlocks.RUSTIC_BRICKS, FLBlocks.RUSTIC_BRICK_DECOR);
        decorationRecipe(FLBlocks.TILES, FLBlocks.TILE_DECOR);

        recipe()
            .input('L', TFCTags.Items.LUMBER)
            .input('S', anyBronzeSheets)
            .input('P', FLBlocks.COPPER_PIPE)
            .pattern("LSL", "LPL", "LLL")
            .shaped(FLBlocks.IRRIGATION_TANK);

        recipe().useTool(TFCTags.Items.TOOLS_CHISEL, FLBlocks.POLISHED_SEALED_BRICKS, FLBlocks.CHISELED_SEALED_BRICKS);
        recipe().useTool(TFCTags.Items.TOOLS_CHISEL, FLBlocks.SEALED_BRICKS, FLBlocks.POLISHED_SEALED_BRICKS);

        recipe()
            .inputIsPrimary(TFCTags.Items.TOOLS_HAMMER)
            .input(FLItems.TREATED_LUMBER)
            .input(FLItems.TREATED_LUMBER)
            .input(wroughtSheets)
            .damageInputs()
            .shapeless(FLItems.BARREL_STAVE);
        recipe()
            .inputIsPrimary(TFCTags.Items.TOOLS_KNIFE)
            .input(Items.PAPER)
            .input(FLItems.BEESWAX)
            .damageInputs()
            .shapeless(FLItems.BOTTLE_LABEL);
        recipe()
            .input(Items.LEATHER_HELMET)
            .input(TFCItems.BURLAP_CLOTH)
            .input(TFCItems.BURLAP_CLOTH)
            .input(itemOf(Powder.WOOD_ASH))
            .shapeless(FLItems.BEEKEEPER_HELMET);
        recipe()
            .input(Items.LEATHER_CHESTPLATE)
            .input(TFCItems.BURLAP_CLOTH)
            .input(TFCItems.BURLAP_CLOTH)
            .input(itemOf(Powder.WOOD_ASH))
            .shapeless(FLItems.BEEKEEPER_CHESTPLATE);
        recipe()
            .input(Items.LEATHER_LEGGINGS)
            .input(TFCItems.BURLAP_CLOTH)
            .input(TFCItems.BURLAP_CLOTH)
            .input(itemOf(Powder.WOOD_ASH))
            .shapeless(FLItems.BEEKEEPER_LEGGINGS);
        recipe()
            .input(Items.LEATHER_BOOTS)
            .input(TFCItems.BURLAP_CLOTH)
            .input(TFCItems.BURLAP_CLOTH)
            .input(itemOf(Powder.WOOD_ASH))
            .shapeless(FLItems.BEEKEEPER_BOOTS);
        recipe()
            .inputIsPrimary(TFCTags.Items.TOOLS_KNIFE)
            .input(notRotten(itemOf(FLFood.ROASTED_COCOA_BEANS)))
            .damageInputs()
            .extraProduct(itemOf(FLFood.COCOA_POWDER))
            .shapeless(itemOf(FLFood.COCOA_BUTTER));

        // Masa
        for (int i = 1; i < 9; i++)
        {
            recipe("masa_" + i)
                .input(FluidContentIngredient.of(Fluids.WATER, 100))
                .input(notRotten(itemOf(FLFood.MASA_FLOUR)), i)
                .copyOldestFood()
                .shapeless(new ItemStack(itemOf(FLFood.MASA), i * 2));
        }

        // Devices for TFC woods
        for (Wood wood : Wood.values())
        {
            recipe()
                .input('X', itemOf(wood, Wood.BlockType.PLANKS))
                .input('Y', Tags.Items.STRINGS)
                .pattern("XXX", " Y ", " Y ")
                .shaped(FLBlocks.HANGERS.get(wood));
            recipe()
                .input('X', itemOf(wood, Wood.BlockType.PLANKS))
                .input('Y', TFCItems.LUMBER.get(wood))
                .pattern("XXX", "YYY", "XXX")
                .shaped(FLBlocks.FOOD_SHELVES.get(wood));
            recipe()
                .input('X', itemOf(wood, Wood.BlockType.LOG))
                .input('Y', TFCItems.LUMBER.get(wood))
                .input('Z', brassRods)
                .pattern("X  ", "ZYY", "X  ")
                .shaped(new ItemStack(FLBlocks.JARBNETS.get(wood), 2));
            recipe()
                .input('X', itemOf(wood, Wood.BlockType.LOG))
                .input('Y', FLItems.TREATED_LUMBER)
                .pattern("XYX", "XYX", "XYX")
                .shaped(new ItemStack(FLBlocks.WINE_SHELVES.get(wood), 4));
            recipe()
                .input('X', TFCItems.LUMBER.get(wood))
                .input('G', TFCItems.GLUE)
                .pattern("XGX", "XXX", "GGG")
                .shaped(FLBlocks.STOMPING_BARRELS.get(wood));
            recipe()
                .input(FLBlocks.STOMPING_BARRELS.get(wood))
                .input(wroughtRods)
                .input(wroughtSheets)
                .input(TFCItems.BRASS_MECHANISMS)
                .shapeless(FLBlocks.BARREL_PRESSES.get(wood));
            recipe()
                .input('X', itemOf(wood, Wood.BlockType.LOG))
                .input('Y', FLItems.BARREL_STAVE)
                .input('Z', TFCItems.GLUE)
                .pattern("XYX", "YZY", "XYX")
                .shaped(FLBlocks.KEGS.get(wood));
        }

        Map<Greenhouse, Greenhouse> greenhouseCleaning = new HashMap<>();
        greenhouseCleaning.put(Greenhouse.WEATHERED_TREATED_WOOD, Greenhouse.TREATED_WOOD);
        greenhouseCleaning.put(Greenhouse.RUSTED_IRON, Greenhouse.IRON);
        greenhouseCleaning.put(Greenhouse.OXIDIZED_COPPER, Greenhouse.COPPER);
        greenhouseCleaning.put(Greenhouse.WEATHERED_COPPER, Greenhouse.COPPER);
        greenhouseCleaning.put(Greenhouse.EXPOSED_COPPER, Greenhouse.COPPER);
        for (var entry : greenhouseCleaning.entrySet())
        {
            for (var type : Greenhouse.BlockType.values())
            {
                var dirty = FLBlocks.GREENHOUSE_BLOCKS.get(entry.getKey()).get(type);
                var clean = FLBlocks.GREENHOUSE_BLOCKS.get(entry.getValue()).get(type);
                recipe(nameOf(dirty) + "_cleaning").useTool(TFCTags.Items.TOOLS_CHISEL, dirty, clean);
            }
        }

        greenhouseRecipes(Greenhouse.IRON, itemOf(Metal.WROUGHT_IRON, Metal.ItemType.ROD));
        greenhouseRecipes(Greenhouse.COPPER, itemOf(Metal.COPPER, Metal.ItemType.ROD));
        greenhouseRecipes(Greenhouse.STAINLESS_STEEL, itemOf(FLMetal.STAINLESS_STEEL, FLMetal.ItemType.ROD));
        greenhouseRecipes(Greenhouse.TREATED_WOOD, FLItems.TREATED_LUMBER);

        jarring(TFCItems.COMPOST, FLItems.COMPOST_JAR, 8);
        jarring(TFCItems.ROTTEN_COMPOST, FLItems.ROTTEN_COMPOST_JAR, 8);
        jarring(TFCBlocks.GROUNDCOVER.get(GroundcoverBlockType.GUANO), FLItems.GUANO_JAR, 8);
        jarring(FLItems.FOODS.get(FLFood.RAW_HONEY), FLItems.HONEY_JAR, 1);

        FLItems.FRUIT_PRESERVES.forEach((food, item) ->
            recipe()
                .input(notRotten(Ingredient.of(item)))
                .shapeless(FLItems.UNSEALED_FRUIT_PRESERVES.get(food)));
        FLItems.JAM.forEach((food, item) ->
            recipe()
                .input(FLItems.UNSEALED_FRUIT_PRESERVES.get(food))
                .shapeless(item));

        makeDough(Food.WHEAT_FLOUR, FLFood.WHEAT_DOUGH);
        makeDough(Food.RYE_FLOUR, FLFood.RYE_DOUGH);
        makeDough(Food.BARLEY_FLOUR, FLFood.BARLEY_DOUGH);
        makeDough(Food.RICE_FLOUR, FLFood.RICE_DOUGH);
        makeDough(Food.MAIZE_FLOUR, FLFood.MAIZE_DOUGH);
        makeDough(Food.OAT_FLOUR, FLFood.OAT_DOUGH);

        sliceBread(Food.WHEAT_BREAD, FLFood.WHEAT_SLICE);
        sliceBread(Food.RYE_BREAD, FLFood.RYE_SLICE);
        sliceBread(Food.BARLEY_BREAD, FLFood.BARLEY_SLICE);
        sliceBread(Food.RICE_BREAD, FLFood.RICE_SLICE);
        sliceBread(Food.MAIZE_BREAD, FLFood.MAIZE_SLICE);
        sliceBread(Food.OAT_BREAD, FLFood.OAT_SLICE);

        sandwich(Food.WHEAT_BREAD, FLFood.WHEAT_FLATBREAD, Food.WHEAT_BREAD_SANDWICH, Food.WHEAT_BREAD_JAM_SANDWICH);
        sandwich(Food.RYE_BREAD, FLFood.RYE_FLATBREAD, Food.RYE_BREAD_SANDWICH, Food.RYE_BREAD_JAM_SANDWICH);
        sandwich(Food.BARLEY_BREAD, FLFood.BARLEY_FLATBREAD, Food.BARLEY_BREAD_SANDWICH, Food.BARLEY_BREAD_JAM_SANDWICH);
        sandwich(Food.RICE_BREAD, FLFood.RICE_FLATBREAD, Food.RICE_BREAD_SANDWICH, Food.RICE_BREAD_JAM_SANDWICH);
        sandwich(Food.MAIZE_BREAD, FLFood.MAIZE_FLATBREAD, Food.MAIZE_BREAD_SANDWICH, Food.MAIZE_BREAD_JAM_SANDWICH);
        sandwich(Food.OAT_BREAD, FLFood.OAT_FLATBREAD, Food.OAT_BREAD_SANDWICH, Food.OAT_BREAD_JAM_SANDWICH);

        for (var metal : FLMetal.values())
        {
            recipe()
                .input('S', FLItems.METAL_ITEMS.get(metal).get(FLMetal.ItemType.SHEET))
                .input('W', ItemTags.PLANKS)
                .input('H', TFCTags.Items.TOOLS_HAMMER)
                .pattern(" SH", "SWS", " S ")
                .shaped(FLBlocks.METALS.get(metal).get(Metal.BlockType.BLOCK));
        }

        var pieMod = new MealModifier(
            FoodData.ofFood(4, 1f, 0.5f, 4.5f)
                .grain(1.0f)
                .fruit(1.5f)
                .dairy(0.5f),
            List.of(
                new MealModifier.MealPortion(Optional.empty(), 0.8f, 0.8f, 0.8f)
            )
        );
        var pumpkinMod = new MealModifier(
            FoodData.ofFood(4, 4.0f, 4f, 4f)
                .grain(1f)
                .fruit(1.5f),
            List.of()
        );
        var pizzaMod = new MealModifier(
            FoodData.ofFood(4, 1f, 0f, 4.5f)
                .grain(1.0f)
                .dairy(0.25f),
            List.of(
                new MealModifier.MealPortion(Optional.empty(), 0.8f, 0.8f, 0.8f)
            )
        );
        var burritoMod = new MealModifier(
            FoodData.ofFood(4, 4.0f, 0f, 4.5f),
            List.of(
                new MealModifier.MealPortion(Optional.empty(), 0.8f, 0.8f, 0.8f)
            )
        );
        recipe()
            .inputIsPrimary(notRotten(itemOf(FLFood.PIE_DOUGH)))
            .input(TFCTags.Items.PRESERVES)
            .input(FLTags.Items.PIE_PANS)
            .addOutputModifier(pieMod)
            .addOutputModifier(AddPiePanModifier.INSTANCE)
            .shapeless(FLItems.FILLED_PIE);
        recipe()
            .inputIsPrimary(notRotten(itemOf(FLFood.PUMPKIN_PIE_DOUGH)))
            .input(FLTags.Items.PIE_PANS)
            .addOutputModifier(pumpkinMod)
            .addOutputModifier(AddPiePanModifier.INSTANCE)
            .shapeless(FLItems.RAW_PUMPKIN_PIE);
        recipe("pizza_with_ingredients_2")
            .inputIsPrimary(notRotten(itemOf(FLFood.PIZZA_DOUGH)))
            .input(notRotten(FLTags.Items.PIZZA_INGREDIENTS))
            .input(notRotten(FLTags.Items.PIZZA_INGREDIENTS))
            .input(notRotten(itemOf(FLFood.SHREDDED_CHEESE)))
            .input(notRotten(itemOf(FLFood.TOMATO_SAUCE)))
            .addOutputModifier(pizzaMod)
            .shapeless(FLItems.RAW_PIZZA);
        recipe("pizza_with_ingredients_1")
            .inputIsPrimary(notRotten(itemOf(FLFood.PIZZA_DOUGH)))
            .input(notRotten(FLTags.Items.PIZZA_INGREDIENTS))
            .input(notRotten(itemOf(FLFood.SHREDDED_CHEESE)))
            .input(notRotten(itemOf(FLFood.TOMATO_SAUCE)))
            .addOutputModifier(pizzaMod)
            .shapeless(FLItems.RAW_PIZZA);
        recipe()
            .inputIsPrimary(notRotten(itemOf(FLFood.PIZZA_DOUGH)))
            .input(notRotten(itemOf(FLFood.SHREDDED_CHEESE)))
            .input(notRotten(itemOf(FLFood.TOMATO_SAUCE)))
            .addOutputModifier(pizzaMod)
            .shapeless(FLItems.RAW_PIZZA);
        recipe()
            .inputIsPrimary(notRotten(FLTags.Items.COOKED_MEATS_AND_SUBSTITUTES))
            .input(notRotten(itemOf(FLFood.SHREDDED_CHEESE)))
            .input(notRotten(itemOf(FLFood.CORN_TORTILLA)))
            .input(notRotten(TFCTags.Items.VEGETABLES))
            .input(notRotten(itemOf(FLFood.SALSA)))
            .addOutputModifier(burritoMod)
            .shapeless(itemOf(FLFood.BURRITO));
        recipe()
            .inputIsPrimary(notRotten(FLTags.Items.COOKED_MEATS_AND_SUBSTITUTES))
            .input(notRotten(itemOf(FLFood.SHREDDED_CHEESE)))
            .input(notRotten(itemOf(FLFood.TACO_SHELL)))
            .input(notRotten(TFCTags.Items.VEGETABLES))
            .input(notRotten(itemOf(FLFood.SALSA)))
            .addOutputModifier(burritoMod)
            .shapeless(itemOf(FLFood.TACO));
        recipe()
            .inputIsPrimary(notRotten(itemOf(Food.COOKED_RICE)))
            .input(notRotten(itemOf(Food.DRIED_SEAWEED)))
            .input(notRotten(TFCTags.Items.RAW_FISH))
            .addOutputModifier(burritoMod)
            .shapeless(itemOf(FLFood.MAKI_ROLL));
        recipe()
            .inputIsPrimary(notRotten(itemOf(Food.COOKED_RICE)))
            .input(notRotten(itemOf(Food.DRIED_SEAWEED)))
            .input(notRotten(itemOf(Food.SHELLFISH)))
            .addOutputModifier(burritoMod)
            .shapeless(itemOf(FLFood.FUTO_MAKI_ROLL));
        recipe()
            .inputIsPrimary(notRotten(itemOf(FLFood.COOKED_PASTA)))
            .input(notRotten(itemOf(FLFood.TOMATO_SAUCE)))
            .addOutputModifier(burritoMod)
            .shapeless(itemOf(FLFood.PASTA_WITH_TOMATO_SAUCE));

    }

    private void sandwich(Food loaf, FLFood flatbread, Food sandwich, Food jamSandwich)
    {
        var meal = new MealModifier(
            FoodData.ofFood(4, 1, 0.5f, 4.5f),
            List.of(
                new MealModifier.MealPortion(
                    Optional.of(Ingredient.of(itemOf(loaf))),
                    0.5f,
                    0.5f,
                    0.5f
                ),
                new MealModifier.MealPortion(
                    Optional.empty(),
                    0.8f,
                    0.8f,
                    0.8f
                )
            )
        );
        Map<String, ItemLike> breadVariants = new HashMap<>();
//        breadVariants.put("bread", itemOf(loaf));
        breadVariants.put("flatbread", itemOf(flatbread));

        Map<String, Ingredient> jamVariants = new HashMap<>();
        jamVariants.put("jar", notRotten(TFCTags.Items.PRESERVES));
        jamVariants.put("jam", notRotten(TFCTags.Items.JAM));

        for (var bread : breadVariants.entrySet())
        {
            for (var pattern : List.of("JXX", "XJX", "XXJ"))
            {
                for (var jam : jamVariants.entrySet())
                {
                    recipe(nameOf(itemOf(jamSandwich)) + "_" + nameOf(bread.getValue()) + "_" + jam.getKey() + "_" + pattern.indexOf('J'))
                        .input('K', TFCTags.Items.TOOLS_KNIFE)
                        .input('B', notRotten(bread.getValue()))
                        .input('J', jam.getValue())
                        .input('X', TFCTags.Items.USABLE_IN_JAM_SANDWICH)
                        .pattern("KB ", pattern, " B ")
                        .damageInputs()
                        .addOutputModifier(meal)
                        .shaped(itemOf(jamSandwich), 2);
                }
            }
            recipe(nameOf(itemOf(sandwich)) + "_" + nameOf(bread.getValue()))
                .input('K', TFCTags.Items.TOOLS_KNIFE)
                .input('B', bread.getValue())
                .input('X', TFCTags.Items.USABLE_IN_JAM_SANDWICH)
                .pattern("KB ", "XXX", " B ")
                .damageInputs()
                .addOutputModifier(meal)
                .shaped(itemOf(sandwich), 2);
        }
    }

    private void sliceBread(Food bread, FLFood slicedBread)
    {
        recipe()
            .inputIsPrimary(TFCTags.Items.TOOLS_KNIFE)
            .input(itemOf(bread))
            .damageInputs()
            .shapeless(new ItemStack(itemOf(slicedBread), 2));
    }

    private void makeDough(Food flour, FLFood dough)
    {
        recipe()
            .input(notRotten(itemOf(flour)))
            .input(FluidContentIngredient.of(fluidOf(ExtraFluid.YEAST_STARTER), 100))
            .input(TFCTags.Items.SWEETENERS)
            .shapeless(new ItemStack(itemOf(dough), 4));
    }

    private void jarring(ItemLike unsealed, ItemLike sealed, int amount)
    {
        if (amount == 8)
        {
            recipe("jarring_" + nameOf(unsealed))
                .input('X', unsealed)
                .input('Y', TFCItems.EMPTY_JAR)
                .pattern("XXX", "XYX", "XXX")
                .shaped(sealed);
        }
        else if (amount == 1)
        {
            recipe("jarring_" + nameOf(unsealed))
                .input(TFCItems.EMPTY_JAR)
                .input(unsealed)
                .shapeless(sealed);
        }
        unjarring(sealed, unsealed, amount);
    }

    private void unjarring(ItemLike sealed, ItemLike unsealed, int amount)
    {
        unjarring(Ingredient.of(sealed), unsealed, amount);
    }

    private void unjarring(Ingredient sealed, ItemLike unsealed, int amount)
    {
        recipe("unjarring_" + nameOf(unsealed))
            .input(sealed)
            .shapeless(new ItemStack(unsealed, amount));
    }

    private void greenhouseRecipes(Greenhouse type, ItemLike material)
    {
        recipe()
            .input('X', material)
            .input('Y', Items.GLASS)
            .pattern("XYX", "XYX", "XYX")
            .shaped(new ItemStack(FLBlocks.GREENHOUSE_BLOCKS.get(type).get(Greenhouse.BlockType.WALL), 8));
        recipe()
            .input('X', material)
            .input('Y', Items.GLASS)
            .pattern("XYX", "YXY")
            .shaped(new ItemStack(FLBlocks.GREENHOUSE_BLOCKS.get(type).get(Greenhouse.BlockType.ROOF_TOP), 8));
        recipe()
            .input('X', material)
            .input('Y', Items.GLASS)
            .pattern("Y  ", "XY ", "XXY")
            .shaped(new ItemStack(FLBlocks.GREENHOUSE_BLOCKS.get(type).get(Greenhouse.BlockType.ROOF), 4));
        recipe()
            .input('X', material)
            .input('Y', Items.GLASS)
            .pattern("XY", "XY", "XY")
            .shaped(new ItemStack(FLBlocks.GREENHOUSE_BLOCKS.get(type).get(Greenhouse.BlockType.DOOR), 2));
        recipe()
            .input('X', material)
            .input('Y', FLItems.REINFORCED_GLASS)
            .pattern("XYX", "YXY")
            .shaped(new ItemStack(FLBlocks.GREENHOUSE_BLOCKS.get(type).get(Greenhouse.BlockType.TRAPDOOR), 8));
        recipe()
            .input('X', material)
            .input('Y', FLItems.REINFORCED_GLASS)
            .pattern("Y  ", "XY ", "XXY")
            .shaped(new ItemStack(FLBlocks.GREENHOUSE_BLOCKS.get(type).get(Greenhouse.BlockType.PANEL_ROOF), 4));
        recipe()
            .input('X', material)
            .input('Y', FLItems.REINFORCED_GLASS)
            .pattern("XYX", "XYX", "XYX")
            .shaped(new ItemStack(FLBlocks.GREENHOUSE_BLOCKS.get(type).get(Greenhouse.BlockType.PANEL_WALL), 8));
        recipe()
            .input('X', FLBlocks.GREENHOUSE_BLOCKS.get(type).get(Greenhouse.BlockType.WALL))
            .input('Y', FLBlocks.COPPER_PIPE)
            .pattern("XY")
            .shaped(new ItemStack(FLBlocks.GREENHOUSE_BLOCKS.get(type).get(Greenhouse.BlockType.PORT), 8));
    }

    private void decorationRecipe(ItemLike input, DecorationBlockHolder deco)
    {
        recipe()
            .input('X', input)
            .pattern("XXX")
            .shaped(deco.slab(), 6);
        recipe()
            .input('X', input)
            .pattern("X  ", "XX ", "XXX")
            .shaped(deco.stair(), 8);
        recipe()
            .input('X', input)
            .pattern("XXX", "XXX")
            .shaped(deco.wall(), 6);
    }

    private DataGenerationHelpers.Builder recipe(String recipeName)
    {
        return new DataGenerationHelpers.Builder((name, r) -> {
            if (name != null) add(name + "_" + recipeName, r);
            else if (recipeName != null) add(recipeName, r);
            else add(r);
        });
    }

    private DataGenerationHelpers.Builder recipe()
    {
        return new DataGenerationHelpers.Builder((name, r) -> {
            if (name != null) add(name, r);
            else add(r);
        });
    }

    enum Grains
    {
        WHEAT(Food.WHEAT_GRAIN, Food.WHEAT_FLOUR, Food.WHEAT_DOUGH, Food.WHEAT_BREAD, FLFood.WHEAT_SLICE),
        RYE(Food.WHEAT_GRAIN, Food.WHEAT_FLOUR, Food.WHEAT_DOUGH, Food.WHEAT_BREAD, FLFood.WHEAT_SLICE),
        BARLEY(Food.WHEAT_GRAIN, Food.WHEAT_FLOUR, Food.WHEAT_DOUGH, Food.WHEAT_BREAD, FLFood.WHEAT_SLICE),
        RICE(Food.WHEAT_GRAIN, Food.WHEAT_FLOUR, Food.WHEAT_DOUGH, Food.WHEAT_BREAD, FLFood.WHEAT_SLICE),
        MAIZE(Food.WHEAT_GRAIN, Food.WHEAT_FLOUR, Food.WHEAT_DOUGH, Food.WHEAT_BREAD, FLFood.WHEAT_SLICE),
        OAT(Food.WHEAT_GRAIN, Food.WHEAT_FLOUR, Food.WHEAT_DOUGH, Food.WHEAT_BREAD, FLFood.WHEAT_SLICE);

        private Grains(Food grain, Food flour, Food dough, Food bread, FLFood breadSlice)
        {

        }
    }
}
