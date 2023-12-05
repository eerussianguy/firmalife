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
import net.dries007.tfc.common.blocks.IForgeBlockExtension;
import net.dries007.tfc.util.Helpers;
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

    @Override
    public ExtendedProperties getExtendedProperties()
    {
        return properties;
    }
}
