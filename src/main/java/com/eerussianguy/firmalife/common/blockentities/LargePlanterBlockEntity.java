package com.eerussianguy.firmalife.common.blockentities;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.items.ItemStackHandler;

import com.eerussianguy.firmalife.common.FLHelpers;
import com.eerussianguy.firmalife.common.blocks.greenhouse.LargePlanterBlock;
import com.eerussianguy.firmalife.common.util.Mechanics;
import com.eerussianguy.firmalife.common.util.Plantable;
import net.dries007.tfc.common.blockentities.FarmlandBlockEntity;
import net.dries007.tfc.common.blockentities.TickableInventoryBlockEntity;
import net.dries007.tfc.util.calendar.Calendars;
import net.dries007.tfc.util.calendar.ICalendarTickable;
import org.jetbrains.annotations.Nullable;

public class LargePlanterBlockEntity extends TickableInventoryBlockEntity<ItemStackHandler> implements ICalendarTickable, GreenhouseReceiver
{
    public static void serverTick(Level level, BlockPos pos, BlockState state, LargePlanterBlockEntity planter)
    {
        planter.checkForCalendarUpdate();
    }

    public static final Component NAME = FLHelpers.blockEntityName("large_planter");
    public static final FarmlandBlockEntity.NutrientType[] NUTRIENTS = FarmlandBlockEntity.NutrientType.values();
    private static final int NUM_SLOTS = 1;

    @Nullable
    private Plantable cachedPlant;
    private float growth, water;

    private float nitrogen, phosphorous, potassium;
    private long lastPlayerTick;
    private boolean climateValid;

    public LargePlanterBlockEntity(BlockPos pos, BlockState state)
    {
        this(FLBlockEntities.LARGE_PLANTER.get(), pos, state, defaultInventory(NUM_SLOTS), NAME);
    }

    public LargePlanterBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state, InventoryFactory<ItemStackHandler> inventoryFactory, Component defaultName)
    {
        super(type, pos, state, inventoryFactory, defaultName);
        cachedPlant = null;
        lastPlayerTick = Calendars.SERVER.getTicks();
        climateValid = false;
        growth = 0;
        water = 0;
        nitrogen = phosphorous = potassium = 0;
    }

    @Override
    public void onCalendarUpdate(long ticks)
    {
        assert level != null;
        BlockState state = level.getBlockState(worldPosition);
        if (state.getBlock() instanceof LargePlanterBlock && !level.isClientSide && cachedPlant != null)
        {
            if (Mechanics.growthTick(level, worldPosition, state, this))
            {
                postGrowthTick(state);
            }
        }
    }

    protected void postGrowthTick(BlockState state)
    {
        assert level != null;
        boolean waterLast = state.getValue(LargePlanterBlock.WATERED);
        boolean waterNow = water > 0;
        if (waterNow != waterLast)
        {
            level.setBlockAndUpdate(worldPosition, state.setValue(LargePlanterBlock.WATERED, waterNow));
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
        super.loadAdditional(nbt);
        lastPlayerTick = nbt.getLong("lastPlayerTick");
        climateValid = nbt.getBoolean("climateValid");
        nitrogen = nbt.getFloat("n");
        phosphorous = nbt.getFloat("p");
        potassium = nbt.getFloat("k");
        water = nbt.getFloat("water");

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
        nbt.putLong("lastPlayerTick", lastPlayerTick);
        nbt.putBoolean("climateValid", climateValid);
        nbt.putFloat("n", nitrogen);
        nbt.putFloat("p", phosphorous);
        nbt.putFloat("k", potassium);
        nbt.putFloat("water", water);

        saveUnique(nbt);
    }

    protected void saveUnique(CompoundTag nbt)
    {
        nbt.putFloat("growth", growth);
    }

    protected void updateCache()
    {
        cachedPlant = Plantable.get(inventory.getStackInSlot(0));
    }

    public boolean checkValid()
    {
        assert level != null;
        return climateValid && level.getBrightness(LightLayer.SKY, worldPosition.above()) >= level.getMaxLightLevel() - 5;
    }

    public int slots()
    {
        return NUM_SLOTS;
    }

    public float getGrowth(int slot)
    {
        return growth;
    }

    public void setGrowth(int slot, float growth)
    {
        this.growth = growth;
    }

    @Nullable
    public Plantable getPlantable(int slot)
    {
        return cachedPlant;
    }

    public float getNutrient(FarmlandBlockEntity.NutrientType type)
    {
        return switch (type)
        {
            case NITROGEN -> nitrogen;
            case PHOSPHOROUS -> phosphorous;
            case POTASSIUM -> potassium;
        };
    }

    public void setNutrient(FarmlandBlockEntity.NutrientType type, float amount)
    {
        switch (type)
        {
            case NITROGEN -> nitrogen = amount;
            case POTASSIUM -> potassium = amount;
            case PHOSPHOROUS -> phosphorous = amount;
        }
    }

    public void addNutrient(FarmlandBlockEntity.NutrientType type, float amount)
    {
        setNutrient(type, getNutrient(type) + amount);
    }

    @Override
    public void addWater(float amount)
    {
        water = Math.min(water + amount, 1);
    }

    @Override
    public void setValid(boolean valid)
    {
        this.climateValid = valid;
    }

    public float getWater()
    {
        return water;
    }

    /**
     * Consume up to {@code amount} of nutrient {@code type}.
     * Additionally, increase all other nutrients by 1/6 the consumed value (effectively, recovering 33% of the consumed nutrients)
     * @return The amount of nutrient {@code type} that was actually consumed.
     */
    public float consumeNutrientAndResupplyOthers(FarmlandBlockEntity.NutrientType type, float amount)
    {
        final float startValue = getNutrient(type);
        final float consumed = Math.min(startValue, amount);

        setNutrient(type, startValue - consumed);
        for (FarmlandBlockEntity.NutrientType other : NUTRIENTS)
        {
            if (other != type)
            {
                addNutrient(other, consumed * (1 / 6f));
            }
        }
        return consumed;
    }
}
