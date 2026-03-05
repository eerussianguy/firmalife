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
        add(FLFood.YAK_CURD, FoodData.of(3f));
        add(FLFood.GOAT_CURD, FoodData.of(3f));
        add(FLFood.MILK_CURD, FoodData.of(3f));
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
        add(FLFood.TOFU, new FoodData(4, 2f, 0.75f, 0, new float[] {0f, 0f, 1.5f, 1f, 0f}, 2f));
        add(FLTags.Items.BREAD_SLICES, new FoodData(4, 0f, 1.5f, 0, new float[] {1f, 0f, 0f, 0f, 0f}, 0.75f), true);
        add(FLFood.TOAST, new FoodData(4, 0f, 1f, 0, new float[] {1f, 0f, 0f, 0f, 0f}, 1.5f));
        add(FLFood.TOAST_WITH_JAM, new FoodData(4, 1f, 2f, 0, new float[] {1f, 0f, 0f, 0f, 0.75f}, 2f));
        add(FLFood.TOAST_WITH_BUTTER, new FoodData(4, 1f, 2f, 0, new float[] {1f, 0f, 0f, 0.25f, 0f}, 2f));
        add(FLFood.BACON, new FoodData(4, 0f, 2f, 0, new float[] {0f, 0f, 0.5f, 0f, 0f}, 0f));
        add(FLFood.COOKED_BACON, new FoodData(4, 0f, 2f, 0, new float[] {0f, 0f, 0.75f, 0f, 0f}, 2f));
        add(FLFood.GARLIC_BREAD, new FoodData(4, 0f, 2f, 0, new float[] {1f, 1f, 0f, 0.1f, 0f}, 2f));
        add(FLFood.COOKED_LASAGNA, new FoodData(4, 1f, 2f, 0, new float[] {1f, 1f, 1f, 0f, 0f}, 3f));
        add(FLTags.Items.FLATBREADS, new FoodData(4, 0f, 1f, 0, new float[] {0.5f, 0f, 0f, 0f, 0f}, 0.75f), true);
        add(FLTags.Items.CHEESES, new FoodData(4, 0f, 0.3f, 0, new float[] {0f, 0f, 0f, 3f, 0f}, 2f), true);
        add(FLFood.SHREDDED_CHEESE, new FoodData(4, 0f, 0.3f, 0, new float[] {0f, 0f, 0f, 0.75f, 0f}, 2f));
        add(FLFood.PICKLED_EGG, new FoodData(4, 10f, 0.3f, 0, new float[] {0f, 0f, 1.5f, 0.25f, 0f}, 2f));
        add(FLTags.Items.CHOCOLATE, new FoodData(4, 0f, 0.3f, 0, new float[] {0.5f, 0f, 0f, 0.5f, 0f}, 1f), true);
        add(FLFood.CHOCOLATE_CHIP_COOKIE, new FoodData(4, 0f, 4.5f, 0, new float[] {1.0f, 0f, 0f, 0.2f, 0f}, 0.5f));
        add(FLFood.SUGAR_COOKIE, new FoodData(4, 0f, 4.5f, 0, new float[] {0.8f, 0f, 0f, 0.1f, 0f}, 0.5f));
        add(FLFood.HARDTACK, new FoodData(4, 0f, 0.02f, 0, new float[] {0.2f, 0f, 0f, 0f, 0f}, 0.1f));
        add(FLTags.Items.GRAPES, new FoodData(4, 2f, 2.5f, 0, new float[] {0f, 0f, 0f, 0f, 0.5f}, 0.5f), true);
        add(FLFood.CORN_TORTILLA, new FoodData(4, 0f, 0.8f, 0, new float[] {0.6f, 0f, 0f, 0f, 0f}, 1f));
        add(FLFood.TACO_SHELL, new FoodData(4, 0f, 0.8f, 0, new float[] {0.6f, 0f, 0f, 0f, 0f}, 1f));
        add(FLFood.TORTILLA_CHIPS, new FoodData(4, 0f, 0.8f, 0, new float[] {0.7f, 0f, 0f, 0f, 0f}, 1.2f));
        add(FLFood.NACHOS, new FoodData(4, 0f, 0.8f, 0, new float[] {0.7f, 0.7f, 0.75f, 0.5f, 0f}, 1.2f));
        add(FLFood.TOMATO_SAUCE, new FoodData(1, 1f, 1f, 0, new float[] {0f, 0.75f, 0f, 0f, 0f}, 1f));
        add(FLFood.SALSA, new FoodData(1, 0.8f, 1f, 0, new float[] {0f, 0.5f, 0f, 0f, 0f}, 1f));
        add(FLFruit.PINEAPPLE, new FoodData(4, 1f, 0.85f, 0, new float[] {0f, 0f, 0f, 0f, 0.75f}, 1f));
        add(FLItems.NIGHTSHADE_BERRY, new FoodData(4, 1f, 0.85f, 0, new float[] {0f, 0f, 0f, 0f, 3.0f}, 1f));
        add(FLFood.VANILLA_ICE_CREAM, new FoodData(4, 1f, 5f, 0, new float[] {0f, 0f, 0f, 0.75f, 0f}, 1f));
        add(FLFood.COOKIE_DOUGH_ICE_CREAM, new FoodData(4, 1f, 5f, 0, new float[] {0.5f, 0f, 0f, 1.0f, 0f}, 1f));
        add(FLFood.CHOCOLATE_ICE_CREAM, new FoodData(4, 1f, 5f, 0, new float[] {0.25f, 0f, 0f, 0.5f, 0f}, 1.5f));
        add(FLFood.STRAWBERRY_ICE_CREAM, new FoodData(4, 1f, 5f, 0, new float[] {0f, 0f, 0f, 0.5f, 0.5f}, 1.5f));
        add(FLFood.BANANA_SPLIT, new FoodData(4, 1f, 5f, 0, new float[] {0.25f, 0f, 0f, 1.75f, 3.5f}, 2f));
        add(FLFruit.FIG, new FoodData(4, 5f, 0.8f, 0, new float[] {0f, 0f, 0f, 0f, 0.9f}, 1f));
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
