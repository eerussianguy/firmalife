package com.eerussianguy.firmalife.common.blocks.greenhouse;

import java.util.Locale;
import java.util.Set;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;

public interface GreenhouseConnectable
{
    //TODO rename to DualPlaneType?
    enum PostType implements StringRepresentable
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

        public PostType combine(PostType other)
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

        public PostType opposite()
        {
            return switch (this)
            {
                case NONE -> BOTH;
                case LEFT -> RIGHT;
                case RIGHT -> LEFT;
                case BOTH -> NONE;
            };
        }

        public boolean contains(PostType other)
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
    }

    enum WallType implements StringRepresentable
    {
        THICK,
        THIN,
        NONE;

        @Override
        public String getSerializedName()
        {
            return name().toLowerCase(Locale.ROOT);
        }
    }

    //TODO migrate to PostType and rename it?
    @Deprecated
    enum SideType implements StringRepresentable
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

        public SideType opposite()
        {
            return switch (this)
            {
                case LEFT -> RIGHT;
                case RIGHT -> LEFT;
                case NONE -> NONE;
            };
        }

        public PostType toPost()
        {
            return switch (this)
            {
                case LEFT -> PostType.LEFT;
                case RIGHT -> PostType.RIGHT;
                case NONE -> PostType.NONE;
            };
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

    static boolean isFacingSameDirection(Property<Direction> property, BlockState state, Direction facing)
    {
        if (state.hasProperty(property))
        {
            return state.getValue(property) == facing;
        }
        return false;
    }

    static Set<Direction> getConnections(BlockState state, BlockPos pos, LevelAccessor level)
    {
        if (state.getBlock() instanceof GreenhouseConnectable connectable)
        {
            return connectable.getConnectionFaces(state, pos, level);
        }
        return Set.of();
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
