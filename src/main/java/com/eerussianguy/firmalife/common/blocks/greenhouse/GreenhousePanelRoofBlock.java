package com.eerussianguy.firmalife.common.blocks.greenhouse;

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
import net.minecraft.world.level.block.AbstractGlassBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
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

public class GreenhousePanelRoofBlock extends AbstractGlassBlock implements IWeatherable, IForgeBlockExtension
{
    public static final VoxelShape[] SHAPES = Helpers.computeHorizontalShapes(dir -> Shapes.or(
        Helpers.rotateShape(dir, 0, 0, 0, 16, 8, 16),
        Helpers.rotateShape(dir, 0, 8, 8, 16, 16, 16)
    ));

    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
    public static final BooleanProperty CW = FLStateProperties.CW;
    public static final BooleanProperty CCW = FLStateProperties.CCW;

    private final ExtendedProperties properties;
    @Nullable private final Supplier<? extends Block> next;

    public GreenhousePanelRoofBlock(ExtendedProperties properties, @Nullable Supplier<? extends Block> next)
    {
        super(properties.properties());
        this.properties = properties;
        this.next = next;
        registerDefaultState(getStateDefinition().any().setValue(FACING, Direction.NORTH).setValue(CW, false).setValue(CCW, false));
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
    @SuppressWarnings("deprecation")
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context)
    {
        return SHAPES[state.getValue(FACING).get2DDataValue()];
    }

    @Override
    @SuppressWarnings("deprecation")
    public BlockState updateShape(BlockState state, Direction facing, BlockState facingState, LevelAccessor level, BlockPos pos, BlockPos facingPos)
    {
        return update(level, pos, state);
    }

    @Override
    @SuppressWarnings("deprecation")
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
        super.createBlockStateDefinition(builder.add(FACING, CCW, CW));
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

    private BlockState update(LevelAccessor level, BlockPos pos, BlockState state)
    {
        final Direction face = state.getValue(FACING).getOpposite();
        final BlockState cws = level.getBlockState(pos.below().relative(face.getClockWise()));
        final boolean cw = cws.getBlock() instanceof GreenhousePanelWallBlock && cws.getValue(GreenhousePanelWallBlock.FACING) == face.getCounterClockWise() && level.getBlockState(pos.relative(face.getClockWise())).isAir();
        final BlockState ccws = level.getBlockState(pos.below().relative(face.getCounterClockWise()));
        final boolean ccw = ccws.getBlock() instanceof GreenhousePanelWallBlock && ccws.getValue(GreenhousePanelWallBlock.FACING) == face.getClockWise() && level.getBlockState(pos.relative(face.getCounterClockWise())).isAir();

        return state.setValue(CW, cw).setValue(CCW, ccw);
    }
}
