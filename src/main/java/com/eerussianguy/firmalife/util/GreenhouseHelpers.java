package com.eerussianguy.firmalife.util;

import net.minecraft.block.Block;
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

    private static boolean validEndWallBlock(World world, BlockPos pos, IBlockState checkState, EnumFacing wallFacing, boolean visual)
    {
        return packet(world, pos, (checkState.getBlock() instanceof BlockGreenhouseWall && checkState.getValue(GLASS) && checkState.getValue(FACING) == wallFacing) || checkState.getBlock() instanceof BlockGreenhouseDoor, visual);
    }

    private static boolean validWallBlock(World world, BlockPos pos, IBlockState state, EnumFacing correctFace, boolean visual)
    {
        return packet(world, pos, state.getBlock() instanceof BlockGreenhouseWall && state.getValue(FACING) == correctFace && state.getValue(GLASS), visual);
    }

    private static boolean validRoofBlock(World world, BlockPos pos, IBlockState state, EnumFacing correctFace, boolean visual)
    {
        return packet(world, pos, state.getBlock() instanceof BlockGreenhouseRoof && state.getValue(FACING) == correctFace && state.getValue(GLASS) && !state.getValue(TOP), visual);
    }

    private static boolean isGoodEndWallSide(World world, BlockPos pos, EnumFacing wallFacing, EnumFacing inward, boolean visual)
    {
        int length = 0;
        BlockPos checkPos = pos;
        IBlockState checkState;
        for (int i = 0; i < 100; i++) // get the length of the end wall
        {
            BlockPos newCheckPos = checkPos.offset(inward, i);
            checkState = world.getBlockState(newCheckPos);
            if (validEndWallBlock(world, newCheckPos, checkState, wallFacing, false))
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
                BlockPos newCheckPos = checkPos.offset(inward, i);
                checkState = world.getBlockState(newCheckPos);
                if (!validEndWallBlock(world, newCheckPos, checkState, wallFacing, visual)) return false;
                if (i == 0 && checkState.getValue(TOP))
                    willThin = true;
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


    private static boolean isGoodArc(World world, BlockPos searchPos, EnumFacing inward, boolean visual)
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
            if (!validWallBlock(world, searchPos, searchState, outward, visual))
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
            else if (!validRoofBlock(world, searchPos, searchState, outward, false) && i > 1 && validRoofBlock(world, searchPos.down(), world.getBlockState(searchPos.down()), inward, false))
            {
                searchPos = searchPos.down();
                searchState = world.getBlockState(searchPos);
                roofSize = i;
                roofsGood = true;
                break;
            }
            if (!validRoofBlock(world, searchPos, searchState, outward, visual))
            {
                return false;
            }
        }
        if (!roofsGood)
        {
            packet(world, searchPos, false, visual);
            return false;
        }
        for (int i = roofSize; i > 0; i--)
        {
            if (!validRoofBlock(world, searchPos, searchState, inward, visual))
            {
                return false;
            }
            searchPos = searchPos.down().offset(inward);
            searchState = world.getBlockState(searchPos);
        }
        for (int i = wallCount; i >= 0; i--)
        {
            if (!validWallBlock(world, searchPos, searchState, inward, visual))
            {
                return false;
            }
            searchPos = searchPos.down();
            searchState = world.getBlockState(searchPos);
        }
        return true;
    }

    private static int getGoodArcs(World world, BlockPos pos, IBlockState state, boolean visual)
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
                if (isGoodArc(world, checkPos, inward, visual))
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

    public static boolean isMultiblockValid(World world, BlockPos pos, IBlockState state, boolean visual, int tier)
    {
        int arcs = getGoodArcs(world, pos, state, visual);
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
        if (isGoodEndWallSide(world, startPos, wallFace, facing, visual))
        {
            if (isGoodEndWallSide(world, startPos.offset(wallFace.getOpposite(), arcs + 1), wallFace.getOpposite(), facing, visual))
            {
                if (arcs > 1)
                {
                    seedPositionData(world, pos, state, arcs);
                    setApproval(world, pos, state, wallFace.getOpposite(), true, visual, tier);
                    return true;
                }
            }
        }
        setApproval(world, pos, state, wallFace.getOpposite(), false, visual, 0);
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

    public static void setApproval(World world, BlockPos pos, IBlockState state, EnumFacing wallDir, boolean approvalStatus, boolean visual, int tier)
    {
        final EnumFacing facing = state.getValue(FACING);
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
                            ((IGreenhouseReceiver) teFound).setValidity(approvalStatus, approvalStatus ? tier : 0);
                        }
                        else
                        {
                            IBlockState checkState = world.getBlockState(checkPos);
                            Block checkBlock = checkState.getBlock();
                            if (checkState.getBlock() instanceof IGreenhouseReceiverBlock)
                            {
                                world.setBlockState(checkPos, ((IGreenhouseReceiverBlock) checkBlock).getStateFor(checkState, approvalStatus, tier));
                            }
                        }
                    }
                }
            }
            if (visual && approvalStatus)
            {
                if (facing.getAxisDirection() == EnumFacing.AxisDirection.NEGATIVE)
                {
                    pos = pos.offset(facing.getOpposite());
                }
                if (wallDir.getAxisDirection() == EnumFacing.AxisDirection.NEGATIVE)
                {
                    pos = pos.offset(wallDir.getOpposite());
                }
                BlockPos secondPos = pos.offset(facing, te.forward).offset(wallDir, te.arcs).up(te.height);
                HelpersFL.sendBoundingBoxPacket(world, pos, secondPos, 0.0F, 1.0F, 0.0F, false);
            }
        }
    }

    private static boolean packet(World world, BlockPos pos, boolean valid, boolean visual)
    {
        if (visual && !valid)
        {
            HelpersFL.sendBoundingBoxPacket(world, pos, pos, 1.0F, 0.0F, 0.0F, true);
        }
        return valid;
    }

    public interface IGreenhouseReceiver
    {
        void setValidity(boolean approvalStatus, int tier);
    }

    public interface IGreenhouseReceiverBlock
    {
        IBlockState getStateFor(IBlockState state, boolean approvalStatus, int tier);
    }
}
