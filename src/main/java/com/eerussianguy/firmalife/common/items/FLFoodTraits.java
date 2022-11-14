package com.eerussianguy.firmalife.common.items;

import com.eerussianguy.firmalife.common.FLHelpers;
import net.dries007.tfc.common.capabilities.food.FoodTrait;

public class FLFoodTraits
{
    public static void init() { }

    public static final FoodTrait DRIED = register("dried", 0.5f);
    public static final FoodTrait FRESH = register("fresh", 1.1f);
    public static final FoodTrait AGED = register("aged", 0.9f);
    public static final FoodTrait VINTAGE = register("vintage", 0.6f);
    public static final FoodTrait OVEN_BAKED = register("oven_baked", 0.9f);
    public static final FoodTrait SMOKED = register("smoked", 0.7f);
    public static final FoodTrait RANCID_SMOKED = register("rancid_smoked", 2.0f);
    public static final FoodTrait RAW = register("raw", 1f);
    public static final FoodTrait SHELVED = register("shelved", 0.4f);
    public static final FoodTrait HUNG = register("hung", 0.35f);

    private static FoodTrait register(String name, float mod)
    {
        return FoodTrait.register(FLHelpers.identifier(name), new FoodTrait(mod, "firmalife.tooltip.food_trait." + name));
    }
}
