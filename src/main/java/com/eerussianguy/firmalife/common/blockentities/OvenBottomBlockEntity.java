package com.eerussianguy.firmalife.common.blockentities;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

import net.minecraftforge.items.ItemStackHandler;

import com.eerussianguy.firmalife.common.FLHelpers;
import com.eerussianguy.firmalife.common.blocks.AbstractOvenBlock;
import com.eerussianguy.firmalife.common.blocks.OvenBottomBlock;
import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.blockentities.TickableInventoryBlockEntity;
import net.dries007.tfc.common.capabilities.heat.HeatCapability;
import net.dries007.tfc.util.Fuel;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.calendar.Calendars;
import net.dries007.tfc.util.calendar.ICalendarTickable;

/**
 * Consumes fuel like a charcoal forge but has no other functionality (like, executing recipes)
 */
public class OvenBottomBlockEntity extends TickableInventoryBlockEntity<ItemStackHandler> implements ICalendarTickable
{
    public static void serverTick(Level level, BlockPos pos, BlockState state, OvenBottomBlockEntity oven)
    {
        oven.checkForLastTickSync();
        oven.checkForCalendarUpdate();

        final boolean cured = state.getValue(AbstractOvenBlock.CURED);
        if (oven.temperature > OvenTopBlockEntity.CURE_TEMP)
        {
            AbstractOvenBlock.cure(level, pos, !cured);
        }

        if (level.getGameTime() % 40 == 0)
        {
           oven.updateLogs(state);
        }

        if (state.getValue(BlockStateProperties.LIT))
        {
            if (oven.burnTicks > 0)
            {
                oven.burnTicks -= oven.airTicks > 0 ? 2 : 1; // burn faster with bellows
            }
            if (oven.burnTicks <= 0 && !oven.consumeFuel())
            {
                oven.extinguish(state);
            }
        }
        else if (oven.burnTemperature > 0)
        {
            oven.extinguish(state);
        }
        if (oven.airTicks > 0)
        {
            oven.airTicks--;
        }

        // Always update temperature / cooking, until the fire pit is not hot anymore
        if (oven.temperature > 0 || oven.burnTemperature > 0)
        {
            oven.temperature = HeatCapability.adjustDeviceTemp(oven.temperature, oven.burnTemperature, oven.airTicks, false);

            // Provide heat to blocks above
            final BlockEntity above = level.getBlockEntity(pos.above());
            if (above != null)
            {
                above.getCapability(HeatCapability.BLOCK_CAPABILITY).ifPresent(cap -> cap.setTemperatureIfWarmer(oven.temperature));
            }
            oven.markForSync();
        }

        // This is here to avoid duplication glitches
        if (oven.needsSlotUpdate)
        {
            oven.cascadeFuelSlots();
            oven.updateLogs(state);
        }
    }

    public static final int SLOT_FUEL_MIN = 0;
    public static final int SLOT_FUEL_MAX = 3;
    private static final int MAX_AIR_TICKS = 600;

    private int burnTicks;
    private float burnTemperature;
    private long lastPlayerTick;
    private boolean needsSlotUpdate;
    private int airTicks;
    private float temperature;

    public OvenBottomBlockEntity(BlockPos pos, BlockState state)
    {
        super(FLBlockEntities.OVEN_BOTTOM.get(), pos, state, defaultInventory(4), FLHelpers.blockEntityName("oven_bottom"));
        temperature = 0;
        burnTemperature = 0;
        burnTicks = 0;
        airTicks = 0;
        lastPlayerTick = Calendars.SERVER.getTicks();
    }

    @Override
    public void onCalendarUpdate(long ticks)
    {
        BlockState state = getBlockState();
        if (state.getValue(BlockStateProperties.LIT))
        {
            HeatCapability.Remainder remainder = HeatCapability.consumeFuelForTicks(ticks, inventory, burnTicks, burnTemperature, SLOT_FUEL_MIN, SLOT_FUEL_MAX);

            burnTicks = remainder.burnTicks();
            burnTemperature = remainder.burnTemperature();
            needsSlotUpdate = true;

            if (remainder.ticks() > 0)
            {
                extinguish(state); // Consumed all fuel, so extinguish and cool instantly
            }
        }
    }

