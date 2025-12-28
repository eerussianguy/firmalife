package com.eerussianguy.firmalife.common.blocks.greenhouse;

import java.util.function.Supplier;
import com.eerussianguy.firmalife.common.blocks.IWeatherable;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.TransparentBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.common.blocks.ExtendedProperties;
import net.dries007.tfc.common.blocks.IForgeBlockExtension;

public class BaseGreenhouseBlock extends TransparentBlock implements IWeatherable, IForgeBlockExtension
{
    @Nullable
    private final Supplier<? extends Block> next;
    private final ExtendedProperties properties;

    public BaseGreenhouseBlock(ExtendedProperties properties, @Nullable Supplier<? extends Block> next)
    {
        super(properties.properties());
        this.next = next;
        this.properties = properties;
    }

    @Override
    public boolean isRandomlyTicking(BlockState state)
    {
        return hasNext();
    }

    @Override
    public void onRandomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource rand)
    {
        IWeatherable.super.onRandomTick(state, level, pos, rand);
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
