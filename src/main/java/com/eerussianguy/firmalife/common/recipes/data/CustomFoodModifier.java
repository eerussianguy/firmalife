package com.eerussianguy.firmalife.common.recipes.data;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.world.inventory.CraftingContainer;

import net.dries007.tfc.common.capabilities.food.*;
import net.dries007.tfc.common.recipes.outputs.ItemStackModifier;

public interface CustomFoodModifier<T extends ItemStackModifier> extends ContainerAwareModifier<T>
{
     int G = Nutrient.GRAIN.ordinal();
     int F = Nutrient.FRUIT.ordinal();
     int V = Nutrient.VEGETABLES.ordinal();
     int P = Nutrient.PROTEIN.ordinal();
     int D = Nutrient.DAIRY.ordinal();

    default List<FoodData> gatherIngredients(CraftingContainer inv, FoodHandler.Dynamic dynamic)
    {
        List<FoodData> ingredients = new ArrayList<>(5);
        for (int i = 0; i < inv.getContainerSize(); i++)
        {
            inv.getItem(i).getCapability(FoodCapability.CAPABILITY).map(IFood::getData).ifPresent(ingredients::add);
        }
        return ingredients;
    }

    @Override
    default void initFoodStats(CraftingContainer inv, FoodHandler.Dynamic handler)
    {
        final List<FoodData> ingredients = gatherIngredients(inv, handler);
        float[] nutrition = freshNutrients();
        nutrition = nutrients(nutrition);
        float saturation = saturation();
        float water = water();
        for (FoodData ingredient : ingredients)
        {
            for (Nutrient nutrient : Nutrient.VALUES)
            {
                nutrition[nutrient.ordinal()] += nutrientModifier() * ingredient.nutrient(nutrient);
            }
            saturation += nutrientModifier() * ingredient.saturation();
            water += nutrientModifier() * ingredient.water();
        }
        handler.setFood(FoodData.create(4, water, saturation, nutrition, decay()));
        handler.setCreationDate(FoodCapability.getRoundedCreationDate());
    }

    default float saturation()
    {
        return 0.5f;
    }

    default float water()
    {
        return 0f;
    }

    default float[] nutrients(float[] input)
    {
        return input;
    }

    default float decay()
    {
        return 1.5f;
    }

    default float nutrientModifier()
    {
        return 0.8f;
    }
}
