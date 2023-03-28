package com.eerussianguy.firmalife.common.container;

import com.eerussianguy.firmalife.common.blockentities.StovetopPotBlockEntity;
import net.minecraft.world.entity.player.Inventory;

public class StovetopPotContainer extends FourContainer<StovetopPotBlockEntity>
{
    public static StovetopPotContainer create(StovetopPotBlockEntity hive, Inventory playerInventory, int windowId)
    {
        return new StovetopPotContainer(hive, playerInventory, windowId).init(playerInventory);
    }

    public StovetopPotContainer(StovetopPotBlockEntity blockEntity, Inventory playerInv, int windowId)
    {
        super(FLContainerTypes.STOVETOP_POT.get(), windowId, blockEntity);
    }
}
