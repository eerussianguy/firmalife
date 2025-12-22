package com.eerussianguy.firmalife.common.blocks.greenhouse;

import java.util.List;
import java.util.Locale;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;

public interface GreenhouseConnectable
{
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
    }

    default boolean canConnectTo(BlockState state, Direction facing, BlockState facingState, LevelAccessor level, BlockPos currentPos, BlockPos facingPos)
    {
        return false;
    }

    BlockState withConnection(BlockState state, Direction facing, BlockState facingState, LevelAccessor level, BlockPos currentPos, BlockPos facingPos);

    List<Direction> getConnectionFaces(BlockState state, BlockPos pos, LevelAccessor level);

    static boolean isFacingSameDirection(Property<Direction> property, BlockState state, Direction facing)
    {
        if (state.hasProperty(property))
        {
            return state.getValue(property) == facing;
        }
        return false;
    }

    static List<Direction> getConnections(BlockState state, BlockPos pos, LevelAccessor level)
    {
        if (state.getBlock() instanceof GreenhouseConnectable connectable)
        {
            return connectable.getConnectionFaces(state, pos, level);
        }
        return List.of();
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
