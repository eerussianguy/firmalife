package com.eerussianguy.firmalife.common.container;

import com.eerussianguy.firmalife.common.blockentities.StovetopPotBlockEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;

import net.dries007.tfc.common.capabilities.Capabilities;
import net.dries007.tfc.common.container.BlockEntityContainer;
import net.dries007.tfc.common.container.CallbackSlot;
import net.dries007.tfc.util.Helpers;

public class StovetopPotContainer extends BlockEntityContainer<StovetopPotBlockEntity>
{
    public static StovetopPotContainer create(StovetopPotBlockEntity pot, Inventory playerInventory, int windowId)
    {
        return new StovetopPotContainer(pot, windowId).init(playerInventory, 20);
    }

    public StovetopPotContainer(StovetopPotBlockEntity pot, int windowId)
    {
        super(FLContainerTypes.STOVETOP_POT.get(), windowId, pot);
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
        final IItemHandler inv = Helpers.getCapability(blockEntity, Capabilities.ITEM);
        if (inv != null)
        {
            for (int i = 0; i < StovetopPotBlockEntity.SLOTS; i++)
            {
                addSlot(new CallbackSlot(blockEntity, inv, i, 62 + i * 18, 20));
            }
        }
    }
}
