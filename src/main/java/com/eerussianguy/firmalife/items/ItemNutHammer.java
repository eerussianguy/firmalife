package com.eerussianguy.firmalife.items;

import javax.annotation.Nonnull;

import net.minecraft.block.BlockLeaves;
import net.minecraft.block.BlockLog;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import net.dries007.tfc.api.capability.size.IItemSize;
import net.dries007.tfc.api.capability.size.Size;
import net.dries007.tfc.api.capability.size.Weight;

public class ItemNutHammer extends Item implements IItemSize
{
    public ItemNutHammer()
    {
        super();
    }

    @Override
    public EnumActionResult onItemUse(EntityPlayer player, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
    {
        IBlockState logState = worldIn.getBlockState(pos);
        // check if the log is in the registry, query the leaves, nut
        int leafCount = 0;
        for (int i = 1; i < 10; i++)
        {
            logState = worldIn.getBlockState(pos.up(i));
            if (!(logState.getBlock() instanceof BlockLog)) // this should be checking that same log
                break;
            for (EnumFacing d : EnumFacing.HORIZONTALS)
            {
                IBlockState leafState = null;
                for (int j = 1; j < 5; j++)
                {
                    BlockPos offsetPos = pos.offset(d, j);
                    leafState = worldIn.getBlockState(offsetPos);
                    if (worldIn.isAirBlock(offsetPos))
                        continue;
                    if (leafState.getBlock() instanceof BlockLeaves)// this should be checking specific leaves
                        leafCount++;
                }
            }
        }
        if (leafCount > 0)
        {
            //data handling
        }

        return EnumActionResult.FAIL;
    }

    @Nonnull
    public Size getSize(ItemStack stack) {
        return Size.VERY_LARGE;
    }

    @Nonnull
    public Weight getWeight(ItemStack stack) {
        return Weight.HEAVY;
    }
}
