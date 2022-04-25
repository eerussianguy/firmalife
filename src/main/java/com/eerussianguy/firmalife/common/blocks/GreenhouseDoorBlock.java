package com.eerussianguy.firmalife.common.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.BlockHitResult;

public class GreenhouseDoorBlock extends DoorBlock
{
    public GreenhouseDoorBlock(Properties properties)
    {
        super(properties);
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

    private int getCloseSound()
    {
        return material == Material.METAL ? 1011 : 1012;
    }

    private int getOpenSound()
    {
        return material == Material.METAL ? 1005 : 1006;
    }
}
