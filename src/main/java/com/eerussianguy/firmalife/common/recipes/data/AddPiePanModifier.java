package com.eerussianguy.firmalife.common.recipes.data;

import com.eerussianguy.firmalife.common.items.FLItems;
import net.minecraft.world.item.ItemStack;

import net.dries007.tfc.common.component.Bowl;
import net.dries007.tfc.common.component.TFCComponents;
import net.dries007.tfc.common.component.food.FoodCapability;
import net.dries007.tfc.common.component.food.IFood;
import net.dries007.tfc.common.component.item.ItemComponent;
import net.dries007.tfc.common.recipes.outputs.ItemStackModifier;
import net.dries007.tfc.common.recipes.outputs.ItemStackModifierType;

public enum AddPiePanModifier implements ItemStackModifier
{
    INSTANCE;

    @Override
    public ItemStack apply(ItemStack stack, ItemStack input, Context context)
    {
        stack.set(TFCComponents.BOWL, Bowl.of(new ItemStack(FLItems.PIE_PAN.get())));
        return stack;
    }

    @Override
    public ItemStackModifierType<?> type()
    {
        return FLItemStackModifiers.ADD_PIE_PAN.get();
    }
}
