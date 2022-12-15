package com.eerussianguy.firmalife.common.recipes.data;

import com.eerussianguy.firmalife.common.items.FLItems;
import net.minecraft.world.item.ItemStack;

import net.dries007.tfc.common.capabilities.food.FoodCapability;
import net.dries007.tfc.common.items.DynamicBowlFood;
import net.dries007.tfc.common.recipes.outputs.ItemStackModifier;

public enum EmptyPanModifier implements ItemStackModifier.SingleInstance<EmptyPanModifier>
{
    INSTANCE;

    @Override
    public ItemStack apply(ItemStack stack, ItemStack input)
    {
        return input.getCapability(FoodCapability.CAPABILITY)
            .filter(cap -> cap instanceof DynamicBowlFood.DynamicBowlHandler)
            .map(cap -> ((DynamicBowlFood.DynamicBowlHandler) cap).getBowl())
            // Reasonable default for display in i.e. JEI for soups obtained directly from the creative menu.
            // Prevents them from displaying empty. Works as long as the bowl handler itself doesn't ever have an empty bowl (it shouldn't)
            .filter(s -> !s.isEmpty())
            .orElseGet(() -> new ItemStack(FLItems.PIE_PAN.get()));
    }

    @Override
    public boolean dependsOnInput()
    {
        return true;
    }

    @Override
    public EmptyPanModifier instance()
    {
        return INSTANCE;
    }
}
