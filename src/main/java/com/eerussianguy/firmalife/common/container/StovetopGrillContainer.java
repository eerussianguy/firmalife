package com.eerussianguy.firmalife.common.container;

import com.eerussianguy.firmalife.common.blockentities.StovetopGrillBlockEntity;
import net.minecraft.world.entity.player.Inventory;


public class StovetopGrillContainer extends FourContainer<StovetopGrillBlockEntity>
{
    public static StovetopGrillContainer create(StovetopGrillBlockEntity hive, Inventory playerInventory, int windowId)
    {
        return new StovetopGrillContainer(hive, playerInventory, windowId).init(playerInventory);
    }

    public StovetopGrillContainer(StovetopGrillBlockEntity blockEntity, Inventory playerInv, int windowId)
    {
        super(FLContainerTypes.STOVETOP_GRILL.get(), windowId, blockEntity);
    }

}
