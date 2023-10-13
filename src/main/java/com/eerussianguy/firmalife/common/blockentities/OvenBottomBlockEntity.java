package com.eerussianguy.firmalife.common.blockentities;

import com.eerussianguy.firmalife.config.FLConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

import net.minecraftforge.items.ItemStackHandler;

import com.eerussianguy.firmalife.common.FLHelpers;
import com.eerussianguy.firmalife.common.FLTags;
import com.eerussianguy.firmalife.common.blocks.ICure;
import com.eerussianguy.firmalife.common.blocks.OvenBottomBlock;
import net.dries007.tfc.common.blockentities.TickableInventoryBlockEntity;
import net.dries007.tfc.common.capabilities.Capabilities;
import net.dries007.tfc.common.capabilities.PartialItemHandler;
import net.dries007.tfc.common.capabilities.heat.HeatCapability;
import net.dries007.tfc.common.items.Powder;
import net.dries007.tfc.common.items.TFCItems;
import net.dries007.tfc.util.Fuel;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.calendar.Calendars;
import net.dries007.tfc.util.calendar.ICalendarTickable;

/**
 * Consumes fuel like a charcoal forge but has no other functionality (like, executing recipes)
 */
public class OvenBottomBlockEntity extends TickableInventoryBlockEntity<ItemStackHandler> implements ICalendarTickable, OvenLike
{
    public static void cure(Level level, BlockState oldState, BlockState newState, BlockPos pos)
    {
        // copy state
        final BlockState placeState = Helpers.copyProperties(newState, oldState);

        // copy data
        level.getBlockEntity(pos, FLBlockEntities.OVEN_BOTTOM.get()).ifPresent(oven -> {
            final float temperature = oven.temperature;
            final int burnTicks = oven.burnTicks;
            final float burnTemperature = oven.burnTemperature;
            final long lastPlayerTick = oven.lastPlayerTick;
            final int airTicks = oven.airTicks;

            NonNullList<ItemStack> items = Helpers.extractAllItems(oven.inventory);

            level.setBlockAndUpdate(pos, placeState);
            level.getBlockEntity(pos, FLBlockEntities.OVEN_BOTTOM.get()).ifPresent(newOven -> {
                newOven.temperature = temperature;
                newOven.burnTicks = burnTicks;
                newOven.burnTemperature = burnTemperature;
                newOven.lastPlayerTick = lastPlayerTick;
                newOven.airTicks = airTicks;
                Helpers.insertAllItems(newOven.inventory, items);
                newOven.markForSync();
            });
        });

    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, OvenBottomBlockEntity oven)
    {
        oven.checkForLastTickSync();
        oven.checkForCalendarUpdate();

        final int updateInterval = 40;
        final boolean cured = state.getBlock() instanceof ICure cure && cure.isCured();
        if (level.getGameTime() % updateInterval == 0)
        {
            OvenLike.regularBlockUpdate(level, pos, state, oven, cured, updateInterval);
            oven.updateLogs();
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
            oven.updateLogs();
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
    private int cureTicks;
    float temperature;

    public OvenBottomBlockEntity(BlockPos pos, BlockState state)
    {
        super(FLBlockEntities.OVEN_BOTTOM.get(), pos, state, defaultInventory(4), FLHelpers.blockEntityName("oven_bottom"));
        temperature = burnTemperature = burnTicks = airTicks = cureTicks = 0;
        lastPlayerTick = Calendars.SERVER.getTicks();

        if (state.hasProperty(OvenBottomBlock.FACING))
        {
            final Direction face = state.getValue(OvenBottomBlock.FACING);
            sidedInventory
                .on(new PartialItemHandler(inventory).insert(0, 1, 2, 3), d -> d == face.getClockWise() || d == face.getCounterClockWise())
                .on(new PartialItemHandler(inventory).extract(0, 1, 2, 3), d -> d == face.getOpposite() || d == Direction.DOWN);
        }
    }

    @Override
    public float getTemperature()
    {
        return temperature;
    }

    @Override
    public int getCureTicks()
    {
        return cureTicks;
    }

    @Override
    public void addCureTicks(int ticks)
    {
        cureTicks += ticks;
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
    public long getLastCalendarUpdateTick()
    {
        return lastPlayerTick;
    }

    @Override
    public void setLastCalendarUpdateTick(long ticks)
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
        cureTicks = nbt.getInt("cureTicks");
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
        nbt.putInt("cureTicks", cureTicks);
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
        return Helpers.isItem(stack, FLTags.Items.OVEN_FUEL);
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
            onFuelConsumed();
            markForSync();
        }
        return burnTicks > 0;
    }

    public void onFuelConsumed()
    {
        assert level != null;
        if (level.getRandom().nextFloat() < FLConfig.SERVER.ovenAshChance.get() && level.getBlockEntity(getBlockPos().below()) instanceof AshTrayBlockEntity tray)
        {
            tray.getCapability(Capabilities.ITEM).ifPresent(inv -> {
                final ItemStack leftover = inv.insertItem(0, new ItemStack(TFCItems.POWDERS.get(Powder.WOOD_ASH).get()), false);
                if (leftover.isEmpty())
                {
                    Helpers.playSound(level, getBlockPos(), SoundEvents.SAND_PLACE);
                    tray.updateBlockState();
                }
            });
        }
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

    private void updateLogs()
    {
        assert level != null;
        final int logs = countLogs();
        BlockState state = level.getBlockState(worldPosition);
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
