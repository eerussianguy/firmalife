package com.eerussianguy.firmalife.common.blockentities;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.util.INBTSerializable;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import org.jetbrains.annotations.NotNull;

import net.dries007.tfc.common.blockentities.InventoryBlockEntity;
import net.dries007.tfc.common.capabilities.DelegateFluidHandler;
import net.dries007.tfc.common.capabilities.FluidTankCallback;
import net.dries007.tfc.common.capabilities.InventoryFluidTank;
import net.dries007.tfc.common.component.heat.IHeatConsumer;
import net.dries007.tfc.util.calendar.ICalendarTickable;

public abstract class BoilingBlockEntity<C extends IItemHandlerModifiable & INBTSerializable<CompoundTag> & IHeatConsumer & IFluidHandler> extends ApplianceBlockEntity<C> implements ICalendarTickable, FluidTankCallback
{
    protected int boilingTicks = 0;
    protected boolean needsRecipeUpdate = true;

    public BoilingBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state, InventoryFactory<C> inventoryFactory, Component name)
    {
        super(type, pos, state, inventoryFactory, name);
    }

    @Override
    public void ranOutDueToCalendar()
    {
        boilingTicks = 0;
    }

    @Override
    public void fluidTankChanged()
    {
        FluidTankCallback.super.fluidTankChanged();
        needsRecipeUpdate = true;
    }

    @Override
    public void setAndUpdateSlots(int slot)
    {
        super.setAndUpdateSlots(slot);
        needsRecipeUpdate = true;
    }

    @Override
    public void loadAdditional(CompoundTag nbt, HolderLookup.Provider access)
    {
        boilingTicks = nbt.getInt("boilingTicks");
        needsRecipeUpdate = true;
        super.loadAdditional(nbt, access);
    }

    @Override
    public void saveAdditional(CompoundTag nbt, HolderLookup.Provider access)
    {
        nbt.putInt("boilingTicks", boilingTicks);
        super.saveAdditional(nbt, access);
    }

    abstract boolean isBoiling();

    public static class BoilingInventory extends ApplianceInventory implements DelegateFluidHandler, FluidTankCallback
    {
        protected final InventoryFluidTank tank;
        private final BoilingBlockEntity<?> boiling;

        public BoilingInventory(InventoryBlockEntity<?> entity, int slots, InventoryFluidTank tank)
        {
            super(entity, slots);
            this.tank = tank;
            this.boiling = (BoilingBlockEntity<?>) entity;
        }

        @Override
        public IFluidHandler getFluidHandler()
        {
            return tank;
        }

        @NotNull
        @Override
        public ItemStack extractItem(int slot, int amount, boolean simulate)
        {
            return boiling.isBoiling() ? ItemStack.EMPTY : inventory.extractItem(slot, amount, simulate);
        }

        @Override
        public CompoundTag serializeNBT(HolderLookup.Provider access)
        {
            CompoundTag nbt = super.serializeNBT(access);
            nbt.put("tank", tank.writeToNBT(access, new CompoundTag()));
            return nbt;
        }

        @Override
        public void deserializeNBT(HolderLookup.Provider access, CompoundTag nbt)
        {
            super.deserializeNBT(access, nbt);
            tank.readFromNBT(access, nbt.getCompound("tank"));
        }
    }
}
