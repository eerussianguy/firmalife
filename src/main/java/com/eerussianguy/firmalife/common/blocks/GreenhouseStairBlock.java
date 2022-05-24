package com.eerussianguy.firmalife.common.blocks;

import java.util.Random;
import java.util.function.Supplier;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import net.dries007.tfc.common.blocks.ExtendedProperties;
import net.dries007.tfc.common.blocks.IForgeBlockExtension;
import org.jetbrains.annotations.Nullable;

public class GreenhouseStairBlock extends GlassStairBlock implements IWeatherable, IForgeBlockExtension
{
    @Nullable
    private final Supplier<? extends Block> next;
    private final ExtendedProperties properties;

    public GreenhouseStairBlock(ExtendedProperties properties, Supplier<BlockState> parent, @Nullable Supplier<? extends Block> next)
    {
        super(parent, properties.properties());
        this.next = next;
        this.properties = properties;
    }

    @Override
    public boolean isRandomlyTicking(BlockState pState)
    {
        return hasNext();
    }

    @Override
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

    @Override
    public ExtendedProperties getExtendedProperties()
    {
        return properties;
    }
}
