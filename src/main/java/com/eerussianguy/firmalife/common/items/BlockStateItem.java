package com.eerussianguy.firmalife.common.items;

import java.util.function.Function;

import javax.annotation.Nullable;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class BlockStateItem extends BlockItem
{
    private final Function<BlockState, BlockState> beforePlace;

    public BlockStateItem(Block block, Function<BlockState, BlockState> beforePlace, Properties properties)
    {
        super(block, properties);
        this.beforePlace = beforePlace;
    }

    @Override
    public String getDescriptionId()
    {
        return getOrCreateDescriptionId();
    }

    @Nullable
    @Override
    protected BlockState getPlacementState(BlockPlaceContext ctx)
    {
        BlockState blockstate = this.getBlock().getStateForPlacement(ctx);
        blockstate = beforePlace.apply(blockstate);
        return blockstate != null && this.canPlace(ctx, blockstate) ? blockstate : null;
    }
}
