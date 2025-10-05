package com.eerussianguy.firmalife.common.blockentities;

import com.eerussianguy.firmalife.FirmaLife;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class HangingPlanterBlockEntity extends LargePlanterBlockEntity
{
    public HangingPlanterBlockEntity(BlockPos pos, BlockState state)
    {
        super(FLBlockEntities.HANGING_PLANTER.get(), pos, state, defaultInventory(LARGE_PLANTER_SLOTS), FirmaLife.MOD_ID);
    }

    @Override
    protected Direction airFindOffset()
    {
        return Direction.DOWN;
    }

    @Override
    public boolean addWater(float amount, @Nullable Direction direction)
    {
        return direction != Direction.DOWN && super.addWater(amount, direction);
    }

    @Override
    public float resetGrowthTo()
    {
        return 0.2f;
    }
}
