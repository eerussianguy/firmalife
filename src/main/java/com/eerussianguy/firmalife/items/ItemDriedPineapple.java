package com.eerussianguy.firmalife.items;

import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.item.ItemStack;

import com.eerussianguy.firmalife.init.FoodDataFL;
import com.eerussianguy.firmalife.registry.ItemsFL;
import mcp.MethodsReturnNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class ItemDriedPineapple extends ItemFoodFL
{
    public ItemDriedPineapple()
    {
        super(FoodDataFL.COCOA_BEANS);
    }

    @Override
    public ItemStack getContainerItem(ItemStack itemStack)
    {
        return new ItemStack(ItemsFL.PINEAPPLE_CHUNKS);
    }

    @Override
    public boolean hasContainerItem(ItemStack stack)
    {
        return true;
    }
}
