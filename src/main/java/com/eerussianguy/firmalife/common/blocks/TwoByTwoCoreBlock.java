package com.eerussianguy.firmalife.common.blocks;

import java.util.function.Supplier;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.common.blocks.ExtendedProperties;

public class TwoByTwoCoreBlock extends FourWayDeviceBlock
{
    public static boolean coreBlockSurvives(LevelReader level, BlockPos pos, BlockState state)
    {
        final Direction facing = state.getValue(FACING);
        final Direction cw = facing.getClockWise();
        final BlockPos.MutableBlockPos cursor = new BlockPos.MutableBlockPos();
        final int dx = facing.getStepX();
        final int cdx = cw.getStepX();
        final int dz = facing.getStepZ();
        final int cdz = cw.getStepZ();

        return TwoByTwoSubBlock.stageAt(dx, 0, dz, level, pos, cursor, 1) &&
            TwoByTwoSubBlock.stageAt(cdx, 0, cdz, level, pos, cursor, 2) &&
            TwoByTwoSubBlock.stageAt(0, 1, 0, level, pos, cursor, 4);
    }

    private static boolean canBePlacedAt(Level level, BlockPos pos, Direction facing)
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

    private final Supplier<? extends Block> sub;

    public TwoByTwoCoreBlock(ExtendedProperties properties, Supplier<? extends Block> sub)
    {
        super(properties, InventoryRemoveBehavior.DROP);
        this.sub = sub;
    }

    @Override
    @Nullable
    public BlockState getStateForPlacement(BlockPlaceContext ctx)
    {
        return canBePlacedAt(ctx.getLevel(), ctx.getClickedPos(), ctx.getHorizontalDirection()) ? defaultBlockState().setValue(FACING, ctx.getHorizontalDirection()) : null;
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack)
    {
        final Direction forward = placer != null ? placer.getDirection() : Direction.NORTH;
        final Direction back = forward.getOpposite();
        final Direction right = forward.getClockWise();
        final BlockPos.MutableBlockPos cursor = new BlockPos.MutableBlockPos().set(pos);

        state = getSub().setValue(FACING, forward);
        cursor.move(forward);
        level.setBlockAndUpdate(cursor, state.setValue(TwoByTwoSubBlock.BARREL_PART, 1));
        cursor.move(right).move(back);
        level.setBlockAndUpdate(cursor, state.setValue(TwoByTwoSubBlock.BARREL_PART, 2));
        cursor.move(forward);
        level.setBlockAndUpdate(cursor, state.setValue(TwoByTwoSubBlock.BARREL_PART, 3));
        cursor.set(pos).move(0, 1, 0);
        level.setBlockAndUpdate(cursor, state.setValue(TwoByTwoSubBlock.BARREL_PART, 4));
        cursor.move(forward);
        level.setBlockAndUpdate(cursor, state.setValue(TwoByTwoSubBlock.BARREL_PART, 5));
        cursor.move(right).move(back);
        level.setBlockAndUpdate(cursor, state.setValue(TwoByTwoSubBlock.BARREL_PART, 6));
        cursor.move(forward);
        level.setBlockAndUpdate(cursor, state.setValue(TwoByTwoSubBlock.BARREL_PART, 7));
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context)
    {
        return TwoByTwoSubBlock.SHAPE_0[state.getValue(FACING).get2DDataValue()];
    }

    private BlockState getSub()
    {
        return sub.get().defaultBlockState();
    }
}
