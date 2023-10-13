package com.eerussianguy.firmalife.common.blocks;

import java.util.function.Supplier;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import net.dries007.tfc.util.Helpers;
import org.jetbrains.annotations.Nullable;

public interface IWeatherable
{
    default void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random)
    {
        Supplier<? extends Block> next = getNext();
        if (next != null && random.nextInt(weatherChance()) == 0)
        {
            level.setBlockAndUpdate(pos, Helpers.copyProperties(next.get().defaultBlockState(), state));
        }
    }

    default int weatherChance()
    {
        return 350;
    }

    default boolean hasNext()
    {
        return getNext() != null;
    }

    @Nullable
    Supplier<? extends Block> getNext();
}
