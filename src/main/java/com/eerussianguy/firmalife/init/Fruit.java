package com.eerussianguy.firmalife.init;

import net.minecraft.item.Item;

import net.dries007.tfc.api.capability.food.FoodData;
import net.dries007.tfc.objects.items.food.ItemFoodTFC;
import net.dries007.tfc.util.agriculture.Food;

import static com.eerussianguy.firmalife.init.FoodDataFL.*;

/**
 * This is an easy way to wrap all the TFC fruits with our data
 */
public enum Fruit
{
    BANANA(ItemFoodTFC.get(Food.BANANA), true, DRIED_FRUIT_DECAY),
    BLACKBERRY(ItemFoodTFC.get(Food.BLACKBERRY), true, DRIED_FRUIT_CATEGORY),
    BLUEBERRY(ItemFoodTFC.get(Food.BLUEBERRY), true, DRIED_FRUIT_CATEGORY),
    BUNCH_BERRY(ItemFoodTFC.get(Food.BUNCH_BERRY), true, DRIED_FRUIT_SATURATION),
    CHERRY(ItemFoodTFC.get(Food.CHERRY), true, DRIED_FRUIT_CATEGORY),
    CLOUD_BERRY(ItemFoodTFC.get(Food.CLOUD_BERRY), true, DRIED_FRUIT_DECAY),
    CRANBERRY(ItemFoodTFC.get(Food.CRANBERRY), true, DRIED_FRUIT_SATURATION),
    ELDERBERRY(ItemFoodTFC.get(Food.ELDERBERRY), true, DRIED_FRUIT_CATEGORY),
    GOOSEBERRY(ItemFoodTFC.get(Food.GOOSEBERRY), true, DRIED_FRUIT_SATURATION),
    GREEN_APPLE(ItemFoodTFC.get(Food.GREEN_APPLE), true, DRIED_FRUIT_DECAY),
    LEMON(ItemFoodTFC.get(Food.LEMON), true, DRIED_FRUIT_DECAY),
    OLIVE(ItemFoodTFC.get(Food.OLIVE), true, DRIED_FRUIT_DECAY),
    ORANGE(ItemFoodTFC.get(Food.ORANGE), true, DRIED_FRUIT_DECAY),
    PEACH(ItemFoodTFC.get(Food.PEACH), true, DRIED_FRUIT_SATURATION),
    PLUM(ItemFoodTFC.get(Food.PLUM), true, DRIED_FRUIT_SATURATION),
    RASPBERRY(ItemFoodTFC.get(Food.RASPBERRY), true, DRIED_FRUIT_SATURATION),
    RED_APPLE(ItemFoodTFC.get(Food.RED_APPLE), true, DRIED_FRUIT_DECAY),
    SNOW_BERRY(ItemFoodTFC.get(Food.SNOW_BERRY), true, DRIED_FRUIT_CATEGORY),
    STRAWBERRY(ItemFoodTFC.get(Food.STRAWBERRY), true, DRIED_FRUIT_SATURATION),
    WINTERGREEN_BERRY(ItemFoodTFC.get(Food.WINTERGREEN_BERRY), true, DRIED_FRUIT_CATEGORY);


    private final Item fruit;
    private final boolean dry;
    private final FoodData driedData;

    Fruit(Item fruit, boolean dry, FoodData driedData)
    {
        this.fruit = fruit;
        this.dry = dry;
        this.driedData = driedData;
    }

    public Item getFruit()
    {
        return this.fruit;
    }

    public boolean canDry()
    {
        return this.dry;
    }


    public FoodData getDriedData()
    {
        return this.driedData;
    }

}
