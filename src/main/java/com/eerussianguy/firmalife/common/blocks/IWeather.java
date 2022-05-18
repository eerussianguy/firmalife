package com.eerussianguy.firmalife.common.blocks;

import java.util.Random;
import java.util.function.Supplier;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import net.dries007.tfc.util.Helpers;
import org.jetbrains.annotations.Nullable;

public interface IWeather
{
    default void randomTick(BlockState state, ServerLevel level, BlockPos pos, Random random)
    {
        Supplier<? extends Block> next = getNext();
        if (next != null && random.nextInt(weatherChance()) == 0)
        {
            level.setBlockAndUpdate(pos, Helpers.copyProperties(next.get().defaultBlockState(), state));
        }
    }

    default int weatherChance()
    {
        return 2;
    }

    default boolean hasNext()
    {
        return getNext() != null;
    }

    @Nullable
    Supplier<? extends Block> getNext();
}
