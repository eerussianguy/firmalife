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
    public boolean hasCraftingRemainingItem(ItemStack stack)
    {
        return true;
    }

    @Override
    public ItemStack getCraftingRemainingItem(ItemStack itemStack)
    {
        return new ItemStack(FLItems.EMPTY_JAR.get());
    }
}
