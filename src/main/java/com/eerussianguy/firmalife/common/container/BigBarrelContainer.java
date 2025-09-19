package com.eerussianguy.firmalife.common.container;

import com.eerussianguy.firmalife.common.FLHelpers;
import com.eerussianguy.firmalife.common.blockentities.BigBarrelBlockEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import net.dries007.tfc.common.capabilities.Capabilities;
import net.dries007.tfc.common.container.BlockEntityContainer;
import net.dries007.tfc.common.container.CallbackSlot;

public class BigBarrelContainer extends BlockEntityContainer<BigBarrelBlockEntity>
{
    public static BigBarrelContainer create(BigBarrelBlockEntity barrel, Inventory playerInv, int windowId)
    {
        return new BigBarrelContainer(windowId, barrel).init(playerInv, 56);
    }

    private BigBarrelContainer(int windowId, BigBarrelBlockEntity blockEntity)
    {
        super(FLMenuTypes.BIG_BARREL.get(), windowId, blockEntity);
    }

    @Override
    protected void addContainerSlots()
    {
        blockEntity.getCapability(Capabilities.ITEM).ifPresent(inventory -> {
            int i = 0;
            for (int y = 0; y < 6; y++)
            {
                for (int x = 0; x < 6; x++)
                {
                    addSlot(new CallbackSlot(blockEntity, inventory, i, 62 + (x * 18), 18 + (y * 18)));
                    i++;
                }
            }
            addSlot(new CallbackSlot(blockEntity, inventory, BigBarrelBlockEntity.SLOT_FLUID_CONTAINER_IN, 35, 18));
            addSlot(new CallbackSlot(blockEntity, inventory, BigBarrelBlockEntity.SLOT_FLUID_CONTAINER_OUT, 35, 54));
        });
    }

    @Override
    protected boolean moveStack(ItemStack stack, int slotIndex)
    {
        return switch (typeOf(slotIndex))
            {
                case MAIN_INVENTORY, HOTBAR -> !moveItemStackTo(stack, 0, BigBarrelBlockEntity.SLOT_FLUID_CONTAINER_IN - 1, false);
                case CONTAINER -> !moveItemStackTo(stack, containerSlots, slots.size(), false);
            };
    }

    @Override
    public void removed(Player player)
    {
        super.removed(player);
        FLHelpers.returnItem(player, slots.get(BigBarrelBlockEntity.SLOT_FLUID_CONTAINER_IN).remove(1));
        FLHelpers.returnItem(player, slots.get(BigBarrelBlockEntity.SLOT_FLUID_CONTAINER_OUT).remove(1));
    }

}
