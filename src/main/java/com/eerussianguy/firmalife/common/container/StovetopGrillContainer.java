package com.eerussianguy.firmalife.common.container;

import com.eerussianguy.firmalife.common.blockentities.StovetopGrillBlockEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;

import net.dries007.tfc.common.capabilities.Capabilities;
import net.dries007.tfc.common.container.BlockEntityContainer;
import net.dries007.tfc.common.container.CallbackSlot;
import net.dries007.tfc.util.Helpers;


public class StovetopGrillContainer extends BlockEntityContainer<StovetopGrillBlockEntity>
{
    public static StovetopGrillContainer create(StovetopGrillBlockEntity grill, Inventory playerInventory, int windowId)
    {
        return new StovetopGrillContainer(grill, windowId).init(playerInventory, 20);
    }

    public StovetopGrillContainer(StovetopGrillBlockEntity grill, int windowId)
    {
        super(FLContainerTypes.STOVETOP_GRILL.get(), windowId, grill);

        addDataSlots(grill.getSyncableData());
    }

    @Override
    protected boolean moveStack(ItemStack stack, int slotIndex)
    {
        return switch (typeOf(slotIndex))
            {
                case MAIN_INVENTORY, HOTBAR -> !moveItemStackTo(stack, 0, StovetopGrillBlockEntity.SLOTS, false);
                case CONTAINER -> !moveItemStackTo(stack, containerSlots, slots.size(), false);
            };
    }

    @Override
    protected void addContainerSlots()
    {
        final IItemHandler inv = Helpers.getCapability(blockEntity, Capabilities.ITEM);
        if (inv != null)
        {
            for (int i = 0; i < StovetopGrillBlockEntity.SLOTS; i++)
            {
                addSlot(new CallbackSlot(blockEntity, inv, i, 62 + i * 18, 20));
            }
        }
    }
}
