package com.eerussianguy.firmalife.items;

import javax.annotation.Nonnull;

import net.minecraft.item.ItemStack;

import com.eerussianguy.firmalife.registry.ModRegistry;
import net.dries007.tfc.api.capability.size.Size;
import net.dries007.tfc.api.capability.size.Weight;
import net.dries007.tfc.objects.items.ItemMisc;

public class ItemRoastedCocoaBeans extends ItemMisc
{

    public ItemRoastedCocoaBeans()
    {
        super(Size.SMALL, Weight.LIGHT);
    }

    @Override
    @Nonnull
    public ItemStack getContainerItem(ItemStack itemStack)
    {
        return new ItemStack(ModRegistry.COCOA_POWDER);
    }

    @Override
    public boolean hasContainerItem(ItemStack stack)
    {
        return true;
    }
}
