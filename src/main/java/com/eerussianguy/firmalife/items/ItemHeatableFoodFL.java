package com.eerussianguy.firmalife.items;

import net.minecraft.item.ItemFood;
import net.minecraftforge.common.capabilities.ICapabilityProvider;

import net.dries007.tfc.api.capability.food.FoodData;
import net.dries007.tfc.api.capability.food.FoodHeatHandler;
import net.dries007.tfc.api.capability.food.IItemFoodTFC;

public class ItemHeatableFoodFL extends ItemFood implements IItemFoodTFC
{
    public FoodData data;

    public ItemHeatableFoodFL(FoodData data)
    {
        super(0, 0.0F, false);
        this.setMaxDamage(0);
        this.data = data;
    }

    @Override
    public ICapabilityProvider getCustomFoodHandler()
    {
        return new FoodHeatHandler(null, data, 1.0F, 200.0F);
    }
}
