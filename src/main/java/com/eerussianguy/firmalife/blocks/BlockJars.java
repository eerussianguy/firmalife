package com.eerussianguy.firmalife.blocks;

import java.util.function.Supplier;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.items.ItemHandlerHelper;

import com.eerussianguy.firmalife.init.StatePropertiesFL;
import com.eerussianguy.firmalife.te.TEHangingPlanter;
import mcp.MethodsReturnNonnullByDefault;
import net.dries007.tfc.api.capability.size.IItemSize;
import net.dries007.tfc.api.capability.size.Size;
import net.dries007.tfc.api.capability.size.Weight;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class BlockJars extends BlockNonCube implements IItemSize
{
    public static final PropertyInteger JARS = StatePropertiesFL.JARS;
    private static final AxisAlignedBB SHAPE = new AxisAlignedBB(2D / 16, 0D, 2D / 16, 14D / 16, 6D / 16, 14D / 16);
    private final Supplier<? extends Item> item;

    public BlockJars(Supplier<? extends Item> item)
    {
        super(Material.GLASS);
        setResistance(1.0f);
        setHardness(1.0f);
        setLightOpacity(0);
        setDefaultState(getBlockState().getBaseState().withProperty(JARS, 1));
        this.item = item;
    }

    @Override
    public void getDrops(NonNullList<ItemStack> drops, IBlockAccess world, BlockPos pos, IBlockState state, int fortune)
    {
        drops.add(new ItemStack(item.get(), state.getValue(JARS)));
    }

    @Override
    @SuppressWarnings("deprecation")
    public IBlockState getStateFromMeta(int meta)
    {
        return getDefaultState().withProperty(JARS, meta + 1);
    }

    @Override
    public int getMetaFromState(IBlockState state)
    {
        return state.getValue(JARS) - 1;
    }

    @Override
    @SuppressWarnings("deprecation")
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos)
    {
        return SHAPE;
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

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ)
    {
        if (world.isRemote || hand == EnumHand.OFF_HAND) return false;

        ItemStack held = player.getHeldItem(hand);
        int jars = state.getValue(JARS);
        if (held.isEmpty() && player.isSneaking())
        {
            ItemHandlerHelper.giveItemToPlayer(player, new ItemStack(item.get()));
            jars--;
            if (jars <= 0)
            {
                world.destroyBlock(pos, false);
            }
            else
            {
                world.setBlockState(pos, state.withProperty(JARS, jars));
            }
            return true;
        }
        return false;
    }

    @Override
    protected BlockStateContainer createBlockState()
    {
        return new BlockStateContainer(this, JARS);
    }

    @Override
    @Nonnull
    public Size getSize(@Nonnull ItemStack stack)
    {
        return Size.VERY_LARGE;
    }

    @Override
    @Nonnull
    public Weight getWeight(@Nonnull ItemStack stack)
    {
        return Weight.MEDIUM;
    }

    private boolean canStay(IBlockAccess world, BlockPos pos)
    {
        IBlockState state = world.getBlockState(pos.down());
        return state.getBlockFaceShape(world, pos.down(), EnumFacing.UP) == BlockFaceShape.SOLID;
    }
}
