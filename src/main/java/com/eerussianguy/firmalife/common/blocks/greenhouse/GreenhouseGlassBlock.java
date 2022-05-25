package com.eerussianguy.firmalife.common.blocks.greenhouse;

import java.util.Random;
import java.util.function.Supplier;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.AbstractGlassBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import com.eerussianguy.firmalife.common.FLTags;
import com.eerussianguy.firmalife.common.blocks.IWeatherable;
import net.dries007.tfc.common.blocks.ExtendedProperties;
import net.dries007.tfc.common.blocks.IForgeBlockExtension;
import net.dries007.tfc.util.Helpers;
import org.jetbrains.annotations.Nullable;

public class GreenhouseGlassBlock extends AbstractGlassBlock implements IWeatherable, IForgeBlockExtension
{
    @Nullable
    private final Supplier<? extends Block> next;
    private final ExtendedProperties properties;

    public GreenhouseGlassBlock(ExtendedProperties properties, @Nullable Supplier<? extends Block> next)
    {
        super(properties.properties());
        this.next = next;
        this.properties = properties;
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

    @Override
    public ExtendedProperties getExtendedProperties()
    {
        return properties;
    }

    @Override
    public boolean skipRendering(BlockState state, BlockState adjacent, Direction side)
    {
        return adjacent.getBlock() instanceof GreenhouseGlassBlock && Helpers.isBlock(adjacent, FLTags.Blocks.GREENHOUSE);
    }
}
