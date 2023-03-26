package com.eerussianguy.firmalife.common.blockentities;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandlerModifiable;

import com.eerussianguy.firmalife.common.FLHelpers;
import com.eerussianguy.firmalife.common.blocks.ICure;
import com.eerussianguy.firmalife.common.blocks.OvenBottomBlock;
import com.eerussianguy.firmalife.common.items.FLFoodTraits;
import com.eerussianguy.firmalife.common.recipes.WrappedHeatingRecipe;
import net.dries007.tfc.common.blockentities.InventoryBlockEntity;
import net.dries007.tfc.common.blockentities.TickableInventoryBlockEntity;
import net.dries007.tfc.common.capabilities.DelegateItemHandler;
import net.dries007.tfc.common.capabilities.InventoryItemHandler;
import net.dries007.tfc.common.capabilities.SidedHandler;
import net.dries007.tfc.common.capabilities.food.FoodCapability;
import net.dries007.tfc.common.capabilities.heat.HeatCapability;
import net.dries007.tfc.common.capabilities.heat.IHeatBlock;
import net.dries007.tfc.common.recipes.inventory.ItemStackInventory;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.calendar.Calendars;
import net.dries007.tfc.util.calendar.ICalendarTickable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Works like a crucible but with a delay on producing the heating recipes.
 */
