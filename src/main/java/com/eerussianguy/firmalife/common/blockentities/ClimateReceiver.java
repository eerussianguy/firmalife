package com.eerussianguy.firmalife.common.blockentities;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

public interface ClimateReceiver
{
    void addWater(float amount);

    void setValid(Level level, BlockPos pos, boolean valid, int tier, boolean cellar);
}
