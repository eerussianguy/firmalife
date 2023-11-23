package com.eerussianguy.firmalife.common.blockentities;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.common.blockentities.InventoryBlockEntity;
import net.dries007.tfc.common.blockentities.TickableInventoryBlockEntity;
import net.dries007.tfc.common.capabilities.DelegateItemHandler;
import net.dries007.tfc.common.capabilities.InventoryItemHandler;
import net.dries007.tfc.common.capabilities.SidedHandler;
import net.dries007.tfc.common.capabilities.heat.HeatCapability;
import net.dries007.tfc.common.capabilities.heat.IHeatBlock;
import net.dries007.tfc.common.recipes.inventory.EmptyInventory;
import net.dries007.tfc.util.IntArrayBuilder;
import net.dries007.tfc.util.calendar.Calendars;
import net.dries007.tfc.util.calendar.ICalendarTickable;

public abstract class ApplianceBlockEntity<C extends IItemHandlerModifiable & INBTSerializable<CompoundTag> & IHeatBlock> extends TickableInventoryBlockEntity<C> implements ICalendarTickable
{
    private long lastUpdateTick;
    protected float temperature = 0;
    protected float targetTemperature = 0;
    protected int targetTemperatureStabilityTicks = 0;
    protected final ContainerData syncableData;

    private final SidedHandler.Noop<IHeatBlock> sidedHeat;

    public ApplianceBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state, InventoryFactory<C> inventoryFactory, Component name)
    {
        super(type, pos, state, inventoryFactory, name);
        lastUpdateTick = Calendars.SERVER.getTicks();

        sidedHeat = new SidedHandler.Noop<>(inventory);
        syncableData = new IntArrayBuilder().add(() -> (int) temperature, value -> temperature = value);
    }

    public ContainerData getSyncableData()
    {
        return syncableData;
    }

    public void tickTemperature()
    {
        if (temperature != targetTemperature)
        {
            temperature = HeatCapability.adjustTempTowards(temperature, targetTemperature);
            onTemperatureAdjusted();
        }
        if (targetTemperatureStabilityTicks > 0)
        {
            targetTemperatureStabilityTicks--;
        }
        if (targetTemperature > 0 && targetTemperatureStabilityTicks == 0)
        {
            // target temperature decays constantly, since it is set externally. As long as we don't consider ourselves 'stable'
            targetTemperature = HeatCapability.adjustTempTowards(targetTemperature, 0);
        }
    }

    public void onTemperatureAdjusted() {}

    @Override
    public void onCalendarUpdate(long ticks)
    {
        assert level != null;
        // Crucible has no fuel to consume, but it does drop the internal target and temperature over time.
        final boolean wasHot = temperature > 0;
        targetTemperature = HeatCapability.adjustTempTowards(targetTemperature, 0, ticks);
        temperature = HeatCapability.adjustTempTowards(temperature, targetTemperature, ticks);
        if (wasHot && temperature == 0)
        {
            ranOutDueToCalendar();
            markForSync();
        }
    }

    public void ranOutDueToCalendar() {}

    @Override
    public long getLastCalendarUpdateTick()
    {
        return lastUpdateTick;
    }

    @Override
    public void setLastCalendarUpdateTick(long tick)
    {
        lastUpdateTick = tick;
    }

    public float getTemperature()
    {
        return temperature;
    }

    @Override
    public void loadAdditional(CompoundTag nbt)
    {
        lastUpdateTick = nbt.getLong("lastTick");
        temperature = nbt.getFloat("temperature");
        targetTemperature = nbt.getFloat("targetTemperature");
        targetTemperatureStabilityTicks = nbt.getInt("targetTemperatureStabilityTicks");
        super.loadAdditional(nbt);
    }

    @Override
    public void saveAdditional(CompoundTag nbt)
    {
        nbt.putLong("lastTick", lastUpdateTick);
        nbt.putFloat("temperature", temperature);
        nbt.putFloat("targetTemperature", targetTemperature);
        nbt.putInt("targetTemperatureStabilityTicks", targetTemperatureStabilityTicks);
        super.saveAdditional(nbt);
    }

    @NotNull
    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> cap, @Nullable Direction side)
    {
        if (cap == HeatCapability.BLOCK_CAPABILITY)
        {
            return sidedHeat.getSidedHandler(side).cast();
        }
        return super.getCapability(cap, side);
    }

    public static class ApplianceInventory implements EmptyInventory, DelegateItemHandler, INBTSerializable<CompoundTag>, CrucibleLikeHeatBlock
    {
        private final ApplianceBlockEntity<?> appliance;
        protected final ItemStackHandler inventory;

        public ApplianceInventory(InventoryBlockEntity<?> entity, int slots)
        {
            this.appliance = (ApplianceBlockEntity<?>) entity;
            this.inventory = new InventoryItemHandler(entity, slots);
        }

        @Override
        public IItemHandlerModifiable getItemHandler()
        {
            return inventory;
        }

        @Override
        public CompoundTag serializeNBT()
        {
            CompoundTag nbt = new CompoundTag();
            nbt.put("inventory", inventory.serializeNBT());
            return nbt;
        }

        @Override
        public void deserializeNBT(CompoundTag nbt)
        {
            inventory.deserializeNBT(nbt.getCompound("inventory"));
        }

        @Override
        public float getTemperature()
        {
            return appliance.temperature;
        }

        @Override
        public void setTargetTemperature(float temp)
        {
            appliance.targetTemperature = temp;
        }

        @Override
        public float getTargetTemperature()
        {
            return appliance.targetTemperature;
        }

        @Override
        public void resetStability()
        {
            appliance.targetTemperatureStabilityTicks = OvenTopBlockEntity.TARGET_TEMPERATURE_STABILITY_TICKS;
            appliance.markForSync();
        }

        @Override
        public void setTemperature(float temperature)
        {
            CrucibleLikeHeatBlock.super.setTemperature(temperature);
            appliance.temperature = temperature;
        }
    }
}
