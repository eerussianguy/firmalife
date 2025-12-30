package com.eerussianguy.firmalife.common.blocks.greenhouse;

import java.util.Set;
import java.util.function.Supplier;
import com.eerussianguy.firmalife.common.FLHelpers;
import com.eerussianguy.firmalife.common.FLTags;
import com.eerussianguy.firmalife.common.blocks.FLStateProperties;
import com.eerussianguy.firmalife.common.blocks.IWeatherable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.BlockItem;
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
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.common.blocks.ExtendedProperties;
import net.dries007.tfc.common.blocks.IForgeBlockExtension;
import net.dries007.tfc.util.Helpers;

public class GreenhousePanelWallBlock extends BaseGreenhouseBlock implements IWeatherable, IForgeBlockExtension, GreenhouseConnectable
{
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
    public static final BooleanProperty LEFT = FLStateProperties.LEFT;
    public static final BooleanProperty RIGHT = FLStateProperties.RIGHT;
    public static final BooleanProperty DOWN = BlockStateProperties.DOWN;
    public static final EnumProperty<PostType> UP = EnumProperty.create("up", PostType.class);
    public static final EnumProperty<SideType> EXTRA_WALL = EnumProperty.create("extra", SideType.class);

    public static final VoxelShape[] SHAPES = Helpers.computeHorizontalShapes(d -> Helpers.rotateShape(d, 0, 0, 0, 16, 16, 2));
    public static final VoxelShape[] LEFT_SHAPES = Helpers.computeHorizontalShapes(dir ->
        Shapes.join(
            SHAPES[dir.get2DDataValue()],
            Helpers.rotateShape(dir, 14, 0, 0, 16, 16, 16),
            BooleanOp.OR
        )
    );
    public static final VoxelShape[] RIGHT_SHAPES = Helpers.computeHorizontalShapes(dir ->
        Shapes.join(
            SHAPES[dir.get2DDataValue()],
            Helpers.rotateShape(dir, 0, 0, 0, 2, 16, 16),
            BooleanOp.OR
        )
    );

    public GreenhousePanelWallBlock(ExtendedProperties properties, @Nullable Supplier<? extends Block> next)
    {
        super(properties, next);
        registerDefaultState(getStateDefinition().any().setValue(UP, PostType.BOTH).setValue(DOWN, false).setValue(LEFT, false).setValue(RIGHT, false).setValue(EXTRA_WALL, SideType.NONE));
    }

    @Override
    public boolean skipRendering(BlockState state, BlockState adjacent, Direction side)
    {
        return connects(adjacent) && Helpers.isBlock(adjacent, FLTags.Blocks.GREENHOUSE);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context)
    {
        final int facing = state.getValue(FACING).get2DDataValue();
        return switch (state.getValue(EXTRA_WALL))
        {
            case NONE -> SHAPES[facing];
            case LEFT -> LEFT_SHAPES[facing];
            case RIGHT -> RIGHT_SHAPES[facing];
        };
    }

    @Override
    @Nullable
    public BlockState getStateForPlacement(BlockPlaceContext context)
    {
        final BlockPos pos = context.getClickedPos();
        final Level level = context.getLevel();
        BlockState currentState = level.getBlockState(pos);
        if (currentState.is(this) && currentState.getValue(EXTRA_WALL) == SideType.NONE)
        {
            Direction facing = currentState.getValue(FACING);
            Direction playerFacing = context.getHorizontalDirection();
            if (facing.getClockWise() == playerFacing)
            {
                return updateConnections(currentState.setValue(EXTRA_WALL, SideType.RIGHT), pos, level, Direction.UP);
            }
            if (facing.getCounterClockWise() == playerFacing)
            {
                return updateConnections(currentState.setValue(EXTRA_WALL, SideType.LEFT), pos, level, Direction.UP);
            }
            return null;
        }
        BlockState state = super.getStateForPlacement(context);
        if (state != null)
        {
            final Direction facing = context.getHorizontalDirection().getOpposite();
            state = updateConnections(state.setValue(FACING, facing), pos, level, facing.getClockWise(), facing.getCounterClockWise(), Direction.DOWN, Direction.UP);
            final BlockState below = level.getBlockState(pos.below());
            if (below.getBlock() instanceof GreenhousePanelWallBlock)
            {
                state = FLHelpers.copyProperties(state, below, LEFT, RIGHT);
            }
            return state;
        }
        return null;
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving)
    {
        super.onRemove(state, level, pos, newState, isMoving);
        fixPanelRoofs(level, pos);
    }

    @Override
    public BlockState updateShape(BlockState state, Direction facing, BlockState facingState, LevelAccessor level, BlockPos currentPos, BlockPos facingPos)
    {
        fixPanelRoofs(level, currentPos);
        return withConnection(state, facing, facingState, level, currentPos, facingPos);
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
        super.createBlockStateDefinition(builder.add(FACING, LEFT, RIGHT, EXTRA_WALL, UP, DOWN));
    }

    @Override
    public BlockState rotate(BlockState state, Rotation rot)
    {
        // TODO include extra wall?
        return state.setValue(FACING, rot.rotate(state.getValue(FACING)));
    }

    @Override
    public BlockState mirror(BlockState state, Mirror mirror)
    {
        //TODO include extra wall?
        return state.rotate(mirror.getRotation(state.getValue(FACING)));
    }

