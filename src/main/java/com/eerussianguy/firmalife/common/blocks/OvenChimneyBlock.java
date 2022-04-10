package com.eerussianguy.firmalife.common.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class OvenChimneyBlock extends Block
{
    public static final BooleanProperty CURED = FLStateProperties.CURED;

    private static final VoxelShape SHAPE = Shapes.join(
        Shapes.block(),
        Block.box(4, 4, 4, 12, 12, 12),
        BooleanOp.ONLY_FIRST
    );

    public OvenChimneyBlock(Properties properties)
    {
        super(properties);
        registerDefaultState(getStateDefinition().any().setValue(CURED, false));
    }

    @Override
    @SuppressWarnings("deprecation")
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context)
    {
        return SHAPE;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        super.createBlockStateDefinition(builder.add(CURED));
    }
}
