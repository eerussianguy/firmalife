package com.eerussianguy.firmalife.util;

import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import com.eerussianguy.firmalife.blocks.BlockGreenhouseDoor;
import com.eerussianguy.firmalife.blocks.BlockGreenhouseRoof;
import com.eerussianguy.firmalife.blocks.BlockGreenhouseWall;
import com.eerussianguy.firmalife.te.TEClimateStation;
import net.dries007.tfc.util.Helpers;

import static com.eerussianguy.firmalife.init.StatePropertiesFL.GLASS;
import static com.eerussianguy.firmalife.init.StatePropertiesFL.TOP;
import static net.minecraft.block.BlockHorizontal.FACING;

public class GreenhouseHelpers
{
    public GreenhouseHelpers() { }

    /**
     * Used for initial check in planters for growth
     */
    public static boolean isSkylightValid(World world, BlockPos pos)
    {
        for (int i = 1; i <= 48; i++) // safer than a while loop
        {
            BlockPos checkPos = pos.up(i);
            if (world.getBlockState(checkPos).getBlock() instanceof BlockGreenhouseRoof && world.canSeeSky(checkPos.up()))
            {
                return true; // we found a roof that is seeing sky
            }
        }
        return false;
    }

    private static boolean isGoodEndWall(IBlockState checkState, EnumFacing wallFacing)
    {
        return (checkState.getBlock() instanceof BlockGreenhouseWall && checkState.getValue(GLASS) && checkState.getValue(FACING) == wallFacing) || checkState.getBlock() instanceof BlockGreenhouseDoor;
    }

    private static boolean isGoodEndWallSide(World world, BlockPos pos, EnumFacing wallFacing, EnumFacing inward)
    {
        int length = 0;
        BlockPos checkPos = pos;
        IBlockState checkState;
        for (int i = 0; i < 100; i++) // get the length of the end wall
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
        if (!(world.getBlockState(pos.offset(inward.getOpposite()).offset(wallFacing.getOpposite())).getBlock() instanceof BlockGreenhouseWall)
        || !(world.getBlockState(pos.offset(inward, length).offset(wallFacing.getOpposite())).getBlock() instanceof BlockGreenhouseWall)
        || length < 3)
            return false;
        checkPos = pos.up();
        while (length > 0)
        {
            boolean willThin = false;
            for (int i = 0; i < length; i++)
            {
                checkState = world.getBlockState(checkPos.offset(inward, i));
                if (i == 0 && checkState.getValue(TOP) && isGoodEndWall(checkState, wallFacing))
                    willThin = true;
                if (!isGoodEndWall(checkState, wallFacing))
                {
                    return false;
                }
            }
            if (willThin)
            {
                length -= 2;
                checkPos = checkPos.offset(inward);
            }
            checkPos = checkPos.up();
        }
        return true;
    }


    private static boolean isGoodWall(IBlockState state, EnumFacing correctFace)
    {
        return state.getBlock() instanceof BlockGreenhouseWall && state.getValue(FACING) == correctFace && state.getValue(GLASS);
    }

    private static boolean isGoodRoof(IBlockState state, EnumFacing correctFace)
    {
        return state.getBlock() instanceof BlockGreenhouseRoof && state.getValue(FACING) == correctFace && state.getValue(GLASS) && !state.getValue(TOP);
    }

    private static boolean isGoodArc(World world, BlockPos searchPos, EnumFacing inward)
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
            if (searchState.getBlock() instanceof BlockGreenhouseRoof && searchState.getValue(TOP) && i > 0)
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

    private static int getGoodArcs(World world, BlockPos pos, IBlockState state)
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
            for (int i = 0; i < 100; i++) // traveling sideways counting how many good arcs we get
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

    public static boolean isMultiblockValid(World world, BlockPos pos, IBlockState state)
    {
        int arcs = getGoodArcs(world, pos, state);
        EnumFacing facing = state.getValue(FACING);
        EnumFacing wallFace;
        BlockPos startPos;
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
                if (arcs > 1)
                {
                    seedPositionData(world, pos, state, arcs);
                    approve(world, pos, state, wallFace.getOpposite());
                    return true;
                }
            }
        }
        deny(world, pos, state, wallFace.getOpposite());
        return false;
    }

    private static void seedPositionData(World world, BlockPos pos, IBlockState state, int arcs)
    {
        TEClimateStation te = Helpers.getTE(world, pos, TEClimateStation.class);
        if (te != null)
        {
            EnumFacing facing = state.getValue(FACING);
            int forwardCount = 1;
            int upCount = 1;
            boolean foundWall = false;
            for (int i = 1; i < 64; i++)
            {
                if (world.getBlockState(pos.offset(facing, i)).getBlock() instanceof BlockGreenhouseWall)
                {
                    foundWall = true;
                    break;
                }
                forwardCount++;
            }
            if (foundWall)
            {
                for (int j = 1; j < 40; j++)
                {
                    if (world.getBlockState(pos.up(j)).getBlock() instanceof BlockGreenhouseRoof)
                    {
                        te.setPositions(forwardCount, arcs, upCount);
                        return;
                    }
                    upCount++;
                }
            }
        }
    }

    public static void approve(World world, BlockPos pos, IBlockState state, EnumFacing wallDir)
    {
        EnumFacing facing = state.getValue(FACING);
        TEClimateStation te = Helpers.getTE(world, pos, TEClimateStation.class);
        if (te != null)
        {
            for (int i = 0; i <= te.forward; i++)
            {
                for (int j = 0; j <= te.arcs; j++)
                {
                    for (int k = 0; k <= te.height; k++)
                    {
                        BlockPos checkPos = pos.offset(facing, i).offset(wallDir, j).up(k);
                        TileEntity teFound = world.getTileEntity(checkPos);
                        if (teFound instanceof IGreenhouseReceiver)
                        {
                            ((IGreenhouseReceiver) teFound).setValidity(true);
                        }
                    }
                }
            }
        }
    }

    public static void deny(World world, BlockPos pos, IBlockState state, EnumFacing wallDir)
    {
        EnumFacing facing = state.getValue(FACING);
        TEClimateStation te = Helpers.getTE(world, pos, TEClimateStation.class);
        if (te != null && te.isSeeded)
        {
            for (int i = 0; i <= te.forward; i++)
            {
                for (int j = 0; j <= te.arcs; j++)
                {
                    for (int k = 0; k <= te.height; k++)
                    {
                        BlockPos checkPos = pos.offset(facing, i).offset(wallDir, j).up(k);
                        TileEntity teFound = world.getTileEntity(checkPos);
                        if (teFound instanceof IGreenhouseReceiver)
                        {
                            ((IGreenhouseReceiver) teFound).setValidity(false);
                        }
                    }
                }
            }
        }
    }
}
