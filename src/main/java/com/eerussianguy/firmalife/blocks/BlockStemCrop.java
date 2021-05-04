package com.eerussianguy.firmalife.blocks;

import java.util.Random;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.block.Block;
import net.minecraft.block.BlockStem;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import com.eerussianguy.firmalife.init.StemCrop;
import com.eerussianguy.firmalife.te.TEStemCrop;
import mcp.MethodsReturnNonnullByDefault;
import net.dries007.tfc.objects.blocks.agriculture.BlockCropSimple;
import net.dries007.tfc.util.Helpers;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public abstract class BlockStemCrop extends BlockCropSimple
{
    public static PropertyDirection FACING = BlockStem.FACING;

    //static factory method is required because
    //superconstructor requires getStageProperty to work
    //but we can't set the property until after the superconstructor
    //this is the workaround.
    public static BlockStemCrop create(StemCrop crop)
    {
        PropertyInteger property = STAGE_MAP.get(crop.getMaxStage() + 1);
        return new BlockStemCrop(crop)
        {
            @Override
            public PropertyInteger getStageProperty()
            {
                return property;
            }
        };
    }


    public BlockStemCrop(StemCrop crop)
    {
        super(crop, false);
        setDefaultState(getDefaultState().withProperty(FACING, EnumFacing.UP));
    }

    @Override
    public TileEntity createTileEntity(World world, IBlockState state) { return new TEStemCrop(); }

    @Override
    @SuppressWarnings("deprecation")
    public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos)
    {
        TEStemCrop te = Helpers.getTE(worldIn, pos, TEStemCrop.class);
        if (te != null)
            return state.withProperty(FACING, te.getFruitDirection());
        //even though the existing stemcrops only show direction when fully grown, this is made available
        //so that subclasses can have directionality while growing if they want
        return state;
    }


    @Override
    public void grow(World world, BlockPos cropPos, IBlockState cropState, Random random)
    {
        if (!world.isRemote)
        {
            //if penultimate stage
            PropertyInteger stageProperty = getStageProperty();
            if (cropState.getProperties().containsKey(stageProperty) && cropState.getValue(stageProperty) == getCrop().getMaxStage() - 1)
            {
                TEStemCrop te = Helpers.getTE(world, cropPos, TEStemCrop.class);
                EnumFacing fruitDirection = te.getFruitDirection();
                BlockPos fruitPos = cropPos.offset(fruitDirection);
                StemCrop crop = (StemCrop) getCrop();
                if (crop.getCropBlock().canPlaceBlockAt(world, fruitPos))
                {
                    IBlockState fruitState = crop.getCropBlock().getDefaultState().withProperty(BlockStemFruit.FACING, fruitDirection.getOpposite());
                    world.setBlockState(fruitPos, fruitState);
                    super.grow(world, cropPos, cropState, random);
                }
            }
            else
            {
                super.grow(world, cropPos, cropState, random);
            }
        }
    }

    @Override
    @Nonnull
    protected BlockStateContainer createBlockState()
    {
        return new BlockStateContainer(this, getStageProperty(), WILD, FACING);
    }

    @Override
    public int getMetaFromState(IBlockState state)
    {
        return super.getMetaFromState(state);
    }


    @Override
    public abstract PropertyInteger getStageProperty();


    @Override
    @SuppressWarnings("deprecation")
    public Vec3d getOffset(IBlockState state, IBlockAccess worldIn, BlockPos pos)
    {
        Vec3d offset = super.getOffset(state, worldIn, pos);
        double x = offset.x, z = offset.z;
        switch (state.getValue(FACING))
        {
            case EAST:
                x = Math.abs(x);
                break;
            case WEST:
                x = -Math.abs(x);
                break;
            case NORTH:
                z = -Math.abs(z);
                break;
            case SOUTH:
                z = Math.abs(z);
                break;
            default:
                break;
        }
        return new Vec3d(x, 0.0D, z);
    }

    @Override
    public void onBlockAdded(World worldIn, BlockPos pos, IBlockState state)
    {
        super.onBlockAdded(worldIn, pos, state);
        //if adding a fully grown and wild crop
        if (state.getValue(getStageProperty()) == getCrop().getMaxStage() && state.getValue(WILD))
        {
            TEStemCrop te = Helpers.getTE(worldIn, pos, TEStemCrop.class);
            EnumFacing fruitDirection = te.getFruitDirection();
            BlockPos targetPos = pos.offset(fruitDirection);
            StemCrop crop = (StemCrop) getCrop();
            if (crop.getCropBlock().canPlaceBlockAt(worldIn, targetPos)) //spawn fruit
            {
                worldIn.setBlockState(targetPos, crop.getCropBlock().getDefaultState().withProperty(BlockStemFruit.FACING, fruitDirection.getOpposite()));
            }
            else //revert back a stage
            {
                worldIn.setBlockState(pos, state.withProperty(getStageProperty(), getCrop().getMaxStage() - 1));
            }
        }
    }

    @Override
    public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block block, BlockPos npos)
    {
        super.neighborChanged(state, worldIn, pos, block, npos);

    }

}
