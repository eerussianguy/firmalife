package com.eerussianguy.firmalife.blocks;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

import com.eerussianguy.firmalife.init.PlanterRegistry;
import com.eerussianguy.firmalife.te.TEQuadPlanter;
import net.dries007.tfc.api.capability.size.IItemSize;
import net.dries007.tfc.api.capability.size.Size;
import net.dries007.tfc.api.capability.size.Weight;
import net.dries007.tfc.objects.fluids.FluidsTFC;
import net.dries007.tfc.util.Helpers;

import static com.eerussianguy.firmalife.init.StatePropertiesFL.*;

public class BlockQuadPlanter extends Block implements IItemSize
{
    public static final AxisAlignedBB QUAD_SHAPE = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.375D, 1.0D);

    public BlockQuadPlanter()
    {
        super(Material.PLANTS, MapColor.BROWN);
        setHardness(1.0f);
        setResistance(1.0f);
        setLightOpacity(0);
        setTickRandomly(true);
        this.setDefaultState(this.blockState.getBaseState().withProperty(WET, false));
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ)
    {
        if (!world.isRemote)
        {
            ItemStack held = player.getHeldItem(hand);
            IFluidHandlerItem cap = held.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null);
            if (cap != null)
            {
                FluidStack stack = cap.drain(Fluid.BUCKET_VOLUME, false);
                {
                    if (stack != null)
                    {
                        if (stack.getFluid() == FluidsTFC.FRESH_WATER.get())
                        {
                            world.setBlockState(pos, state.withProperty(WET, true));
                            return true;
                        }
                    }
                }
            }
            int slot = 0;
            if (hitX > 0.5 && hitZ > 0.5)
            {
                slot = 1;
            }
            else if (hitX > 0.5 && hitZ < 0.5)
            {
                slot = 2;
            }
            else if (hitX < 0.5 && hitZ > 0.5)
            {
                slot = 3;
            }
            TEQuadPlanter te = Helpers.getTE(world, pos, TEQuadPlanter.class);
            if (te != null)
            {
                IItemHandler inventory = te.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);
                if (inventory != null)
                {
                    ItemStack slotStack = inventory.getStackInSlot(slot);
                    PlanterRegistry recipe = PlanterRegistry.get(held);
                    if (slotStack.isEmpty() && recipe != null)
                    {
                        inventory.insertItem(slot, held, false);
                        if (!player.isCreative())
                            held.shrink(1);
                        te.onInsert(slot);
                    }
                    else if (!slotStack.isEmpty() && recipe != null && held.isEmpty())
                    {
                        te.tryHarvest(player, slot);

                    }
                }
            }
        }
        return true;
    }

    @Override
    @SuppressWarnings("deprecation")
    @Nonnull
    public IBlockState getStateFromMeta(int meta)
    {
        return this.getDefaultState().withProperty(WET, meta == 1);
    }

    @Override
    public int getMetaFromState(IBlockState state)
    {
        return state.getValue(WET) ? 1 : 0;
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
        return Size.NORMAL;
    }

    @Nonnull
    @Override
    public Weight getWeight(@Nonnull ItemStack stack)
    {
        return Weight.HEAVY;
    }

    @Override
    @Nonnull
    protected BlockStateContainer createBlockState()
    {
        return new BlockStateContainer(this, WET);
    }

    @Override
    @SuppressWarnings("deprecation")
    @Nonnull
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos)
    {
        return QUAD_SHAPE;
    }

    @Override
    @SuppressWarnings("deprecation")
    public AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, IBlockAccess worldIn, BlockPos pos)
    {
        return QUAD_SHAPE;
    }

    @Override
    @SuppressWarnings("deprecation")
    @Nonnull
    public EnumBlockRenderType getRenderType(IBlockState state)
    {
        return EnumBlockRenderType.MODEL;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public BlockRenderLayer getRenderLayer()
    {
        return BlockRenderLayer.CUTOUT;
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
        return world.getBlockState(pos.down()).getBlockFaceShape(world, pos.down(), EnumFacing.UP) == BlockFaceShape.SOLID;
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
        return new TEQuadPlanter();
    }
}