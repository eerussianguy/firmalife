package com.eerussianguy.firmalife.blocks;

import java.util.Random;
import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import com.eerussianguy.firmalife.init.PlantsFL;
import com.eerussianguy.firmalife.registry.BlocksFL;
import com.eerussianguy.firmalife.world.WorldgenCinnamon;
import net.dries007.tfc.objects.blocks.wood.BlockSaplingTFC;

@ParametersAreNonnullByDefault
public class BlockCinnamonSapling extends BlockSaplingTFC
{
    public BlockCinnamonSapling()
    {
        super(PlantsFL.CINNAMON_TREE);
    }

    @Override
    public void grow(World world, Random rand, BlockPos pos, IBlockState blockState)
    {
        WorldgenCinnamon.generateCinnamon(world, rand, pos, false);
    }
}
