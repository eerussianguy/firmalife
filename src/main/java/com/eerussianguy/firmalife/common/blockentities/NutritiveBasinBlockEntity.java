package com.eerussianguy.firmalife.common.blockentities;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import net.dries007.tfc.common.blockentities.TFCBlockEntity;

public class NutritiveBasinBlockEntity extends TFCBlockEntity
{
    public NutritiveBasinBlockEntity(BlockPos pos, BlockState state)
    {
        super(FLBlockEntities.NUTRITIVE_BASIN.get(), pos, state);
    }

}
