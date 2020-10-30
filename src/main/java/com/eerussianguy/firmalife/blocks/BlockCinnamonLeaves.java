package com.eerussianguy.firmalife.blocks;

import java.util.List;
import java.util.Random;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.google.common.collect.ImmutableList;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import com.eerussianguy.firmalife.init.PlantsFL;
import com.eerussianguy.firmalife.registry.BlocksFL;
import net.dries007.tfc.objects.blocks.wood.BlockLeavesTFC;

public class BlockCinnamonLeaves extends BlockLeavesTFC
{
    public BlockCinnamonLeaves()
    {
        // LOOK AWAY!!!!
        // Is this necessary? I don't know
        super(PlantsFL.CINNAMON_TREE);
    }

    @Override
    public void getDrops(NonNullList<ItemStack> drops, IBlockAccess world, BlockPos pos, IBlockState state, int fortune)
    {
        int chance = 10; // this should be config
        if (RANDOM.nextInt(101) < chance)
        {
            drops.add(new ItemStack(BlocksFL.CINNAMON_SAPLING));
        }
    }

    @Override
    public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand)
    {
        worldIn.scheduleUpdate(pos, state.getBlock(), 1);
    }

    @Override
    @SuppressWarnings("deprecation")
    public void neighborChanged(IBlockState state, World world, BlockPos pos, @Nullable Block blockIn, @Nullable BlockPos fromPos)
    {
        for (EnumFacing d : EnumFacing.VALUES)
        {
            for (int i = 1; i < 4; i++)
            {
                Block offsetBlock = world.getBlockState(pos.offset(d, i)).getBlock();
                if (offsetBlock instanceof BlockCinnamonLog)
                    return;
            }
        }
        world.destroyBlock(pos, true);
    }

    @Override
    @Nonnull
    public List<ItemStack> onSheared(ItemStack item, IBlockAccess world, BlockPos pos, int fortune)
    {
        return ImmutableList.of(ItemStack.EMPTY);
    }
}
