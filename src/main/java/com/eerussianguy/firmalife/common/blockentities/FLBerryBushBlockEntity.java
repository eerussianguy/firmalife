package com.eerussianguy.firmalife.common.blockentities;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import net.dries007.tfc.common.blockentities.BerryBushBlockEntity;

public class FLBerryBushBlockEntity extends BerryBushBlockEntity
{
    public FLBerryBushBlockEntity(BlockPos pos, BlockState state)
    {
        super(pos, state);
    }

    protected FLBerryBushBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state)
    {
        super(type, pos, state);
    }

    @Override
    public BlockEntityType<?> getType()
    {
        return FLBlockEntities.BERRY_BUSH.get();
    }
}
