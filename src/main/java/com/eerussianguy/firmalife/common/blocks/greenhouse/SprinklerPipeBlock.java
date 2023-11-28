package com.eerussianguy.firmalife.common.blocks.greenhouse;

import com.eerussianguy.firmalife.common.blocks.FLBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.common.blocks.DirectionPropertyBlock;
import net.dries007.tfc.common.blocks.ExtendedBlock;
import net.dries007.tfc.common.blocks.ExtendedProperties;
import net.dries007.tfc.common.fluids.FluidHelpers;
import net.dries007.tfc.util.Helpers;

public class SprinklerPipeBlock extends ExtendedBlock implements DirectionPropertyBlock
{
    private static final VoxelShape[] SHAPES = new VoxelShape[64];

    static
    {
        final VoxelShape north = box(6, 6, 0, 10, 10, 6);
        final VoxelShape south = box(6, 6, 10, 10, 10, 16);
        final VoxelShape west = box(10, 6, 6, 16, 10, 10);
        final VoxelShape east = box(0, 6, 6, 6, 10, 10);
        final VoxelShape up = box(6, 10, 6, 10, 16, 10);
        final VoxelShape down = box(6, 0, 6, 10, 6, 10);

        // Must match Direction.ordinal order
        final VoxelShape[] directions = new VoxelShape[] {down, up, north, south, east, west};

        final VoxelShape center = box(6, 6, 6, 10, 10, 10);

        for (int i = 0; i < SHAPES.length; i++)
        {
            VoxelShape shape = center;
            for (Direction direction : Helpers.DIRECTIONS)
            {
                if (((i >> direction.ordinal()) & 1) == 1)
                {
                    shape = Shapes.or(shape, directions[direction.ordinal()]);
                }
            }
            SHAPES[i] = shape;
        }
    }

    public SprinklerPipeBlock(ExtendedProperties properties)
    {
        super(properties);
        registerDefaultState(DirectionPropertyBlock.setAllDirections(getStateDefinition().any(), false));
    }

    @Override
    public boolean propagatesSkylightDown(BlockState state, BlockGetter level, BlockPos pos)
    {
        return true;
    }

    @Override
    @SuppressWarnings("deprecation")
    public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos currentPos, BlockPos neighborPos)
    {
        FluidHelpers.tickFluid(level, currentPos, state);
        return updateConnectedSides(level, currentPos, state, null);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context)
    {
        return updateConnectedSides(context.getLevel(), context.getClickedPos(), defaultBlockState(), context.getNearestLookingDirection());
    }

    @Override
    @SuppressWarnings("deprecation")
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context)
    {
        int index = 0;
        for (Direction side : Helpers.DIRECTIONS)
        {
            if (state.getValue(DirectionPropertyBlock.getProperty(side)))
            {
                index |= 1 << side.ordinal();
            }
        }
        return SHAPES[index];
    }

    @Override
    @SuppressWarnings("deprecation")
    public BlockState rotate(BlockState state, Rotation rotation)
    {
        return DirectionPropertyBlock.rotate(state, rotation);
    }

    @Override
    @SuppressWarnings("deprecation")
    public BlockState mirror(BlockState state, Mirror mirror)
    {
        return DirectionPropertyBlock.mirror(state, mirror);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        builder.add(PROPERTIES);
    }

    private BlockState updateConnectedSides(LevelAccessor level, BlockPos pos, BlockState state, @Nullable Direction defaultDirection)
    {
        int openSides = 0;
        @Nullable Direction openDirection = null;
        for (final Direction direction : Helpers.DIRECTIONS)
        {
            final BooleanProperty property = DirectionPropertyBlock.getProperty(direction);

            if (defaultDirection == null && state.getValue(property))
            {
                defaultDirection = direction;
            }

            final BlockPos adjacentPos = pos.relative(direction);
            final BlockState adjacentState = level.getBlockState(adjacentPos);
            final boolean adjacentConnection = connectsToPipeInDirection(adjacentState, direction);
            if (adjacentConnection)
            {
                openSides++;
                openDirection = direction;
            }

            state = state.setValue(property, adjacentConnection);
        }

        if (openSides == 0)
        {
            // Either we called this method with a non-null default direction, or
            // The state must have already been in-world, which must have had at least one direction previously, which we would have taken as the default
            assert defaultDirection != null;

            return state.setValue(DirectionPropertyBlock.getProperty(defaultDirection), true)
                .setValue(DirectionPropertyBlock.getProperty(defaultDirection.getOpposite()), true);
        }
        if (openSides == 1)
        {
            // If we only have a single open side, then we always treat this as a straight pipe.
            return state.setValue(DirectionPropertyBlock.getProperty(openDirection.getOpposite()), true);
        }

        return state;
    }

    private boolean connectsToPipeInDirection(BlockState state, Direction direction)
    {
        final Block block = state.getBlock();
        return block == this ||
            (block instanceof AbstractSprinklerBlock sprinkler && sprinkler.getPipeConnection().test(direction)) ||
            (block instanceof GreenhousePortBlock && state.getValue(GreenhousePortBlock.AXIS) == direction.getAxis()) ||
            (block == FLBlocks.IRRIGATION_TANK.get() && direction.getAxis().isHorizontal());
    }
}
