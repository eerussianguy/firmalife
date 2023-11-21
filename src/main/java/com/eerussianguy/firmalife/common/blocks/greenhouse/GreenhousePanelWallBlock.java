package com.eerussianguy.firmalife.common.blocks.greenhouse;

import java.util.function.Supplier;
import com.eerussianguy.firmalife.common.FLTags;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.common.blocks.ExtendedProperties;
import net.dries007.tfc.util.Helpers;

public class GreenhousePanelWallBlock extends GreenhouseWallBlock
{
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;

    public static final VoxelShape[] SHAPES = Helpers.computeHorizontalShapes(d -> Helpers.rotateShape(d, 0, 0, 0, 16, 16, 2));

    public GreenhousePanelWallBlock(ExtendedProperties properties, @Nullable Supplier<? extends Block> next)
    {
        super(properties, next);
    }

    @Override
    @SuppressWarnings("deprecation")
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context)
    {
        return SHAPES[state.getValue(FACING).get2DDataValue()];
    }

    @Override
    @Nullable
    public BlockState getStateForPlacement(BlockPlaceContext ctx)
    {
        final BlockState state = super.getStateForPlacement(ctx);
        if (state != null)
        {
            final Level level = ctx.getLevel();
            final BlockState below = level.getBlockState(ctx.getClickedPos().below());
            if (below.getBlock() instanceof GreenhousePanelWallBlock)
            {
                return Helpers.copyProperties(state, below);
            }
            return state.setValue(FACING, ctx.getHorizontalDirection().getOpposite());
        }
        return null;
    }

    @Override
    @SuppressWarnings("deprecation")
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving)
    {
        super.onRemove(state, level, pos, newState, isMoving);
        fixPanelRoofs(level, pos);
    }

    @Override
    public BlockState updateShape(BlockState state, Direction facing, BlockState facingState, LevelAccessor level, BlockPos currentPos, BlockPos facingPos)
    {
        fixPanelRoofs(level, currentPos);
        return super.updateShape(state, facing, facingState, level, currentPos, facingPos);
    }

    private void fixPanelRoofs(LevelAccessor level, BlockPos pos)
    {
        for (Direction dir : Direction.Plane.HORIZONTAL)
        {
            final BlockPos relativePos = pos.above().relative(dir);
            final BlockState relativeState = level.getBlockState(relativePos);
            if (relativeState.getBlock() instanceof GreenhousePanelRoofBlock)
            {
                level.scheduleTick(relativePos, relativeState.getBlock(), 1);
            }
        }
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        super.createBlockStateDefinition(builder.add(FACING));
    }

    @Override
    @SuppressWarnings("deprecation")
    public BlockState rotate(BlockState state, Rotation rot)
    {
        return state.setValue(FACING, rot.rotate(state.getValue(FACING)));
    }

    @Override
    @SuppressWarnings("deprecation")
    public BlockState mirror(BlockState state, Mirror mirror)
    {
        return state.rotate(mirror.getRotation(state.getValue(FACING)));
    }

    @Override
    public boolean connects(BlockState adjacent)
    {
        return Helpers.isBlock(adjacent, FLTags.Blocks.GREENHOUSE_PANEL_WALLS);
    }
}
