package com.eerussianguy.firmalife.providers;

import java.util.concurrent.CompletableFuture;
import com.eerussianguy.firmalife.Accessors;
import com.eerussianguy.firmalife.common.FLTags;
import com.eerussianguy.firmalife.common.items.FLFood;
import com.eerussianguy.firmalife.common.items.FLItems;
import com.eerussianguy.firmalife.common.util.FLFruit;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;

import net.dries007.tfc.TerraFirmaCraft;
import net.dries007.tfc.common.component.food.FoodCapability;
import net.dries007.tfc.common.component.food.FoodData;
import net.dries007.tfc.common.component.food.FoodDefinition;
import net.dries007.tfc.common.items.Food;
import net.dries007.tfc.util.Helpers;

import static net.dries007.tfc.common.component.food.FoodData.*;

public class BuiltinFoods extends DataManagerProvider<FoodDefinition> implements Accessors
{
    public BuiltinFoods(PackOutput output, CompletableFuture<HolderLookup.Provider> lookup)
    {
        super(FoodCapability.MANAGER, output, lookup, TerraFirmaCraft.MOD_ID);
    }

    @Override
    protected void addData(HolderLookup.Provider provider)
    {
        add(FLFood.FROTHY_COCONUT, FoodData.of(3f));
        add(FLFood.SOY_MIXTURE, FoodData.of(3f));
        add(FLFood.YAK_CURD, FoodData.of(3f).dairy(0.2f));
        add(FLFood.GOAT_CURD, FoodData.of(3f).dairy(0.2f));
        add(FLFood.MILK_CURD, FoodData.of(3f).dairy(0.2f));
        add(FLFood.DARK_CHOCOLATE_BLEND, FoodData.of(3f));
        add(FLFood.MILK_CHOCOLATE_BLEND, FoodData.of(3f));
        add(FLFood.WHITE_CHOCOLATE_BLEND, FoodData.of(3f));
        add(FLTags.Items.SMASHED_GRAPES, FoodData.of(2.5f), true);
        add(FLFood.COOKIE_DOUGH, FoodData.of(3f));
        add(FLFood.HARDTACK_DOUGH, FoodData.of(3f));
        add(FLFood.CHOCOLATE_CHIP_COOKIE_DOUGH, FoodData.of(3f));
        add(FLFood.WHEAT_DOUGH, FoodData.of(3f));
        add(FLFood.OAT_DOUGH, FoodData.of(3f));
        add(FLFood.RYE_DOUGH, FoodData.of(3f));
        add(FLFood.BARLEY_DOUGH, FoodData.of(3f));
        add(FLFood.RICE_DOUGH, FoodData.of(3f));
        add(FLFood.MAIZE_DOUGH, FoodData.of(3f));
        add(FLFood.BUTTER, FoodData.of(3f));
        add(FLFood.PIE_DOUGH, FoodData.of(3f));
        add(FLFood.PIZZA_DOUGH, FoodData.of(3f));
        add(FLFood.PUMPKIN_PIE_DOUGH, FoodData.of(3f));
        add(FLFood.COCOA_BEANS, FoodData.of(0.25f));
        add(FLFood.RAW_EGG_NOODLES, FoodData.of(0.25f));
        add(FLFood.RAW_RICE_NOODLES, FoodData.of(0.25f));
        add(FLFood.RAW_LASAGNA, FoodData.of(3f));
        add(FLFood.ROASTED_COCOA_BEANS, FoodData.of(3f));
        add(FLFood.COCOA_POWDER, FoodData.of(0.25f));
        add(FLFood.COCOA_BUTTER, FoodData.of(0.25f));
        add(FLFood.CURED_MAIZE, FoodData.of(1.0f));
        add(FLFood.TOMATO_SAUCE_MIX, FoodData.of(2.0f));
        add(FLFood.NIXTAMAL, FoodData.of(0.3f));
        add(FLFood.MASA_FLOUR, FoodData.of(0.8f));
        add(FLFood.SPICED_FLOUR, FoodData.of(0.8f));
        add(FLFood.MASA, FoodData.of(2.0f));
        add(FLFood.DEHYDRATED_SOYBEANS, FoodData.of(0.5f));
        add(FLFood.SOYBEAN_PASTE, FoodData.of(0.6f));
        add(FLFood.RAW_HONEY, FoodData.of(0.6f));
        add(FLFood.FLAVORFUL_COOKED_RICE, ofFood(2.5f, 5, 1).grain(1.7f));
        add(FLFood.TOFU, ofFood(0.75f, 2f, 2f).vegetables(1.5f).protein(1f));
        add(FLTags.Items.BREAD_SLICES, ofFood(1.5f, 0f, 0.75f).grain(1f), true);
        add(FLFood.TOAST, ofFood(1f, 0f, 1.5f).grain(1f));
        add(FLFood.TOAST_WITH_JAM, ofFood(2f, 1f, 2f).grain(1f).fruit(0.75f));
        add(FLFood.TOAST_WITH_BUTTER, ofFood(2f, 1f, 2f).grain(1f).dairy(0.25f));
        add(FLFood.BACON, ofFood(2f, 0f, 0f).protein(0.5f));
        add(FLFood.COOKED_BACON, ofFood(2f, 0f, 2f).protein(0.75f));
        add(FLFood.GARLIC_BREAD, ofFood(2f, 0f, 2f).grain(1f).vegetables(1f).dairy(0.1f));
        add(FLFood.COOKED_LASAGNA, ofFood(2f, 1f, 3f).grain(1f).vegetables(1f).dairy(1f));
        add(FLTags.Items.FLATBREADS, ofFood(1f, 0f, 0.75f).grain(0.5f), true);
        add(FLTags.Items.CHEESES, ofFood(0.3f, 0f, 2f).dairy(3f), true);
        add(FLFood.SHREDDED_CHEESE, ofFood(0.3f, 0f, 2f).dairy(0.75f));
        add(FLFood.PICKLED_EGG, ofFood(0.3f, 10f, 2f).vegetables(0.25f).protein(1.5f));
        add(FLTags.Items.CHOCOLATE, ofFood(0.3f, 0f, 1f).grain(0.5f).dairy(0.5f), true);
        add(FLFood.CHOCOLATE_CHIP_COOKIE, ofFood(4.5f, 0f, 0.5f).grain(1f).protein(0.2f));
        add(FLFood.SUGAR_COOKIE, ofFood(4.5f, 0f, 0.5f).grain(0.8f).protein(0.1f));
        add(FLFood.HARDTACK, ofFood(0.02f, 0f, 0.1f).grain(0.2f));
        add(FLTags.Items.GRAPES, ofFood(2.5f, 2f, 0.5f).fruit(0.5f), true);
        add(FLFood.CORN_TORTILLA, ofFood(0.8f, 0f, 1f).grain(0.6f));
        add(FLFood.TACO_SHELL, ofFood(0.8f, 0f, 1f).grain(0.6f));
        add(FLFood.TORTILLA_CHIPS, ofFood(0.8f, 0f, 1.2f).grain(0.7f));
        add(FLFood.NACHOS, ofFood(0.8f, 0f, 1.2f).grain(0.7f).vegetables(1.45f).dairy(0.5f).protein(0.5f));
        add(FLFood.TOMATO_SAUCE, ofFood(1, 1f, 1f, 1f).fruit(0.75f));
        add(FLFood.SALSA, ofFood(1, 1f, 0.8f, 1f).fruit(0.5f));
        add(FLFruit.PINEAPPLE, ofFood(0.85f, 1f, 1f).fruit(0.75f));
        add(FLItems.NIGHTSHADE_BERRY, ofFood(0.85f, 1f, 1f).fruit(3f));
        add(FLFood.VANILLA_ICE_CREAM, ofFood(5f, 1f, 1f).dairy(0.75f));
        add(FLFood.COOKIE_DOUGH_ICE_CREAM, ofFood(5f, 1f, 1f).grain(0.5f).dairy(1f));
        add(FLFood.CHOCOLATE_ICE_CREAM, ofFood(5f, 1f, 1.5f).grain(0.25f).dairy(0.5f));
        add(FLFood.STRAWBERRY_ICE_CREAM, ofFood(5f, 1f, 1.5f).fruit(0.5f).dairy(0.5f));
        add(FLFood.BANANA_SPLIT, ofFood(5f, 1f, 2f).grain(0.25f).fruit(1.75f).dairy(3.5f));
        add(FLFruit.FIG, ofFood(0.8f, 5f, 1f).fruit(0.9f));
        add(FLItems.FILLED_PIE, FoodData.of(4.5f));
        add(FLItems.RAW_PUMPKIN_PIE, FoodData.of(4.5f));
        add(FLFood.COOKED_PIE, FoodData.of(4.5f));
        add(FLItems.STINKY_SOUP, FoodData.of(4.5f));
        add(FLFood.COOKED_PASTA, FoodData.of(4.5f));
        add(FLFood.COOKED_RICE_NOODLES, FoodData.of(4.5f));
        add(FLFood.PASTA_WITH_TOMATO_SAUCE, FoodData.of(4.5f));
        add(FLItems.RAW_PIZZA, FoodData.of(4.5f));
        add(FLFood.COOKED_PIZZA, FoodData.of(4.5f));
        add(FLFood.BURRITO, FoodData.of(4.5f));
        add(FLFood.RICE_PILAF, FoodData.of(4.5f));
        add(FLFood.CARNE_ASADA, FoodData.of(4.5f));
        add(FLFood.TACO, FoodData.of(4.5f));
        add(FLFood.MAKI_ROLL, FoodData.of(4.5f));
        add(FLFood.FUTO_MAKI_ROLL, FoodData.of(4.5f));
        //i dont think i needed this?
//        add(Helpers.identifier("pumpkin_pie"), new FoodDefinition(Ingredient.of(Items.PUMPKIN_PIE), FoodData.of(4.5f), true));
    }

    private void add(FLFood item, FoodData food)
    {
        add(FLItems.FOODS.get(item), food);
    }

    private void add(FLFruit item, FoodData food)
    {
        add(FLItems.FRUITS.get(item), food);
    }

    private void add(ItemLike item, FoodData food)
    {
        add(item, food, true);
    }

    private void add(ItemLike item, FoodData food, boolean edible)
    {
        add(nameOf(item).replace("food/", ""), new FoodDefinition(Ingredient.of(item), food, edible));
    }

    private void add(TagKey<Item> tag, FoodData food, boolean edible)
    {
        add(tag.location().getPath().replace("foods/", ""), new FoodDefinition(Ingredient.of(tag), food, edible));
    }

}