    @Override
    protected boolean canBeReplaced(BlockState state, BlockPlaceContext context)
    {
        Direction facing = state.getValue(FACING);
        if (state.getValue(EXTRA_WALL) != SideType.NONE)
        {
            return false;
        }
        if (context.getItemInHand().getItem() instanceof BlockItem blockItem && blockItem.getBlock() instanceof GreenhousePanelWallBlock wall)
        {
            double y = context.getClickLocation().y;
            int blockY = context.getClickedPos().getY();
            Direction clickedFace = context.getClickedFace();
            if (clickedFace == facing)
            {
                // Clicked the front of the block
                return false;
            }
            if (y >= blockY + 1 && clickedFace == Direction.UP)
            {
                // Clicked on top of the block
                return false;
            }
            if (y == blockY && clickedFace == Direction.DOWN)
            {
                // Clicked the bottom of the block
                return false;
            }
            // Require clicking on the up/down/opposite face to place the extra wall, so that you can still
            // use the sides of the block to place walls
            return wall == this && (clickedFace == Direction.UP || clickedFace == Direction.DOWN || clickedFace == facing.getOpposite());
        }
        return super.canBeReplaced(state, context);
    }

    public Set<Direction> getWallStates(BlockState state)
    {
        Direction facing = state.getValue(FACING);
        SideType extra = state.getValue(EXTRA_WALL);
        return extra != SideType.NONE ? Set.of(facing, extra.getDirection(facing)) : Set.of(facing);
    }

    public static Set<Direction> getWallStates2(BlockState state)
    {
        if (state.getBlock() instanceof GreenhousePanelWallBlock wall)
        {
            Direction facing = state.getValue(FACING);
            SideType extra = state.getValue(EXTRA_WALL);
            return extra != SideType.NONE ? Set.of(facing, extra.getDirection(facing)) : Set.of(facing);
        }
        return Set.of();
    }

    public boolean connects(BlockState adjacent)
    {
        return Helpers.isBlock(adjacent, FLTags.Blocks.GREENHOUSE_PANEL_WALLS);
    }

    @Override
    public BlockState withConnection(BlockState state, Direction facing, BlockState facingState, LevelAccessor level, BlockPos currentPos, BlockPos facingPos)
    {
        final Direction currentFacing = state.getValue(FACING);
        final SideType side = state.getValue(EXTRA_WALL);
        if (facing == Direction.DOWN)
        {
            if (facingState.getBlock() instanceof GreenhousePanelWallBlock)
            {
                Set<Direction> facingWallStates = getWallStates2(facingState);
                if (side == SideType.NONE)
                {
                    return state.setValue(DOWN, facingWallStates.contains(currentFacing));
                }
                else
                {
                    return state.setValue(DOWN, facingWallStates.containsAll(getWallStates2(state)));
                }
            }
            return state.setValue(DOWN, false);
        }
        if (facing == Direction.UP)
        {
            return state.setValue(UP, getTopConnection(state, facingState));
        }
        Direction right = getRightDirection(currentFacing, side);
        Direction left = getLeftDirection(currentFacing, side);
        if (facing == left.getClockWise())
        {
            return state.setValue(LEFT, canConnectTo(state, facing, facingState, level, currentPos, facingPos));
        }
        if (facing == right.getCounterClockWise())
        {
            return state.setValue(RIGHT, canConnectTo(state, facing, facingState, level, currentPos, facingPos));
        }
        return state;
    }

    private Direction getLeftDirection(Direction direction, SideType side)
    {
        return side == SideType.LEFT ? direction.getClockWise() : direction;
    }

    private Direction getRightDirection(Direction direction, SideType side)
    {
        return side == SideType.RIGHT ? direction.getCounterClockWise() : direction;
    }

    private PostType getTopConnection(BlockState state, BlockState facingState)
    {
        Direction currentFacing = state.getValue(FACING);
        SideType side = state.getValue(EXTRA_WALL);

        if (facingState.getBlock() instanceof GreenhousePanelRoofBlock)
        {
            Direction roofFacing = facingState.getValue(FACING);
            if (side == SideType.NONE)
            {
                return currentFacing == roofFacing ? PostType.BOTH : PostType.NONE;
            }

            PostType wallPostType = (currentFacing == roofFacing ? side.opposite() : SideType.NONE).toPost();
            PostType extraPostType = (side.getDirection(currentFacing) == roofFacing ? side : SideType.NONE).toPost();
            return wallPostType.combine(extraPostType);
        }
        return isSameFacingWall(state, facingState) ? PostType.NONE : PostType.BOTH;
    }

    private boolean isSameFacingWall(BlockState state, BlockState facingState)
    {
        return facingState.getBlock() instanceof GreenhousePanelWallBlock wall && getWallStates(state).containsAll(wall.getWallStates(facingState));
    }

    @Override
    public Set<Direction> getConnectionFaces(BlockState state, BlockPos pos, LevelAccessor level)
    {
        Direction facing = state.getValue(FACING);
        SideType extra = state.getValue(EXTRA_WALL);
        return switch (extra)
        {
            case LEFT -> Set.of(facing.getClockWise().getClockWise(), facing.getCounterClockWise());
            case RIGHT -> Set.of(facing.getClockWise(), facing.getCounterClockWise().getCounterClockWise());
            case NONE -> Set.of(facing.getClockWise(), facing.getCounterClockWise());
        };
    }

    @Override
    public boolean canConnectTo(BlockState state, Direction facing, BlockState facingState, LevelAccessor level, BlockPos currentPos, BlockPos facingPos)
    {
        return GreenhouseConnectable.hasConnectionAt(facingState, facingPos, level, facing.getOpposite());
    }
}
