package com.eerussianguy.firmalife.common.blockentities;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.items.ItemStackHandler;

import com.eerussianguy.firmalife.common.FLHelpers;
import com.eerussianguy.firmalife.common.blocks.greenhouse.LargePlanterBlock;
import com.eerussianguy.firmalife.common.util.Mechanics;
import com.eerussianguy.firmalife.common.util.Plantable;
import net.dries007.tfc.common.blockentities.FarmlandBlockEntity;
import net.dries007.tfc.common.blockentities.IFarmland;
import net.dries007.tfc.common.blockentities.TickableInventoryBlockEntity;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.calendar.Calendars;
import net.dries007.tfc.util.calendar.ICalendarTickable;
import org.jetbrains.annotations.Nullable;

public class LargePlanterBlockEntity extends TickableInventoryBlockEntity<ItemStackHandler> implements ICalendarTickable, ClimateReceiver, IFarmland
{
    public static void serverTick(Level level, BlockPos pos, BlockState state, LargePlanterBlockEntity planter)
    {
        planter.checkForCalendarUpdate();
        planter.checkForLastTickSync();
    }

    public static final Component NAME = FLHelpers.blockEntityName("large_planter");
    protected static final int LARGE_PLANTER_SLOTS = 1;

    @Nullable
    private Plantable cachedPlant;
    private float growth;

    private float nitrogen, phosphorous, potassium, water;
    private long lastUpdateTick;
    private long lastGrowthTick;
    private boolean climateValid;
    private int tier;

    public LargePlanterBlockEntity(BlockPos pos, BlockState state)
    {
        this(FLBlockEntities.LARGE_PLANTER.get(), pos, state, defaultInventory(LARGE_PLANTER_SLOTS), NAME);
    }

    public LargePlanterBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state, InventoryFactory<ItemStackHandler> inventoryFactory, Component defaultName)
    {
        super(type, pos, state, inventoryFactory, defaultName);
        cachedPlant = null;
        climateValid = false;
        growth = 0;
        water = 0;
        tier = 0;
        nitrogen = phosphorous = potassium = 0;
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
    public void loadAdditional(CompoundTag nbt)
    {
        super.loadAdditional(nbt);
        lastUpdateTick = nbt.getLong("lastUpdateTick");
        lastGrowthTick = nbt.getLong("lastGrowthTick");
        climateValid = nbt.getBoolean("climateValid");
        nitrogen = nbt.getFloat("n");
        phosphorous = nbt.getFloat("p");
        potassium = nbt.getFloat("k");
        water = nbt.getFloat("water");
        tier = nbt.getInt("tier");

        loadUnique(nbt);
        updateCache();
    }

    protected void loadUnique(CompoundTag nbt)
    {
        growth = nbt.getFloat("growth");
    }

    @Override
    public void saveAdditional(CompoundTag nbt)
    {
        super.saveAdditional(nbt);
        nbt.putLong("lastUpdateTick", lastUpdateTick);
        nbt.putLong("lastGrowthTick", lastGrowthTick);
        nbt.putBoolean("climateValid", climateValid);
        nbt.putFloat("n", nitrogen);
        nbt.putFloat("p", phosphorous);
        nbt.putFloat("k", potassium);
        nbt.putFloat("water", water);
        nbt.putInt("tier", tier);

        saveUnique(nbt);
    }

    protected void saveUnique(CompoundTag nbt)
    {
        nbt.putFloat("growth", growth);
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
        else if (airFind != null && !level.getBlockState(worldPosition.relative(airFind)).isAir())
        {
            complaint = "air_needed";
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
            case NITROGEN -> nitrogen;
            case PHOSPHOROUS -> phosphorous;
            case POTASSIUM -> potassium;
        };
    }

    @Override
    public void setNutrient(FarmlandBlockEntity.NutrientType type, float amount)
    {
        amount = Mth.clamp(amount, 0f, 1f);
        switch (type)
        {
            case NITROGEN -> nitrogen = amount;
            case POTASSIUM -> potassium = amount;
            case PHOSPHOROUS -> phosphorous = amount;
        }
        markForSync();
    }

    @Override
    public void setAndUpdateSlots(int slot)
    {
        updateCache();
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
    public void setValid(Level level, BlockPos pos, boolean valid, int tier, boolean cellar)
    {
        if (!cellar)
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
}
