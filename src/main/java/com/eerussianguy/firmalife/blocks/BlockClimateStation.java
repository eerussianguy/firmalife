package com.eerussianguy.firmalife.blocks;

import javax.annotation.Nonnull;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import net.dries007.tfc.api.capability.size.IItemSize;
import net.dries007.tfc.api.capability.size.Size;
import net.dries007.tfc.api.capability.size.Weight;

import static com.eerussianguy.firmalife.init.StatePropertiesFL.*;
import static net.minecraft.block.BlockHorizontal.FACING;

public class BlockClimateStation extends Block implements IItemSize
{
    public BlockClimateStation()
    {
        super(Material.WOOD, MapColor.GREEN);
        setHardness(1.0f);
        setResistance(0.5f);
        setSoundType(SoundType.WOOD);
        setTickRandomly(true);
        this.setDefaultState(this.blockState.getBaseState().withProperty(FACING, EnumFacing.EAST).withProperty(STASIS, false));
    }

    private boolean isGoodEndWall(IBlockState checkState, EnumFacing wallFacing)
    {
        return (checkState.getBlock() instanceof BlockGreenhouseWall && checkState.getValue(GLASS) && checkState.getValue(FACING) == wallFacing) || checkState.getBlock() instanceof BlockGreenhouseDoor;
    }

    private boolean isGoodEndWallSide(World world, BlockPos pos, EnumFacing wallFacing, EnumFacing inward)
    {
        int length = 0;
        BlockPos checkPos = pos;
        IBlockState checkState = null;
        for (int i = 0; i < 100; i++)
        {
            checkState = world.getBlockState(checkPos.offset(inward, i));
            if (isGoodEndWall(checkState, wallFacing))
            {
                length++;
            }
            else
            {
                break;
            }
        }
        if (!(world.getBlockState(pos.offset(inward.getOpposite()).offset(wallFacing.getOpposite())).getBlock() instanceof BlockGreenhouseWall))
            return false;
        if (!(world.getBlockState(pos.offset(inward, length).offset(wallFacing.getOpposite())).getBlock() instanceof BlockGreenhouseWall))
            return false;
        if (length < 3)
            return false;
        checkPos = pos.up();
        while (length > 0)
        {
            for (int i = 0; i < length; i++)
            {
                checkState = world.getBlockState(checkPos.offset(inward, i));
                if (!isGoodEndWall(checkState, wallFacing))
                {
                    return false;
                }
            }
            length -= 2;
            checkPos = checkPos.up().offset(inward);
        }
        return true;
    }


    private boolean isGoodWall(IBlockState state, EnumFacing correctFace)
    {
        return state.getBlock() instanceof BlockGreenhouseWall && state.getValue(FACING) == correctFace && state.getValue(GLASS);
    }

    private boolean isGoodRoof(IBlockState state, EnumFacing correctFace)
    {
        return state.getBlock() instanceof BlockGreenhouseRoof && state.getValue(FACING) == correctFace && state.getValue(GLASS) && !state.getValue(TOP);
    }

