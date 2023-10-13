package com.eerussianguy.firmalife.common.blocks;

import java.util.Random;
import java.util.function.Supplier;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.RandomSource;
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
import org.jetbrains.annotations.Nullable;

public abstract class AbstractOvenBlock extends FourWayDeviceBlock implements ICure
{
    @Nullable
    public static BlockPos locateChimney(LevelAccessor level, BlockPos pos, BlockState state)
    {
        final BlockPos.MutableBlockPos mutable = new BlockPos.MutableBlockPos();
        final Direction back = state.getValue(FACING).getOpposite();
        final Direction left = back.getClockWise();
        pos = pos.relative(back);

        for (int i = -2; i <= 2; i++)
        {
            mutable.set(pos).move(left, i);
            BlockState stateAt = level.getBlockState(mutable);
            if (Helpers.isBlock(stateAt, FLTags.Blocks.CHIMNEYS))
            {
                do
                {
                    mutable.move(Direction.UP);
                    stateAt = level.getBlockState(mutable);
                }
                while (Helpers.isBlock(stateAt, FLTags.Blocks.CHIMNEYS));
                return mutable.immutable();
            }
        }
        return null;
    }

    public static void cureAllAround(Level level, BlockPos pos, boolean myself)
    {
        BlockPos.MutableBlockPos mutable = new BlockPos.MutableBlockPos();
        for (Direction d : Helpers.DIRECTIONS)
        {
            mutable.set(pos).move(d);
            BlockState state = level.getBlockState(mutable);
            if (state.getBlock() instanceof ICure oven)
            {
                oven.cure(level, state, mutable);
            }
        }
        BlockState myState = level.getBlockState(pos);
        if (myself && myState.getBlock() instanceof ICure oven)
        {
            oven.cure(level, myState, pos);
        }
    }

    public static final BooleanProperty HAS_CHIMNEY = FLStateProperties.HAS_CHIMNEY;

    @Nullable
    private final Supplier<? extends Block> curedBlock;

    public AbstractOvenBlock(ExtendedProperties properties, @Nullable Supplier<? extends Block> curedBlock)
    {
        super(properties, InventoryRemoveBehavior.DROP);
        this.curedBlock = curedBlock;
    }

    public boolean isInsulated(LevelAccessor level, BlockPos pos, BlockState state)
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

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random)
    {
        FLClientHelpers.randomParticle(ParticleTypes.SMOKE, random, pos, level, 0.05f);
        FLClientHelpers.randomParticle(ParticleTypes.FLAME, random, pos, level, 0.05f);

        if (state.getValue(FLStateProperties.HAS_CHIMNEY))
        {
            pos = locateChimney(level, pos, state);
            if (pos != null)
            {
                FLClientHelpers.randomParticle(ParticleTypes.CAMPFIRE_COSY_SMOKE, random, pos, level, 0.05f);
            }
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
    @Nullable
    public Block getCured()
    {
        return curedBlock == null ? null : curedBlock.get();
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        super.createBlockStateDefinition(builder.add(HAS_CHIMNEY));
    }

}
