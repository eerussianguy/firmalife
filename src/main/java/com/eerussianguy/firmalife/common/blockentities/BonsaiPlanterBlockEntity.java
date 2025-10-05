package com.eerussianguy.firmalife.common.blockentities;

import com.eerussianguy.firmalife.FirmaLife;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class BonsaiPlanterBlockEntity extends LargePlanterBlockEntity
{
    public BonsaiPlanterBlockEntity(BlockPos pos, BlockState state)
    {
        super(FLBlockEntities.BONSAI_PLANTER.get(), pos, state, defaultInventory(LARGE_PLANTER_SLOTS), FirmaLife.MOD_ID);
    }

    @Override
    public float resetGrowthTo()
    {
        return 0.2f;
    }
}
