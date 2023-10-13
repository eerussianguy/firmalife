package com.eerussianguy.firmalife.common.recipes.data;

import com.eerussianguy.firmalife.common.items.FLItems;
import net.minecraft.world.item.ItemStack;

import net.dries007.tfc.common.capabilities.food.DynamicBowlHandler;
import net.dries007.tfc.common.capabilities.food.FoodCapability;
import net.dries007.tfc.common.recipes.outputs.ItemStackModifier;

public enum AddPiePanModifier implements ItemStackModifier.SingleInstance<AddPiePanModifier>
{
    INSTANCE;

    @Override
    public AddPiePanModifier instance()
    {
        return INSTANCE;
    }

    @Override
    public ItemStack apply(ItemStack stack, ItemStack input)
    {
        stack.getCapability(FoodCapability.CAPABILITY).ifPresent(cap -> {
            if (cap instanceof DynamicBowlHandler handler)
            {
                handler.setBowl(new ItemStack(FLItems.PIE_PAN.get()));
            }
        });
        return stack;
    }
}
