package com.eerussianguy.firmalife.common.blockentities;

import com.eerussianguy.firmalife.common.FLHelpers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class HydroponicPlanterBlockEntity extends QuadPlanterBlockEntity
{
    public HydroponicPlanterBlockEntity(BlockPos pos, BlockState state)
    {
        super(FLBlockEntities.HYDROPONIC_PLANTER.get(), pos, state, FLHelpers.blockEntityName("hydroponic_planter"));
    }

    @Override
    @Nullable
    public Component getInvalidReason()
    {
        return super.getInvalidReason();
    }

    @Override
    public float getWater()
    {
        assert level != null;
        return hasBasin() ? 1f : 0f;
    }

    public boolean hasBasin()
    {
        return true;
    }

    @Override
    public boolean addWater(float amount, @Nullable Direction direction)
    {
        return false;
    }

    @Override
    public void drainWater(float amount) { }
}