    /**
     *   X
     *  X X
     * X   X
     * X   X
     * X   X
     * @param world This is my house
     * @param searchPos My house is a greenhouse
     * @param inward It is very green
     * @return a haiku
     */
    private boolean isGoodArc(World world, BlockPos searchPos, EnumFacing inward)
    {
        EnumFacing outward = inward.getOpposite();
        IBlockState searchState = world.getBlockState(searchPos);

        int wallCount = 0;
        boolean wallsGood = false;
        for (int i = 0; i < 8; i++)
        {
            if (searchState.getBlock() instanceof BlockGreenhouseWall && searchState.getValue(TOP) && i > 1)
            {
                wallsGood = true;
                wallCount = i;
                break;
            }
            if (!isGoodWall(searchState, outward))
                return false;
            searchPos = searchPos.up();
            searchState = world.getBlockState(searchPos);
        }
        if (!wallsGood)
            return false;
        boolean roofsGood = false;
        int roofSize = 0;
        for (int i = 0; i < 8; i++)
        {
            searchPos = searchPos.up().offset(inward);
            searchState = world.getBlockState(searchPos);
            if (searchState.getBlock() instanceof BlockGreenhouseRoof && searchState.getValue(TOP) && i > 1)
            {
                roofsGood = true;
                roofSize = i;
                searchPos = searchPos.down().offset(inward);
                searchState = world.getBlockState(searchPos);
                break;
            }
            else if (!isGoodRoof(searchState, outward) && i > 1 && isGoodRoof(world.getBlockState(searchPos.down()), inward))
            {
                searchPos = searchPos.down();
                searchState = world.getBlockState(searchPos);
                roofSize = i;
                roofsGood = true;
                break;
            }
            if (!isGoodRoof(searchState, outward))
            {
                return false;
            }
        }
        if (!roofsGood)
            return false;
        for (int i = roofSize; i > 0; i--)
        {
            if (!isGoodRoof(searchState, inward))
            {
                return false;
            }
            searchPos = searchPos.down().offset(inward);
            searchState = world.getBlockState(searchPos);
        }
        for (int i = wallCount; i >= 0; i--)
        {
            if (!isGoodWall(searchState, inward))
            {
                return false;
            }
            searchPos = searchPos.down();
            searchState = world.getBlockState(searchPos);
        }
        return true;
    }

    private int numberOfGoodArcs(World world, BlockPos pos, IBlockState state)
    {
        EnumFacing inward = state.getValue(FACING);
        EnumFacing outward = inward.getOpposite();
        EnumFacing correctDir = null;
        int returnValue = 0;

        for (EnumFacing d : new EnumFacing[] {inward.rotateY(), inward.rotateYCCW()})
        {
            if (world.getBlockState(pos.offset(d)).getBlock() instanceof BlockGreenhouseWall)
            {
                correctDir = d.getOpposite();
            }
        }
        if (correctDir != null)
        {
            for (int i = 0; i < 100; i++)
            {
                BlockPos checkPos = pos.offset(outward).offset(correctDir, i);
                if (isGoodArc(world, checkPos, inward))
                {
                    returnValue++;
                }
                else
                {
                    return returnValue;
                }
            }
        }
        return returnValue;
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ)
    {
        if (!world.isRemote && hand == EnumHand.MAIN_HAND)
        {
            int arcs = numberOfGoodArcs(world, pos, state);
            EnumFacing facing = state.getValue(FACING);
            EnumFacing wallFace = null;
            BlockPos startPos = null;
            if (world.getBlockState(pos.offset(facing.rotateY())).getBlock() instanceof BlockGreenhouseWall)
            {
                wallFace = facing.rotateY();
                startPos = pos.offset(facing.rotateY());
            }
            else if (world.getBlockState(pos.offset(facing.rotateYCCW())).getBlock() instanceof BlockGreenhouseWall)
            {
                wallFace = facing.rotateYCCW();
                startPos = pos.offset(facing.rotateYCCW());
            }
            else
            {
                return false;
            }
            if (isGoodEndWallSide(world, startPos, wallFace, facing))
            {
                if (isGoodEndWallSide(world, startPos.offset(wallFace.getOpposite(), arcs + 1), wallFace.getOpposite(), facing))
                {
                    world.setBlockState(pos, state.withProperty(STASIS, arcs > 1));
                }
            }
            return true;
        }
        return false;
    }


    @Override
    @SuppressWarnings("deprecation")
    @Nonnull
    public IBlockState getStateFromMeta(int meta)
    {
        return this.getDefaultState().withProperty(FACING, EnumFacing.byHorizontalIndex(meta)).withProperty(STASIS, meta > 3);
    }

    @Override
    public int getMetaFromState(IBlockState state)
    {
        return state.getValue(FACING).getHorizontalIndex() + (state.getValue(STASIS) ? 4 : 0);
    }

    @Override
    @SuppressWarnings("deprecation")
    @Nonnull
    public IBlockState getStateForPlacement(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer)
    {
        return getDefaultState().withProperty(FACING, placer.getHorizontalFacing().getOpposite());
    }

    @Override
    @Nonnull
    protected BlockStateContainer createBlockState()
    {
        return new BlockStateContainer(this, FACING, STASIS);
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
}
