package com.eerussianguy.firmalife.common.blocks.greenhouse;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.common.blocks.ExtendedProperties;
import net.dries007.tfc.util.Helpers;

public class SprinklerBlock extends AbstractSprinklerBlock
{
    public static final VoxelShape SHAPE_X = Shapes.or(
        box(6, 14, 6, 10, 16, 10),
        box(1, 12, 5, 15, 14, 11)
    );

    public static final VoxelShape SHAPE_Z = Shapes.or(
        Helpers.rotateShape(Direction.WEST, 6, 14, 6, 10, 16, 10),
        Helpers.rotateShape(Direction.WEST, 1, 12, 5, 15, 14, 11)
    );

    public static final EnumProperty<Direction.Axis> AXIS = BlockStateProperties.HORIZONTAL_AXIS;

    public SprinklerBlock(ExtendedProperties properties)
    {
        super(properties, d -> d == Direction.DOWN, origin -> BlockPos.betweenClosed(origin.offset(-2, -6, -2), origin.offset(2, -1, 2)), 2, -1, new Vec3(0.5, 0.745, 0.5));
        registerDefaultState(getStateDefinition().any().setValue(AXIS, Direction.Axis.X).setValue(STASIS, false));
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context)
    {
        return defaultBlockState().setValue(AXIS, context.getHorizontalDirection().getAxis());
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        super.createBlockStateDefinition(builder.add(AXIS));
    }

    @Override
    @SuppressWarnings("deprecation")
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context)
    {
        return state.getValue(AXIS) == Direction.Axis.X ? SHAPE_X : SHAPE_Z;
    }

}
