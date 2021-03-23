package com.eerussianguy.firmalife.blocks;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import mcp.MethodsReturnNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class BlockTrellis extends BlockNonCube
{
    private static final AxisAlignedBB SHAPE = new AxisAlignedBB(2.0D / 16, 0.0D, 2.0D / 16, 14.0D / 16, 1.0D, 14.0D / 16);

    public BlockTrellis()
    {
        super(Material.IRON);
        setHardness(1.0f);
        setResistance(1.0f);
        setLightOpacity(0);
    }

    @Override
    @SuppressWarnings("deprecation")
    @Nonnull
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos)
    {
        return SHAPE;
    }

    @SuppressWarnings("deprecation")
    @Override
    public void neighborChanged(IBlockState state, World world, BlockPos pos, Block blockIn, BlockPos fromPos)
    {
        if (!canStay(world, pos))
        {
            world.destroyBlock(pos, true);
        }
    }

    private boolean canStay(IBlockAccess world, BlockPos pos)
    {
        IBlockState state = world.getBlockState(pos.down());
        return state.getBlock() instanceof BlockTrellis || state.getBlockFaceShape(world, pos.down(), EnumFacing.UP) == BlockFaceShape.SOLID;
    }

    @Override
    public boolean canPlaceBlockOnSide(World worldIn, BlockPos pos, EnumFacing side)
    {
        if (!canStay(worldIn, pos)) return false;
        return super.canPlaceBlockOnSide(worldIn, pos, side);
    }
}
