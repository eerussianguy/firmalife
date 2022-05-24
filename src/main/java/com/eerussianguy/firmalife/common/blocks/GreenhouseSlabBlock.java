package com.eerussianguy.firmalife.common.blocks;

import java.util.Random;
import java.util.function.Supplier;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import net.dries007.tfc.common.blocks.ExtendedProperties;
import org.jetbrains.annotations.Nullable;

public class GreenhouseSlabBlock extends GlassSlabBlock implements IWeatherable
{
    @Nullable
    private final Supplier<? extends Block> next;

    public GreenhouseSlabBlock(ExtendedProperties properties, @Nullable Supplier<? extends Block> next)
    {
        super(properties);
        this.next = next;
    }

    @Override
    public boolean isRandomlyTicking(BlockState pState)
    {
        return hasNext();
    }

    @Override
    @SuppressWarnings("deprecation")
    public void randomTick(BlockState state, ServerLevel level, BlockPos pos, Random rand)
    {
        IWeatherable.super.randomTick(state, level, pos, rand);
    }

    @Override
    @Nullable
    public Supplier<? extends Block> getNext()
    {
        return next;
    }
}
