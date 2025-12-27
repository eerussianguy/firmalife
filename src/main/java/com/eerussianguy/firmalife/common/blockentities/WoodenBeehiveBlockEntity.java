package com.eerussianguy.firmalife.common.blockentities;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;

import net.dries007.tfc.common.capabilities.PartialItemHandler;

public class WoodenBeehiveBlockEntity extends FLBeehiveBlockEntity
{
    public WoodenBeehiveBlockEntity(BlockPos pos, BlockState state)
    {
        super(pos, state, FLBlockEntities.BEEHIVE.get(), 4);

        sidedInventory
            .on(new PartialItemHandler(inventory).insert(0, 1, 2, 3), Direction.Plane.HORIZONTAL)
            .on(new PartialItemHandler(inventory).extract(0, 1, 2, 3), Direction.DOWN);
    }


}
