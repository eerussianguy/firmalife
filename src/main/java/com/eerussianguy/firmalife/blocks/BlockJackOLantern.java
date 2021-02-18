package com.eerussianguy.firmalife.blocks;

import java.util.Random;
import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import mcp.MethodsReturnNonnullByDefault;
import net.dries007.tfc.ConfigTFC;
import net.dries007.tfc.api.capability.size.IItemSize;
import net.dries007.tfc.api.capability.size.Size;
import net.dries007.tfc.api.capability.size.Weight;
import net.dries007.tfc.objects.CreativeTabsTFC;
import net.dries007.tfc.objects.advancements.TFCTriggers;
import net.dries007.tfc.objects.blocks.BlockTorchTFC;
import net.dries007.tfc.objects.blocks.property.ILightableBlock;
import net.dries007.tfc.objects.te.TETickCounter;
import net.dries007.tfc.util.Helpers;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class BlockJackOLantern extends BlockHorizontal implements IItemSize, ILightableBlock
{
    private final Carving carving;

    public BlockJackOLantern(Carving carving)
    {
        super(Material.GOURD);
        setDefaultState(blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH).withProperty(LIT, false));
        setTickRandomly(true);
        setCreativeTab(CreativeTabsTFC.CT_DECORATIONS);
        setHardness(1f);
        setLightLevel(0.75f);
        setTickRandomly(true);
        this.carving = carving;
    }

    @Override
    public Size getSize(ItemStack stack)
    {
        return Size.LARGE;
    }

    @Override
    public Weight getWeight(ItemStack stack)
    {
        return Weight.HEAVY;
    }

    public boolean canPlaceBlockAt(World worldIn, BlockPos pos)
    {
        return worldIn.getBlockState(pos).getBlock().isReplaceable(worldIn, pos) && worldIn.isSideSolid(pos.down(), EnumFacing.UP);
    }

    @SuppressWarnings("deprecation")
    public IBlockState getStateForPlacement(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer)
    {
        return this.getDefaultState().withProperty(FACING, placer.getHorizontalFacing().getOpposite());
    }

    @SuppressWarnings("deprecation")
    public IBlockState withRotation(IBlockState state, Rotation rot)
    {
        return state.withProperty(FACING, rot.rotate(state.getValue(FACING)));
    }

    @SuppressWarnings("deprecation")
    public IBlockState withMirror(IBlockState state, Mirror mirrorIn)
    {
        return state.withRotation(mirrorIn.toRotation(state.getValue(FACING)));
    }

    @SuppressWarnings("deprecation")
    public IBlockState getStateFromMeta(int meta)
    {
        return getDefaultState().withProperty(FACING, EnumFacing.byHorizontalIndex(meta)).withProperty(LIT, meta >= 4);
    }

    public int getMetaFromState(IBlockState state)
    {
        return (state.getValue(LIT) ? 4 : 0) + state.getValue(FACING).getHorizontalIndex();
    }

    protected BlockStateContainer createBlockState()
    {
        return new BlockStateContainer(this, FACING, LIT);
    }

    public void randomTick(World worldIn, BlockPos pos, IBlockState state, Random random)
    {
        //taken from BlockTorchTFC
        TETickCounter te = Helpers.getTE(worldIn, pos, TETickCounter.class);
        if (te != null)
        {
            //last twice as long as a torch. balance this by being less bright
            if (!worldIn.isRemote && te.getTicksSinceUpdate() > (2 * ConfigTFC.General.OVERRIDES.torchTime) && ConfigTFC.General.OVERRIDES.torchTime > 0)
            {
                worldIn.setBlockState(pos, state.withProperty(LIT, false));
                te.resetCounter();
            }
        }
    }

    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
    {
        //taken from BlockTorchTFC
        if (!worldIn.isRemote)
        {
            ItemStack stack = playerIn.getHeldItem(hand);
            TETickCounter tile = Helpers.getTE(worldIn, pos, TETickCounter.class);
            if (BlockTorchTFC.canLight(stack))
            {
                TFCTriggers.LIT_TRIGGER.trigger((EntityPlayerMP) playerIn, state.getBlock()); // Trigger lit block
                worldIn.setBlockState(pos, worldIn.getBlockState(pos).withProperty(LIT, true));
                if (tile != null)
                    tile.resetCounter();
            }
            else
            {
                worldIn.setBlockState(pos, state.withProperty(LIT, false));
            }
        }
        return true;
    }

    public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack)
    {
        //taken from BlockTorchTFC
        // Set the initial counter value
        TETickCounter tile = Helpers.getTE(worldIn, pos, TETickCounter.class);
        if (tile != null)
        {
            tile.resetCounter();
        }
        super.onBlockPlacedBy(worldIn, pos, state, placer, stack);
    }

    public int getLightValue(IBlockState state, IBlockAccess world, BlockPos pos)
    {
        return state.getValue(LIT) ? super.getLightValue(state, world, pos) : 0;
    }

    public boolean hasTileEntity(IBlockState state)
    {
        return true;
    }

    public TileEntity createTileEntity(World world, IBlockState state)
    {
        return new TETickCounter();
    }

    public Carving getCarving()
    {
        return carving;
    }

    public enum Carving implements IStringSerializable
    {
        NONE("none", "XXXXX", "XXXXX", "XXXXX", "XXXXX", "X   X"),
        CIRCLE("circle", "XX XX", "X   X", "     ", "X   X", "XX XX"),
        FACE("face", "XXXXX", "X X X", "XXXXX", "X   X", "XXXXX"),
        CREEPER("creeper", "XXXXX", "X X X", "XX XX", "X   X", "X X X"),
        AXE("axe", "X XXX", "    X", "     ", "    X", "X XXX"),
        HAMMER("hammer", "XXXXX", "     ", "     ", "XX XX", "XXXXX"),
        PICKAXE("pickaxe", "XXXXX", "X   X", " XXX ", "XXXXX", "XXXXX");

        private final String name;
        private final String[] craftPattern;

        Carving(String name, String... pattern)
        {
            this.name = name;
            this.craftPattern = pattern;
        }

        public String getName()
        {
            return name;
        }

        public String[] getCraftPattern()
        {
            return craftPattern;
        }
    }
}
