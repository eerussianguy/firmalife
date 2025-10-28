package com.eerussianguy.firmalife.common.container;

import net.minecraft.world.entity.player.Inventory;

import com.eerussianguy.firmalife.common.blockentities.FLBeehiveBlockEntity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandler;

import net.dries007.tfc.common.capabilities.BlockCapabilities;
import net.dries007.tfc.common.container.BlockEntityContainer;
import net.dries007.tfc.common.container.slot.CallbackSlot;
import net.dries007.tfc.util.Helpers;

public class BeehiveContainer extends BlockEntityContainer<FLBeehiveBlockEntity>
{
    public static BeehiveContainer create(FLBeehiveBlockEntity hive, Inventory playerInventory, int windowId)
    {
        return new BeehiveContainer(hive, playerInventory, windowId).init(playerInventory);
    }

    public BeehiveContainer(FLBeehiveBlockEntity blockEntity, Inventory playerInv, int windowId)
    {
        super(FLMenuTypes.BEEHIVE.get(), windowId, blockEntity);
    }

    @Override
    protected void addContainerSlots()
    {
        IItemHandler inventory = Helpers.getCapability(BlockCapabilities.ITEM, blockEntity);
        if (inventory != null) {
            for (int i = 0; i < FLBeehiveBlockEntity.FRAME_SLOTS; i++)
            {
                addSlot(new CallbackSlot(blockEntity, inventory, i, 44 + (i * 18), 19));
            }
            addSlot(new CallbackSlot(blockEntity, inventory, FLBeehiveBlockEntity.SLOT_JAR_IN, 134, 19));
            addSlot(new CallbackSlot(blockEntity, inventory, FLBeehiveBlockEntity.SLOT_JAR_OUT, 134, 55));
        }
    }

    @Override
    protected boolean moveStack(ItemStack stack, int slotIndex)
    {
        return switch (typeOf(slotIndex))
            {
                case MAIN_INVENTORY, HOTBAR -> !moveItemStackTo(stack, 0, FLBeehiveBlockEntity.SLOT_JAR_OUT, false);
                case CONTAINER -> !moveItemStackTo(stack, containerSlots, slots.size(), false);
            };
    }
}
