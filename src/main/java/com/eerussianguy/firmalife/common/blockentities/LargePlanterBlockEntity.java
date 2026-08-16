package com.eerussianguy.firmalife.common.blockentities;

import com.eerussianguy.firmalife.FirmaLife;
import com.eerussianguy.firmalife.common.blocks.greenhouse.PickerBlock;
import com.eerussianguy.firmalife.common.blocks.greenhouse.LargePlanterBlock;
import com.eerussianguy.firmalife.common.util.Mechanics;
import com.eerussianguy.firmalife.common.util.Plantable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.common.blockentities.CropBlockEntity;
import net.dries007.tfc.common.blockentities.FarmlandBlockEntity;
import net.dries007.tfc.common.blockentities.IFarmland;
import net.dries007.tfc.common.blockentities.TickableInventoryBlockEntity;
import net.dries007.tfc.util.calendar.Calendars;
import net.dries007.tfc.util.calendar.ICalendarTickable;

public class LargePlanterBlockEntity extends TickableInventoryBlockEntity<ItemStackHandler> implements ICalendarTickable, ClimateReceiver, IFarmland
{
    public static void serverTick(Level level, BlockPos pos, BlockState state, LargePlanterBlockEntity planter)
    {
        planter.checkForCalendarUpdate();
        planter.checkForLastTickSync();
    }

    protected static final int LARGE_PLANTER_SLOTS = 1;

    @Nullable
    private Plantable cachedPlant;
    private float growth, yield;

    /** The nutrient content of the planter itself, as {@link IFarmland}. */
    private float nContent, pContent, kContent;
    /** How much of each nutrient the crop growing in that slot has taken up so far, like {@link CropBlockEntity} */
    private final float[] nAbsorbed, pAbsorbed, kAbsorbed;

    private float water;
    private long lastUpdateTick;
    private long lastGrowthTick;
    protected boolean climateValid;
    protected int tier;

    public LargePlanterBlockEntity(BlockPos pos, BlockState state)
    {
        this(FLBlockEntities.LARGE_PLANTER.get(), pos, state, defaultInventory(LARGE_PLANTER_SLOTS), FirmaLife.MOD_ID);
    }