    @Override
    public long getLastUpdateTick()
    {
        return lastPlayerTick;
    }

    @Override
    public void setLastUpdateTick(long ticks)
    {
        lastPlayerTick = ticks;
    }

    @Override
    public void loadAdditional(CompoundTag nbt)
    {
        temperature = nbt.getFloat("temperature");
        burnTicks = nbt.getInt("burnTicks");
        airTicks = nbt.getInt("airTicks");
        burnTemperature = nbt.getFloat("burnTemperature");
        lastPlayerTick = nbt.getLong("lastPlayerTick");
        super.loadAdditional(nbt);
    }

    @Override
    public void saveAdditional(CompoundTag nbt)
    {
        nbt.putFloat("temperature", temperature);
        nbt.putInt("burnTicks", burnTicks);
        nbt.putInt("airTicks", airTicks);
        nbt.putFloat("burnTemperature", burnTemperature);
        nbt.putLong("lastPlayerTick", lastPlayerTick);
        super.saveAdditional(nbt);
    }

    @Override
    public void setAndUpdateSlots(int slot)
    {
        super.setAndUpdateSlots(slot);
        needsSlotUpdate = true;
    }

    @Override
    public int getSlotStackLimit(int slot)
    {
        return 1;
    }

    @Override
    public boolean isItemValid(int slot, ItemStack stack)
    {
        return Helpers.isItem(stack, TFCTags.Items.FIREPIT_FUEL);
    }

    /**
     * Attempts to consume one piece of fuel. Returns if the device consumed any fuel (and so, ended up lit)
     */
    private boolean consumeFuel()
    {
        final ItemStack fuelStack = inventory.getStackInSlot(SLOT_FUEL_MIN);
        if (!fuelStack.isEmpty())
        {
            // Try and consume a piece of fuel
            inventory.setStackInSlot(SLOT_FUEL_MIN, ItemStack.EMPTY);
            needsSlotUpdate = true;
            Fuel fuel = Fuel.get(fuelStack);
            if (fuel != null)
            {
                burnTicks += fuel.getDuration();
                burnTemperature = fuel.getTemperature();
            }
            markForSync();
        }
        return burnTicks > 0;
    }

    public boolean light(BlockState state)
    {
        assert level != null;
        if (consumeFuel())
        {
            level.setBlockAndUpdate(worldPosition, state.setValue(BlockStateProperties.LIT, true));
            return true;
        }
        return false;
    }

    public void extinguish(BlockState state)
    {
        assert level != null;
        level.setBlockAndUpdate(worldPosition, state.setValue(BlockStateProperties.LIT, false));
        burnTemperature = 0;
        burnTicks = 0;
        markForSync();
    }

    public void onAirIntake(int amount)
    {
        airTicks += amount;
        if (airTicks > MAX_AIR_TICKS)
        {
            airTicks = MAX_AIR_TICKS;
        }
    }

    private void cascadeFuelSlots()
    {
        // This will cascade all fuel down to the lowest available slot
        int lowestAvailSlot = SLOT_FUEL_MIN;
        for (int i = SLOT_FUEL_MIN; i <= SLOT_FUEL_MAX; i++)
        {
            ItemStack stack = inventory.getStackInSlot(i);
            if (!stack.isEmpty())
            {
                // Move to lowest avail slot
                if (i > lowestAvailSlot)
                {
                    inventory.setStackInSlot(lowestAvailSlot, stack.copy());
                    inventory.setStackInSlot(i, ItemStack.EMPTY);
                }
                lowestAvailSlot++;
            }
        }
        needsSlotUpdate = false;
    }

    private void updateLogs(BlockState state)
    {
        final int logs = countLogs();
        if (state.getValue(OvenBottomBlock.LOGS) != logs)
        {
            assert level != null;
            level.setBlockAndUpdate(worldPosition, state.setValue(OvenBottomBlock.LOGS, logs));
        }
    }

    private int countLogs()
    {
        int logs = 0;
        for (int i = SLOT_FUEL_MIN; i <= SLOT_FUEL_MAX; i++)
        {
            ItemStack stack = inventory.getStackInSlot(i);
            if (!stack.isEmpty())
            {
                logs++;
            }
        }
        return logs;
    }

}
