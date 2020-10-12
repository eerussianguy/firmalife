package com.eerussianguy.firmalife.blocks;

import java.util.Random;
import javax.annotation.Nonnull;

import net.minecraft.block.Block;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import net.dries007.tfc.api.capability.size.IItemSize;
import net.dries007.tfc.api.capability.size.Size;
import net.dries007.tfc.api.capability.size.Weight;
import net.dries007.tfc.objects.advancements.TFCTriggers;
import net.dries007.tfc.objects.blocks.property.ILightableBlock;
import net.dries007.tfc.objects.items.ItemFireStarter;

import static com.eerussianguy.firmalife.init.StatePropertiesFL.CURED;
import static net.minecraft.block.BlockHorizontal.FACING;

public class BlockOven extends Block implements ILightableBlock, IItemSize
{

    public BlockOven()
    {
        super(Material.ROCK, MapColor.RED_STAINED_HARDENED_CLAY);
        setHardness(2.0f);
        setResistance(3.0f);
        setTickRandomly(true);
        this.setDefaultState(this.blockState.getBaseState().withProperty(CURED, false).withProperty(FACING, EnumFacing.NORTH).withProperty(LIT, false));
    }

    public void cascadeLight(World world, BlockPos center, boolean flag)
    {
        if (!world.isRemote)
        {
            IBlockState ovenState = world.getBlockState(center);
            if (ovenState.getBlock() instanceof BlockOven)
            {
                world.setBlockState(center, ovenState.withProperty(LIT, flag));

                EnumFacing facing = ovenState.getValue(FACING);
                EnumFacing left = facing.rotateYCCW();
                EnumFacing right = facing.rotateY();

                BlockPos leftCheck = center.offset(left);
                IBlockState leftState = world.getBlockState(leftCheck);
                Block leftBlock = leftState.getBlock();
                while (leftBlock instanceof BlockOven)
                {
                    world.setBlockState(leftCheck, leftState.withProperty(LIT, flag));

                    leftCheck = leftCheck.offset(left);
                    leftState = world.getBlockState(leftCheck);
                    leftBlock = leftState.getBlock();
                }

                BlockPos rightCheck = center.offset(right);
                IBlockState rightState = world.getBlockState(rightCheck);
                Block rightBlock = rightState.getBlock();
                while (rightBlock instanceof BlockOven)
                {
                    world.setBlockState(rightCheck, rightState.withProperty(LIT, flag));

                    rightCheck = rightCheck.offset(right);
                    rightState = world.getBlockState(rightCheck);
                    rightBlock = rightState.getBlock();
                }
            }
        }
    }

    private boolean isValidHorizontal(World world, BlockPos ovenPos)
    {
        IBlockState ovenState = world.getBlockState(ovenPos);
        EnumFacing facing = ovenState.getValue(FACING);
        EnumFacing left = facing.rotateYCCW();
        EnumFacing right = facing.rotateY();
        IBlockState leftState = world.getBlockState(ovenPos.offset(left));
        IBlockState rightState = world.getBlockState(ovenPos.offset(right));
        IBlockState[] checkStates = {leftState, rightState};

        for (IBlockState state : checkStates)
        {
            Block b = state.getBlock();
            if(!(b instanceof BlockOven || b instanceof BlockOvenWall))
            {
                return false; // return false if it's not an oven or oven wall
            }
            if (b instanceof BlockOven)
            {
                if (state.getValue(FACING) != ovenState.getValue(FACING))
                {
                    return false; // if it's an oven, it should face the same way
                }
            }
            if (b instanceof BlockOvenWall)
            {
                if (state == leftState)
                {
                    if (leftState.getValue(FACING) != facing.getOpposite())
                    {
                        return false; // if it's a wall, it should be rotated to touch the oven properly
                    }
                }
                if (state == rightState)
                {
                    if (rightState.getValue(FACING) != facing)
                    {
                        return false; // see above
                    }
                }
            }
        }
        return true;
    }

