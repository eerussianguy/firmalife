package com.eerussianguy.firmalife.common.blockentities;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandlerModifiable;

import com.eerussianguy.firmalife.common.FLHelpers;
import com.eerussianguy.firmalife.common.blocks.AbstractOvenBlock;
import net.dries007.tfc.common.blockentities.InventoryBlockEntity;
import net.dries007.tfc.common.blockentities.TickableInventoryBlockEntity;
import net.dries007.tfc.common.capabilities.DelegateItemHandler;
import net.dries007.tfc.common.capabilities.InventoryItemHandler;
import net.dries007.tfc.common.capabilities.SidedHandler;
import net.dries007.tfc.common.capabilities.heat.HeatCapability;
import net.dries007.tfc.common.capabilities.heat.IHeatBlock;
import net.dries007.tfc.common.recipes.HeatingRecipe;
import net.dries007.tfc.common.recipes.inventory.ItemStackInventory;
import net.dries007.tfc.util.calendar.Calendars;
import net.dries007.tfc.util.calendar.ICalendar;
import net.dries007.tfc.util.calendar.ICalendarTickable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Works like a crucible but with a delay on producing the heating recipes.
 */
public class OvenTopBlockEntity extends TickableInventoryBlockEntity<OvenTopBlockEntity.OvenInventory> implements ICalendarTickable
{
    public static final int SLOTS = 4;
    public static final int SLOT_INPUT_END = 3;
    public static final int SLOT_INPUT_START = 0;
    public static final int TARGET_TEMPERATURE_STABILITY_TICKS = 400;
    public static final float CURE_TEMP = 600f;
    public static final int COOK_TIME = ICalendar.TICKS_IN_HOUR * 2;

    private final SidedHandler.Noop<IHeatBlock> sidedHeat;

    public static void serverTick(Level level, BlockPos pos, BlockState state, OvenTopBlockEntity oven)
    {
        oven.checkForLastTickSync();
        oven.checkForCalendarUpdate();

        if (oven.needsRecipeUpdate)
        {
            oven.needsRecipeUpdate = false;
            oven.updateCaches();
        }

        if (oven.temperature != oven.targetTemperature)
        {
            oven.temperature = HeatCapability.adjustTempTowards(oven.temperature, oven.targetTemperature);
        }
        final boolean cured = state.getValue(AbstractOvenBlock.CURED);
        if (oven.temperature > CURE_TEMP)
        {
            AbstractOvenBlock.cure(level, pos, !cured);
        }

        if (oven.targetTemperatureStabilityTicks > 0)
        {
            oven.targetTemperatureStabilityTicks--;
        }
        if (oven.targetTemperature > 0 && oven.targetTemperatureStabilityTicks == 0)
        {
            // target temperature decays constantly, since it is set externally. As long as we don't consider ourselves 'stable'
            oven.targetTemperature = HeatCapability.adjustTempTowards(oven.targetTemperature, 0);
        }

        if (!cured) return;
        for (int i = SLOT_INPUT_START; i <= SLOT_INPUT_END; i++)
        {
            final int slot = i;
            final ItemStack inputStack = oven.inventory.getStackInSlot(i);
            if (!inputStack.isEmpty())
            {
                inputStack.getCapability(HeatCapability.CAPABILITY).ifPresent(cap -> {

                    // Always heat up the item regardless if it is melting or not
                    if (cap.getTemperature() < oven.temperature)
                    {
                        HeatCapability.addTemp(cap, oven.temperature, 2 + oven.temperature * 0.0025f); // Breaks even at 400 C
                    }

                    final HeatingRecipe recipe = oven.cachedRecipes[slot];
                    if (recipe != null && recipe.isValidTemperature(cap.getTemperature()))
                    {
                        if (oven.cookTicks[slot]++ > COOK_TIME)
                        {
                            // todo: ItemStackModifier
                            // Convert input
                            final ItemStackInventory inventory = new ItemStackInventory(inputStack);
                            final ItemStack outputItem = recipe.assemble(inventory);

                            // Output transformations
                            outputItem.getCapability(HeatCapability.CAPABILITY).ifPresent(outputCap -> outputCap.setTemperature(oven.temperature));

                            // Add output to crucible
                            oven.inventory.setStackInSlot(slot, outputItem);
                            oven.markForSync();
                        }
                    }
                });
            }
        }
    }


    private float temperature;
    private float targetTemperature;
    private int targetTemperatureStabilityTicks;
    private long lastUpdateTick;
    private boolean needsRecipeUpdate;
    private final HeatingRecipe[] cachedRecipes;
    private int[] cookTicks;

