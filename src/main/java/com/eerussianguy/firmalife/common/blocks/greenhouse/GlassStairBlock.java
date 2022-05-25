package com.eerussianguy.firmalife.common.blocks.greenhouse;

import java.util.function.Supplier;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class GlassStairBlock extends StairBlock
{
    public GlassStairBlock(Supplier<BlockState> state, Properties properties)
    {
        super(state, properties);
    }

    @Override
    @SuppressWarnings("deprecation")
    public VoxelShape getVisualShape(BlockState pState, BlockGetter pReader, BlockPos pPos, CollisionContext pContext)
    {
        return Shapes.empty();
    }

    @Override
    @SuppressWarnings("deprecation")
    public float getShadeBrightness(BlockState pState, BlockGetter pLevel, BlockPos pPos)
    {
        return 1.0F;
    }

    @Override
    public boolean propagatesSkylightDown(BlockState pState, BlockGetter pReader, BlockPos pPos)
    {
        return true;
    }

    @Override
    @SuppressWarnings("deprecation")
    public boolean skipRendering(BlockState state, BlockState adjacent, Direction side)
    {
        if (!(adjacent.getBlock() instanceof GlassStairBlock))
        {
            return false;
        }
        return state.getValue(HALF) == adjacent.getValue(HALF) && state.getValue(FACING) == adjacent.getValue(FACING);
    }
}
