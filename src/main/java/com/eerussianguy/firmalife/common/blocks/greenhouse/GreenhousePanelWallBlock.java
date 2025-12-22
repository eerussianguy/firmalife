package com.eerussianguy.firmalife.common.blocks.greenhouse;

import java.util.List;
import java.util.function.Supplier;
import com.eerussianguy.firmalife.common.FLTags;
import com.eerussianguy.firmalife.common.blocks.FLStateProperties;
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
import net.dries007.tfc.util.Helpers;

public class GreenhousePanelWallBlock extends GreenhouseWallBlock implements GreenhouseConnectable
{
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
    public static final BooleanProperty LEFT = FLStateProperties.LEFT;
    public static final BooleanProperty RIGHT = FLStateProperties.RIGHT;
    public static final EnumProperty<SideType> EXTRA_WALL = EnumProperty.create("extra", GreenhouseConnectable.SideType.class);

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
        registerDefaultState(getStateDefinition().any().setValue(UP, false).setValue(DOWN, false).setValue(LEFT, false).setValue(RIGHT, false).setValue(EXTRA_WALL, SideType.NONE));
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
        BlockState currentState = context.getLevel().getBlockState(context.getClickedPos());
        if (currentState.is(this) && currentState.getValue(EXTRA_WALL) == SideType.NONE)
        {
            Direction facing = currentState.getValue(FACING);
            Direction playerFacing = context.getHorizontalDirection();
            if (facing.getClockWise() == playerFacing)
            {
                return currentState.setValue(EXTRA_WALL, SideType.RIGHT);
            }
            if (facing.getCounterClockWise() == playerFacing)
            {
                return currentState.setValue(EXTRA_WALL, SideType.LEFT);
            }
            return null;
        }
        final BlockState state = defaultBlockState(); //TODO super
        if (state != null)
        {
            final Level level = context.getLevel();
            final BlockState below = level.getBlockState(context.getClickedPos().below());
            if (below.getBlock() instanceof GreenhousePanelWallBlock)
            {
                return Helpers.copyProperties(state, below);
            }
            return state.setValue(FACING, context.getHorizontalDirection().getOpposite());
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
        super.createBlockStateDefinition(builder.add(FACING, LEFT, RIGHT, EXTRA_WALL));
    }

    @Override
    public BlockState rotate(BlockState state, Rotation rot)
    {
        return state.setValue(FACING, rot.rotate(state.getValue(FACING)));
    }

    @Override
    public BlockState mirror(BlockState state, Mirror mirror)
    {
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

    @Override
    public boolean connects(BlockState adjacent)
    {
        return Helpers.isBlock(adjacent, FLTags.Blocks.GREENHOUSE_PANEL_WALLS);
    }

    @Override
    public BlockState withConnection(BlockState state, Direction facing, BlockState facingState, LevelAccessor level, BlockPos currentPos, BlockPos facingPos)
    {
        final Direction currentFacing = state.getValue(FACING);
        final SideType extraWall = state.getValue(EXTRA_WALL);
        if (facing == Direction.UP)
        {
            return state.setValue(UP, isSameFacingWall(facingState, currentFacing));
        }
        if (facing == Direction.DOWN)
        {
            return state.setValue(DOWN, isSameFacingWall(facingState, currentFacing));
        }
        Direction left = currentFacing.getClockWise();
        Direction right = currentFacing.getCounterClockWise();

        if (extraWall == SideType.LEFT)
        {
            left = left.getClockWise();
        }
        if (extraWall == SideType.RIGHT)
        {
            right = right.getCounterClockWise();
        }

        if (facing == left)
        {
            return state.setValue(LEFT, canConnectTo(state, facing, facingState, level, currentPos, facingPos));
        }
        if (facing == right)
        {
            return state.setValue(RIGHT, canConnectTo(state, facing, facingState, level, currentPos, facingPos));
        }

        return state;
    }

    private boolean isSameFacingWall(BlockState facingState, Direction currentFacing)
    {
        return facingState.getBlock() instanceof GreenhousePanelWallBlock && GreenhouseConnectable.isFacingSameDirection(FACING, facingState, currentFacing);
    }

    @Override
    public List<Direction> getConnectionFaces(BlockState state, BlockPos pos, LevelAccessor level)
    {
        Direction facing = state.getValue(FACING);
        SideType extra = state.getValue(EXTRA_WALL);
        if (extra == SideType.NONE)
        {
            return List.of(facing.getClockWise(), facing.getCounterClockWise());
        }
        if (extra == SideType.LEFT)
        {
            return List.of(facing.getClockWise().getClockWise(), facing.getCounterClockWise());
        }
        if (extra == SideType.RIGHT)
        {
            return List.of(facing.getClockWise(), facing.getCounterClockWise().getCounterClockWise());
        }
        return List.of();
    }

    @Override
    public boolean canConnectTo(BlockState state, Direction facing, BlockState facingState, LevelAccessor level, BlockPos currentPos, BlockPos facingPos)
    {
        Direction currentFacing = state.getValue(FACING);
        if (facing == Direction.UP)
        {
            if (facingState.getBlock() instanceof GreenhousePanelRoofBlock)
            {
                Direction roofFacing = facingState.getValue(FACING);
                return roofFacing == currentFacing.getClockWise() || roofFacing == currentFacing.getCounterClockWise();
            }
            return isSameFacingWall(facingState, currentFacing);
        }
        if (facing == Direction.DOWN)
        {
            return isSameFacingWall(facingState, currentFacing);
        }
        return GreenhouseConnectable.hasConnectionAt(facingState, facingPos, level, facing.getOpposite());
    }
}
