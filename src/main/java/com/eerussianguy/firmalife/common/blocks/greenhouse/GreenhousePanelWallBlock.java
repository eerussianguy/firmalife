package com.eerussianguy.firmalife.common.blocks.greenhouse;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;
import com.eerussianguy.firmalife.common.FLHelpers;
import com.eerussianguy.firmalife.common.blocks.FLStateProperties;
import com.eerussianguy.firmalife.common.blocks.IWeatherable;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.datafixers.util.Pair;
import net.minecraft.ChatFormatting;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
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
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.client.IHighlightHandler;
import net.dries007.tfc.common.blocks.ExtendedProperties;
import net.dries007.tfc.common.blocks.IForgeBlockExtension;
import net.dries007.tfc.util.Helpers;

public class GreenhousePanelWallBlock extends BaseGreenhouseBlock implements IWeatherable, IForgeBlockExtension, GreenhouseConnectable, IHighlightHandler
{
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
    public static final BooleanProperty LEFT = FLStateProperties.LEFT;
    public static final BooleanProperty RIGHT = FLStateProperties.RIGHT;
    public static final BooleanProperty DOWN = BlockStateProperties.DOWN;
    public static final EnumProperty<DualSide> UP = EnumProperty.create("up", DualSide.class);
    public static final EnumProperty<Side> EXTRA_WALL = EnumProperty.create("extra", Side.class);

    public static final VoxelShape[] SHAPES = Helpers.computeHorizontalShapes(d -> Helpers.rotateShape(d, 0, 0, 0, 16, 16, 2));
    public static final VoxelShape[] LEFT_SHAPES = Helpers.computeHorizontalShapes(dir ->
        Shapes.or(
            SHAPES[dir.get2DDataValue()],
            Helpers.rotateShape(dir, 14, 0, 0, 16, 16, 16)
        )
    );
    public static final VoxelShape[] RIGHT_SHAPES = Helpers.computeHorizontalShapes(dir ->
        Shapes.or(
            SHAPES[dir.get2DDataValue()],
            Helpers.rotateShape(dir, 0, 0, 0, 2, 16, 16)
        )
    );

    public GreenhousePanelWallBlock(ExtendedProperties properties, @Nullable Supplier<? extends Block> next)
    {
        super(properties, next);
        registerDefaultState(getStateDefinition().any().setValue(UP, DualSide.NONE).setValue(DOWN, false).setValue(LEFT, false).setValue(RIGHT, false).setValue(EXTRA_WALL, Side.NONE));
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag)
    {
        tooltipComponents.add(Component.translatable("firmalife.tooltip.greenhouse_panel_corner").withStyle(ChatFormatting.GRAY));
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
        final BlockState currentState = level.getBlockState(pos);
        if (currentState.is(this) && currentState.getValue(EXTRA_WALL) == Side.NONE)
        {
            Direction facing = currentState.getValue(FACING);
            Direction playerFacing = context.getHorizontalDirection();
            if (facing.getClockWise() == playerFacing)
            {
                return updateConnections(currentState.setValue(EXTRA_WALL, Side.RIGHT), pos, level, Direction.UP);
            }
            if (facing.getCounterClockWise() == playerFacing)
            {
                return updateConnections(currentState.setValue(EXTRA_WALL, Side.LEFT), pos, level, Direction.UP);
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
                state = FLHelpers.copyProperties(state, below, LEFT, RIGHT, FACING);
            }
            return state;
        }
        return null;
    }

    @Override
    public BlockState updateShape(BlockState state, Direction facing, BlockState facingState, LevelAccessor level, BlockPos currentPos, BlockPos facingPos)
    {
        return withConnection(state, facing, facingState, level, currentPos, facingPos);
    }

    @Override
    public boolean drawHighlight(Level level, BlockPos blockPos, Player player, BlockHitResult blockHitResult, PoseStack poseStack, MultiBufferSource multiBufferSource, Vec3 vec3)
    {
        final BlockState state = level.getBlockState(blockPos);
        final Direction facing = state.getValue(FACING);
        final Side side = state.getValue(EXTRA_WALL);
        final Direction selectedFace = blockHitResult.getDirection();
        final Direction playerFacing = player.getDirection();
        if (!itemMatchesThis(player.getMainHandItem()))
        {
            return false;
        }
        if (side == Side.NONE && selectedFace == facing.getOpposite())
        {
            if (playerFacing == facing.getClockWise())
            {
                IHighlightHandler.drawBox(poseStack, RIGHT_SHAPES[facing.get2DDataValue()], multiBufferSource, blockPos, vec3, 1f, 0f, 0f, 1f);
                return true;
            }
            else if (playerFacing == facing.getCounterClockWise())
            {
                IHighlightHandler.drawBox(poseStack, LEFT_SHAPES[facing.get2DDataValue()], multiBufferSource, blockPos, vec3, 1f, 0f, 0f, 1f);
                return true;
            }
        }
        return false;
    }

    private boolean itemMatchesThis(ItemStack stack)
    {
        return stack.getItem() instanceof BlockItem blockItem && blockItem.getBlock() instanceof GreenhousePanelWallBlock wall && wall == this;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        super.createBlockStateDefinition(builder.add(FACING, LEFT, RIGHT, EXTRA_WALL, UP, DOWN));
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
        final Direction facing = state.getValue(FACING);
        if (state.getValue(EXTRA_WALL) != Side.NONE)
        {
            return false;
        }
        if (itemMatchesThis(context.getItemInHand()))
        {
            final double y = context.getClickLocation().y;
            final int blockY = context.getClickedPos().getY();
            final Direction clickedFace = context.getClickedFace();
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
            return clickedFace == Direction.UP || clickedFace == Direction.DOWN || clickedFace == facing.getOpposite();
        }
        return super.canBeReplaced(state, context);
    }

