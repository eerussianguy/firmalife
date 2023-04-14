package com.eerussianguy.firmalife.common.blockentities;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import com.eerussianguy.firmalife.common.FLHelpers;
import com.eerussianguy.firmalife.common.blocks.ICure;
import com.eerussianguy.firmalife.common.items.FLFoodTraits;
import com.eerussianguy.firmalife.common.recipes.WrappedHeatingRecipe;
import net.dries007.tfc.common.blockentities.InventoryBlockEntity;
import net.dries007.tfc.common.capabilities.food.FoodCapability;
import net.dries007.tfc.common.capabilities.heat.HeatCapability;
import net.dries007.tfc.common.recipes.inventory.ItemStackInventory;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.calendar.ICalendarTickable;

/**
 * Works like a crucible but with a delay on producing the heating recipes.
 */
public class OvenTopBlockEntity extends ApplianceBlockEntity<ApplianceBlockEntity.ApplianceInventory> implements ICalendarTickable, OvenLike
{
    public static void cure(Level level, BlockState oldState, BlockState newState, BlockPos pos)
    {
        // copy state
        final BlockState placeState = Helpers.copyProperties(newState, oldState);

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

        final boolean cured = state.getBlock() instanceof ICure cure && cure.isCured();
        final int updateInterval = 40;
        if (level.getGameTime() % updateInterval == 0)
        {
            OvenLike.regularBlockUpdate(level, pos, state, oven, cured, updateInterval);
        }
        oven.tickTemperature();

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

    private boolean needsRecipeUpdate;
    private final WrappedHeatingRecipe[] cachedRecipes;
    private int[] cookTicks;
    private int cureTicks;

    public OvenTopBlockEntity(BlockPos pos, BlockState state)
    {
        super(FLBlockEntities.OVEN_TOP.get(), pos, state, OvenInventory::new, FLHelpers.blockEntityName("oven_top"));
        cureTicks = 0;
        cachedRecipes = new WrappedHeatingRecipe[4];
        cookTicks = new int[] {0, 0, 0, 0};
    }

    @Override
    public void loadAdditional(CompoundTag nbt)
    {
        cookTicks = nbt.getIntArray("cookTicks");
        cureTicks = nbt.getInt("cureTicks");
        needsRecipeUpdate = true;
        super.loadAdditional(nbt);
    }

    @Override
    public void saveAdditional(CompoundTag nbt)
    {
        nbt.putIntArray("cookTicks", cookTicks);
        nbt.putInt("cureTicks", cureTicks);
        super.saveAdditional(nbt);
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
    public void ranOutDueToCalendar()
    {
        for (int i = 0; i < SLOTS; i++)
        {
            cookTicks[i] = 0;
        }
    }

    @Override
    public void onTemperatureAdjusted()
    {
        assert level != null;
        for (Direction d : Direction.Plane.HORIZONTAL)
        {
            if (level.getBlockEntity(getBlockPos().relative(d)) instanceof OvenTopBlockEntity otherOven)
            {
                otherOven.getCapability(HeatCapability.BLOCK_CAPABILITY).ifPresent(cap -> cap.setTemperatureIfWarmer(temperature - 100f));
            }
        }
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
        return Helpers.mightHaveCapability(stack, HeatCapability.CAPABILITY);
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

    public static class OvenInventory extends ApplianceBlockEntity.ApplianceInventory
    {
        public OvenInventory(InventoryBlockEntity<?> entity)
        {
            super(entity, SLOTS);
        }
    }
}
