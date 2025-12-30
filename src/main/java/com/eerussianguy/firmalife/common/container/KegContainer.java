package com.eerussianguy.firmalife.common.container;

import com.eerussianguy.firmalife.common.blockentities.KegBlockEntity;
import com.eerussianguy.firmalife.common.blocks.KegCoreBlock;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.items.IItemHandler;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.common.capabilities.BlockCapabilities;
import net.dries007.tfc.common.component.heat.HeatCapability;
import net.dries007.tfc.common.component.heat.IHeat;
import net.dries007.tfc.common.container.BlockEntityContainer;
import net.dries007.tfc.common.container.ButtonHandlerContainer;
import net.dries007.tfc.common.container.slot.CallbackSlot;
import net.dries007.tfc.util.Helpers;

public class KegContainer extends BlockEntityContainer<KegBlockEntity> implements ButtonHandlerContainer
{
    public static KegContainer create(KegBlockEntity barrel, Inventory playerInv, int windowId)
    {
        return new KegContainer(windowId, barrel).init(playerInv, 76);
    }

    private KegContainer(int windowId, KegBlockEntity blockEntity)
    {
        super(FLMenuTypes.KEG.get(), windowId, blockEntity);
    }

    @Override
    public void clicked(int slot, int button, ClickType clickType, Player player)
    {
        if (slot >= 0 && slot < KegBlockEntity.SLOTS && blockEntity.getBlockState().getValue(KegCoreBlock.SEALED))
        {
            return;
        }
        super.clicked(slot, button, clickType, player);
    }

    @Override
    public void onButtonPress(int buttonID, @Nullable CompoundTag extraNBT)
    {
        final Level level = blockEntity.getLevel();
        if (level != null)
        {
            KegCoreBlock.toggleSeal(level, blockEntity.getBlockPos(), blockEntity.getBlockState());
        }
    }

    @Override
    protected void addContainerSlots()
    {
        final IItemHandler inventory = Helpers.getCapability(BlockCapabilities.ITEM, blockEntity);
        if (inventory != null)
        {
            addSlot(new CallbackSlot(blockEntity, inventory, KegBlockEntity.SLOT_FLUID_CONTAINER_IN, 35, 18));
            addSlot(new CallbackSlot(blockEntity, inventory, KegBlockEntity.SLOT_FLUID_CONTAINER_OUT, 35, 54));

            int i = KegBlockEntity.SLOT_INPUT_START;
            for (int y = 0; y < 6; y++)
            {
                for (int x = 0; x < 6; x++)
                {
                    addSlot(new CallbackSlot(blockEntity, inventory, i, 62 + (x * 18), 18 + (y * 18)));
                    i++;
                }
            }
        }
    }

    @Override
    protected boolean moveStack(ItemStack stack, int slotIndex)
    {
        if (blockEntity.getBlockState().getValue(KegCoreBlock.SEALED))
        {
            return true;
        }

        final @Nullable IHeat heat = HeatCapability.get(stack);
        final boolean containerSlot = stack.getCapability(Capabilities.FluidHandler.ITEM) != null
            && heat != null
            && heat.getTemperature() == 0;

        return switch (typeOf(slotIndex))
        {
            case MAIN_INVENTORY, HOTBAR -> containerSlot
                ? !moveItemStackTo(stack, KegBlockEntity.SLOT_FLUID_CONTAINER_IN, KegBlockEntity.SLOT_FLUID_CONTAINER_IN + 1, false)
                : !moveItemStackTo(stack, KegBlockEntity.SLOT_INPUT_START, slots.size(), false);
            case CONTAINER -> !moveItemStackTo(stack, containerSlots, slots.size(), false);
        };
    }

}
