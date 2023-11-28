package com.eerussianguy.firmalife.common.blockentities;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public interface ClimateReceiver
{
    @Nullable
    static ClimateReceiver get(Level level, BlockPos pos)
    {
        if (level.getBlockEntity(pos) instanceof ClimateReceiver receiver)
        {
            return receiver;
        }
        else if (level.getBlockState(pos).getBlock() instanceof ClimateReceiver receiver)
        {
            return receiver;
        }
        return null;
    }

    default boolean addWater(float amount)
    {
        return addWater(amount, null);
    }

    default boolean addWater(float amount, @Nullable Direction direction)
    {
        return false;
    }

    void setValid(Level level, BlockPos pos, boolean valid, int tier, boolean cellar);
}
