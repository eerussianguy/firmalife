package com.eerussianguy.firmalife.common.blocks.greenhouse;

import java.util.Locale;
import java.util.Set;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;

public interface GreenhouseConnectable
{
    enum DualSide implements StringRepresentable
    {
        NONE,
        LEFT,
        RIGHT,
        BOTH;

        @Override
        public String getSerializedName()
        {
            return name().toLowerCase(Locale.ROOT);
        }

        public DualSide combine(DualSide other)
        {
            if (this == NONE)
            {
                return other;
            }
            if (other == NONE)
            {
                return this;
            }
            if (this == other)
            {
                return this;
            }
            return BOTH;
        }

        public DualSide subtract(DualSide other)
        {
            if (this == NONE || other == BOTH || this == other)
            {
                return NONE;
            }
            if (this == BOTH && other != NONE)
            {
                return other.opposite();
            }
            return this;
        }

        public DualSide opposite()
        {
            return switch (this)
            {
                case NONE -> BOTH;
                case LEFT -> RIGHT;
                case RIGHT -> LEFT;
                case BOTH -> NONE;
            };
        }

        public boolean contains(DualSide other)
        {
            if (other == this)
            {
                return true;
            }
            if (this == BOTH || other == NONE)
            {
                return true;
            }
            return false;
        }

        public static DualSide resolve(boolean left, boolean right)
        {
            if (left != right)
            {
                if (left)
                {
                    return LEFT;
                }
                return RIGHT;
            }
            if (left)
            {
                return BOTH;
            }
            return NONE;
        }
    }

    enum Side implements StringRepresentable
    {
        LEFT,
        RIGHT,
        NONE;

        @Override
        public String getSerializedName()
        {
            return name().toLowerCase(Locale.ROOT);
        }

        public Direction getDirection(Direction direction)
        {
            return switch (this)
            {
                case LEFT -> direction.getClockWise();
                case RIGHT -> direction.getCounterClockWise();
                case NONE -> direction;
            };
        }

        public Side opposite()
        {
            return switch (this)
            {
                case LEFT -> RIGHT;
                case RIGHT -> LEFT;
                case NONE -> NONE;
            };
        }

        public DualSide dual()
        {
            return switch (this)
            {
                case LEFT -> DualSide.LEFT;
                case RIGHT -> DualSide.RIGHT;
                case NONE -> DualSide.NONE;
            };
        }
    }

    enum Size implements StringRepresentable
    {
        NONE,
        THIN,
        THICK;

        @Override
        public String getSerializedName()
        {
            return name().toLowerCase(Locale.ROOT);
        }
    }

    default boolean canConnectTo(BlockState state, Direction facing, BlockState facingState, LevelAccessor level, BlockPos currentPos, BlockPos facingPos)
    {
        return false;
    }

    BlockState withConnection(BlockState state, Direction facing, BlockState facingState, LevelAccessor level, BlockPos currentPos, BlockPos facingPos);

    Set<Direction> getConnectionFaces(BlockState state, BlockPos pos, LevelAccessor level);

    default BlockState updateConnections(BlockState state, BlockPos pos, LevelAccessor level, Direction... directions)
    {
        for (Direction direction : directions)
        {
            BlockPos targetPos = pos.relative(direction);
            state = withConnection(state, direction, level.getBlockState(targetPos), level, pos, targetPos);
        }
        return state;
    }

    static boolean hasConnectionAt(BlockState state, BlockPos pos, LevelAccessor level, Direction direction)
    {
        if (state.getBlock() instanceof GreenhouseConnectable connectable)
        {
            return connectable.getConnectionFaces(state, pos, level).contains(direction);
        }
        return false;
    }
}
