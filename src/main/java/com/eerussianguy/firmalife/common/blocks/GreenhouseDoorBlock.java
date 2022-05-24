package com.eerussianguy.firmalife.common.blocks;

import java.util.Random;
import java.util.function.Supplier;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.BlockHitResult;

import net.dries007.tfc.common.blocks.ExtendedProperties;
import net.dries007.tfc.common.blocks.IForgeBlockExtension;
import net.dries007.tfc.util.Helpers;
import org.jetbrains.annotations.Nullable;

public class GreenhouseDoorBlock extends DoorBlock implements IWeatherable, IForgeBlockExtension
{
    @Nullable
    private final Supplier<? extends Block> next;
    private final ExtendedProperties properties;

    public GreenhouseDoorBlock(ExtendedProperties properties, @Nullable Supplier<? extends Block> next)
    {
        super(properties.properties());
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
    public void randomTick(BlockState lower, ServerLevel level, BlockPos pos, Random rand)
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
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit)
    {
        state = state.cycle(OPEN);
        level.setBlock(pos, state, 10);
        level.levelEvent(player, state.getValue(OPEN) ? getOpenSound() : getCloseSound(), pos, 0);
        level.gameEvent(player, isOpen(state) ? GameEvent.BLOCK_OPEN : GameEvent.BLOCK_CLOSE, pos);
        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    @Override
    public ExtendedProperties getExtendedProperties()
    {
        return properties;
    }

    private int getCloseSound()
    {
        return material == Material.METAL ? 1011 : 1012;
    }

    private int getOpenSound()
    {
        return material == Material.METAL ? 1005 : 1006;
    }

    @Override
    @SuppressWarnings("deprecation")
    public boolean skipRendering(BlockState state, BlockState adjacent, Direction side)
    {
        // specifically we don't want this behavior from door to door
        return false;
    }
}
