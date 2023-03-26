package com.eerussianguy.firmalife.common.blockentities;

import java.util.ArrayList;
import java.util.List;
import com.eerussianguy.firmalife.common.FLHelpers;
import com.eerussianguy.firmalife.common.blocks.VatBlock;
import com.eerussianguy.firmalife.common.recipes.FLRecipeTypes;
import com.eerussianguy.firmalife.common.recipes.VatRecipe;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.blockentities.InventoryBlockEntity;
import net.dries007.tfc.common.blockentities.TickableInventoryBlockEntity;
import net.dries007.tfc.common.capabilities.Capabilities;
import net.dries007.tfc.common.capabilities.DelegateFluidHandler;
import net.dries007.tfc.common.capabilities.DelegateItemHandler;
import net.dries007.tfc.common.capabilities.FluidTankCallback;
import net.dries007.tfc.common.capabilities.InventoryFluidTank;
import net.dries007.tfc.common.capabilities.InventoryItemHandler;
import net.dries007.tfc.common.capabilities.PartialFluidHandler;
import net.dries007.tfc.common.capabilities.PartialItemHandler;
import net.dries007.tfc.common.capabilities.SidedHandler;
import net.dries007.tfc.common.capabilities.heat.HeatCapability;
import net.dries007.tfc.common.capabilities.heat.IHeatBlock;
import net.dries007.tfc.common.recipes.inventory.EmptyInventory;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.calendar.Calendars;
import net.dries007.tfc.util.calendar.ICalendarTickable;

public class VatBlockEntity extends TickableInventoryBlockEntity<VatBlockEntity.VatInventory> implements ICalendarTickable, FluidTankCallback
{
    public static void serverTick(Level level, BlockPos pos, BlockState state, VatBlockEntity vat)
    {
        vat.checkForLastTickSync();
        vat.checkForCalendarUpdate();

        final List<ItemStack> excess = vat.inventory.excess;
        if (!excess.isEmpty() && vat.inventory.getStackInSlot(0).isEmpty())
        {
            vat.inventory.setStackInSlot(0, excess.remove(0));
        }
        if (vat.needsRecipeUpdate)
        {
            vat.updateCachedRecipe();
        }

        if (vat.temperature != vat.targetTemperature)
        {
            vat.temperature = HeatCapability.adjustTempTowards(vat.temperature, vat.targetTemperature);
        }
        if (vat.targetTemperatureStabilityTicks > 0)
        {
            vat.targetTemperatureStabilityTicks--;
        }
        if (vat.targetTemperature > 0 && vat.targetTemperatureStabilityTicks == 0)
        {
            // target temperature decays constantly, since it is set externally. As long as we don't consider ourselves 'stable'
            vat.targetTemperature = HeatCapability.adjustTempTowards(vat.targetTemperature, 0);
        }
        vat.handleCooking();
    }

    public static final int CAPACITY = 10_000;

    private int boilingTicks = 0;
    private long lastUpdateTick;
    private float temperature = 0;
    private float targetTemperature = 0;
    private int targetTemperatureStabilityTicks = 0;
    private final SidedHandler.Builder<IFluidHandler> sidedFluidInventory;
    private boolean needsRecipeUpdate = true;
    @Nullable private VatRecipe cachedRecipe = null;

    private final SidedHandler.Noop<IHeatBlock> sidedHeat;

    public VatBlockEntity(BlockPos pos, BlockState state)
    {
        super(FLBlockEntities.VAT.get(), pos, state, VatInventory::new, FLHelpers.blockEntityName("vat"));
        lastUpdateTick = Calendars.SERVER.getTicks();

        sidedFluidInventory = new SidedHandler.Builder<>(inventory);
        sidedFluidInventory.on(new PartialFluidHandler(inventory).insert(), Direction.UP)
        .on(new PartialFluidHandler(inventory).extract(), Direction.Plane.HORIZONTAL);

        sidedInventory.on(new PartialItemHandler(inventory).insert(), Direction.Plane.HORIZONTAL);

        sidedHeat = new SidedHandler.Noop<>(inventory);
    }

