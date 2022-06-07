package com.eerussianguy.firmalife.common.blockentities;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.state.BlockState;

import com.eerussianguy.firmalife.common.FLHelpers;

public class HangingPlanterBlockEntity extends LargePlanterBlockEntity
{
    public static final Component NAME = FLHelpers.blockEntityName("hanging_planter");

    public HangingPlanterBlockEntity(BlockPos pos, BlockState state)
    {
        super(FLBlockEntities.HANGING_PLANTER.get(), pos, state, defaultInventory(LARGE_PLANTER_SLOTS), NAME);
    }
}