public class OvenTopBlockEntity extends TickableInventoryBlockEntity<OvenTopBlockEntity.OvenInventory> implements ICalendarTickable, OvenLike
{
    public static void cure(Level level, BlockState oldState, BlockState newState, BlockPos pos)
    {
        // copy state
        final BlockState placeState = newState.setValue(OvenBottomBlock.FACING, oldState.getValue(OvenBottomBlock.FACING));

        // copy data
        level.getBlockEntity(pos, FLBlockEntities.OVEN_TOP.get()).ifPresent(oven -> {
            final float temperature = oven.temperature;
            final float targetTemperature = oven.targetTemperature;
            final int targetTemperatureStabilityTicks = oven.targetTemperatureStabilityTicks;
            NonNullList<ItemStack> items = Helpers.extractAllItems(oven.inventory);

            level.setBlockAndUpdate(pos, placeState);
            level.getBlockEntity(pos, FLBlockEntities.OVEN_TOP.get()).ifPresent(newOven -> {
                newOven.temperature = temperature;
                newOven.targetTemperature = targetTemperature;
                newOven.targetTemperatureStabilityTicks = targetTemperatureStabilityTicks;
                Helpers.insertAllItems(newOven.inventory, items);
                newOven.markForSync();
            });
        });

    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, OvenTopBlockEntity oven)
    {
        if (oven.needsRecipeUpdate)
        {
            oven.needsRecipeUpdate = false;
            oven.updateCaches();
        }
        oven.checkForLastTickSync();
        oven.checkForCalendarUpdate();

        if (oven.temperature != oven.targetTemperature)
        {
            oven.temperature = HeatCapability.adjustTempTowards(oven.temperature, oven.targetTemperature);

            for (Direction d : Direction.Plane.HORIZONTAL)
            {
                if (level.getBlockEntity(pos.relative(d)) instanceof OvenTopBlockEntity otherOven)
                {
                    otherOven.getCapability(HeatCapability.BLOCK_CAPABILITY).ifPresent(cap -> cap.setTemperatureIfWarmer(oven.temperature - 100f));
                }
            }
        }
        final boolean cured = state.getBlock() instanceof ICure cure && cure.isCured();
        final int updateInterval = 40;
        if (level.getGameTime() % updateInterval == 0)
        {
            OvenLike.regularBlockUpdate(level, pos, state, oven, cured, updateInterval);
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

                    final WrappedHeatingRecipe recipe = oven.cachedRecipes[slot];
                    if (recipe != null && recipe.isValidTemperature(cap.getTemperature()))
                    {
                        if (oven.cookTicks[slot]++ > recipe.duration())
                        {
                            // Convert input
                            final ItemStackInventory inventory = new ItemStackInventory(inputStack);
                            final ItemStack outputItem = recipe.assemble(inventory);

                            // Output transformations
                            outputItem.getCapability(HeatCapability.CAPABILITY).ifPresent(outputCap -> outputCap.setTemperature(oven.temperature));
                            FoodCapability.applyTrait(outputItem, FLFoodTraits.OVEN_BAKED);

                            // Add output to oven
                            oven.inventory.setStackInSlot(slot, outputItem);
                            oven.markForSync();
                            oven.needsRecipeUpdate = true;
                        }
                    }
                });
            }
        }
    }

    public static final int SLOTS = 4;
    public static final int SLOT_INPUT_END = 3;
    public static final int SLOT_INPUT_START = 0;
    public static final int TARGET_TEMPERATURE_STABILITY_TICKS = 400;

    private final SidedHandler.Noop<IHeatBlock> sidedHeat;


    float temperature;
    private float targetTemperature;
    private int targetTemperatureStabilityTicks;
    private long lastPlayerTick;
    private boolean needsRecipeUpdate;
    private final WrappedHeatingRecipe[] cachedRecipes;
    private int[] cookTicks;
    private int cureTicks;

    public OvenTopBlockEntity(BlockPos pos, BlockState state)
    {
        super(FLBlockEntities.OVEN_TOP.get(), pos, state, OvenInventory::new, FLHelpers.blockEntityName("oven_top"));
        temperature = targetTemperature = targetTemperatureStabilityTicks = cureTicks = 0;
        lastPlayerTick = Calendars.SERVER.getTicks();
        cachedRecipes = new WrappedHeatingRecipe[4];
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
        cureTicks = nbt.getInt("cureTicks");
        lastPlayerTick = nbt.getLong("lastTick");
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
        nbt.putInt("cureTicks", cureTicks);
        nbt.putLong("lastTick", lastPlayerTick);
        super.saveAdditional(nbt);
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
        return lastPlayerTick;
    }

    @Override
    public void setLastUpdateTick(long ticks)
    {
        lastPlayerTick = ticks;
    }

    @Override
    public void setAndUpdateSlots(int slot)
    {
        super.setAndUpdateSlots(slot);
        singleRecipeUpdate(slot, true);
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
            singleRecipeUpdate(slot, false);
        }
    }

    private void singleRecipeUpdate(int slot, boolean wipe)
    {
        final WrappedHeatingRecipe previous = cachedRecipes[slot];
        cachedRecipes[slot] = WrappedHeatingRecipe.getRecipe(inventory.getStackInSlot(slot));
        if (wipe && previous != cachedRecipes[slot] || cachedRecipes[slot] == null)
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

    public int getTicksLeft(int slot)
    {
        assert level != null;
        if (cookTicks[slot] == 0)
        {
            return -1;
        }
        if (cachedRecipes[slot] == null)
        {
            if (level.isClientSide)
            {
                singleRecipeUpdate(slot, true);
            }
        }
        if (cachedRecipes[slot] != null)
        {
            return cachedRecipes[slot].duration() - cookTicks[slot];
        }
        return -1;
    }

    static class OvenInventory implements DelegateItemHandler, CrucibleLikeHeatBlock, INBTSerializable<CompoundTag>
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
        public void setTargetTemperature(float temp)
        {
            oven.targetTemperature = temp;
        }

        @Override
        public float getTargetTemperature()
        {
            return oven.targetTemperature;
        }

        @Override
        public void setTemperature(float temperature)
        {
            CrucibleLikeHeatBlock.super.setTemperature(temperature);
            oven.temperature = temperature;
        }

        @Override
        public void resetStability()
        {
            oven.targetTemperatureStabilityTicks = TARGET_TEMPERATURE_STABILITY_TICKS;
            oven.markForSync();
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