    public float getTemperature()
    {
        return temperature;
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
    public void onCalendarUpdate(long ticks)
    {
        assert level != null;
        // Crucible has no fuel to consume, but it does drop the internal target and temperature over time.
        final boolean wasHot = temperature > 0;
        targetTemperature = HeatCapability.adjustTempTowards(targetTemperature, 0, ticks);
        temperature = HeatCapability.adjustTempTowards(temperature, targetTemperature, ticks);
        if (wasHot && temperature == 0)
        {
            boilingTicks = 0;
            markForSync();
        }
    }

    @Override
    public long getLastUpdateTick()
    {
        return lastUpdateTick;
    }

    @Override
    public void setLastUpdateTick(long tick)
    {
        lastUpdateTick = tick;
    }


    @Override
    public void loadAdditional(CompoundTag nbt)
    {
        lastUpdateTick = nbt.getLong("lastTick");
        temperature = nbt.getFloat("temperature");
        targetTemperature = nbt.getFloat("targetTemperature");
        targetTemperatureStabilityTicks = nbt.getInt("targetTemperatureStabilityTicks");
        boilingTicks = nbt.getInt("boilingTicks");
        needsRecipeUpdate = true;
        super.loadAdditional(nbt);
    }

    @Override
    public void saveAdditional(CompoundTag nbt)
    {
        nbt.putLong("lastTick", lastUpdateTick);
        nbt.putFloat("temperature", temperature);
        nbt.putFloat("targetTemperature", targetTemperature);
        nbt.putInt("targetTemperatureStabilityTicks", targetTemperatureStabilityTicks);
        nbt.putInt("boilingTicks", boilingTicks);
        super.saveAdditional(nbt);
    }

    private void updateCachedRecipe()
    {
        assert level != null;
        if (inventory.excess.isEmpty())
        {
            cachedRecipe = level.getRecipeManager().getRecipeFor(FLRecipeTypes.VAT.get(), inventory, level).orElse(null);
        }
        else
        {
            cachedRecipe = null;
        }
        needsRecipeUpdate = false;
    }

    public void handleCooking()
    {
        assert level != null;
        if (isBoiling())
        {
            assert cachedRecipe != null;
            if (boilingTicks < cachedRecipe.getDuration())
            {
                boilingTicks++;
                if (boilingTicks == 1) markForSync();
            }
            else
            {
                final VatRecipe recipe = cachedRecipe;
                cachedRecipe = null;
                recipe.assembleOutputs(inventory);
                boilingTicks = 0;
                updateCachedRecipe();
                markForSync();
                if (getBlockState().hasProperty(VatBlock.SEALED))
                {
                    level.setBlockAndUpdate(getBlockPos(), getBlockState().setValue(VatBlock.SEALED, false));
                }
            }
        }
        else if (boilingTicks > 0)
        {
            boilingTicks = 0;
            markForSync();
        }
    }

    @NotNull
    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> cap, @Nullable Direction side)
    {
        if (cap == HeatCapability.BLOCK_CAPABILITY)
        {
            return sidedHeat.getSidedHandler(side).cast();
        }
        if (cap == Capabilities.FLUID)
        {
            return sidedFluidInventory.getSidedHandler(side).cast();
        }
        return super.getCapability(cap, side);
    }

    public boolean isBoiling()
    {
        assert level != null;
        if (getBlockState().hasProperty(VatBlock.SEALED) && !getBlockState().getValue(VatBlock.SEALED))
        {
            return false;
        }
        if (level.isClientSide)
        {
            return boilingTicks > 0;
        }
        return cachedRecipe != null && temperature > cachedRecipe.getTemperature();
    }

    public static class VatInventory implements EmptyInventory, DelegateItemHandler, DelegateFluidHandler, INBTSerializable<CompoundTag>, CrucibleLikeHeatBlock, FluidTankCallback
    {
        private final VatBlockEntity vat;
        private final ItemStackHandler inventory;
        private final FluidTank tank;
        private final List<ItemStack> excess;

        public VatInventory(InventoryBlockEntity<VatInventory> entity)
        {
            this.vat = (VatBlockEntity) entity;
            this.inventory = new InventoryItemHandler(entity, 1);
            this.tank = new InventoryFluidTank(CAPACITY, fluid -> Helpers.isFluid(fluid.getFluid(), TFCTags.Fluids.USABLE_IN_POT), vat);
            this.excess = new ArrayList<>();
        }

        public void insertItemWithOverflow(ItemStack stack)
        {
            final ItemStack remainder = inventory.insertItem(0, stack, false);
            if (!remainder.isEmpty())
            {
                excess.add(remainder);
            }
        }

        @NotNull
        @Override
        public ItemStack extractItem(int slot, int amount, boolean simulate)
        {
            return vat.isBoiling() ? ItemStack.EMPTY : inventory.extractItem(slot, amount, simulate);
        }

        @Override
        public IFluidHandler getFluidHandler()
        {
            return tank;
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
            nbt.put("tank", tank.writeToNBT(new CompoundTag()));
            FLHelpers.writeItemStackList(excess, nbt, "excess");
            return nbt;
        }

        @Override
        public void deserializeNBT(CompoundTag nbt)
        {
            inventory.deserializeNBT(nbt.getCompound("inventory"));
            tank.readFromNBT(nbt.getCompound("tank"));
            FLHelpers.readItemStackList(excess, nbt, "excess");
        }

        @Override
        public float getTemperature()
        {
            return vat.temperature;
        }

        @Override
        public void setTargetTemperature(float temp)
        {
            vat.targetTemperature = temp;
        }

        @Override
        public float getTargetTemperature()
        {
            return vat.targetTemperature;
        }

        @Override
        public void resetStability()
        {
            vat.targetTemperatureStabilityTicks = OvenTopBlockEntity.TARGET_TEMPERATURE_STABILITY_TICKS;
            vat.markForSync();
        }

        @Override
        public void setTemperature(float temperature)
        {
            CrucibleLikeHeatBlock.super.setTemperature(temperature);
            vat.temperature = temperature;
        }

    }
}
