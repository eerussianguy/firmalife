package com.eerussianguy.firmalife.blocks;

import java.util.Random;
import java.util.function.Supplier;
import javax.annotation.Nonnull;

import net.minecraft.block.Block;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.*;
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
import net.minecraftforge.items.ItemHandlerHelper;

import com.eerussianguy.firmalife.registry.ItemsFL;
import net.dries007.tfc.Constants;
import net.dries007.tfc.api.capability.size.IItemSize;
import net.dries007.tfc.api.capability.size.Size;
import net.dries007.tfc.api.capability.size.Weight;
import net.dries007.tfc.api.types.Plant;
import net.dries007.tfc.objects.blocks.plants.BlockPlantTFC;
import net.dries007.tfc.objects.fluids.FluidsTFC;
import net.dries007.tfc.util.OreDictionaryHelper;
import net.dries007.tfc.util.calendar.CalendarTFC;

import static com.eerussianguy.firmalife.init.StatePropertiesFL.*;

public class BlockPlanter extends Block implements IItemSize
{
    public static final AxisAlignedBB PLANTER_SHAPE = new AxisAlignedBB(0.25D, 0.0D, 0.25D, 0.75D, 1.0D, 0.75D);
    public static final AxisAlignedBB BOX_SHAPE = new AxisAlignedBB(0.25D, 0.0D, 0.25D, 0.75D, 0.25D, 0.75D);

    private final Supplier<Item> drop;
    private final int flowerDay;
    private final Plant plant;

    public BlockPlanter(Supplier<Item> drop, Plant plant, int flowerDay)
    {
        super(Material.PLANTS, MapColor.GREEN);
        setHardness(1.0f);
        setResistance(1.0f);
        setLightOpacity(0);
        setTickRandomly(true);
        this.plant = plant;
        this.flowerDay = flowerDay;
        this.drop = drop;
        this.setDefaultState(this.blockState.getBaseState().withProperty(GROWN, false).withProperty(CAN_GROW, true).withProperty(WET, false));
    }

    private boolean canGrow(World world, BlockPos pos, IBlockState state)
    {
        if (!world.canSeeSky(pos.up()))
        {
            if (world.isAirBlock(pos.up()))
            {
                return world.getLight(pos.up()) > 10;
            }
        }
        return false;
    }


    @Override
    public void randomTick(World world, BlockPos pos, IBlockState state, Random random)
    {
        if (!world.isRemote)
        {
            if (!state.getValue(GROWN))
            {
                int day = CalendarTFC.CALENDAR_TIME.getDayOfMonth();
                if (day == flowerDay)
                {
                    if (canGrow(world, pos, state) && state.getValue(CAN_GROW) && state.getValue(WET))
                    {
                        world.setBlockState(pos, state.withProperty(GROWN, true).withProperty(CAN_GROW, false).withProperty(WET, false));
                    }
                }
                else if (!state.getValue(CAN_GROW) && canGrow(world, pos, state))
                {
                    world.setBlockState(pos, state.withProperty(CAN_GROW, true));
                }
            }
        }
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
            if (state.getValue(GROWN))
            {
                if (OreDictionaryHelper.doesStackMatchOre(held, "knife"))
                {
                    if (Constants.RNG.nextFloat() < 0.2)
                    {
                        ItemHandlerHelper.giveItemToPlayer(player, new ItemStack(Item.getItemFromBlock(BlockPlantTFC.get(plant))));
                    }
                }
                world.setBlockState(pos, state.withProperty(GROWN, false).withProperty(CAN_GROW, false));
                ItemHandlerHelper.giveItemToPlayer(player, new ItemStack(drop.get(), 1));
            }
        }
        return true;
    }


    @Override
    public void getDrops(NonNullList<ItemStack> drops, IBlockAccess world, BlockPos pos, IBlockState state, int fortune)
    {
        drops.add(new ItemStack(ItemsFL.PLANTER));
        if (state.getValue(GROWN))
            drops.add(new ItemStack(drop.get()));
    }

    @Override
    @SuppressWarnings("deprecation")
    @Nonnull
    public IBlockState getStateFromMeta(int meta)
    {
        boolean can_grow = false;
        boolean grown = false;
        if (meta >= 4)
        {
            meta -= 4;
            can_grow = true;
        }
        if (meta >= 2)
        {
            meta -= 2;
            grown = true;
        }
        return this.getDefaultState().withProperty(WET, meta == 1).withProperty(GROWN, grown).withProperty(CAN_GROW, can_grow);
    }

    @Override
    public int getMetaFromState(IBlockState state)
    {
        int wet = state.getValue(WET) ? 1 : 0;
        int grown = state.getValue(GROWN) ? 2 : 0;
        int can_grow = state.getValue(CAN_GROW) ? 4 : 0;
        return wet + grown + can_grow;
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
        return new BlockStateContainer(this, GROWN, CAN_GROW, WET);
    }

    @Override
    @SuppressWarnings("deprecation")
    @Nonnull
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos)
    {
        return PLANTER_SHAPE;
    }

    @Override
    @SuppressWarnings("deprecation")
    public AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, IBlockAccess worldIn, BlockPos pos)
    {
        return BOX_SHAPE;
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
}
