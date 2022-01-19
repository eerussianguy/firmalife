package com.eerussianguy.firmalife.blocks;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;

import com.eerussianguy.firmalife.te.TETurntable;
import mcp.MethodsReturnNonnullByDefault;
import net.dries007.tfc.util.Helpers;

import static com.eerussianguy.firmalife.init.StatePropertiesFL.CLAY;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class BlockTurntable extends BlockNonCube
{
    private static final AxisAlignedBB SHAPE = new AxisAlignedBB(4.0D / 16, 0.0D, 4.0D / 16, 12.0D / 16, 5.0D / 16, 12.0D / 16);

    public BlockTurntable()
    {
        super(Material.IRON);
        setHardness(1.0F);
        setResistance(1.0F);
        setDefaultState(getBlockState().getBaseState().withProperty(CLAY, 0));
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ)
    {
        if (!world.isRemote && hand == EnumHand.MAIN_HAND)
        {
            ItemStack held = player.getHeldItem(hand);
            if (player.isSneaking())
            {
                TETurntable te = Helpers.getTE(world, pos, TETurntable.class);
                if (te != null && te.hasPottery()) te.rotate();
                return true;
            }
            if (held.getItem() == Items.CLAY_BALL)
            {
                int clay = state.getValue(CLAY);
                if (clay < 4 && held.getCount() > 5)
                {
                    held.shrink(5);
                    world.setBlockState(pos, state.withProperty(CLAY, clay + 1));
                    return true;
                }
            }
            else
            {
                TETurntable te = Helpers.getTE(world, pos, TETurntable.class);
                if (te != null)
                {
                    IItemHandler cap = te.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);
                    if (cap != null)
                    {
                        ItemStack invStack = cap.getStackInSlot(0);
                        if (invStack.isEmpty() && TETurntable.isPottery(held))
                        {
                            ItemStack leftover = cap.insertItem(0, held.splitStack(1), false);
                            ItemHandlerHelper.giveItemToPlayer(player, leftover);
                            return true;
                        }
                        else if (!invStack.isEmpty() && held.isEmpty())
                        {
                            ItemStack leftover = cap.extractItem(0, 1, false);
                            ItemHandlerHelper.giveItemToPlayer(player, leftover);
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    @Override
    @SuppressWarnings("deprecation")
    public IBlockState getStateFromMeta(int meta)
    {
        return getDefaultState().withProperty(CLAY, meta);
    }

    @Override
    public int getMetaFromState(IBlockState state)
    {
        return state.getValue(CLAY);
    }

    @Override
    @Nonnull
    protected BlockStateContainer createBlockState()
    {
        return new BlockStateContainer(this, CLAY);
    }

    @Override
    public boolean hasTileEntity(IBlockState state)
    {
        return true;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(World world, IBlockState state)
    {
        return new TETurntable();
    }

    @Override
    public void breakBlock(World world, BlockPos pos, IBlockState state)
    {
        TETurntable te = Helpers.getTE(world, pos, TETurntable.class);
        if (te != null)
        {
            te.onBreakBlock(world, pos, state);
        }
        super.breakBlock(world, pos, state);
    }

    @Override
    @SuppressWarnings("deprecation")
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos)
    {
        return SHAPE;
    }
}
