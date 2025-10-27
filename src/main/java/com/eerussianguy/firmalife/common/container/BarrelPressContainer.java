package com.eerussianguy.firmalife.common.container;

import com.eerussianguy.firmalife.common.blockentities.BarrelPressBlockEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandler;

import net.dries007.tfc.common.capabilities.BlockCapabilities;
import net.dries007.tfc.common.container.BlockEntityContainer;
import net.dries007.tfc.common.container.slot.CallbackSlot;
import net.dries007.tfc.util.Helpers;

public class BarrelPressContainer extends BlockEntityContainer<BarrelPressBlockEntity>
{
    public static BarrelPressContainer create(BarrelPressBlockEntity press, Inventory playerInventory, int windowId)
    {
        return new BarrelPressContainer(press, playerInventory, windowId).init(playerInventory);
    }

    public BarrelPressContainer(BarrelPressBlockEntity blockEntity, Inventory playerInv, int windowId)
    {
        super(FLMenuTypes.BARREL_PRESS.get(), windowId, blockEntity);
    }

    @Override
    protected void addContainerSlots()
    {
        IItemHandler inventory = Helpers.getCapability(BlockCapabilities.ITEM, blockEntity);
        if (inventory != null) {
            addSlot(new CallbackSlot(blockEntity, inventory, BarrelPressBlockEntity.SLOT_GRAPES, 17, 19));
            addSlot(new CallbackSlot(blockEntity, inventory, BarrelPressBlockEntity.SLOT_GRAPES + 1, 52, 19));
            addSlot(new CallbackSlot(blockEntity, inventory, BarrelPressBlockEntity.SLOT_GRAPES + 2, 70, 19));
            addSlot(new CallbackSlot(blockEntity, inventory, BarrelPressBlockEntity.SLOT_GRAPES + 3, 88, 19));
            addSlot(new CallbackSlot(blockEntity, inventory, BarrelPressBlockEntity.SLOT_GRAPES + 4, 106, 19));
            addSlot(new CallbackSlot(blockEntity, inventory, BarrelPressBlockEntity.SLOT_WINE_IN, 132, 19));
            addSlot(new CallbackSlot(blockEntity, inventory, BarrelPressBlockEntity.SLOT_WINE_OUT, 132, 55));
            addSlot(new CallbackSlot(blockEntity, inventory, BarrelPressBlockEntity.SLOT_CORK, 154, 19));
            addSlot(new CallbackSlot(blockEntity, inventory, BarrelPressBlockEntity.SLOT_LABEL, 154, 37));
        }
    }

    @Override
    protected boolean moveStack(ItemStack stack, int slotIndex)
    {
        return switch (typeOf(slotIndex))
            {
                case MAIN_INVENTORY, HOTBAR -> !moveItemStackTo(stack, 0, BarrelPressBlockEntity.SLOT_LABEL + 1, false);
                case CONTAINER -> !moveItemStackTo(stack, containerSlots, slots.size(), false);
            };
    }
}
