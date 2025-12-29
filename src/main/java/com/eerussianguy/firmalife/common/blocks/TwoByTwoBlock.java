package com.eerussianguy.firmalife.common.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.common.blocks.ExtendedProperties;
import net.dries007.tfc.util.Helpers;

public class TwoByTwoBlock extends FourWayDeviceBlock
{
    public static final IntegerProperty BARREL_PART = FLStateProperties.BARREL_PART;

    private static boolean canPlaceBigBarrel(Level level, BlockPos pos, BlockState state, Direction facing)
    {
        for (BlockPos testPos : BlockPos.betweenClosed(pos, pos.relative(facing).relative(facing.getClockWise()).above()))
        {
            if (!level.getBlockState(testPos).canBeReplaced())
            {
                return false;
            }
        }
        return true;
    }

    private static boolean canPartSurvive(LevelReader level, BlockPos pos, BlockState state)
    {
        if (!(state.getBlock() instanceof TwoByTwoBlock))
        {
            return false;
        }
        final Direction facing = state.getValue(FACING);
        final Direction cw = facing.getClockWise();
        final BlockPos.MutableBlockPos cursor = new BlockPos.MutableBlockPos();
        final int dx = facing.getStepX();
        final int cdx = cw.getStepX();
        final int dz = facing.getStepZ();
        final int cdz = cw.getStepZ();

        return switch (state.getValue(BARREL_PART))
            {
                case 0 -> stageAt(dx, 0, dz, level, pos, cursor, 1) &&
                    stageAt(cdx, 0, cdz, level, pos, cursor, 2) &&
                    stageAt(0, 1, 0, level, pos, cursor, 4);
                case 1 -> stageAt(-dx, 0, -dz, level, pos, cursor, 0) &&
                    stageAt(cdx, 0, cdz, level, pos, cursor, 3) &&
                    stageAt(0, 1, 0, level, pos, cursor, 5);
                case 2 -> stageAt(dx, 0, dz, level, pos, cursor, 3) &&
                    stageAt(-cdx, 0, -cdz, level, pos, cursor, 0) &&
                    stageAt(0, 1, 0, level, pos, cursor, 6);
                case 3 -> stageAt(-dx, 0, -dz, level, pos, cursor, 2) &&
                    stageAt(-cdx, 0, -cdz, level, pos, cursor, 1) &&
                    stageAt(0, 1, 0, level, pos, cursor, 7);
                case 4 -> stageAt(dx, 0, dz, level, pos, cursor, 5) &&
                    stageAt(cdx, 0, cdz, level, pos, cursor, 6) &&
                    stageAt(0, -1, 0, level, pos, cursor, 0);
                case 5 -> stageAt(-dx, 0, -dz, level, pos, cursor, 4) &&
                    stageAt(cdx, 0, cdz, level, pos, cursor, 7) &&
                    stageAt(0, -1, 0, level, pos, cursor, 1);
                case 6 -> stageAt(dx, 0, dz, level, pos, cursor, 7) &&
                    stageAt(-cdx, 0, -cdz, level, pos, cursor, 4) &&
                    stageAt(0, -1, 0, level, pos, cursor, 2);
                case 7 -> stageAt(-dx, 0, -dz, level, pos, cursor, 6) &&
                    stageAt(-cdx, 0, -cdz, level, pos, cursor, 5) &&
                    stageAt(0, -1, 0, level, pos, cursor, 3);
                default -> false;
            };
    }

    private static boolean stageAt(int dx, int dy, int dz, LevelReader level, BlockPos origin, BlockPos.MutableBlockPos cursor, int stageWanted)
    {
        cursor.set(origin).move(dx, dy, dz);
        final BlockState state = level.getBlockState(cursor);
        return state.getBlock() instanceof TwoByTwoBlock && state.getValue(BARREL_PART) == stageWanted;
    }

