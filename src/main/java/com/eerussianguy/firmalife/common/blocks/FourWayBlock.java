package com.eerussianguy.firmalife.common.blocks;

import net.minecraft.world.level.block.GlazedTerracottaBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.PushReaction;

public class FourWayBlock extends GlazedTerracottaBlock
{
    public FourWayBlock(Properties properties)
    {
        super(properties);
    }

    @Override
    @SuppressWarnings("deprecation")
    public PushReaction getPistonPushReaction(BlockState state)
    {
        return this.material.getPushReaction();
    }
}
