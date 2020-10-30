package com.eerussianguy.firmalife.blocks;

import java.util.Random;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import com.eerussianguy.firmalife.init.PlantsFL;
import com.eerussianguy.firmalife.registry.BlocksFL;
import net.dries007.tfc.objects.blocks.wood.BlockSaplingTFC;

public class BlockCinnamonSapling extends BlockSaplingTFC
{

    public BlockCinnamonSapling()
    {
        super(PlantsFL.CINNAMON_TREE);
    }

    @Override
    public void grow(World world, Random rand, BlockPos pos, IBlockState blockState)
    {
        for (int air = 1; air < 15; air++)
        {
            if (!world.isAirBlock(pos.offset(EnumFacing.UP, air)))
                return;
        }
        int height = 7 + rand.nextInt(5);
        IBlockState leaves = BlocksFL.CINNAMON_LEAVES.getDefaultState();
        for (int trunk = 0; trunk < height; trunk++)
        {
            BlockPos trunkPos = pos.offset(EnumFacing.UP, trunk);
            world.setBlockState(trunkPos, BlocksFL.CINNAMON_LOG.getDefaultState());
            if (trunk < 3)
                continue;
            for (EnumFacing d : EnumFacing.HORIZONTALS)
            {
                world.setBlockState(trunkPos.offset(d, 1), leaves);
                if (rand.nextFloat() < 1 - (float) trunk / height)
                {
                    world.setBlockState(trunkPos.offset(d, 2), leaves);
                }
                else { continue; }
                if (trunk < 0.3 * height && rand.nextFloat() < (1 - (float) trunk / (height)) / 3)
                    world.setBlockState(trunkPos.offset(d, 3), leaves);
            }
        }
        world.setBlockState(pos.offset(EnumFacing.UP, height), leaves);
    }
}