    public static final VoxelShape[] SHAPE_0 = Helpers.computeHorizontalShapes(dir -> Helpers.rotateShape(dir, 0, 2, 0, 14, 16, 14));
    public static final VoxelShape[] SHAPE_1 = Helpers.computeHorizontalShapes(dir -> Helpers.rotateShape(dir, 2, 2, 0, 16, 16, 16));
    public static final VoxelShape[] SHAPE_2 = Helpers.computeHorizontalShapes(dir -> Helpers.rotateShape(dir, 0, 2, 0, 14, 16, 14));
    public static final VoxelShape[] SHAPE_3 = Helpers.computeHorizontalShapes(dir -> Helpers.rotateShape(dir, 0, 2, 0, 14, 16, 16));
    public static final VoxelShape[] SHAPE_4 = Helpers.computeHorizontalShapes(dir -> Helpers.rotateShape(dir, 2, 0, 0, 16, 14, 14));
    public static final VoxelShape[] SHAPE_5 = Helpers.computeHorizontalShapes(dir -> Helpers.rotateShape(dir, 2, 0, 0, 16, 14, 16));
    public static final VoxelShape[] SHAPE_6 = Helpers.computeHorizontalShapes(dir -> Helpers.rotateShape(dir, 0, 0, 0, 14, 14, 14));
    public static final VoxelShape[] SHAPE_7 = Helpers.computeHorizontalShapes(dir -> Helpers.rotateShape(dir, 0, 0, 0, 14, 14, 16));
    public static final VoxelShape[][] SHAPES = {SHAPE_0, SHAPE_1, SHAPE_2, SHAPE_3, SHAPE_4, SHAPE_5, SHAPE_6, SHAPE_7};

    public static BlockPos findZeroPos(BlockPos pos, BlockState state)
    {
        final Direction facing = state.getValue(FACING);
        int part = state.getValue(BARREL_PART);
        if (part > 3)
        {
            pos = pos.below();
            part -= 4;
        }
        return switch (part)
        {
            case 1 -> pos.relative(facing.getOpposite());
            case 2 -> pos.relative(facing.getCounterClockWise());
            case 3 -> pos.relative(facing.getCounterClockWise()).relative(facing.getOpposite());
            default -> pos;
        };
    }

    public TwoByTwoBlock(ExtendedProperties properties)
    {
        super(properties, InventoryRemoveBehavior.DROP);
        registerDefaultState(getStateDefinition().any().setValue(BARREL_PART, 0).setValue(FACING, Direction.NORTH));
    }

    @Override
    public ItemInteractionResult useItemOn(ItemStack held, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit)
    {
        final BlockPos zero = findZeroPos(pos, state);
        return useCoreBlock(held, level.getBlockState(zero), level, zero, player, hand, hit.withPosition(zero));
    }

    public ItemInteractionResult useCoreBlock(ItemStack held, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit)
    {
        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state)
    {
        return state.getValue(BARREL_PART) == 0 ? super.newBlockEntity(pos, state) : null;
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context)
    {
        return SHAPES[state.getValue(BARREL_PART)][state.getValue(FACING).get2DDataValue()];
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos)
    {
        return state.getValue(BARREL_PART) == 0 || canPartSurvive(level, pos, state);
    }

    @Override
    public BlockState updateShape(BlockState state, Direction facing, BlockState facingState, LevelAccessor level, BlockPos pos, BlockPos facingPos)
    {
        return canPartSurvive(level, pos, state) ? state : Blocks.AIR.defaultBlockState();
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        super.createBlockStateDefinition(builder.add(BARREL_PART));
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx)
    {
        return canPlaceBigBarrel(ctx.getLevel(), ctx.getClickedPos(), defaultBlockState(), ctx.getHorizontalDirection()) ? defaultBlockState().setValue(FACING, ctx.getHorizontalDirection()) : null;
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack)
    {
        super.setPlacedBy(level, pos, state, placer, stack);
        final Direction forward = placer != null ? placer.getDirection() : Direction.NORTH;
        final Direction back = forward.getOpposite();
        final Direction right = forward.getClockWise();
        final BlockPos.MutableBlockPos cursor = new BlockPos.MutableBlockPos().set(pos);

        state = state.setValue(FACING, forward);
        cursor.move(forward);
        level.setBlockAndUpdate(cursor, state.setValue(BARREL_PART, 1));
        cursor.move(right).move(back);
        level.setBlockAndUpdate(cursor, state.setValue(BARREL_PART, 2));
        cursor.move(forward);
        level.setBlockAndUpdate(cursor, state.setValue(BARREL_PART, 3));
        cursor.set(pos).move(0, 1, 0);
        level.setBlockAndUpdate(cursor, state.setValue(BARREL_PART, 4));
        cursor.move(forward);
        level.setBlockAndUpdate(cursor, state.setValue(BARREL_PART, 5));
        cursor.move(right).move(back);
        level.setBlockAndUpdate(cursor, state.setValue(BARREL_PART, 6));
        cursor.move(forward);
        level.setBlockAndUpdate(cursor, state.setValue(BARREL_PART, 7));
    }
}
