package com.eerussianguy.firmalife.init;

import net.dries007.tfc.api.capability.food.FoodData;
import net.dries007.tfc.api.capability.food.FoodTrait;

public class FoodDataFL
{
    public static final FoodData CHOCOLATE = new FoodData(4, 0.0F, 0.2F, 0.2F, 0.0F, 0.0F, 0.0F, 0.8F, 1.1F);
    public static final FoodData COCOA_BEANS = new FoodData(4, 0.0F, 0.1F, 0.1F, 0.0F, 0.0F, 0.1F, 0.0F, 0.5F);
    //copied from squash
    public static final FoodData PUMPKIN = new FoodData(4, 1F, 0F, 0F, 1.4F, 0F, 0F, 0F, 1.8F);

    public static final FoodData DRIED_COCOA_BEANS = new FoodData(4, 0.0F, 0.1F, 0.1F, 0.0F, 0.0F, 0.1F, 0.0F, 0.5F);

    public static final FoodData MILK_CURD = new FoodData(4, 0F, 1F, 0F, 0F, 0F, 0F, 2F, 3.0F);
    public static final FoodData CHEESE_BRINED = new FoodData(4, 0F, 2F, 0F, 0F, 0F, 0F, 3F, 0.3F);
    public static final FoodData CHEESE_SALTED = new FoodData(4, 0F, 2F, 0F, 0F, 0F, 0F, 3F, 0.3F);

    public static final FoodData DRIED_FRUIT_SATURATION = new FoodData(4, 0F, 0.5F, 0F, 0.75F, 0F, 0F, 0F, 0.8F);
    public static final FoodData DRIED_FRUIT_DECAY = new FoodData(4, 0F, 0.2F, 0F, 1F, 0F, 0F, 0F, 0.8F);
    public static final FoodData DRIED_FRUIT_CATEGORY = new FoodData(4, 0F, 0.2F, 0F, 0.75F, 0F, 0F, 0F, 0.8F);

    public static final FoodData UNCRACKED_NUT = new FoodData(4, 0.0F, 0.1F, 0.1F, 0.0F, 0.0F, 0.0F, 0.0F, 0.4F);
    public static final FoodData NUT = new FoodData(4, 0.0F, 0.5F, 0.9F, 0.0F, 0.0F, 0.6F, 0.0F, 1.0F);
    public static final FoodData ROASTED_NUT = new FoodData(4, 0.0F, 1.0F, 1.2F, 0.0F, 0.0F, 0.8F, 0.0F, 1.1F);

    public static final FoodData DOUGH = new FoodData(4, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 3.0F);
    public static final FoodData FLOUR = new FoodData(4, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.5F);
    public static final FoodData FLATBREAD = new FoodData(4, 1.0F, 0.0F, 1.0F, 0.0F, 0.0F, 0.0F, 0.0F, 1.0F);
    public static final FoodData PIZZA = new FoodData(4, 1.0F, 0.0F, 1.0F, 0.0F, 0.5F, 0.0F, 0.0F, 1.0F);
    public static final FoodData SLICE = new FoodData(4, 0.0F, 0.75F, 1.0F, 0.0F, 0.0F, 0.0F, 0.0F, 1.5F);
    public static final FoodData TOAST = new FoodData(4, 0.0F, 1.5F, 1.0F, 0.0F, 0.0F, 0.0F, 0.0F, 1.8F);
    public static final FoodData SANDWICH = new FoodData(4, 0.0F, 3.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 5F);
    public static final FoodData TRAIL_MIX = new FoodData(4, 0.0F, 1.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.9F);
    public static final FoodData GROUND_SOYBEANS = new FoodData(4, 0.0F, 0.5F, 0.0F, 0.0F, 0.5F, 1.0F, 0.0F, 2.5F);
    public static final FoodData TOFU = new FoodData(4, 0.8F, 2.0F, 0.0F, 0.0F, 0.5F, 1.5F, 0.0F, 2.0F);

    public static final FoodData GARLIC_BREAD = new FoodData(4, 0.5F, 0.75F, 0.5F, 0.0F, 2.0F, 0.0F, 0.0F, 0.5F);
    public static final FoodData PICKLED_EGG = new FoodData(4, 0.0F, 0.5F, 0.0F, 0.0F, 0.0F, 0.75F, 0.25F, 0.75F);

    public static final FoodData PINEAPPLE = new FoodData(4, 0.5F, 4.1F, 0.0F, 0.75F, 0.0F, 0.0F, 0.0F, 4.9F);
    public static final FoodData MELON = new FoodData(4, 1.5F, 1.0F, 0.0F, 1.0F, 0.0F, 0.0F, 0.0F, 4.9F);

    public static final FoodTrait SMOKED = new FoodTrait("smoked", 0.25F);
    public static final FoodTrait FRESH = new FoodTrait("fresh", 1.4F); // These should eventually do something besides just modifying decay rate, for now they're here as an incomplete feature
    public static final FoodTrait AGED = new FoodTrait("aged", 1.0F);
    public static final FoodTrait VINTAGE = new FoodTrait("vintage", 0.6F);
}

