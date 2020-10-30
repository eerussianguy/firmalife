package com.eerussianguy.firmalife.blocks;

import javax.annotation.Nonnull;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import net.dries007.tfc.api.capability.size.IItemSize;
import net.dries007.tfc.api.capability.size.Size;
import net.dries007.tfc.api.capability.size.Weight;
import net.dries007.tfc.util.OreDictionaryHelper;

import static com.eerussianguy.firmalife.init.StatePropertiesFL.GLASS;
import static com.eerussianguy.firmalife.init.StatePropertiesFL.TOP;
import static net.minecraft.block.BlockHorizontal.FACING;

public class BlockGreenhouseWall extends Block implements IItemSize
{
    public static final AxisAlignedBB GREEN_WALL_EAST = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 0.25D, 1.0D, 1.0D);
    public static final AxisAlignedBB GREEN_WALL_WEST = new AxisAlignedBB(0.75D, 0.0D, 0.0D, 1.0D, 1.0D, 1.0D);
    public static final AxisAlignedBB GREEN_WALL_SOUTH = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 1.0D, 0.25D);
    public static final AxisAlignedBB GREEN_WALL_NORTH = new AxisAlignedBB(0.0D, 0.0D, 0.75D, 1.0D, 1.0D, 1.0D);

    public BlockGreenhouseWall()
    {
        super(Material.GLASS, MapColor.GRAY);
        setHardness(2.0f);
        setResistance(3.0f);
        setLightOpacity(0);
        setSoundType(SoundType.GLASS);
        this.setDefaultState(this.blockState.getBaseState().withProperty(GLASS, false).withProperty(FACING, EnumFacing.EAST).withProperty(TOP, false));
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ)
    {
        if (!world.isRemote)
        {
            ItemStack held = player.getHeldItem(hand);
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
        }
        return true;
    }


    @SideOnly(Side.CLIENT)
    @Override
    public BlockRenderLayer getRenderLayer()
    {
        return BlockRenderLayer.CUTOUT;
    }

    @Override
    @SuppressWarnings("deprecation")
    public boolean shouldSideBeRendered(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing side)
    {
        return !(world.getBlockState(pos.offset(side)).getBlock() instanceof BlockGreenhouseWall);
    }

    @SuppressWarnings("deprecation")
    @Override
    public boolean isFullCube(IBlockState state)
    {
        return false;
    }

    @Override
    @SuppressWarnings("deprecation")
    public boolean isSideSolid(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing side)
    {
        return state.getValue(FACING).getOpposite() == side;
    }

    @Override
    @SuppressWarnings("deprecation")
    @Nonnull
    public IBlockState getStateForPlacement(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer)
    {
        if (facing.getAxis() == EnumFacing.Axis.Y)
        {
            facing = placer.getHorizontalFacing().getOpposite();
        }
        boolean isTop = false;
        if (!(worldIn.getBlockState(pos.up()).getBlock() instanceof BlockGreenhouseWall))
        {
            isTop = true;
        }
        if (worldIn.getBlockState(pos.down()).getBlock() instanceof BlockGreenhouseWall)
        {
            facing = worldIn.getBlockState(pos.down()).getValue(FACING);
        }
        return getDefaultState().withProperty(FACING, facing).withProperty(TOP, isTop);
    }

    @Override
    @SuppressWarnings("deprecation")
    public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos)
    {
        if (!worldIn.isRemote)
        {
            if (!(worldIn.getBlockState(pos.up()).getBlock() instanceof BlockGreenhouseWall))
            {
                worldIn.setBlockState(pos, state.withProperty(TOP, true));
            }
            else
            {
                worldIn.setBlockState(pos, state.withProperty(TOP, false));
            }
        }
    }

    @Override
    @SuppressWarnings("deprecation")
    @Nonnull
    public IBlockState getStateFromMeta(int meta)
    {
        boolean glass = meta > 7;
        boolean top = meta > 11 || (meta > 3 && meta < 8);
        int facing = meta;
        if (top)
            facing -= 4;
        if (glass)
            facing -= 8;
        return this.getDefaultState().withProperty(GLASS, glass).withProperty(TOP, top).withProperty(FACING, EnumFacing.byHorizontalIndex(facing));
    }

    @Override
    public int getMetaFromState(IBlockState state)
    {
        int facing = state.getValue(FACING).getHorizontalIndex(); //0, 1, 2, 3
        int glass = state.getValue(GLASS) ? 8 : 0; // true = 8, false = 0
        int top = state.getValue(TOP) ? 4 : 0; // true = 0, false = 4

        return facing + glass + top;
    }

    @Override
    @SuppressWarnings("deprecation")
    @Nonnull
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos)
    {
        switch (state.getValue(FACING))
        {
            case NORTH:
            default:
                return GREEN_WALL_NORTH;
            case SOUTH:
                return GREEN_WALL_SOUTH;
            case WEST:
                return GREEN_WALL_WEST;
            case EAST:
                return GREEN_WALL_EAST;
        }
    }

    @Override
    @SuppressWarnings("deprecation")
    public boolean isOpaqueCube(IBlockState state)
    {
        return false;
    }

    @Override
    @SuppressWarnings("deprecation")
    public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face)
    {
        return BlockFaceShape.UNDEFINED;
    }

    @Nonnull
    @Override
    public Size getSize(@Nonnull ItemStack stack)
    {
        return Size.NORMAL;
    }

    @Nonnull
    @Override
    public Weight getWeight(@Nonnull ItemStack stack)
    {
        return Weight.MEDIUM;
    }

    @Override
    @Nonnull
    protected BlockStateContainer createBlockState()
    {
        return new BlockStateContainer(this, FACING, GLASS, TOP);
    }
}
