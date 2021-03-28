package com.eerussianguy.firmalife.blocks;

import java.util.List;
import java.util.Random;
import java.util.function.Supplier;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
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

import com.eerussianguy.firmalife.init.FoodDataFL;
import com.eerussianguy.firmalife.init.StatePropertiesFL;
import com.eerussianguy.firmalife.te.TEString;
import mcp.MethodsReturnNonnullByDefault;
import net.dries007.tfc.api.capability.food.CapabilityFood;
import net.dries007.tfc.api.capability.food.FoodTrait;
import net.dries007.tfc.api.capability.food.IFood;
import net.dries007.tfc.api.recipes.heat.HeatRecipe;
import net.dries007.tfc.objects.blocks.devices.BlockFirePit;
import net.dries007.tfc.objects.te.TEFirePit;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.OreDictionaryHelper;
import net.dries007.tfc.util.calendar.ICalendar;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class BlockString extends BlockNonCube
{
    public static final PropertyEnum<EnumFacing.Axis> AXIS = StatePropertiesFL.XZ;
    private static final AxisAlignedBB SHAPE = new AxisAlignedBB(0.0D, 8.0D / 16, 7.0D / 16, 1.0D, 10.0D / 16, 9.0D / 16);
    private static final AxisAlignedBB SHAPE_90 = new AxisAlignedBB(7.0D / 16, 8.0D / 16, 0.0D, 9.0D / 16, 10.0D / 16, 1.0D);
    private final Supplier<? extends Item> item;

    public BlockString(Supplier<? extends Item> item)
    {
        super(Material.CLOTH);
        setHardness(0.2f);
        setResistance(0.2f);
        setLightLevel(0);
        setTickRandomly(true);
        this.item = item;
        this.setDefaultState(getBlockState().getBaseState().withProperty(AXIS, EnumFacing.Axis.X));
    }

    @Override
    @SuppressWarnings("deprecation")
    @Nonnull
    public IBlockState getStateFromMeta(int meta)
    {
        return getDefaultState().withProperty(AXIS, meta == 1 ? EnumFacing.Axis.X : EnumFacing.Axis.Z);
    }

    @Override
    public int getMetaFromState(IBlockState state)
    {
        return state.getValue(AXIS) == EnumFacing.Axis.X ? 1 : 0;
    }

    @Override
    @SuppressWarnings("deprecation")
    @Nonnull
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos)
    {
        return state.getValue(AXIS) == EnumFacing.Axis.X ? SHAPE : SHAPE_90;
    }

    public Item getItemDropped(IBlockState state, Random rand, int fortune)
    {
        return item.get();
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ)
    {
        if (!world.isRemote && hand == EnumHand.MAIN_HAND)
        {
            TEString te = Helpers.getTE(world, pos, TEString.class);
            if (te == null) return false;
            ItemStack held = player.getHeldItem(hand);
            if (held.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null)) return false;
            IItemHandler inv = te.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);
            if (inv == null) return false;
            ItemStack current = inv.getStackInSlot(0);
            if (!held.isEmpty() && current.isEmpty())
            {
                IFood cap = held.getCapability(CapabilityFood.CAPABILITY, null);
                if (cap != null)
                {
                    List<FoodTrait> traits = cap.getTraits();
                    boolean isFoodValid = (traits.contains(FoodTrait.BRINED) && OreDictionaryHelper.doesStackMatchOre(held, "categoryMeat") && HeatRecipe.get(held) != null) || OreDictionaryHelper.doesStackMatchOre(held, "cheese");
                    if (!traits.contains(FoodDataFL.SMOKED) && isFoodValid)
                    {
                        ItemStack leftover = inv.insertItem(0, held.splitStack(1), false);
                        Helpers.spawnItemStack(world, pos.add(0.5D, 0.5D, 0.5D), leftover);
                        te.markForSync();
                        return true;
                    }
                }
            }
            else if (held.isEmpty() && !current.isEmpty())
            {
                Helpers.spawnItemStack(world, pos, inv.extractItem(0, 1, false));
                te.markForSync();
                return true;
            }
        }
        return false;
    }

    @Override
    public void randomTick(World world, BlockPos pos, IBlockState state, Random random)
    {
        if (world.isRemote) return;
        TEString te = Helpers.getTE(world, pos, TEString.class);
        if (te == null) return;

        if (world.isRainingAt(pos.up()) || !isFired(world, pos))
        {
            te.resetCounter();
        }
        else if (te.getTicksSinceUpdate() >= (ICalendar.TICKS_IN_HOUR * 4))
        {
            IItemHandler cap = te.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);
            if (cap != null)
                te.tryCook();
        }
    }

    private static boolean isFired(World world, BlockPos pos)
    {
        pos = pos.down();
        IBlockState state = world.getBlockState(pos);
        if (state.getBlock() instanceof BlockFirePit)
        {
            if (state.getValue(BlockFirePit.LIT))
            {
                TEFirePit te = Helpers.getTE(world, pos, TEFirePit.class);
                if (te != null)
                {
                    IItemHandler cap = te.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);
                    if (cap != null)
                    {
                        for (int i = TEFirePit.SLOT_FUEL_CONSUME; i <= TEFirePit.SLOT_FUEL_INPUT; i++)
                        {
                            ItemStack stack = cap.getStackInSlot(i);
                            if (stack.isEmpty() || OreDictionaryHelper.doesStackMatchOre(stack, "logWood")) continue;
                            return false;
                        }
                        return true;
                    }
                }
            }
        }
        return false;
    }

    @Override
    @SuppressWarnings("deprecation")
    @Nonnull
    public IBlockState getStateForPlacement(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer)
    {
        return getStateForPlacement(worldIn, placer, facing, pos);
    }

    public IBlockState getStateForPlacement(World world, EntityLivingBase placer, EnumFacing facing, BlockPos pos)
    {
        if (facing.getAxis() == EnumFacing.Axis.Y)
        {
            facing = placer.getHorizontalFacing();
        }
        IBlockState offState = world.getBlockState(pos.offset(facing));
        if (offState.getBlock() instanceof BlockString)
        {
            if (facing.getAxis() != offState.getValue(AXIS))
                facing = facing.rotateYCCW();
        }
        return getDefaultState().withProperty(AXIS, facing.getAxis());
    }

    @Override
    public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack)
    {
        TEString tile = Helpers.getTE(worldIn, pos, TEString.class);
        if (tile != null)
        {
            tile.resetCounter();
        }
        super.onBlockPlacedBy(worldIn, pos, state, placer, stack);
    }

    @Override
    @Nonnull
    protected BlockStateContainer createBlockState()
    {
        return new BlockStateContainer(this, AXIS);
    }

    @Override
    public boolean hasTileEntity(IBlockState state)
    {
        return true;
    }

    @Override
    @Nullable
    public TileEntity createTileEntity(World world, IBlockState state)
    {
        return new TEString();
    }
}
