package com.eerussianguy.firmalife.common.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import net.dries007.tfc.common.blocks.ExtendedProperties;

public class SkepBlock extends BaseBeehiveBlock
{
    public static final VoxelShape SHAPE = Shapes.or(
        box(3, 0, 3, 13, 3, 13),
        box(4, 3, 4, 12, 6, 12),
        box(5, 6, 5, 11, 7, 11),
        box(7, 7, 7, 9, 8, 9)
    );

    public SkepBlock(ExtendedProperties properties)
    {
        super(properties, 1);
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context)
    {
        return SHAPE;
    }
}
