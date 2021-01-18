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
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
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
import com.eerussianguy.firmalife.te.TEQuadPlanter;
import com.eerussianguy.firmalife.util.IWaterable;
import mcp.MethodsReturnNonnullByDefault;
import net.dries007.tfc.api.capability.size.IItemSize;
import net.dries007.tfc.api.capability.size.Size;
import net.dries007.tfc.api.capability.size.Weight;
import net.dries007.tfc.client.gui.overlay.IHighlightHandler;
import net.dries007.tfc.objects.fluids.FluidsTFC;
import net.dries007.tfc.util.Helpers;

import static com.eerussianguy.firmalife.init.StatePropertiesFL.WET;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class BlockQuadPlanter extends Block implements IItemSize, IHighlightHandler
{
    public static final AxisAlignedBB QUAD_SHAPE = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.375D, 1.0D);
    private static final AxisAlignedBB[] HITBOXES = new AxisAlignedBB[] {
        new AxisAlignedBB(0.0D, 0.0D, 0.0D, 0.5D, 0.375D, 0.5D),// < <
        new AxisAlignedBB(0.5D, 0.0D, 0.5D, 1.0D, 0.375D, 1.0D),// > >
        new AxisAlignedBB(0.5D, 0.0D, 0.0D, 1.0D, 0.375D, 0.5D),// > <
        new AxisAlignedBB(0.0D, 0.0D, 0.5D, 0.5D, 0.375D, 1.0D) // < >
    };
    public static final UnlistedCropProperty CROP_1 = new UnlistedCropProperty(1);
    public static final UnlistedCropProperty CROP_2 = new UnlistedCropProperty(2);
    public static final UnlistedCropProperty CROP_3 = new UnlistedCropProperty(3);
    public static final UnlistedCropProperty CROP_4 = new UnlistedCropProperty(4);

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
                            TEQuadPlanter te = Helpers.getTE(world, pos, TEQuadPlanter.class);
                            if (te != null)
                            {
                                te.addWater(4);
                                world.setBlockState(pos, state.withProperty(WET, true));
                                return true;
                            }

                        }
                    }
                }
            }
            int slot = getSlotForHit(hitX, hitZ);
            TEQuadPlanter te = Helpers.getTE(world, pos, TEQuadPlanter.class);
            if (te != null)
            {
                IItemHandler inventory = te.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);
                if (inventory != null)
                {
                    ItemStack slotStack = inventory.getStackInSlot(slot);
                    PlanterRecipe recipe = PlanterRecipe.get(held);
                    if (slotStack.isEmpty() && !held.isEmpty() && recipe != null)
                    {
                        inventory.insertItem(slot, held, false);
                        if (!player.isCreative()) held.shrink(1);
                        te.onInsert(slot);
                    }
                    else if (player.isSneaking() && held.isEmpty() && !slotStack.isEmpty()) // probably not useful but better to cover it
                    {
                        te.tryHarvest(player, slot);
                    }

                }
            }
        }
        return true;
    }

    /**
     * dx dy dz logic comes from TFC
     */
    @Override
    public boolean drawHighlight(World world, BlockPos pos, EntityPlayer player, RayTraceResult rayTraceResult, double partialTicks)
    {
        double dx = player.lastTickPosX + (player.posX - player.lastTickPosX) * partialTicks;
        double dy = player.lastTickPosY + (player.posY - player.lastTickPosY) * partialTicks;
        double dz = player.lastTickPosZ + (player.posZ - player.lastTickPosZ) * partialTicks;

        int lookingSlot = getSlotForHit(rayTraceResult.hitVec.x - pos.getX(), rayTraceResult.hitVec.z - pos.getZ());
        for (int i = 0; i < 4; i++)
        {
            IHighlightHandler.drawBox(HITBOXES[i].offset(pos).offset(-dx, -dy, -dz).grow(0.002D), 1.0F, lookingSlot == i ? 1.0F : 0, 0, 0, 0.4F);
        }
        return true;
    }

    private int getSlotForHit(double hitX, double hitZ)
    {
        if (hitX > 0.5 && hitZ > 0.5)
        {
            return 1;
        }
        else if (hitX > 0.5 && hitZ < 0.5)
        {
            return 2;
        }
        else if (hitX < 0.5 && hitZ > 0.5)
        {
            return 3;
        }
        else
        {
            return 0;
        }
    }

    @Override
    protected BlockStateContainer createBlockState()
    {
        IProperty [] listedProperties = new IProperty[] {WET};
        IUnlistedProperty [] unlistedProperties = new IUnlistedProperty[] {CROP_1, CROP_2, CROP_3, CROP_4};
        return new ExtendedBlockState(this, listedProperties, unlistedProperties);
    }

    @Override
    public IBlockState getExtendedState(IBlockState state, IBlockAccess world, BlockPos pos)
    {
        if (state instanceof IExtendedBlockState)
        {
            IExtendedBlockState extension = (IExtendedBlockState)state;
            PlanterRecipe.PlantInfo[] plants = getCrops(world, pos);
            extension = extension.withProperty(CROP_1, plants[0]).withProperty(CROP_2, plants[1]).withProperty(CROP_3, plants[2]).withProperty(CROP_4, plants[3]);
            return extension;
        }
        return state;
    }

    private PlanterRecipe.PlantInfo[] getCrops(IBlockAccess world, BlockPos pos)
    {
        TEQuadPlanter te = Helpers.getTE(world, pos, TEQuadPlanter.class);
        PlanterRecipe.PlantInfo[] plants = new PlanterRecipe.PlantInfo[] {null, null, null, null};
        if (te != null)
        {
            for (int i = 0; i < 4; i++)
            {
                plants[i] = new PlanterRecipe.PlantInfo(te.getRecipe(i), te.getStage(i));
            }
        }
        return plants;
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
    public Size getSize( ItemStack stack)
    {
        return Size.NORMAL;
    }

    @Override
    public Weight getWeight( ItemStack stack)
    {
        return Weight.HEAVY;
    }

    @Override
    @SuppressWarnings("deprecation")
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