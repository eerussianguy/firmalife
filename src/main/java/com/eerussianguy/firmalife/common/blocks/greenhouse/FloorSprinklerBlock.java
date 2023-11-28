package com.eerussianguy.firmalife.common.blocks.greenhouse;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import net.dries007.tfc.common.blocks.ExtendedProperties;

public class FloorSprinklerBlock extends AbstractSprinklerBlock
{
    public static final VoxelShape SHAPE = box(6, 0, 6, 10, 5, 10);

    public FloorSprinklerBlock(ExtendedProperties properties)
    {
        super(properties, d -> d == Direction.UP, origin -> BlockPos.betweenClosed(origin.offset(-2, 1, -2), origin.offset(2, 6, 2)), 2, 1, new Vec3(0.5, 5d / 16d + 0.01, 0.5));
    }

    @Override
    @SuppressWarnings("deprecation")
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context)
    {
        return SHAPE;
    }
}
