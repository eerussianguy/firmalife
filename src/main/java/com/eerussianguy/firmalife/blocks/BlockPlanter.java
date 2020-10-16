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

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.ItemHandlerHelper;

import com.eerussianguy.firmalife.registry.ModRegistry;
import net.dries007.tfc.Constants;
import net.dries007.tfc.api.capability.size.IItemSize;
import net.dries007.tfc.api.capability.size.Size;
import net.dries007.tfc.api.capability.size.Weight;
import net.dries007.tfc.util.calendar.CalendarTFC;

import static com.eerussianguy.firmalife.init.StatePropertiesFL.CAN_GROW;
import static com.eerussianguy.firmalife.init.StatePropertiesFL.GROWN;

public class BlockPlanter extends Block implements IItemSize
{
    public static final AxisAlignedBB PLANTER_SHAPE = new AxisAlignedBB(0.25D, 0.0D, 0.25D, 0.75D, 1.0D, 0.75D);
    public static final AxisAlignedBB BOX_SHAPE = new AxisAlignedBB(0.25D, 0.0D, 0.25D, 0.75D, 0.25D, 0.75D);

    private final Supplier<Item> drop;
    private final int flowerDay;

    public BlockPlanter(Supplier<Item> drop, int flowerDay)
    {
        super(Material.PLANTS, MapColor.GREEN);
        setHardness(1.0f);
        setResistance(1.0f);
        setLightOpacity(0);
        setTickRandomly(true);
        this.flowerDay = flowerDay;
        this.drop = drop;
        this.setDefaultState(this.blockState.getBaseState().withProperty(GROWN, false).withProperty(CAN_GROW, true));
    }

    private boolean canGrow(World world, BlockPos pos, IBlockState state)
    {
        if (!world.canSeeSky(pos.up()))
        {
            if (world.isAirBlock(pos.up()))
            {
                if (world.getLight(pos.up()) > 10)
                {
                    return true;
                }
            }
        }
        return false;
    }


    @Override
    public void randomTick(World world, BlockPos pos, IBlockState state, Random random) {
        if (!world.isRemote)
        {
            if (!state.getValue(GROWN))
            {
                int day = CalendarTFC.CALENDAR_TIME.getDayOfMonth();
                if (day == flowerDay)
                {
                    if (canGrow(world, pos, state) && state.getValue(CAN_GROW))
                    {
                        world.setBlockState(pos, state.withProperty(GROWN, true).withProperty(CAN_GROW, false));
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
            if (state.getValue(GROWN))
            {
                world.setBlockState(pos, state.withProperty(GROWN, false).withProperty(CAN_GROW, false));
                ItemHandlerHelper.giveItemToPlayer(player, new ItemStack(drop.get(), 1 ));
            }
        }
        return true;
    }


    @Override
    public void getDrops(NonNullList<ItemStack> drops, IBlockAccess world, BlockPos pos, IBlockState state, int fortune)
    {
        drops.add(new ItemStack(ModRegistry.PLANTER));
        if (state.getValue(GROWN))
            drops.add(new ItemStack(drop.get()));
    }

    @Override
    @SuppressWarnings("deprecation")
    @Nonnull
    public IBlockState getStateFromMeta(int meta)
    {
        return this.getDefaultState().withProperty(GROWN, meta == 1);
    }

    @Override
    public int getMetaFromState(IBlockState state)
    {
        return state.getValue(GROWN) ? 1 : 0;
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
        return new BlockStateContainer(this, GROWN, CAN_GROW);
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
