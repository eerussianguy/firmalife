package com.eerussianguy.firmalife.common.recipes.data;

import com.eerussianguy.firmalife.common.items.FLItems;
import net.minecraft.world.item.ItemStack;

import net.dries007.tfc.common.component.TFCComponents;
import net.dries007.tfc.common.component.item.ItemComponent;
import net.dries007.tfc.common.recipes.outputs.ItemStackModifier;
import net.dries007.tfc.common.recipes.outputs.ItemStackModifierType;

public enum EmptyPanModifier implements ItemStackModifier
{
    INSTANCE;

    @Override
    public ItemStack apply(ItemStack stack, ItemStack input, Context context)
    {
        ItemComponent bowl = stack.get(TFCComponents.BOWL);
        // Reasonable default for display in i.e. JEI for soups obtained directly from the creative menu.
        // Prevents them from displaying empty. Works as long as the bowl handler itself doesn't ever have an empty bowl (it shouldn't)
        if (bowl != null && !bowl.stack().isEmpty()) {
            return bowl.stack();
        }
        return new ItemStack(FLItems.PIE_PAN.get());
    }

    @Override
    public boolean dependsOnInput()
    {
        return true;
    }

    @Override
    public ItemStackModifierType<?> type()
    {
        return FLItemStackModifiers.EMPTY_PAN.get();
    }

}
