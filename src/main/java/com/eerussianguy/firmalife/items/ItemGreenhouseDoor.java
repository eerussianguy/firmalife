package com.eerussianguy.firmalife.items;

import javax.annotation.Nonnull;

import net.minecraft.item.ItemDoor;
import net.minecraft.item.ItemStack;

import com.eerussianguy.firmalife.blocks.BlockGreenhouseDoor;
import net.dries007.tfc.api.capability.size.IItemSize;
import net.dries007.tfc.api.capability.size.Size;
import net.dries007.tfc.api.capability.size.Weight;

public class ItemGreenhouseDoor extends ItemDoor implements IItemSize
{
    public ItemGreenhouseDoor(BlockGreenhouseDoor door)
    {
        super(door);
    }

    @Nonnull
    public Size getSize(ItemStack stack) {
        return Size.VERY_LARGE;
    }

    @Nonnull
    public Weight getWeight(ItemStack stack) {
        return Weight.HEAVY;
    }

    public int getItemStackLimit(ItemStack stack) {
        return this.getStackSize(stack);
    }
}
