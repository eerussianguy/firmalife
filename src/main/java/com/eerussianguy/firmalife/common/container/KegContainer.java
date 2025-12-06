package com.eerussianguy.firmalife.common.container;

import com.eerussianguy.firmalife.common.FLHelpers;
import com.eerussianguy.firmalife.common.blockentities.KegBlockEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandler;

import net.dries007.tfc.common.capabilities.BlockCapabilities;
import net.dries007.tfc.common.container.BlockEntityContainer;
import net.dries007.tfc.common.container.slot.CallbackSlot;
import net.dries007.tfc.util.Helpers;

public class KegContainer extends BlockEntityContainer<KegBlockEntity>
{
    public static KegContainer create(KegBlockEntity barrel, Inventory playerInv, int windowId)
    {
        return new KegContainer(windowId, barrel).init(playerInv, 56);
    }

    private KegContainer(int windowId, KegBlockEntity blockEntity)
    {
        super(FLMenuTypes.KEG.get(), windowId, blockEntity);
    }

    @Override
    protected void addContainerSlots()
    {
        IItemHandler inventory = Helpers.getCapability(BlockCapabilities.ITEM, blockEntity);
        if (inventory != null) {
            int i = 0;
            for (int y = 0; y < 6; y++)
            {
                for (int x = 0; x < 6; x++)
                {
                    addSlot(new CallbackSlot(blockEntity, inventory, i, 62 + (x * 18), 18 + (y * 18)));
                    i++;
                }
            }
            addSlot(new CallbackSlot(blockEntity, inventory, KegBlockEntity.SLOT_FLUID_CONTAINER_IN, 35, 18));
            addSlot(new CallbackSlot(blockEntity, inventory, KegBlockEntity.SLOT_FLUID_CONTAINER_OUT, 35, 54));
        }
    }

    @Override
    protected boolean moveStack(ItemStack stack, int slotIndex)
    {
        return switch (typeOf(slotIndex))
            {
                case MAIN_INVENTORY, HOTBAR -> !moveItemStackTo(stack, 0, KegBlockEntity.SLOT_FLUID_CONTAINER_IN - 1, false);
                case CONTAINER -> !moveItemStackTo(stack, containerSlots, slots.size(), false);
            };
    }

    @Override
    public void removed(Player player)
    {
        super.removed(player);
        FLHelpers.returnItem(player, slots.get(KegBlockEntity.SLOT_FLUID_CONTAINER_IN).remove(1));
        FLHelpers.returnItem(player, slots.get(KegBlockEntity.SLOT_FLUID_CONTAINER_OUT).remove(1));
    }

}
