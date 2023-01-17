package com.eerussianguy.firmalife.common.blockentities;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import net.dries007.tfc.common.blockentities.TickCounterBlockEntity;

public class FLTickCounterBlockEntity extends TickCounterBlockEntity
{
    public static void reset(Level level, BlockPos pos)
    {
        level.getBlockEntity(pos, FLBlockEntities.TICK_COUNTER.get()).ifPresent(TickCounterBlockEntity::resetCounter);
    }

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
