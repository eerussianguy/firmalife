package com.eerussianguy.firmalife.common.blocks.greenhouse;

import java.util.function.Supplier;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.AbstractGlassBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;

import com.eerussianguy.firmalife.common.FLTags;
import com.eerussianguy.firmalife.common.blocks.IWeatherable;
import net.dries007.tfc.common.blocks.ExtendedProperties;
import net.dries007.tfc.common.blocks.IForgeBlockExtension;
import net.dries007.tfc.util.Helpers;
import org.jetbrains.annotations.Nullable;

public class GreenhouseWallBlock extends AbstractGlassBlock implements IWeatherable, IForgeBlockExtension
{
    // these properties indicate the lack of another of the same wall above this.
    public static final BooleanProperty UP = BlockStateProperties.UP;
    public static final BooleanProperty DOWN = BlockStateProperties.DOWN;

    @Nullable
    private final Supplier<? extends Block> next;
    private final ExtendedProperties properties;

    public GreenhouseWallBlock(ExtendedProperties properties, @Nullable Supplier<? extends Block> next)
    {
        super(properties.properties());
        this.next = next;
        this.properties = properties;
        registerDefaultState(getStateDefinition().any().setValue(UP, false).setValue(DOWN, false));
    }

    @Override
    public boolean isRandomlyTicking(BlockState state)
    {
        return hasNext();
    }

    @Override
    @SuppressWarnings("deprecation")
    public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource rand)
    {
        IWeatherable.super.randomTick(state, level, pos, rand);
    }

    @Override
    @Nullable
    public Supplier<? extends Block> getNext()
    {
        return next;
    }

    @Override
    public ExtendedProperties getExtendedProperties()
    {
        return properties;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        super.createBlockStateDefinition(builder.add(UP, DOWN));
    }

    @Override
    @SuppressWarnings("deprecation")
    public BlockState updateShape(BlockState state, Direction facing, BlockState facingState, LevelAccessor level, BlockPos currentPos, BlockPos facingPos)
    {
        if (facing == Direction.UP)
        {
            state = state.setValue(UP, !connects(facingState));
        }
        else if (facing == Direction.DOWN)
        {
            state = state.setValue(DOWN, !connects(facingState));
        }
        return state;
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx)
    {
        final Level level = ctx.getLevel();
        final BlockPos pos = ctx.getClickedPos();
        return defaultBlockState().setValue(UP, !Helpers.isBlock(level.getBlockState(pos.above()), this)).setValue(DOWN, !Helpers.isBlock(level.getBlockState(pos.below()), this));
    }

    @Override
    public boolean skipRendering(BlockState state, BlockState adjacent, Direction side)
    {
        return connects(adjacent) && Helpers.isBlock(adjacent, FLTags.Blocks.GREENHOUSE);
    }

    public boolean connects(BlockState adjacent)
    {
        return Helpers.isBlock(adjacent, FLTags.Blocks.GREENHOUSE_FULL_WALLS);
    }
}
