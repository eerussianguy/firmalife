package com.eerussianguy.firmalife.common.blocks.greenhouse;

import java.util.Set;
import java.util.function.Supplier;
import com.eerussianguy.firmalife.common.blocks.IWeatherable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
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
import net.minecraft.world.level.block.state.properties.EnumProperty;
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
    public static final EnumProperty<Size> CW = EnumProperty.create("cw", Size.class);
    public static final EnumProperty<Size> CCW = EnumProperty.create("ccw", Size.class);
    public static final BooleanProperty BACK = BooleanProperty.create("back");
    public static final BooleanProperty BOTTOM = BlockStateProperties.BOTTOM;
    public static final EnumProperty<DualSide> DIAGONAL = EnumProperty.create("diagonal", DualSide.class);
    public static final EnumProperty<DualSide> SIDES = EnumProperty.create("sides", DualSide.class);

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
                .setValue(CW, Size.NONE)
                .setValue(CCW, Size.NONE)
                .setValue(BACK, false)
                .setValue(BOTTOM, false)
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
        return withConnection(state, facing, facingState, level, pos, facingPos);
    }

    @Override
    public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random)
    {
        level.setBlockAndUpdate(pos, updateDiagonals(level, pos, state));
    }

    @Override
    @Nullable
    public BlockState getStateForPlacement(BlockPlaceContext ctx)
    {
        final BlockState state = super.getStateForPlacement(ctx);
        if (state == null)
        {
            return null;
        }
        else
        {
            final Level level = ctx.getLevel();
            final BlockPos pos = ctx.getClickedPos();
            final Direction facing = ctx.getHorizontalDirection().getOpposite();
            return updateConnections(updateDiagonals(level, pos, state.setValue(FACING, facing)), pos, level, facing.getOpposite(), facing.getClockWise(), facing.getCounterClockWise(), Direction.DOWN);
        }
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        super.createBlockStateDefinition(builder.add(FACING, CCW, CW, BACK, BOTTOM, DIAGONAL, SIDES));
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

    private BlockState updateDiagonals(LevelAccessor level, BlockPos pos, BlockState state)
    {
        final Direction facing = state.getValue(FACING);
        final Direction oppositeFacing = facing.getOpposite();

        final BlockPos upPos = pos.relative(oppositeFacing, 1).above();
        final BlockPos downPos = pos.relative(facing, 1).below();

        final BlockState downState = level.getBlockState(downPos);
        final BlockState upState = level.getBlockState(upPos);

        final boolean up = isValidPanel(upState, facing);
        final boolean down = isValidPanel(downState, facing);

        if (up)
        {
            level.scheduleTick(upPos, upState.getBlock(), 1);
        }
        if (down)
        {
            level.scheduleTick(downPos, downState.getBlock(), 1);
        }

        return state.setValue(DIAGONAL, DualSide.resolve(up, down));
    }

    private static boolean isValidPanel(BlockState state, Direction facing)
    {
        return state.getBlock() instanceof GreenhousePanelRoofBlock && state.getValue(FACING) == facing;
    }

    @Override
    public BlockState withConnection(BlockState state, Direction facing, BlockState facingState, LevelAccessor level, BlockPos currentPos, BlockPos facingPos)
    {
        final Direction currentFacing = state.getValue(FACING);
        final Direction oppositeFacing = currentFacing.getOpposite();
        if (facing == oppositeFacing)
        {
            return state.setValue(BACK, facingState.getBlock() instanceof GreenhousePanelRoofBlock || facingState.isFaceSturdy(level, facingPos, oppositeFacing));
        }
        if (facing == Direction.DOWN)
        {
            final Size cw = getSideSize(facingState, currentFacing.getClockWise(), GreenhousePanelWallBlock.LEFT);
            final Size ccw = getSideSize(facingState, currentFacing.getCounterClockWise(), GreenhousePanelWallBlock.RIGHT);
            return state.setValue(CW, cw).setValue(CCW, ccw).setValue(BOTTOM, facingState.isFaceSturdy(level, facingPos, Direction.UP));
        }
        final boolean isValid = isValidPanel(level.getBlockState(facingPos), currentFacing);
        final DualSide side = state.getValue(SIDES);
        if (facing == currentFacing.getClockWise())
        {
            return state.setValue(SIDES, isValid ? side.combine(DualSide.LEFT) : side.subtract(DualSide.LEFT));
        }
        if (facing == currentFacing.getCounterClockWise())
        {
            return state.setValue(SIDES, isValid ? side.combine(DualSide.RIGHT) : side.subtract(DualSide.RIGHT));
        }
        return state;
    }

    private static Size getSideSize(BlockState state, Direction facing, BooleanProperty side)
    {
        if (GreenhousePanelWallBlock.getWallStates(state).contains(facing))
        {
            if (state.getValue(side))
            {
                return Size.THIN;
            }
            return Size.THICK;
        }
        return Size.NONE;
    }

    @Override
    public Set<Direction> getConnectionFaces(BlockState state, BlockPos pos, LevelAccessor level)
    {
        final Direction facing = state.getValue(FACING);
        return Set.of(facing.getOpposite(), Direction.DOWN, facing.getClockWise(), facing.getCounterClockWise());
    }
}
