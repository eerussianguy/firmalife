package com.eerussianguy.firmalife.blocks;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.block.Block;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
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
import net.minecraftforge.common.property.ExtendedBlockState;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

import com.eerussianguy.firmalife.recipe.PlanterRecipe;
import com.eerussianguy.firmalife.render.UnlistedCropProperty;
import com.eerussianguy.firmalife.te.TEPlanter;
import mcp.MethodsReturnNonnullByDefault;
import net.dries007.tfc.api.capability.size.IItemSize;
import net.dries007.tfc.api.capability.size.Size;
import net.dries007.tfc.api.capability.size.Weight;
import net.dries007.tfc.objects.fluids.FluidsTFC;
import net.dries007.tfc.util.Helpers;

import static com.eerussianguy.firmalife.init.StatePropertiesFL.WET;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class BlockLargePlanter extends Block implements IItemSize
{
    private static final AxisAlignedBB LARGE_SHAPE = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.5D, 1.0D);
    public static final UnlistedCropProperty CROP = new UnlistedCropProperty(1);

    public BlockLargePlanter()
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
                            TEPlanter te = Helpers.getTE(world, pos, TEPlanter.class);
                            if (te != null)
                            {
                                te.setWater(2);
                                world.setBlockState(pos, state.withProperty(WET, true));
                                return true;
                            }

                        }
                    }
                }
            }
            TEPlanter te = Helpers.getTE(world, pos, TEPlanter.class);
            if (te != null)
            {
                IItemHandler inventory = te.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);
                if (inventory != null)
                {
                    ItemStack slotStack = inventory.getStackInSlot(0);
                    PlanterRecipe recipe = PlanterRecipe.get(held);
                    if (slotStack.isEmpty() && !held.isEmpty() && recipe != null && recipe.isLarge())
                    {
                        inventory.insertItem(0, held, false);
                        if (!player.isCreative()) held.shrink(1);
                        te.onInsert(0);
                    }
                    else if (player.isSneaking() && held.isEmpty() && !slotStack.isEmpty()) // probably not useful but better to cover it
                    {
                        te.tryHarvest(player, 0);
                    }
                }
            }
        }
        return true;
    }

    @Override
    protected BlockStateContainer createBlockState()
    {
        return new ExtendedBlockState(this, new IProperty[] {WET}, new IUnlistedProperty[] {CROP});
    }

    @Override
    public IBlockState getExtendedState(IBlockState state, IBlockAccess world, BlockPos pos)
    {
        if (state instanceof IExtendedBlockState)
        {
            IExtendedBlockState extension = (IExtendedBlockState) state;
            PlanterRecipe.PlantInfo plant = getCrop(world, pos);
            extension = extension.withProperty(CROP, plant);
            return extension;
        }
        return state;
    }

    @Nullable
    private PlanterRecipe.PlantInfo getCrop(IBlockAccess world, BlockPos pos)
    {
        TEPlanter te = Helpers.getTE(world, pos, TEPlanter.class);
        return te != null ? new PlanterRecipe.PlantInfo(te.getRecipe(0), te.getStage(0)) : null;
    }

    @Override
    @SuppressWarnings("deprecation")
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

    @Override
    public Size getSize(ItemStack stack)
    {
        return Size.NORMAL;
    }

    @Override
    public Weight getWeight(ItemStack stack)
    {
        return Weight.HEAVY;
    }

    @Override
    @SuppressWarnings("deprecation")
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos)
    {
        return LARGE_SHAPE;
    }

    @Override
    @SuppressWarnings("deprecation")
    public AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, IBlockAccess worldIn, BlockPos pos)
    {
        return LARGE_SHAPE;
    }

    @Override
    @SuppressWarnings("deprecation")
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
        return new TEPlanter();
    }
}
