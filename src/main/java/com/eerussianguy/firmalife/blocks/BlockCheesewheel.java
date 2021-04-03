package com.eerussianguy.firmalife.blocks;

import java.util.Random;
import java.util.function.Supplier;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.ItemHandlerHelper;

import com.eerussianguy.firmalife.init.AgingFL;
import mcp.MethodsReturnNonnullByDefault;
import net.dries007.tfc.api.capability.size.IItemSize;
import net.dries007.tfc.api.capability.size.Size;
import net.dries007.tfc.api.capability.size.Weight;
import net.dries007.tfc.util.OreDictionaryHelper;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class BlockCheesewheel extends Block implements IItemSize
{
    public static final PropertyInteger WEDGES = PropertyInteger.create("wedges", 0, 3);
    public static final PropertyEnum AGE = PropertyEnum.create("age", AgingFL.class);
    protected static final AxisAlignedBB CHEESEWHEEL_AABB = new AxisAlignedBB(0.0625D, 0.0D, 0.0625D, 0.9375D, 0.5D, 0.9375D);

    private final Supplier<? extends Item> item;

    // TODO: Implement aging process
    // TODO: Custom model and condensed textures
    public BlockCheesewheel(Supplier<? extends Item> item)
    {
        super(Material.CAKE);
        this.setDefaultState(this.blockState.getBaseState().withProperty(WEDGES, 0).withProperty(AGE, AgingFL.FRESH));
        this.setTickRandomly(true);
        this.item = item;
    }

    @Override
    @SuppressWarnings("deprecation")
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos)
    {
        return CHEESEWHEEL_AABB;
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand handIn, EnumFacing facing, float hitX, float hitY, float hitZ)
    {
        ItemStack stack = playerIn.getHeldItem(handIn);
        if (OreDictionaryHelper.doesStackMatchOre(stack, "knife"))
        {
            if (!worldIn.isRemote)
            {
                stack.damageItem(1, playerIn);
                return this.cutCheese(worldIn, pos, state, playerIn);
            }
        }
        return true;
    }

    private boolean cutCheese(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn)
    {
        int i = state.getValue(WEDGES);
        if (i < 3)
            worldIn.setBlockState(pos, state.withProperty(WEDGES, i + 1), 3);
        else
            worldIn.setBlockToAir(pos);

        ItemHandlerHelper.giveItemToPlayer(playerIn, new ItemStack(item.get()));
        return true;
    }

    public boolean canPlaceBlockAt(World worldIn, BlockPos pos) {
        return super.canPlaceBlockAt(worldIn, pos) && this.canBlockStay(worldIn, pos);
    }

    @SuppressWarnings("deprecation")
    @Override
    public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos)
    {
        if (!this.canBlockStay(worldIn, pos))
        {
            worldIn.setBlockToAir(pos);
        }
    }

    @SuppressWarnings("deprecation")
    @Override
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }

    @SuppressWarnings("deprecation")
    @Override
    public boolean isFullCube(IBlockState state) {
        return false;
    }

    private boolean canBlockStay(World worldIn, BlockPos pos) {
        return worldIn.getBlockState(pos.down()).getMaterial().isSolid();
    }

    @SideOnly(Side.CLIENT)
    @Override
    public BlockRenderLayer getRenderLayer() {
        return BlockRenderLayer.CUTOUT;
    }

    @SuppressWarnings("deprecation")
    @Override
    public IBlockState getStateFromMeta(int meta) {
        return this.getDefaultState().withProperty(WEDGES, meta % 4).withProperty(AGE, AgingFL.values()[meta / 4]);
    }


    @Override
    public int getMetaFromState(IBlockState state)
    {
        AgingFL age = (AgingFL) state.getValue(AGE);
        return state.getValue(WEDGES) + age.getID();
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, WEDGES, AGE);
    }

    @Override
    public Item getItemDropped(IBlockState state, Random rand, int fortune) {
        return Items.AIR;
    }

    @Override
    @Nonnull
    public Size getSize(@Nonnull ItemStack stack) {
        return Size.LARGE;
    }

    @Override
    @Nonnull
    public Weight getWeight(@Nonnull ItemStack stack) {
        return Weight.HEAVY;
    }
}
