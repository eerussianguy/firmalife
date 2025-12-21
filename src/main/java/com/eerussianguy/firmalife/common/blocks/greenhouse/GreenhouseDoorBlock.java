package com.eerussianguy.firmalife.common.blocks.greenhouse;

import java.util.List;
import java.util.function.Supplier;
import com.eerussianguy.firmalife.common.blocks.IWeatherable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.common.blocks.ExtendedProperties;
import net.dries007.tfc.common.blocks.IForgeBlockExtension;
import net.dries007.tfc.util.Helpers;

public class GreenhouseDoorBlock extends DoorBlock implements IWeatherable, IForgeBlockExtension, GreenhouseConnectable
{
    @Nullable
    private final Supplier<? extends Block> next;
    private final ExtendedProperties properties;

    public GreenhouseDoorBlock(ExtendedProperties properties, @Nullable Supplier<? extends Block> next, BlockSetType set)
    {
        super(set, properties.properties());
        this.next = next;
        this.properties = properties;
    }

    @Override
    public boolean isRandomlyTicking(BlockState state)
    {
        return hasNext() && state.getValue(HALF) == DoubleBlockHalf.LOWER;
    }

    @Override
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


    @Override
    public BlockState withConnection(BlockState state, Direction facing, BlockState facingState, LevelAccessor level, BlockPos currentPos, BlockPos facingPos)
    {
        return state;
    }

    @Override
    public List<Direction> getConnectionFaces(BlockState state, BlockPos pos, LevelAccessor level)
    {
        Direction facing = state.getValue(FACING);
        return List.of(facing.getClockWise(), facing.getCounterClockWise());
    }
}
