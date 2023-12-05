package com.eerussianguy.firmalife.common.blocks.greenhouse;

import java.util.function.Supplier;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import com.eerussianguy.firmalife.common.FLTags;
import com.eerussianguy.firmalife.common.blocks.IWeatherable;
import net.dries007.tfc.common.blocks.ExtendedProperties;
import net.dries007.tfc.util.Helpers;
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
    public void onRandomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource rand)
    {
        IWeatherable.super.onRandomTick(state, level, pos, rand);
    }

    @Override
    public boolean skipRendering(BlockState state, BlockState adjacent, Direction side)
    {
        return Helpers.isBlock(adjacent, FLTags.Blocks.GREENHOUSE) && super.skipRendering(state, adjacent, side);
    }

    @Override
    @Nullable
    public Supplier<? extends Block> getNext()
    {
        return next;
    }
}
