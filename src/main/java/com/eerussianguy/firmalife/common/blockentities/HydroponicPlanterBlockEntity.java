package com.eerussianguy.firmalife.common.blockentities;

import com.eerussianguy.firmalife.common.FLHelpers;
import com.eerussianguy.firmalife.common.blocks.FLBlocks;
import com.eerussianguy.firmalife.common.blocks.greenhouse.NutritiveBasinBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.util.Helpers;

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
        if (!hasBasin())
        {
            return Component.translatable("firmalife.greenhouse.no_basin");
        }
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
        assert level != null;
        final BlockState state = level.getBlockState(worldPosition.below());
        return Helpers.isBlock(state, FLBlocks.NUTRITIVE_BASIN.get()) && state.getValue(NutritiveBasinBlock.WATERED);
    }

    @Override
    public boolean addWater(float amount)
    {
        return false;
    }

    @Override
    public void drainWater(float amount) { }
}
