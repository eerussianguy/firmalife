package com.eerussianguy.firmalife.common.container;

import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;

import net.dries007.tfc.common.blockentities.InventoryBlockEntity;
import net.dries007.tfc.common.capabilities.Capabilities;
import net.dries007.tfc.common.container.BlockEntityContainer;
import net.dries007.tfc.common.container.CallbackSlot;

public class FourContainer<T extends InventoryBlockEntity<?>> extends BlockEntityContainer<T>
{
    public FourContainer(MenuType<?> containerType, int windowId, T blockEntity)
    {
        super(containerType, windowId, blockEntity);
    }


    @Override
    protected boolean moveStack(ItemStack stack, int slotIndex)
    {
        return switch (typeOf(slotIndex))
            {
                case MAIN_INVENTORY, HOTBAR -> !moveItemStackTo(stack, 0, 4, false);
                case CONTAINER -> !moveItemStackTo(stack, containerSlots, slots.size(), false);
            };
    }

    @Override
    protected void addContainerSlots()
    {
        blockEntity.getCapability(Capabilities.ITEM).ifPresent(handler -> {
            addSlot(new CallbackSlot(blockEntity, handler, 0, 71, 23));
            addSlot(new CallbackSlot(blockEntity, handler, 1, 89, 23));
            addSlot(new CallbackSlot(blockEntity, handler, 2, 71, 41));
            addSlot(new CallbackSlot(blockEntity, handler, 3, 89, 41));
        });
    }
}
