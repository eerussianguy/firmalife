package com.eerussianguy.firmalife.common.container;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;

import net.minecraftforge.items.CapabilityItemHandler;

import com.eerussianguy.firmalife.common.blockentities.FLBeehiveBlockEntity;
import net.dries007.tfc.common.container.BlockEntityContainer;
import net.dries007.tfc.common.container.CallbackSlot;

public class BeehiveContainer extends BlockEntityContainer<FLBeehiveBlockEntity>
{
    public static BeehiveContainer create(FLBeehiveBlockEntity hive, Inventory playerInventory, int windowId)
    {
        return new BeehiveContainer(hive, playerInventory, windowId).init(playerInventory);
    }

    public BeehiveContainer(FLBeehiveBlockEntity blockEntity, Inventory playerInv, int windowId)
    {
        super(FLContainerTypes.BEEHIVE.get(), windowId, blockEntity);
    }

    @Override
    protected boolean moveStack(ItemStack stack, int slotIndex)
    {
        return switch (typeOf(slotIndex))
            {
                case MAIN_INVENTORY, HOTBAR -> !moveItemStackTo(stack, 0, FLBeehiveBlockEntity.SLOTS, false);
                case CONTAINER -> !moveItemStackTo(stack, containerSlots, slots.size(), false);
            };
    }

    @Override
    protected void addContainerSlots()
    {
        blockEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent(handler -> {
            addSlot(new CallbackSlot(blockEntity, handler, 0, 71, 23));
            addSlot(new CallbackSlot(blockEntity, handler, 1, 89, 23));
            addSlot(new CallbackSlot(blockEntity, handler, 2, 71, 41));
            addSlot(new CallbackSlot(blockEntity, handler, 3, 89, 41));
        });
    }
}
