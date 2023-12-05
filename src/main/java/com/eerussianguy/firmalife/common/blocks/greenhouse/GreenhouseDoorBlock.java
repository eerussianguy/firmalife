package com.eerussianguy.firmalife.common.blocks.greenhouse;

import java.util.Random;
import java.util.function.Supplier;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;

import com.eerussianguy.firmalife.common.blocks.IWeatherable;
import net.dries007.tfc.common.blocks.ExtendedProperties;
import net.dries007.tfc.common.blocks.IForgeBlockExtension;
import net.dries007.tfc.util.Helpers;
import org.jetbrains.annotations.Nullable;

public class GreenhouseDoorBlock extends DoorBlock implements IWeatherable, IForgeBlockExtension
{
    @Nullable
    private final Supplier<? extends Block> next;
    private final ExtendedProperties properties;

    public GreenhouseDoorBlock(ExtendedProperties properties, @Nullable Supplier<? extends Block> next, BlockSetType set)
    {
        super(properties.properties(), set);
        this.next = next;
        this.properties = properties;
    }

    @Override
    public boolean isRandomlyTicking(BlockState state)
    {
        return hasNext() && state.getValue(HALF) == DoubleBlockHalf.LOWER;
    }

    @Override
    @SuppressWarnings("deprecation")
    public void onRandomTick(BlockState lower, ServerLevel level, BlockPos pos, RandomSource rand)
    {
        Supplier<? extends Block> next = getNext();
        if (next != null && rand.nextInt(weatherChance()) == 0)
        {
            BlockPos above = pos.above();
            BlockState upper = level.getBlockState(above);

            BlockState nextState = next.get().defaultBlockState();

            level.destroyBlock(pos, false);
            level.destroyBlock(above, false);
            level.setBlock(pos, Helpers.copyProperties(nextState, lower), 3);
            level.setBlock(above, Helpers.copyProperties(nextState, upper), 2);
        }
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
