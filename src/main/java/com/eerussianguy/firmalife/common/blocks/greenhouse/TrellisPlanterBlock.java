package com.eerussianguy.firmalife.common.blocks.greenhouse;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import net.dries007.tfc.common.blocks.ExtendedProperties;

public class TrellisPlanterBlock extends LargePlanterBlock
{
    private static final VoxelShape TRELLIS_SHAPE = box(2, 0, 2, 14, 16, 14);

    public TrellisPlanterBlock(ExtendedProperties properties)
    {
        super(properties);
    }

    @Override
    public PlanterType getPlanterType()
    {
        return PlanterType.TRELLIS;
    }

    @Override
    protected float resetGrowthTo()
    {
        return 0.2f;
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context)
    {
        return TRELLIS_SHAPE;
    }
}
