package com.eerussianguy.firmalife.common.blocks.greenhouse;

import java.util.function.Supplier;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.common.blocks.ExtendedProperties;

public class GreenhousePortBlock extends GreenhouseWallBlock
{
    public static final EnumProperty<Direction.Axis> AXIS = BlockStateProperties.HORIZONTAL_AXIS;

    public GreenhousePortBlock(ExtendedProperties properties, @Nullable Supplier<? extends Block> next)
    {
        super(properties, next);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context)
    {
        final BlockState state = super.getStateForPlacement(context);
        return state != null ? state.setValue(AXIS, context.getHorizontalDirection().getAxis()) : null;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        super.createBlockStateDefinition(builder.add(AXIS));
    }
}