    public LargePlanterBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state, InventoryFactory<ItemStackHandler> inventoryFactory, String modId)
    {
        super(type, pos, state, inventoryFactory, modId);
        cachedPlant = null;
        climateValid = false;
        growth = 0;
        yield = 0;
        water = 0;
        tier = 0;
        nContent = pContent = kContent = 0;
        nAbsorbed = new float[slots()];
        pAbsorbed = new float[slots()];
        kAbsorbed = new float[slots()];
        lastUpdateTick = Integer.MIN_VALUE;
        lastGrowthTick = Calendars.SERVER.getTicks();
    }

    @Override
    public int getSlotStackLimit(int slot)
    {
        return 1;
    }

    @Override
    public boolean isItemValid(int slot, ItemStack stack)
    {
        return Plantable.get(stack) != null;
    }

    @Override
    public void onCalendarUpdate(long ticks)
    {
        assert level != null;
        BlockState state = level.getBlockState(worldPosition);
        if (state.getBlock() instanceof LargePlanterBlock && !level.isClientSide)
        {
            if (Mechanics.growthTick(level, worldPosition, state, this))
            {
                updateBlockState(state);
            }
        }
    }

    public void updateBlockState(BlockState state)
    {
        assert level != null;
        boolean waterLast = state.getValue(LargePlanterBlock.WATERED);
        boolean waterNow = getWater() > 0;
        if (waterNow != waterLast)
        {
            level.setBlockAndUpdate(worldPosition, state.setValue(LargePlanterBlock.WATERED, waterNow));
        }
    }

    public long getLastGrowthTick()
    {
        return lastGrowthTick;
    }

    public void setLastGrowthTick(long lastGrowthTick)
    {
        this.lastGrowthTick = lastGrowthTick;
        markForSync();
    }

    @Override
    public long getLastCalendarUpdateTick()
    {
        return lastUpdateTick;
    }

    @Override
    public void setLastCalendarUpdateTick(long ticks)
    {
        lastUpdateTick = ticks;
    }

    @Override
    public void loadAdditional(CompoundTag nbt, HolderLookup.Provider access)
    {
        super.loadAdditional(nbt, access);
        lastUpdateTick = nbt.getLong("lastUpdateTick");
        lastGrowthTick = nbt.getLong("lastGrowthTick");
        climateValid = nbt.getBoolean("climateValid");
        loadNutrientsWithoutSync(nbt);
        for (int slot = 0; slot < nAbsorbed.length; slot++)
        {
            nAbsorbed[slot] = readAbsorbed(nbt, "nAbsorbed" + slot);
            pAbsorbed[slot] = readAbsorbed(nbt, "pAbsorbed" + slot);
            kAbsorbed[slot] = readAbsorbed(nbt, "kAbsorbed" + slot);
        }
        water = nbt.getFloat("water");
        tier = nbt.getInt("tier");

        loadUnique(nbt);
        updateCache();
    }

    protected void loadUnique(CompoundTag nbt)
    {
        growth = nbt.getFloat("growth");
        yield = nbt.getFloat("yield");
    }

    // todo 26.1: remove
    private static float readAbsorbed(CompoundTag nbt, String key)
    {
        return nbt.contains(key, CompoundTag.TAG_FLOAT) ? nbt.getFloat(key) : 0f;
    }

    @Override
    public void saveAdditional(CompoundTag nbt, HolderLookup.Provider access)
    {
        super.saveAdditional(nbt, access);
        nbt.putLong("lastUpdateTick", lastUpdateTick);
        nbt.putLong("lastGrowthTick", lastGrowthTick);
        nbt.putBoolean("climateValid", climateValid);
        saveNutrients(nbt);
        for (int slot = 0; slot < nAbsorbed.length; slot++)
        {
            nbt.putFloat("nAbsorbed" + slot, nAbsorbed[slot]);
            nbt.putFloat("pAbsorbed" + slot, pAbsorbed[slot]);
            nbt.putFloat("kAbsorbed" + slot, kAbsorbed[slot]);
        }
        nbt.putFloat("water", water);
        nbt.putInt("tier", tier);

        saveUnique(nbt);
    }

    protected void saveUnique(CompoundTag nbt)
    {
        nbt.putFloat("growth", growth);
        nbt.putFloat("yield", yield);
    }

    public void updateCache()
    {
        cachedPlant = Plantable.get(inventory.getStackInSlot(0));
        markForSync();
        if (level != null)
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_CLIENTS);
    }

    public boolean checkValid()
    {
        return getInvalidReason() == null;
    }

    private boolean skylightValid(BlockPos pos)
    {
        assert level != null;
        return level.getBrightness(LightLayer.SKY, pos) >= level.getMaxLightLevel() - 5;
    }

    public boolean isClimateValid()
    {
        return climateValid;
    }

    @Nullable
    public Component getInvalidReason()
    {
        assert level != null;
        final Direction airFind = airFindOffset();
        String complaint = null;
        if (!climateValid)
        {
            complaint = "climate_invalid";
        }
        else if (!skylightValid(worldPosition))
        {
            complaint = "no_sky";
        }
        else if (getWater() <= 0)
        {
            complaint = "dehydrated";
        }
        else
        {
            if (airFind != null)
            {
                BlockState state = level.getBlockState(worldPosition.relative(airFind));
                if (!state.isAir() && !(state.getBlock() instanceof PickerBlock && airFind == Direction.UP))
                {
                    complaint = "air_needed";
                }
            }
        }
        return complaint == null ? null : Component.translatable("firmalife.greenhouse." + complaint);
    }

    @Nullable
    protected Direction airFindOffset()
    {
        return Direction.UP;
    }

    public int getTier()
    {
        return tier;
    }

    public int slots()
    {
        return LARGE_PLANTER_SLOTS;
    }

    public float getGrowth(int slot)
    {
        return growth;
    }

    public float getYield(int slot)
    {
        return yield;
    }

    public void setYield(int slot, float yield)
    {
        this.yield = yield;
    }

    public void setGrowth(int slot, float growth)
    {
        if (growth > 0.99f) growth = 1f;
        this.growth = growth;
        markForSync();
    }

    @Nullable
    public Plantable getPlantable(int slot)
    {
        return cachedPlant;
    }

    @Override
    public float getNutrient(FarmlandBlockEntity.NutrientType type)
    {
        return switch (type)
        {
            case NITROGEN -> nContent;
            case PHOSPHOROUS -> pContent;
            case POTASSIUM -> kContent;
        };
    }

    @Override
    public void setNutrient(FarmlandBlockEntity.NutrientType type, float amount)
    {
        setNutrientWithoutSync(type, amount);
        markForSync();
    }

    @Override
    public void setNutrientWithoutSync(FarmlandBlockEntity.NutrientType type, float amount)
    {
        amount = Mth.clamp(amount, 0f, 1f);
        switch (type)
        {
            case NITROGEN -> nContent = amount;
            case POTASSIUM -> kContent = amount;
            case PHOSPHOROUS -> pContent = amount;
        }
    }

    public float getNAbsorbed(int slot)
    {
        return nAbsorbed[slot];
    }

    public float getPAbsorbed(int slot)
    {
        return pAbsorbed[slot];
    }

    public float getKAbsorbed(int slot)
    {
        return kAbsorbed[slot];
    }

    /**
     * Records nutrients taken up out of the planter by the crop in {@code slot}. This is <strong>not</strong> the same as
     * {@link #setNutrient}, which is the planter's own nutrient content - the amount recorded here is what was removed from it.
     */
    public void addNutrients(int slot, float n, float p, float k)
    {
        nAbsorbed[slot] += n;
        pAbsorbed[slot] += p;
        kAbsorbed[slot] += k;
        markForSync();
    }

    public void resetAbsorbed(int slot)
    {
        nAbsorbed[slot] = pAbsorbed[slot] = kAbsorbed[slot] = 0;
        markForSync();
    }

    @Override
    public float getAdditionalWater()
    {
        return 0;
    }

    @Override
    public void waterTick() { }

    @Override
    public void setAdditionalWater(float amount) { }

    @Override
    public void setAdditionalWaterWithoutSync(float amount) { }

    @Override
    public long getLastWaterTick()
    {
        return 0;
    }

    @Override
    public void setLastWaterTick(long l) { }

    @Override
    public void setAndUpdateSlots(int slot)
    {
        final Plantable previous = slot >= 0 && slot < slots() ? getPlantable(slot) : null;
        updateCache();
        if (slot >= 0 && slot < slots() && previous != getPlantable(slot))
        {
            resetAbsorbed(slot); // A different crop is in the slot now, so it has taken up nothing yet
        }
    }

    @Override
    public boolean addWater(float amount, @Nullable Direction direction)
    {
        assert level != null;
        if (water < 0.99f)
        {
            water = Math.min(water + amount, 1f);
            updateBlockState(level.getBlockState(worldPosition));
            markForSync();
            return true;
        }
        return false;
    }

    public void drainWater(float amount)
    {
        assert level != null;
        water = Math.max(0, water - amount);
        updateBlockState(level.getBlockState(worldPosition));
        markForSync();
    }

    @Override
    public void setValid(Level level, BlockPos pos, boolean valid, int tier, ClimateType climate)
    {
        if (climate == ClimateType.GREENHOUSE)
        {
            this.climateValid = valid;
            this.tier = tier;
        }
        markForSync();
    }

    public float getWater()
    {
        return water;
    }

    public void afterGrowthTickStep(boolean wasGrowing)
    {

    }

    public float resetGrowthTo()
    {
        return 0;
    }
}
