package com.eerussianguy.firmalife.common.items;

import net.dries007.tfc.common.capabilities.food.FoodTrait;
import net.dries007.tfc.util.Helpers;

public class FLFoodTraits
{
    public static void init() { }

    public static final FoodTrait DRIED = register("dried", 0.5f);

    private static FoodTrait register(String name, float mod)
    {
        return FoodTrait.register(Helpers.identifier(name), new FoodTrait(mod, "firmalife.tooltip.food_trait." + name));
    }
}
