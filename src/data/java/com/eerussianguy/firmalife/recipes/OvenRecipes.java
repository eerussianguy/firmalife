package com.eerussianguy.firmalife.recipes;

import com.eerussianguy.firmalife.common.items.FLFood;
import com.eerussianguy.firmalife.common.items.FLItems;
import com.eerussianguy.firmalife.common.recipes.OvenRecipe;
import com.eerussianguy.firmalife.common.recipes.data.CopyDynamicFoodModifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;

import net.dries007.tfc.common.items.Food;
import net.dries007.tfc.common.recipes.outputs.ItemStackProvider;

public interface OvenRecipes extends Recipes
{
    default void ovenRecipes()
    {
        cook(
            notRotten(FLItems.FILLED_PIE),
            ItemStackProvider.of(
                new ItemStack(itemOf(FLFood.COOKED_PIE)),
                CopyDynamicFoodModifier.INSTANCE
            ),
            400,
            1000
        );
        cook(
            notRotten(FLItems.RAW_PIZZA),
            ItemStackProvider.of(
                new ItemStack(itemOf(FLFood.COOKED_PIZZA)),
                CopyDynamicFoodModifier.INSTANCE
            ),
            400,
            1000
        );
        cook(
            notRotten(FLItems.RAW_PUMPKIN_PIE),
            ItemStackProvider.of(
                new ItemStack(Items.PUMPKIN_PIE),
                CopyDynamicFoodModifier.INSTANCE
            ),
            400,
            1000
        );
        cook(
            notRotten(itemOf(FLFood.COCOA_BEANS)),
            itemOf(FLFood.ROASTED_COCOA_BEANS),
            400,
            1000
        );
        cook(
            notRotten(itemOf(FLFood.CORN_TORTILLA)),
            itemOf(FLFood.TACO_SHELL),
            400,
            1000
        );
        cook(
            notRotten(itemOf(FLFood.COOKIE_DOUGH)),
            itemOf(FLFood.SUGAR_COOKIE),
            400,
            1000
        );
        cook(
            notRotten(itemOf(FLFood.CHOCOLATE_CHIP_COOKIE_DOUGH)),
            itemOf(FLFood.CHOCOLATE_CHIP_COOKIE),
            400,
            1000
        );
        cook(
            notRotten(itemOf(FLFood.HARDTACK_DOUGH)),
            itemOf(FLFood.HARDTACK),
            400,
            1000
        );
        cook(
            notRotten(itemOf(FLFood.RAW_LASAGNA)),
            itemOf(FLFood.COOKED_LASAGNA),
            400,
            1000
        );
        cook(
            notRotten(itemOf(FLFood.WHEAT_DOUGH)),
            itemOf(Food.WHEAT_BREAD),
            200,
            1000
        );
        cook(
            notRotten(itemOf(FLFood.RYE_DOUGH)),
            itemOf(Food.RYE_BREAD),
            200,
            1000
        );
        cook(
            notRotten(itemOf(FLFood.BARLEY_DOUGH)),
            itemOf(Food.BARLEY_BREAD),
            200,
            1000
        );
        cook(
            notRotten(itemOf(FLFood.RICE_DOUGH)),
            itemOf(Food.RICE_BREAD),
            200,
            1000
        );
        cook(
            notRotten(itemOf(FLFood.MAIZE_DOUGH)),
            itemOf(Food.MAIZE_BREAD),
            200,
            1000
        );
        cook(
            notRotten(itemOf(FLFood.OAT_DOUGH)),
            itemOf(Food.OAT_BREAD),
            200,
            1000
        );
    }

    private void cook(Ingredient input, ItemLike output, int temperature, int time)
    {
        cook(input, ItemStackProvider.of(output), temperature, time);
    }

    private void cook(Ingredient input, ItemStackProvider output, int temperature, int time)
    {
        add(new OvenRecipe(input, output, temperature, time));
    }

}
