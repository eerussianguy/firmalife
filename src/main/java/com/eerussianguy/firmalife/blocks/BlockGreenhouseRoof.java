package com.eerussianguy.firmalife.blocks;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import com.eerussianguy.firmalife.util.HelpersFL;
import net.dries007.tfc.util.OreDictionaryHelper;

import static com.eerussianguy.firmalife.init.StatePropertiesFL.GLASS;
import static com.eerussianguy.firmalife.init.StatePropertiesFL.TOP;
import static net.minecraft.block.BlockHorizontal.FACING;

@ParametersAreNonnullByDefault
public class BlockGreenhouseRoof extends BlockGreenhouseWall
{
    public static final AxisAlignedBB BASE = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.5D, 1.0D);
    public static final AxisAlignedBB ROOF_SHAPE_EAST = new AxisAlignedBB(0.5D, 0.5D, 0.0D, 1.0D, 1.0D, 0.5D).union(BASE);
    public static final AxisAlignedBB ROOF_SHAPE_WEST = new AxisAlignedBB(0.0D, 0.5D, 0.5D, 1.0D, 1.0D, 1.0D).union(BASE);
    public static final AxisAlignedBB ROOF_SHAPE_SOUTH = new AxisAlignedBB(0.0D, 0.5D, 0.0D, 0.5D, 1.0D, 1.0D).union(BASE);
    public static final AxisAlignedBB ROOF_SHAPE_NORTH = new AxisAlignedBB(0.5D, 0.5D, 0.0D, 1.0D, 1.0D, 1.0D).union(BASE);

    public BlockGreenhouseRoof()
    {
        super();
        setHardness(2.0f);
        setResistance(3.0f);
        setLightOpacity(0);
        setSoundType(SoundType.METAL);
        this.setDefaultState(this.blockState.getBaseState().withProperty(GLASS, false).withProperty(FACING, EnumFacing.EAST).withProperty(TOP, false));
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ)
    {
        if (!world.isRemote)
        {
            ItemStack held = player.getHeldItem(hand);
            if (OreDictionaryHelper.doesStackMatchOre(held, "greenhouse")) return false;
            if (!state.getValue(GLASS))
            {
                if (held.getCount() > 1 && OreDictionaryHelper.doesStackMatchOre(held, "paneGlass"))
                {
                    world.setBlockState(pos, state.withProperty(GLASS, true));
                    if (!player.isCreative())
                        held.shrink(2);
                    return true;
                }
            }
            if (HelpersFL.doesStackMatchTool(held, "hammer"))
            {
                if (!player.isSneaking())
                {
                    world.setBlockState(pos, state.withProperty(TOP, !state.getValue(TOP)));
                }
                else if (!state.getValue(TOP))
                {
                    world.setBlockState(pos, state.withProperty(FACING, state.getValue(FACING).rotateY()));
                }
            }
        }
        return true;
    }

    @Override
    @Nonnull
    public IBlockState getStateForPlacement(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer)
    {
        return getDefaultState().withProperty(FACING, placer.getHorizontalFacing().getOpposite());
    }

    @Override
    public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos)
    {
        //do nothing, for now
    }

    @Override
    public boolean isSideSolid(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing side)
    {
        return state.getValue(FACING).getOpposite() == side || side == EnumFacing.DOWN;
    }

    @Override
    @Nonnull
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos)
    {
        if (state.getValue(TOP))
        {
            return BASE;
        }
        switch (state.getValue(FACING))
        {
            case NORTH:
            default:
                return ROOF_SHAPE_NORTH;
            case SOUTH:
                return ROOF_SHAPE_SOUTH;
            case WEST:
                return ROOF_SHAPE_WEST;
            case EAST:
                return ROOF_SHAPE_EAST;
        }
    }

    @Override
    @SuppressWarnings("deprecation")
    public AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, IBlockAccess worldIn, BlockPos pos)
    {
        return blockState.getValue(TOP) ? BASE : FULL_BLOCK_AABB;
    }

    @Override
    @Nonnull
    public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face)
    {
        return (state.getValue(GLASS) && (face == EnumFacing.DOWN || face == state.getValue(FACING).getOpposite())) ? BlockFaceShape.SOLID : BlockFaceShape.UNDEFINED;
    }
}