    public static Set<Direction> getWallStates(BlockState state)
    {
        if (state.getBlock() instanceof GreenhousePanelWallBlock)
        {
            final Direction facing = state.getValue(FACING);
            final Side extra = state.getValue(EXTRA_WALL);
            return extra != Side.NONE ? Set.of(facing, extra.getDirection(facing)) : Set.of(facing);
        }
        return Set.of();
    }

    @Override
    public BlockState withConnection(BlockState state, Direction facing, BlockState facingState, LevelAccessor level, BlockPos currentPos, BlockPos facingPos)
    {
        final Direction currentFacing = state.getValue(FACING);
        final Side side = state.getValue(EXTRA_WALL);
        if (facing == Direction.DOWN)
        {
            if (facingState.getBlock() instanceof GreenhousePanelWallBlock)
            {
                Set<Direction> facingWallStates = getWallStates(facingState);
                if (side == Side.NONE)
                {
                    return state.setValue(DOWN, facingWallStates.contains(currentFacing));
                }
                else
                {
                    return state.setValue(DOWN, facingWallStates.containsAll(getWallStates(state)));
                }
            }
            return state.setValue(DOWN, false);
        }
        if (facing == Direction.UP)
        {
            return state.setValue(UP, getTopConnection(state, facingState));
        }
        final Direction right = side == Side.RIGHT ? currentFacing.getCounterClockWise() : currentFacing;
        final Direction left = side == Side.LEFT ? currentFacing.getClockWise() : currentFacing;
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

    private DualSide getTopConnection(BlockState state, BlockState facingState)
    {
        final Direction currentFacing = state.getValue(FACING);
        final Side extraWall = state.getValue(EXTRA_WALL);

        if (facingState.getBlock() instanceof GreenhousePanelRoofBlock)
        {
            Direction roofFacing = facingState.getValue(FACING);
            if (extraWall == Side.NONE)
            {
                return currentFacing == roofFacing ? DualSide.NONE : DualSide.BOTH;
            }

            // The side of the wall that has the same facing as the roof should not have a connection
            // The main wall and extra wall determine the connection type for the opposite
            DualSide wallPostType = (currentFacing == roofFacing ? extraWall : Side.NONE).dual();
            DualSide extraPostType = (extraWall.getDirection(currentFacing) == roofFacing ? extraWall.opposite() : Side.NONE).dual();
            return wallPostType.combine(extraPostType);
        }
        // Check the top connection for both the base wall and the extra wall
        final Set<Direction> facingWallStates = getWallStates(facingState);
        final boolean matchesMainWall = facingWallStates.contains(currentFacing);
        if (extraWall == Side.NONE)
        {
            return matchesMainWall ? DualSide.BOTH : DualSide.NONE;
        }
        final boolean matchesExtraWall = facingWallStates.contains(extraWall.getDirection(currentFacing));
        if (matchesMainWall && matchesExtraWall)
        {
            return DualSide.BOTH;
        }
        else if (matchesExtraWall)
        {
            return extraWall.dual();
        }
        else if (matchesMainWall)
        {
            return extraWall.opposite().dual();
        }
        return DualSide.NONE;
    }

    @Override
    public Set<Direction> getConnectionFaces(BlockState state, BlockPos pos, LevelAccessor level)
    {
        final Direction facing = state.getValue(FACING);
        final Side extra = state.getValue(EXTRA_WALL);
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

    /**
     * Locates the blocks on either side of a segment of panel walls, required to validate greenhouses.
     */
    public static class WallSegmentLocator
    {
        private final Map<BlockPos, Pair<Pair<BlockPos, BlockState>, Pair<BlockPos, BlockState>>> cache = new HashMap<>();

        private Pair<BlockPos, BlockState> traceWall(Level level, BlockPos pos, Direction wallDirection, Direction scanDirection, Set<BlockPos> seen)
        {
            seen.add(pos.immutable());
            BlockPos.MutableBlockPos currentPos = pos.mutable();
            BlockState currentState = level.getBlockState(pos);
            while (currentState.getBlock() instanceof GreenhousePanelWallBlock)
            {
                currentPos.move(scanDirection);
                currentState = level.getBlockState(currentPos);
                Set<Direction> currentWallStates = getWallStates(currentState);
                if (currentWallStates.contains(wallDirection))
                {
                    seen.add(currentPos.immutable());
                    if (currentWallStates.size() > 1)
                    {
                        return Pair.of(currentPos.immutable(), currentState);
                    }
                }
                else
                {
                    // Block is either: not a wall, or is a wall not facing correctly
                    return Pair.of(currentPos.immutable(), currentState);
                }

            }
            return Pair.of(currentPos.immutable(), currentState);
        }

        public Pair<Pair<BlockPos, BlockState>, Pair<BlockPos, BlockState>> scan(Level level, BlockPos pos, Direction wallDirection)
        {
            HashSet<BlockPos> seen = new HashSet<>();
            var result = cache.computeIfAbsent(pos, p -> {
                Pair<BlockPos, BlockState> left = traceWall(level, pos, wallDirection, wallDirection.getClockWise(), seen);
                Pair<BlockPos, BlockState> right = traceWall(level, pos, wallDirection, wallDirection.getCounterClockWise(), seen);
                return Pair.of(left, right);
            });
            for (BlockPos found : seen)
            {
                cache.put(found, result);
            }
            return result;
        }
    }
}
