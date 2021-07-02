package com.eerussianguy.firmalife.blocks;

import java.util.function.Supplier;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.block.Block;
import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import com.eerussianguy.firmalife.init.StatePropertiesFL;
import mcp.MethodsReturnNonnullByDefault;
import net.dries007.tfc.api.capability.size.IItemSize;

import static com.eerussianguy.firmalife.init.StatePropertiesFL.STAGE;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class BlockHangingPlanter extends BlockBonsai
{
    public static final PropertyEnum<EnumFacing.Axis> AXIS = StatePropertiesFL.XZ;
    private static final AxisAlignedBB SHAPE = new AxisAlignedBB(0.0D, 0.75D, 0.0D, 1.0D, 1.0D, 1.0D);

    public BlockHangingPlanter(Supplier<? extends Item> fruit, Supplier<? extends Item> seed, int period)
    {
        super(fruit, seed, period, 0, Material.IRON);
        this.setDefaultState(this.getBlockState().getBaseState().withProperty(AXIS, EnumFacing.Axis.X).withProperty(STAGE, 0));
    }

    @Override
    @Nonnull
    public IBlockState getStateFromMeta(int meta)
    {
        EnumFacing.Axis axis = EnumFacing.Axis.Z;
        if (meta > 2)
        {
            axis = EnumFacing.Axis.X;
            meta -= 3;
        }
        return getDefaultState().withProperty(AXIS, axis).withProperty(STAGE, meta);
    }

    @Override
    public int getMetaFromState(IBlockState state)
    {
        EnumFacing.Axis axis = state.getValue(AXIS); // 0, 3
        int stage = state.getValue(STAGE); // 0, 1, 2
        return stage + (axis == EnumFacing.Axis.X ? 3 : 0);
    }

    @Override
    @Nonnull
    protected BlockStateContainer createBlockState()
    {
        return new BlockStateContainer(this, AXIS, STAGE);
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos)
    {
        return SHAPE;
    }

    @Override
    @SuppressWarnings("deprecation")
    public void neighborChanged(IBlockState state, World world, BlockPos pos, Block blockIn, BlockPos fromPos)
    {
        if (!world.isRemote)
        {
            for (EnumFacing d : EnumFacing.Plane.HORIZONTAL)
            {
                Block block = world.getBlockState(pos.offset(d)).getBlock();
                if (block instanceof BlockGreenhouseWall || block instanceof BlockHangingPlanter) return;
            }
            world.destroyBlock(pos, true);
        }
    }

    @Override
    @SuppressWarnings("deprecation")
    @Nonnull
    public IBlockState getStateForPlacement(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer)
    {
        if (facing.getAxis() == EnumFacing.Axis.Y)
        {
            facing = placer.getHorizontalFacing();
        }
        IBlockState state = worldIn.getBlockState(pos.offset(facing));
        final Block block = state.getBlock();
        if (block instanceof BlockGreenhouseWall)
        {
            facing = state.getValue(BlockHorizontal.FACING);
        }
        else if (block instanceof BlockHangingPlanter)
        {
            return getDefaultState().withProperty(AXIS, state.getValue(AXIS));
        }
        return getDefaultState().withProperty(AXIS, facing.getAxis());
    }
}
