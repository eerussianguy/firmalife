package com.eerussianguy.firmalife.common.blockentities;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.items.ItemStackHandler;

import com.eerussianguy.firmalife.common.FLHelpers;

public class QuadPlanterBlockEntity extends LargePlanterBlockEntity
{
    public static final Component NAME = FLHelpers.blockEntityName("quad_planter");

    public QuadPlanterBlockEntity(BlockPos pos, BlockState state)
    {
        super(FLBlockEntities.QUAD_PLANTER.get(), pos, state, defaultInventory(4), NAME);
    }
}
