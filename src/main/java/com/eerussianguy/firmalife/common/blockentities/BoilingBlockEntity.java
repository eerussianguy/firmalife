package com.eerussianguy.firmalife.common.blockentities;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.common.blockentities.InventoryBlockEntity;
import net.dries007.tfc.common.capabilities.Capabilities;
import net.dries007.tfc.common.capabilities.DelegateFluidHandler;
import net.dries007.tfc.common.capabilities.FluidTankCallback;
import net.dries007.tfc.common.capabilities.InventoryFluidTank;
import net.dries007.tfc.common.capabilities.PartialFluidHandler;
import net.dries007.tfc.common.capabilities.SidedHandler;
import net.dries007.tfc.common.capabilities.heat.IHeatBlock;
import net.dries007.tfc.util.calendar.ICalendarTickable;

public abstract class BoilingBlockEntity<C extends IItemHandlerModifiable & INBTSerializable<CompoundTag> & IHeatBlock & IFluidHandler> extends ApplianceBlockEntity<C> implements ICalendarTickable, FluidTankCallback
{
    protected int boilingTicks = 0;
    protected boolean needsRecipeUpdate = true;
    private final SidedHandler.Builder<IFluidHandler> sidedFluidInventory;

    public BoilingBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state, InventoryFactory<C> inventoryFactory, Component name)
    {
        super(type, pos, state, inventoryFactory, name);

        sidedFluidInventory = new SidedHandler.Builder<>(inventory);
        sidedFluidInventory.on(new PartialFluidHandler(inventory).insert(), Direction.UP)
            .on(new PartialFluidHandler(inventory).extract(), Direction.Plane.HORIZONTAL);
    }

    @NotNull
    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> cap, @Nullable Direction side)
    {
        if (cap == Capabilities.FLUID)
        {
            return sidedFluidInventory.getSidedHandler(side).cast();
        }
        return super.getCapability(cap, side);
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
    public void loadAdditional(CompoundTag nbt)
    {
        boilingTicks = nbt.getInt("boilingTicks");
        needsRecipeUpdate = true;
        super.loadAdditional(nbt);
    }

    @Override
    public void saveAdditional(CompoundTag nbt)
    {
        nbt.putInt("boilingTicks", boilingTicks);
        super.saveAdditional(nbt);
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
        public CompoundTag serializeNBT()
        {
            CompoundTag nbt = super.serializeNBT();
            nbt.put("tank", tank.writeToNBT(new CompoundTag()));
            return nbt;
        }

        @Override
        public void deserializeNBT(CompoundTag nbt)
        {
            super.deserializeNBT(nbt);
            tank.readFromNBT(nbt.getCompound("tank"));
        }
    }
}
