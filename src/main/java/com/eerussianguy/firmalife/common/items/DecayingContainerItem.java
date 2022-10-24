package com.eerussianguy.firmalife.common.items;

import net.minecraft.world.item.ItemStack;

import com.eerussianguy.firmalife.common.FLTags;
import net.dries007.tfc.common.items.DecayingItem;
import net.dries007.tfc.util.Helpers;

public class DecayingContainerItem extends DecayingItem
{
    public DecayingContainerItem(Properties properties)
    {
        super(properties);
    }

    @Override
    public boolean hasContainerItem(ItemStack stack)
    {
        return Helpers.isItem(stack, FLTags.Items.CONTAINS_PIE_PAN);
    }

    @Override
    public ItemStack getContainerItem(ItemStack stack)
    {
        if (Helpers.isItem(stack, FLTags.Items.CONTAINS_PIE_PAN))
        {
            return new ItemStack(FLItems.PIE_PAN.get());
        }
        return ItemStack.EMPTY;
    }
}
