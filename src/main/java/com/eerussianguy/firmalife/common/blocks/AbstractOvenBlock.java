package com.eerussianguy.firmalife.common.blocks;

import java.util.Random;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;

import com.eerussianguy.firmalife.client.FLClientHelpers;
import com.eerussianguy.firmalife.common.FLTags;
import net.dries007.tfc.common.blocks.ExtendedProperties;
import net.dries007.tfc.util.Helpers;

public abstract class AbstractOvenBlock extends FourWayDeviceBlock
{
    public static boolean insulated(LevelAccessor level, BlockPos pos, BlockState state)
    {
        BlockPos.MutableBlockPos mutable = new BlockPos.MutableBlockPos();
        for (Direction d : Direction.Plane.HORIZONTAL)
        {
            if (d != state.getValue(FACING))
            {
                mutable.set(pos).move(d);
                BlockState stateAt = level.getBlockState(mutable);
                if (!Helpers.isBlock(stateAt, FLTags.Blocks.OVEN_INSULATION))
                {
                    return false;
                }
            }
        }
        return true;
    }

    public static void cure(Level level, BlockPos pos, boolean myself)
    {
        BlockPos.MutableBlockPos mutable = new BlockPos.MutableBlockPos();
        for (Direction d : Helpers.DIRECTIONS)
        {
            mutable.set(pos).relative(d);
            BlockState state = level.getBlockState(mutable);
            if (state.hasProperty(CURED) && !state.getValue(CURED))
            {
                level.setBlockAndUpdate(mutable, state.setValue(CURED, true));
            }
        }
        if (myself)
        {
            level.setBlockAndUpdate(pos, level.getBlockState(pos).setValue(CURED, true));
        }
    }

    public static final BooleanProperty CURED = FLStateProperties.CURED;

    public AbstractOvenBlock(ExtendedProperties properties)
    {
        super(properties, InventoryRemoveBehavior.DROP);
        registerDefaultState(getStateDefinition().any().setValue(CURED, false).setValue(FACING, Direction.NORTH));
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, Random random)
    {
        FLClientHelpers.randomParticle(ParticleTypes.SMOKE, random, pos, level, 0.1f);
        FLClientHelpers.randomParticle(ParticleTypes.FLAME, random, pos, level, 0.1f);

        pos = pos.relative(state.getValue(FACING).getOpposite());
        BlockState stateAt = level.getBlockState(pos);
        if (Helpers.isBlock(stateAt, FLTags.Blocks.CHIMNEYS))
        {
            BlockPos.MutableBlockPos mutable = new BlockPos.MutableBlockPos();
            do
            {
                mutable.move(Direction.UP);
                stateAt = level.getBlockState(mutable);
            }
            while (Helpers.isBlock(stateAt, FLTags.Blocks.CHIMNEYS));
            FLClientHelpers.randomParticle(ParticleTypes.CAMPFIRE_COSY_SMOKE, random, mutable, level, 0.4f);
        }
        else
        {
            pos = pos.offset(Helpers.triangle(random, 3), random.nextInt(3), Helpers.triangle(random, 3));
            if (level.getBlockState(pos).isAir())
            {
                FLClientHelpers.randomParticle(ParticleTypes.CAMPFIRE_COSY_SMOKE, random, pos, level, 0.3f);
            }
        }
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        super.createBlockStateDefinition(builder.add(CURED));
    }
}
