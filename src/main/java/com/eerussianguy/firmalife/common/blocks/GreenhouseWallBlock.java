package com.eerussianguy.firmalife.common.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.AbstractGlassBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;

import net.dries007.tfc.util.Helpers;

public class GreenhouseWallBlock extends AbstractGlassBlock
{
    // these properties indicate the lack of another of the same wall above this.
    public static final BooleanProperty UP = BlockStateProperties.UP;
    public static final BooleanProperty DOWN = BlockStateProperties.DOWN;

    public GreenhouseWallBlock(Properties properties)
    {
        super(properties);
        registerDefaultState(getStateDefinition().any().setValue(UP, false).setValue(DOWN, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        builder.add(UP, DOWN);
    }

        @Override
    @SuppressWarnings("deprecation")
    public BlockState updateShape(BlockState state, Direction facing, BlockState facingState, LevelAccessor level, BlockPos currentPos, BlockPos facingPos)
    {
        if (facing == Direction.UP && !Helpers.isBlock(facingState, this))
        {
            state = state.setValue(UP, true);
        }
        else if (facing == Direction.DOWN && !Helpers.isBlock(facingState, this))
        {
            state = state.setValue(DOWN, true);
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
}
