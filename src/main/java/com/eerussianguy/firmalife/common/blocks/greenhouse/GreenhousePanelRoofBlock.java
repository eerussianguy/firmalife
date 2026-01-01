package com.eerussianguy.firmalife.common.blocks.greenhouse;

import java.util.Set;
import java.util.function.Supplier;
import com.eerussianguy.firmalife.common.blocks.FLStateProperties;
import com.eerussianguy.firmalife.common.blocks.IWeatherable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.TransparentBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.common.blocks.ExtendedProperties;
import net.dries007.tfc.common.blocks.IForgeBlockExtension;
import net.dries007.tfc.util.Helpers;

public class GreenhousePanelRoofBlock extends TransparentBlock implements IWeatherable, IForgeBlockExtension, GreenhouseConnectable
{
    public static final VoxelShape[] SHAPES = Helpers.computeHorizontalShapes(dir -> Shapes.or(
        Helpers.rotateShape(dir, 0, 0, 0, 16, 8, 16),
        Helpers.rotateShape(dir, 0, 8, 8, 16, 16, 16)
    ));

    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
    public static final BooleanProperty CW = FLStateProperties.CW;
    public static final BooleanProperty CCW = FLStateProperties.CCW;
    public static final BooleanProperty UP = BlockStateProperties.UP;
    public static final BooleanProperty DOWN = BlockStateProperties.DOWN;
    public static final BooleanProperty LEFT = FLStateProperties.LEFT;
    public static final BooleanProperty RIGHT = FLStateProperties.RIGHT;

    private final ExtendedProperties properties;
    @Nullable private final Supplier<? extends Block> next;

    public GreenhousePanelRoofBlock(ExtendedProperties properties, @Nullable Supplier<? extends Block> next)
    {
        super(properties.properties());
        this.properties = properties;
        this.next = next;
        registerDefaultState(
            getStateDefinition()
                .any()
                .setValue(FACING, Direction.NORTH)
                .setValue(CW, false)
                .setValue(CCW, false)
                .setValue(DOWN, true)
                .setValue(UP, true)
                .setValue(LEFT, false)
                .setValue(RIGHT, false)
        );
    }

    @Override
    public @Nullable Supplier<? extends Block> getNext()
    {
        return next;
    }

    @Override
    public ExtendedProperties getExtendedProperties()
    {
        return properties;
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context)
    {
        return SHAPES[state.getValue(FACING).get2DDataValue()];
    }

    @Override
    public BlockState updateShape(BlockState state, Direction facing, BlockState facingState, LevelAccessor level, BlockPos pos, BlockPos facingPos)
    {
        return update(level, pos, state);
    }

    @Override
    public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random)
    {
        level.setBlockAndUpdate(pos, update(level, pos, state));
    }

    @Override
    @Nullable
    public BlockState getStateForPlacement(BlockPlaceContext ctx)
    {
        final BlockState state = super.getStateForPlacement(ctx);
        return state == null ? null : update(ctx.getLevel(), ctx.getClickedPos(), state.setValue(FACING, ctx.getHorizontalDirection().getOpposite()));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        super.createBlockStateDefinition(builder.add(FACING, CCW, CW, UP, DOWN, LEFT, RIGHT));
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

    private BlockState update(LevelAccessor level, BlockPos pos, BlockState state)
    {
        final Direction facing = state.getValue(FACING);
        final Direction oppositeFacing = facing.getOpposite();
        final BlockPos upPos = pos.relative(oppositeFacing, 1).above();
        final BlockPos downPos = pos.relative(facing, 1).below();

        final BlockState below = level.getBlockState(pos.below());
        final BlockState cws = level.getBlockState(pos.below().relative(oppositeFacing.getClockWise()));
        final BlockState ccws = level.getBlockState(pos.below().relative(oppositeFacing.getCounterClockWise()));
        final BlockState downState = level.getBlockState(downPos);
        final BlockState upState = level.getBlockState(upPos);

        //below.getValue(GreenhousePanelWallBlock.FACING) == facing.getClockWise()
        final boolean cw = GreenhousePanelWallBlock.getWallStates(below).contains(facing.getClockWise());
        final boolean ccw = GreenhousePanelWallBlock.getWallStates(below).contains(facing.getCounterClockWise());
        final boolean up = isValidPanel(upState, facing);
        final boolean down = isValidPanel(downState, facing);
        final boolean left = !isValidPanel(level.getBlockState(pos.relative(facing.getClockWise())), facing);
        final boolean right = !isValidPanel(level.getBlockState(pos.relative(facing.getCounterClockWise())), facing);

        if (!up)
        {
            level.scheduleTick(upPos, upState.getBlock(), 1);
        }
        if (!down)
        {
            level.scheduleTick(downPos, downState.getBlock(), 1);
        }

        return state.setValue(CW, cw).setValue(CCW, ccw).setValue(UP, up).setValue(DOWN, down).setValue(LEFT, left).setValue(RIGHT, right);
    }

    private boolean isValidPanel(BlockState state, Direction facing)
    {
        return !(state.getBlock() instanceof GreenhousePanelRoofBlock) || state.getValue(FACING) != facing;
    }

    @Override
    public BlockState withConnection(BlockState state, Direction facing, BlockState facingState, LevelAccessor level, BlockPos currentPos, BlockPos facingPos)
    {
        return null;
    }

    @Override
    public Set<Direction> getConnectionFaces(BlockState state, BlockPos pos, LevelAccessor level)
    {
        Direction facing = state.getValue(FACING);
        return Set.of(facing.getOpposite(), Direction.DOWN, facing.getClockWise(), facing.getCounterClockWise());
    }
}
