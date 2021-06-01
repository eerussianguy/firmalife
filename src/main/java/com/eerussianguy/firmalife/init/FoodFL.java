package com.eerussianguy.firmalife.init;

import net.dries007.tfc.api.capability.food.FoodData;

public enum FoodFL
{
    DARK_CHOCOLATE(FoodDataFL.CHOCOLATE, new String[] {"chocolate"}, false),
    MILK_CHOCOLATE(FoodDataFL.CHOCOLATE, new String[] {"chocolate"}, false),
    WHITE_CHOCOLATE(FoodDataFL.CHOCOLATE, new String[] {"chocolate"}, false),
    COCOA_BEANS(FoodDataFL.COCOA_BEANS),
    PUMPKIN_SCOOPED(FoodDataFL.PUMPKIN),
    PUMPKIN_CHUNKS(FoodDataFL.PUMPKIN),
    PICKLED_EGG(FoodDataFL.PICKLED_EGG),
    DRIED_COCOA_BEANS(FoodDataFL.DRIED_COCOA_BEANS),
    TOAST(FoodDataFL.TOAST),
    GARLIC_BREAD(FoodDataFL.GARLIC_BREAD),
    TOMATO_SAUCE(FoodDataFL.DOUGH),
    CHESTNUT_BREAD(FoodDataFL.FLATBREAD, new String[] {"categoryGrain", "categoryBread"}, true),
    PINEAPPLE(FoodDataFL.COCOA_BEANS),
    PINEAPPLE_CHUNKS(FoodDataFL.PINEAPPLE),
    ACORN_FRUIT(FoodDataFL.NUT, new String[] {"nut"}, false),
    ACORNS(FoodDataFL.UNCRACKED_NUT),
    CHESTNUTS(FoodDataFL.UNCRACKED_NUT),
    ROASTED_CHESTNUTS(FoodDataFL.ROASTED_NUT, new String[] {"nut"}, false),
    CHESTNUT_DOUGH(FoodDataFL.DOUGH),
    CHESTNUT_FLOUR(FoodDataFL.FLOUR),
    PECAN_NUTS(FoodDataFL.UNCRACKED_NUT),
    PECANS(FoodDataFL.NUT, new String[] {"nut"}, false),
    PINECONE(FoodDataFL.UNCRACKED_NUT),
    PINE_NUTS(FoodDataFL.NUT, new String[] {"nut"}, false),
    COCONUT(FoodDataFL.UNCRACKED_NUT),
    MELON(FoodDataFL.MELON),
    TOFU(FoodDataFL.TOFU),
    GROUND_SOYBEANS(FoodDataFL.GROUND_SOYBEANS),
    RAW_HONEY(FoodDataFL.DOUGH, new String[] {"sweetener"}, false),
    MILK_CURD(FoodDataFL.MILK_CURD, new String[] {"categoryDairy"}, false),
    GOAT_CURD(FoodDataFL.MILK_CURD, new String[] {"categoryDairy"}, false),
    YAK_CURD(FoodDataFL.MILK_CURD, new String[] {"categoryDairy"}, false),
    CHEDDAR(FoodDataFL.CHEESE_SALTED, new String[] {"categoryDairy"}, true),
    CHEVRE(FoodDataFL.CHEESE_SALTED, new String[] {"categoryDairy"}, true),
    RAJYA_METOK(FoodDataFL.CHEESE_SALTED, new String[] {"categoryDairy"}, true),
    GOUDA(FoodDataFL.CHEESE_BRINED, new String[] {"categoryDairy"}, true),
    FETA(FoodDataFL.CHEESE_BRINED, new String[] {"categoryDairy"}, true),
    SHOSHA(FoodDataFL.CHEESE_BRINED, new String[] {"categoryDairy"}, true),
    PIZZA_DOUGH(FoodDataFL.DOUGH),
    COOKED_PIZZA(FoodDataFL.FLATBREAD);

    private final FoodData data;
    private final String[] nameOverrides;
    private final boolean replaceOres;

    FoodFL(FoodData data, String[] nameOverrides, boolean replaceOres)
    {
        this.data = data;
        this.nameOverrides = nameOverrides;
        this.replaceOres = replaceOres;
    }

    FoodFL(FoodData data)
    {
        this.data = data;
        this.nameOverrides = null;
        this.replaceOres = false;
    }

    public FoodData getData()
    {
        return data;
    }

    public String[] getNameOverrides()
    {
        return nameOverrides;
    }

    public boolean isReplacingOres()
    {
        return replaceOres;
    }
}
