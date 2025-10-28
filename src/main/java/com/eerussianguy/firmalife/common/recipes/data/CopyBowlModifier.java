package com.eerussianguy.firmalife.common.recipes.data;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import net.dries007.tfc.common.component.Bowl;
import net.dries007.tfc.common.component.TFCComponents;
import net.dries007.tfc.common.component.item.ItemComponent;
import net.dries007.tfc.common.recipes.outputs.ItemStackModifier;
import net.dries007.tfc.common.recipes.outputs.ItemStackModifierType;

public enum CopyBowlModifier implements ItemStackModifier
{
    INSTANCE;

    @Override
    public ItemStack apply(ItemStack stack, ItemStack input, Context context)
    {
        ItemComponent inputBowl = stack.get(TFCComponents.BOWL);

        if (inputBowl != null)
        {
            if (!inputBowl.stack().isEmpty())
            {
                stack.set(TFCComponents.BOWL, inputBowl);
            }
            else
            {
                stack.set(TFCComponents.BOWL, Bowl.of(new ItemStack(Items.BOWL)));
            }
        }
        return stack;
    }

    @Override
    public ItemStackModifierType<?> type()
    {
        return FLItemStackModifiers.COPY_BOWL.get();
    }
}
