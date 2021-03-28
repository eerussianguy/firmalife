package com.eerussianguy.firmalife.blocks;

import java.util.Random;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;

import com.eerussianguy.firmalife.recipe.DryingRecipe;
import com.eerussianguy.firmalife.te.TELeafMat;
import mcp.MethodsReturnNonnullByDefault;
import net.dries007.tfc.api.capability.size.IItemSize;
import net.dries007.tfc.api.capability.size.Size;
import net.dries007.tfc.api.capability.size.Weight;
import net.dries007.tfc.objects.items.ceramics.ItemSmallVessel;
import net.dries007.tfc.util.Helpers;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class BlockLeafMat extends Block implements IItemSize
{
    public static final AxisAlignedBB MAT_SHAPE = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.125D, 1.0D);

    public BlockLeafMat()
    {
        super(Material.PLANTS, MapColor.GREEN);
        setHardness(1.0F);
        setResistance(1.0F);
        setLightOpacity(0);
        setSoundType(SoundType.PLANT);
        setTickRandomly(true);
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ)
    {
        if (!world.isRemote)
        {
            ItemStack held = player.getHeldItem(hand);
            if (held.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null)) return false;
            TELeafMat te = Helpers.getTE(world, pos, TELeafMat.class);
            if (te != null)
            {
                IItemHandler inventory = te.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);
                if (inventory != null)
                {
                    ItemStack tryStack = new ItemStack(held.getItem(), 1);
                    if (DryingRecipe.get(tryStack) != null && inventory.getStackInSlot(0).isEmpty())
                    {
                        ItemStack leftover = inventory.insertItem(0, held.splitStack(1), false);
                        ItemHandlerHelper.giveItemToPlayer(player, leftover);
                        te.start();
                        te.markForSync();
                        return true;
                    }
                    if (held.isEmpty() && player.isSneaking())
                    {
                        ItemStack takeStack = inventory.extractItem(0, 1, false);
                        Helpers.spawnItemStack(world, pos, takeStack);
                        te.deleteSlot();
                        te.clear();
                        te.markForSync();
                    }
                }
            }
        }
        return true;
    }

    @Override
    public void randomTick(World worldIn, BlockPos pos, IBlockState state, Random random)
    {
        if (worldIn.isRainingAt(pos.up()))
        {
            TELeafMat te = Helpers.getTE(worldIn, pos, TELeafMat.class);
            if (te != null)
            {
                te.rain();
            }
        }
    }

    @Override
    public void breakBlock(World world, BlockPos pos, IBlockState state)
    {
        TELeafMat te = Helpers.getTE(world, pos, TELeafMat.class);
        if (te != null)
        {
            te.onBreakBlock(world, pos, state);
        }
        super.breakBlock(world, pos, state);
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
        return Size.SMALL;
    }

    @Nonnull
    @Override
    public Weight getWeight(@Nonnull ItemStack stack)
    {
        return Weight.LIGHT;
    }

    @Override
    @SuppressWarnings("deprecation")
    @Nonnull
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos)
    {
        return MAT_SHAPE;
    }

    @Override
    @SuppressWarnings("deprecation")
    @Nonnull
    public EnumBlockRenderType getRenderType(IBlockState state)
    {
        return EnumBlockRenderType.MODEL;
    }

    @SuppressWarnings("deprecation")
    @Override
    public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face)
    {
        return BlockFaceShape.UNDEFINED;
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
        return new TELeafMat();
    }
}