    public OvenTopBlockEntity(BlockPos pos, BlockState state)
    {
        super(FLBlockEntities.OVEN_TOP.get(), pos, state, OvenInventory::new, FLHelpers.blockEntityName("oven_top"));
        temperature = targetTemperature = targetTemperatureStabilityTicks = 0;
        lastUpdateTick = Calendars.SERVER.getTicks();
        cachedRecipes = new HeatingRecipe[4];
        cookTicks = new int[] {0, 0, 0, 0};

        sidedHeat = new SidedHandler.Noop<>(inventory);
    }

    @Override
    public void loadAdditional(CompoundTag nbt)
    {
        temperature = nbt.getFloat("temperature");
        targetTemperature = nbt.getFloat("targetTemperature");
        targetTemperatureStabilityTicks = nbt.getInt("targetTemperatureStabilityTicks");
        cookTicks = nbt.getIntArray("cookTicks");
        needsRecipeUpdate = true;
        super.loadAdditional(nbt);
    }

    @Override
    public void saveAdditional(CompoundTag nbt)
    {
        nbt.putFloat("temperature", temperature);
        nbt.putFloat("targetTemperature", targetTemperature);
        nbt.putInt("targetTemperatureStabilityTicks", targetTemperatureStabilityTicks);
        nbt.putIntArray("cookTicks", cookTicks);
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

    @Override
    public void onCalendarUpdate(long ticks)
    {
        assert level != null;
        // Crucible has no fuel to consume, but it does drop the internal target and temperature over time.
        targetTemperature = HeatCapability.adjustTempTowards(targetTemperature, 0, ticks);
        temperature = HeatCapability.adjustTempTowards(temperature, targetTemperature, ticks);
    }

    @Override
    public long getLastUpdateTick()
    {
        return lastUpdateTick;
    }

    @Override
    public void setLastUpdateTick(long ticks)
    {
        lastUpdateTick = ticks;
    }

    @Override
    public void setAndUpdateSlots(int slot)
    {
        super.setAndUpdateSlots(slot);
        singleRecipeUpdate(slot);
    }

    @Override
    public boolean isItemValid(int slot, ItemStack stack)
    {
        return stack.getCapability(HeatCapability.CAPABILITY).isPresent();
    }

    @Override
    public int getSlotStackLimit(int slot)
    {
        return 1;
    }

    private void updateCaches()
    {
        for (int slot = SLOT_INPUT_START; slot <= SLOT_INPUT_END; slot++)
        {
            singleRecipeUpdate(slot);
        }
    }

    private void singleRecipeUpdate(int slot)
    {
        HeatingRecipe previous = cachedRecipes[slot];
        cachedRecipes[slot] = HeatingRecipe.getRecipe(inventory.getStackInSlot(slot));
        if (previous != cachedRecipes[slot] || cachedRecipes[slot] == null)
        {
            cookTicks[slot] = 0;
        }
    }

    public void extinguish()
    {
        for (int i = SLOT_INPUT_START; i < SLOT_INPUT_END; i++)
        {
            cookTicks[i] = 0;
            cachedRecipes[i] = null;
        }
        temperature = 0;
        targetTemperature = 0;
        targetTemperatureStabilityTicks = 0;
    }

    static class OvenInventory implements DelegateItemHandler, IHeatBlock, INBTSerializable<CompoundTag>
    {
        private final OvenTopBlockEntity oven;
        private final InventoryItemHandler inventory;

        public OvenInventory(InventoryBlockEntity<?> blockEntity)
        {
            this.oven = (OvenTopBlockEntity) blockEntity;
            this.inventory = new InventoryItemHandler(blockEntity, SLOTS);
        }

        @Override
        public IItemHandlerModifiable getItemHandler()
        {
            return inventory;
        }

        @Override
        public float getTemperature()
        {
            return oven.temperature;
        }

        @Override
        public void setTemperature(float temperature)
        {
            oven.targetTemperature = temperature;
            oven.targetTemperatureStabilityTicks = TARGET_TEMPERATURE_STABILITY_TICKS;
            oven.markForSync();
        }

        @Override
        public void setTemperatureIfWarmer(float temperature)
        {
            // Override to still cause an update to the stability ticks
            if (temperature >= oven.temperature)
            {
                oven.temperature = temperature;
                oven.targetTemperatureStabilityTicks = TARGET_TEMPERATURE_STABILITY_TICKS;
                oven.markForSync();
            }
        }

        @Override
        public CompoundTag serializeNBT()
        {
            final CompoundTag nbt = new CompoundTag();
            nbt.put("inventory", inventory.serializeNBT());
            return nbt;
        }

        @Override
        public void deserializeNBT(CompoundTag nbt)
        {
            inventory.deserializeNBT(nbt.getCompound("inventory"));
        }
    }
}
