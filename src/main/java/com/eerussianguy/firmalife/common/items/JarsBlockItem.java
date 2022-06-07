package com.eerussianguy.firmalife.common.items;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;

public class JarsBlockItem extends BlockItem
{
    public JarsBlockItem(Block block, Properties properties)
    {
        super(block, properties);
    }

    @Override
    public boolean hasContainerItem(ItemStack stack)
    {
        return true;
    }

    @Override
    public ItemStack getContainerItem(ItemStack itemStack)
    {
        return new ItemStack(FLItems.EMPTY_JAR.get());
    }
}
