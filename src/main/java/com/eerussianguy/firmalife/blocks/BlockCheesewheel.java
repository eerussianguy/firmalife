package com.eerussianguy.firmalife.blocks;

import java.util.Random;
import java.util.function.Supplier;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.block.Block;
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
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.ItemHandlerHelper;

import com.eerussianguy.firmalife.ConfigFL;
import com.eerussianguy.firmalife.init.AgingFL;
import com.eerussianguy.firmalife.init.StatePropertiesFL;
import mcp.MethodsReturnNonnullByDefault;
import net.dries007.tfc.api.capability.food.CapabilityFood;
import net.dries007.tfc.api.capability.food.FoodTrait;
import net.dries007.tfc.api.capability.size.IItemSize;
import net.dries007.tfc.api.capability.size.Size;
import net.dries007.tfc.api.capability.size.Weight;
import net.dries007.tfc.objects.te.TETickCounter;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.OreDictionaryHelper;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class BlockCheesewheel extends BlockNonCube implements IItemSize
{
    public static final PropertyInteger WEDGES = StatePropertiesFL.WEDGES;
    public static final PropertyEnum<AgingFL> AGE = StatePropertiesFL.AGE;
    protected static final AxisAlignedBB CHEESEWHEEL_AABB = new AxisAlignedBB(0.0625D, 0.0D, 0.0625D, 0.9375D, 0.5D, 0.9375D); // This could have a more complex bounding box

    private final Supplier<? extends Item> item;

    public BlockCheesewheel(Supplier<? extends Item> item)
    {
        super(Material.CAKE);
        this.setDefaultState(this.blockState.getBaseState().withProperty(WEDGES, 0).withProperty(AGE, AgingFL.FRESH));
        this.setTickRandomly(true);
        this.setHardness(1.0F);
        this.setSoundType(SoundType.CLOTH);
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
        int stateWedges = state.getValue(WEDGES);
        FoodTrait ageTrait = state.getValue(AGE).getTrait();

        if (stateWedges < 3)
        {
            worldIn.setBlockState(pos, state.withProperty(WEDGES, stateWedges + 1), 3);
        }
        else
        {
            worldIn.setBlockToAir(pos);
        }

        ItemStack output = new ItemStack(item.get());
        CapabilityFood.applyTrait(output, ageTrait);

        ItemHandlerHelper.giveItemToPlayer(playerIn, output);
        return true;
    }


    public boolean canPlaceBlockAt(World worldIn, BlockPos pos)
    {
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

    private boolean canBlockStay(World worldIn, BlockPos pos)
    {
        return worldIn.getBlockState(pos.down()).getMaterial().isSolid();
    }

    public void randomTick(World worldIn, BlockPos pos, IBlockState state, Random random)
    {
        TETickCounter te = Helpers.getTE(worldIn, pos, TETickCounter.class);
        if (te != null)
        {
            if (!worldIn.isRemote)
            {
                long ticksSinceUpdate = te.getTicksSinceUpdate();
                // If the cheese isn't cut and ready to age
                if (state.getValue(AGE) == AgingFL.FRESH && state.getValue(WEDGES) == 0 && ticksSinceUpdate > ConfigFL.General.BALANCE.cheeseTicksToAged)
                {
                    worldIn.setBlockState(pos, state.withProperty(AGE, AgingFL.AGED));
                    te.resetCounter();
                }
                else if (state.getValue(AGE) == AgingFL.AGED && state.getValue(WEDGES) == 0 && ticksSinceUpdate > ConfigFL.General.BALANCE.cheeseTicksToVintage)
                {
                    worldIn.setBlockState(pos, state.withProperty(AGE, AgingFL.VINTAGE));
                }
            }
        }
    }

    public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack)
    {
        //taken from BlockJackOLantern which in turn was taken from BlockTorchTFC
        // Set the initial counter value
        TETickCounter tile = Helpers.getTE(worldIn, pos, TETickCounter.class);
        if (tile != null)
            tile.resetCounter();

        super.onBlockPlacedBy(worldIn, pos, state, placer, stack);
    }

    @SuppressWarnings("deprecation")
    @Override
    public IBlockState getStateFromMeta(int meta)
    {
        return this.getDefaultState().withProperty(WEDGES, meta % 4).withProperty(AGE, AgingFL.values()[meta / 4]);
    }


    @Override
    public int getMetaFromState(IBlockState state)
    {
        AgingFL age = state.getValue(AGE);
        return state.getValue(WEDGES) + age.getID();
    }

    @Override
    public boolean hasTileEntity(IBlockState state)
    {
        return true;
    }

    @Override
    public TileEntity createTileEntity(World world, IBlockState state)
    {
        return new TETickCounter();
    }

    @Override
    protected BlockStateContainer createBlockState()
    {
        return new BlockStateContainer(this, WEDGES, AGE);
    }

    @Override
    public void getDrops(NonNullList<ItemStack> drops, IBlockAccess world, BlockPos pos, IBlockState state, int fortune)
    {
        ItemStack cheese = new ItemStack(item.get(), 4 - state.getValue(WEDGES));
        CapabilityFood.applyTrait(cheese, state.getValue(AGE).getTrait());

        drops.add(cheese);
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
        return Weight.HEAVY;
    }
}
