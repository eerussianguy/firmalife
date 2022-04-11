package com.eerussianguy.firmalife.common.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import org.jetbrains.annotations.Nullable;

public interface ICure
{
    void cure(Level level, BlockState state, BlockPos pos);

    @Nullable
    Block getCured();

    default boolean isCured()
    {
        return getCured() == null;
    }
}
