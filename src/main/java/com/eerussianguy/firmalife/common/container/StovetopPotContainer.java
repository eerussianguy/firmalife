package com.eerussianguy.firmalife.common.container;

import com.eerussianguy.firmalife.common.blockentities.StovetopPotBlockEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;

import net.dries007.tfc.common.container.BlockEntityContainer;
import net.dries007.tfc.common.container.slot.CallbackSlot;

public class StovetopPotContainer extends BlockEntityContainer<StovetopPotBlockEntity>
{
    public static StovetopPotContainer create(StovetopPotBlockEntity pot, Inventory playerInventory, int windowId)
    {
        return new StovetopPotContainer(pot, windowId).init(playerInventory, 20);
    }

    public StovetopPotContainer(StovetopPotBlockEntity pot, int windowId)
    {
        super(FLMenuTypes.STOVETOP_POT.get(), windowId, pot);
        addDataSlots(pot.getSyncableData());
    }

    @Override
    protected boolean moveStack(ItemStack stack, int slotIndex)
    {
        return switch (typeOf(slotIndex))
            {
                case MAIN_INVENTORY, HOTBAR -> !moveItemStackTo(stack, 0, StovetopPotBlockEntity.SLOTS, false);
                case CONTAINER -> !moveItemStackTo(stack, containerSlots, slots.size(), false);
            };
    }

    @Override
    protected void addContainerSlots()
    {
        addSlot(new CallbackSlot(blockEntity, 0, 65, 23));
        addSlot(new CallbackSlot(blockEntity, 1, 83, 23));
        addSlot(new CallbackSlot(blockEntity, 2, 56, 41));
        addSlot(new CallbackSlot(blockEntity, 3, 74, 41));
        addSlot(new CallbackSlot(blockEntity, 4, 92, 41));
    }
}