    public boolean isValid(World world, BlockPos center)
    {
        IBlockState ovenState = world.getBlockState(center);
        if (ovenState.getBlock() instanceof BlockOven)
        {
            EnumFacing facing = ovenState.getValue(FACING);
            EnumFacing left = facing.rotateYCCW();
            EnumFacing right = facing.rotateY();

            boolean leftFlag = false;
            boolean rightFlag = false;

            BlockPos leftCheck = center.offset(left);
            Block leftBlock = world.getBlockState(leftCheck).getBlock();
            while (leftBlock instanceof BlockOven || leftBlock instanceof BlockOvenWall)
            {
                if (leftBlock instanceof BlockOvenWall)
                {
                    if (world.getBlockState(leftCheck).getValue(FACING) == facing.getOpposite())
                    {
                        leftFlag = true;
                    }
                    break;
                }
                leftCheck = leftCheck.offset(left);
                leftBlock = world.getBlockState(leftCheck).getBlock();
            }
            if (leftFlag)
            {
                BlockPos rightCheck = center.offset(right);
                Block rightBlock = world.getBlockState(rightCheck).getBlock();
                while (rightBlock instanceof BlockOven || rightBlock instanceof BlockOvenWall)
                {
                    if (rightBlock instanceof BlockOvenWall)
                    {
                        if (world.getBlockState(rightCheck).getValue(FACING) == facing)
                        {
                            rightFlag = true;
                        }
                        break;
                    }
                    rightCheck = rightCheck.offset(right);
                    rightBlock = world.getBlockState(rightCheck).getBlock();
                }
                if (rightFlag)
                {
                    boolean upFlag = false;
                    BlockPos upCheck = center.up();
                    Block upBlock = world.getBlockState(upCheck).getBlock();
                    int chimneyCount = 0;
                    while (upBlock instanceof BlockOvenChimney)
                    {
                        if (world.isAirBlock(upCheck.up()))
                        {
                            upFlag = true;
                            break;
                        }
                        chimneyCount++;
                        upCheck = upCheck.up();
                        upBlock = world.getBlockState(upCheck).getBlock();
                    }
                    if (upFlag && chimneyCount > 1 && world.canBlockSeeSky(upCheck))
                    {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ)
    {
        if (!world.isRemote)
        {
            if (!state.getValue(LIT))
            {
                ItemStack held = player.getHeldItem(hand);
                if (isValid(world, pos) && ItemFireStarter.onIgnition(held))
                {
                    TFCTriggers.LIT_TRIGGER.trigger((EntityPlayerMP) player, state.getBlock()); // Trigger lit block
                    cascadeLight(world, pos, true);
                    //handle TE
                    return true;
                }
                if (!player.isSneaking())
                {
                    // do something
                }
            }
            // if it's lit, you shouldn't be able to do anything
            // however if the recipe is not valid you shouldn't be able to light it
        }
        return true;
    }

    @Override
    @SuppressWarnings("deprecation")
    public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos)
    {
        if (!worldIn.isRemote)
        {
            if (state.getValue(LIT) && !isValidHorizontal(worldIn, pos))
            {
                worldIn.setBlockState(pos, state.withProperty(LIT, false));
            }
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void randomDisplayTick(IBlockState stateIn, World worldIn, BlockPos pos, Random rand)
    {
        if (stateIn.getValue(LIT))
        {
            worldIn.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, pos.getX() + rand.nextFloat(), pos.getY() + 1, pos.getZ() + rand.nextFloat(),
                0f, 0.1f + 0.1f * rand.nextFloat(), 0f);
            if (worldIn.getTotalWorldTime() % 80 == 0)
            {
                worldIn.playSound((double) pos.getX() + 0.5D, pos.getY(), (double) pos.getZ() + 0.5D, SoundEvents.BLOCK_FIRE_AMBIENT, SoundCategory.BLOCKS, 0.5F, 0.6F, false);
            }
        }
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
        return getDefaultState().withProperty(FACING, facing);
    }

    @Override
    @SuppressWarnings("deprecation")
    @Nonnull
    public IBlockState getStateFromMeta(int meta)
    {
        boolean cured = meta > 7;
        boolean lit = meta > 11 || (meta > 3 && meta < 8);
        int facing = meta;
        if (lit)
            facing -= 4;
        if (cured)
            facing -= 8;
        return this.getDefaultState().withProperty(CURED, cured).withProperty(LIT, lit).withProperty(FACING, EnumFacing.byHorizontalIndex(facing));
    }

    @Override
    public int getMetaFromState(IBlockState state)
    {
        int facing = state.getValue(FACING).getHorizontalIndex(); //0, 1, 2, 3
        int cured = state.getValue(CURED) ? 8 : 0; // true = 8, false = 0
        int lit = state.getValue(LIT) ? 4 : 0; // true = 0, false = 4

        return facing + cured + lit;
    }

    @Override
    public int getLightValue(IBlockState state, IBlockAccess world, BlockPos pos)
    {
        return state.getValue(LIT) ? 15 : 0;
    }

    @Override
    @SuppressWarnings("deprecation")
    public boolean isOpaqueCube(IBlockState state)
    {
        return false;
    }

    @Nonnull
    @Override
    public Size getSize(@Nonnull ItemStack stack)
    {
        return Size.LARGE; // Can only store in chests
    }

    @Nonnull
    @Override
    public Weight getWeight(@Nonnull ItemStack stack)
    {
        return Weight.VERY_HEAVY; // Stacksize = 1
    }

    @Override
    @Nonnull
    protected BlockStateContainer createBlockState()
    {
        return new BlockStateContainer(this, FACING, LIT, CURED);
    }

    @Override
    @SuppressWarnings("deprecation")
    @Nonnull
    public EnumBlockRenderType getRenderType(IBlockState state)
    {
        return EnumBlockRenderType.MODEL;
    }
}
