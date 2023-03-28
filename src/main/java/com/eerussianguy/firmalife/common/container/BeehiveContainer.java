package com.eerussianguy.firmalife.common.container;

import net.minecraft.world.entity.player.Inventory;

import com.eerussianguy.firmalife.common.blockentities.FLBeehiveBlockEntity;

public class BeehiveContainer extends FourContainer<FLBeehiveBlockEntity>
{
    public static BeehiveContainer create(FLBeehiveBlockEntity hive, Inventory playerInventory, int windowId)
    {
        return new BeehiveContainer(hive, playerInventory, windowId).init(playerInventory);
    }

    public BeehiveContainer(FLBeehiveBlockEntity blockEntity, Inventory playerInv, int windowId)
    {
        super(FLContainerTypes.BEEHIVE.get(), windowId, blockEntity);
    }

}
