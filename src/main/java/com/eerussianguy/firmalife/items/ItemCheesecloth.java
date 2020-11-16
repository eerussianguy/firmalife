package com.eerussianguy.firmalife.items;

import javax.annotation.Nonnull;

import net.minecraft.item.ItemStack;

import com.eerussianguy.firmalife.init.FoodDataFL;
import com.eerussianguy.firmalife.registry.ItemsFL;
import net.dries007.tfc.api.capability.size.Size;
import net.dries007.tfc.api.capability.size.Weight;
import net.dries007.tfc.objects.items.ItemMisc;

public class ItemCheesecloth extends ItemMisc
{
    //todo: Extend ItemFoodFL to track decay and make Cheesecloth edible
    public ItemCheesecloth()
    {
        super(Size.NORMAL, Weight.MEDIUM);
    }

    @Override
    @Nonnull
    public ItemStack getContainerItem(ItemStack itemStack)
    {
        return new ItemStack(ItemsFL.DIRTY_CHEESECLOTH);
    }

    @Override
    public boolean hasContainerItem(ItemStack stack)
    {
        return true;
    }
}
