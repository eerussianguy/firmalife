package com.eerussianguy.firmalife.client.screen;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

import com.eerussianguy.firmalife.common.container.BeehiveContainer;
import net.dries007.tfc.client.screen.TFCContainerScreen;

public class BeehiveScreen extends TFCContainerScreen<BeehiveContainer>
{
    public BeehiveScreen(BeehiveContainer container, Inventory playerInventory, Component name)
    {
        super(container, playerInventory, name, INVENTORY_2x2);
    }
}
