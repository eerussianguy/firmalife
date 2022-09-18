package com.eerussianguy.firmalife.common;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import com.eerussianguy.firmalife.common.blockentities.FLBlockEntities;
import net.dries007.tfc.common.blockentities.TickCounterBlockEntity;

public class FLTickCounterBlockEntity extends TickCounterBlockEntity
{
    public FLTickCounterBlockEntity(BlockPos pos, BlockState state)
    {
        super(FLBlockEntities.TICK_COUNTER.get(), pos, state);
    }

    @Override
    public BlockEntityType<?> getType()
    {
        return FLBlockEntities.TICK_COUNTER.get();
    }
}
