package com.eerussianguy.firmalife.blocks;

import java.util.Random;
import java.util.function.Supplier;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.block.Block;
import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.properties.PropertyInteger;
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

import com.eerussianguy.firmalife.init.StatePropertiesFL;
import com.eerussianguy.firmalife.te.TEHangingPlanter;
import mcp.MethodsReturnNonnullByDefault;
import net.dries007.tfc.Constants;
import net.dries007.tfc.api.capability.size.IItemSize;
import net.dries007.tfc.api.capability.size.Size;
import net.dries007.tfc.api.capability.size.Weight;
import net.dries007.tfc.objects.te.TETickCounter;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.calendar.ICalendar;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class BlockHangingPlanter extends BlockNonCube implements IItemSize
{
    public static final PropertyEnum<EnumFacing.Axis> AXIS = StatePropertiesFL.XZ;
    public static final PropertyInteger STAGE = StatePropertiesFL.STAGE;

    private static final AxisAlignedBB SHAPE = new AxisAlignedBB(0.0D, 0.75D, 0.0D, 1.0D, 1.0D, 1.0D);

    private final Supplier<? extends Item> fruit;
    private final Supplier<? extends Item> seed;

    public BlockHangingPlanter(Supplier<? extends Item> fruit, Supplier<? extends Item> seed)
    {
        super(Material.IRON);
        setHardness(2.0f);
        setResistance(3.0f);
        setLightOpacity(0);
        setSoundType(SoundType.METAL);
        setTickRandomly(true);
        this.fruit = fruit;
        this.seed = seed;
        this.setDefaultState(this.getBlockState().getBaseState().withProperty(AXIS, EnumFacing.Axis.X).withProperty(STAGE, 0));
    }

    @Override
    @SuppressWarnings("deprecation")
    @Nonnull
    public IBlockState getStateFromMeta(int meta)
    {
        EnumFacing.Axis axis = EnumFacing.Axis.Z;
        if (meta > 2)
        {
            axis = EnumFacing.Axis.X;
            meta -= 3;
        }
        return getDefaultState().withProperty(AXIS, axis).withProperty(STAGE, meta);
    }

    @Override
    public int getMetaFromState(IBlockState state)
    {
        EnumFacing.Axis axis = state.getValue(AXIS); // 0, 3
        int stage = state.getValue(STAGE); // 0, 1, 2
        return stage + (axis == EnumFacing.Axis.X ? 3 : 0);
    }

    @Override
    @SuppressWarnings("deprecation")
    @Nonnull
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos)
    {
        return SHAPE;
    }

    @Override
    public void randomTick(World world, BlockPos pos, IBlockState state, Random random)
    {
        if (!world.isRemote)
        {
            TEHangingPlanter te = Helpers.getTE(world, pos, TEHangingPlanter.class);
            int stage = state.getValue(STAGE);
            if (te != null && te.isClimateValid() && te.getTicksSinceUpdate() >= (ICalendar.TICKS_IN_DAY * 13) && stage < 2)
            {
                world.setBlockState(pos, state.withProperty(STAGE, stage + 1));
                te.reduceCounter(ICalendar.TICKS_IN_DAY * 10);
            }
        }
    }

    @Override
    @SuppressWarnings("deprecation")
    public void neighborChanged(IBlockState state, World world, BlockPos pos, Block blockIn, BlockPos fromPos)
    {
        if (!world.isRemote)
        {
            for (EnumFacing d : EnumFacing.Plane.HORIZONTAL)
            {
                Block block = world.getBlockState(pos.offset(d)).getBlock();
                if (block instanceof BlockGreenhouseWall || block instanceof BlockHangingPlanter) return;
            }
            world.destroyBlock(pos, true);
        }
    }


    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ)
    {
        if (!world.isRemote)
        {
            TEHangingPlanter te = Helpers.getTE(world, pos, TEHangingPlanter.class);
            if (te == null) return false;
            ItemStack held = player.getHeldItem(hand);
            if (held.isEmpty() && state.getValue(STAGE) == 2)
            {
                Helpers.spawnItemStack(world, pos.down(), new ItemStack(fruit.get()));
                if (Constants.RNG.nextInt(7) == 0)
                    Helpers.spawnItemStack(world, pos.down(), new ItemStack(seed.get()));
                world.setBlockState(pos, state.withProperty(STAGE, 0));
                te.resetCounter();
                return true;
            }
        }
        return false;
    }

    @Override
    @SuppressWarnings("deprecation")
    @Nonnull
    public IBlockState getStateForPlacement(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer)
    {
        if (facing.getAxis() == EnumFacing.Axis.Y)
        {
            facing = placer.getHorizontalFacing();
        }
        IBlockState state = worldIn.getBlockState(pos.offset(facing));
        final Block block = state.getBlock();
        if (block instanceof BlockGreenhouseWall)
        {
            facing = state.getValue(BlockHorizontal.FACING);
        }
        else if (block instanceof BlockHangingPlanter)
        {
            return getDefaultState().withProperty(AXIS, state.getValue(AXIS));
        }
        return getDefaultState().withProperty(AXIS, facing.getAxis());
    }

    @Override
    public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack)
    {
        TETickCounter tile = Helpers.getTE(worldIn, pos, TETickCounter.class);
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
        return new BlockStateContainer(this, AXIS, STAGE);
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
        return new TEHangingPlanter();
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
        return Weight.MEDIUM;
    }
}
