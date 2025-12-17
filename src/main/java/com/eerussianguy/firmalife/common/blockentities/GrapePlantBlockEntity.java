package com.eerussianguy.firmalife.common.blockentities;

import java.util.ArrayList;
import java.util.List;
import com.eerussianguy.firmalife.common.blocks.plant.GrapeGroundPlantOnStringBlock;
import com.eerussianguy.firmalife.common.blocks.plant.IGrape;
import com.eerussianguy.firmalife.common.items.FLFoodTraits;
import com.eerussianguy.firmalife.common.util.FLClimateRanges;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.registries.DeferredHolder;

import net.dries007.tfc.client.overworld.SolarCalculator;
import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.blockentities.TickableBlockEntity;
import net.dries007.tfc.common.blocks.plant.fruit.Lifecycle;
import net.dries007.tfc.common.component.food.FoodTrait;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.calendar.Calendars;
import net.dries007.tfc.util.calendar.ICalendar;
import net.dries007.tfc.util.calendar.ICalendarTickable;
import net.dries007.tfc.util.calendar.Month;
import net.dries007.tfc.util.climate.Climate;
import net.dries007.tfc.util.climate.ClimateRange;

public class GrapePlantBlockEntity extends TickableBlockEntity implements ICalendarTickable
{
    public static void serverTick(Level level, BlockPos pos, BlockState state, GrapePlantBlockEntity plant)
    {
        plant.checkForCalendarUpdate();
        plant.checkForLastTickSync();
    }

    public static final int UPDATE_INTERVAL = ICalendar.CALENDAR_TICKS_IN_DAY;

    private long lastUpdateTick; // The last tick this crop was ticked via the block entity's tick() method. A delta of > 1 is used to detect time skips
    private long lastGrowthTick; // The last tick the crop block was ticked via ICropBlock#growthTick()
    private float growth = 0f;
    private int[] soilData = new int[] {0, 0, 0, 0, 0}; // grass, gravel, dirt, 0, -1

    private boolean hasBees = false;

    public GrapePlantBlockEntity(BlockPos pos, BlockState state)
    {
        super(FLBlockEntities.GRAPE_PLANT.get(), pos, state);
        lastGrowthTick = Calendars.SERVER.getTicks();
        lastUpdateTick = Integer.MIN_VALUE;
    }

    @Override
    public void onCalendarUpdate(long ticks)
    {
        assert level != null;
        tryUpdate();
    }

    public void tryUpdate()
    {
        final long now = Calendars.SERVER.getTicks();
        //handle update interval
        if (now > (lastGrowthTick + UPDATE_INTERVAL))
        {
            while (lastGrowthTick < now)
            {
                updateTick();
                lastGrowthTick += UPDATE_INTERVAL;
            }
            recordOwnData();
            markForSync();
        }
    }

    public void updateTick()
    {
        assert level != null;

        Lifecycle lifecycle = Lifecycle.DORMANT;
        if (FLClimateRanges.GRAPES.get().checkTemperature(Climate.getTemperature(level, worldPosition), false) == ClimateRange.Result.VALID)
        {
            growth += 0.125f + (level.random.nextFloat() * 0.05f);
            if (growth >= 1f)
            {
                advanceAt(worldPosition);
                advanceAt(worldPosition.above());
                growth = 0f;
                markForSync();
            }
            lifecycle = Lifecycle.HEALTHY;
            final Month month = Calendars.get(level).getHemispheralCalendarMonthOfYear(SolarCalculator.getInNorthernHemisphere(worldPosition, level));
            if (month == Month.JUNE)
            {
                lifecycle = Lifecycle.FLOWERING;
            }
            else if (month == Month.JULY)
            {
                lifecycle = Lifecycle.FRUITING;
            }
        }
        lifecycleAt(worldPosition, lifecycle);
        lifecycleAt(worldPosition.above(), lifecycle);
    }

    private void advanceAt(BlockPos pos)
    {
        assert level != null;
        final BlockState state = level.getBlockState(pos);
        if (state.getBlock() instanceof IGrape grape)
        {
            grape.advance(level, pos, state);
        }
    }

    private void lifecycleAt(BlockPos pos, Lifecycle lifecycle)
    {
        assert level != null;
        final BlockState state = level.getBlockState(pos);
        if (state.getBlock() instanceof IGrape grape)
        {
            grape.advanceLifecycle(level, pos, state, lifecycle);
        }
    }

    public BlockPos getBrainPos()
    {
        assert level != null;
        BlockState state = level.getBlockState(worldPosition);
        final Direction dir = getStringDirection(state);
        final BlockPos.MutableBlockPos cursor = new BlockPos.MutableBlockPos();
        cursor.set(worldPosition);
        while (state.getBlock() instanceof GrapeGroundPlantOnStringBlock)
        {
            cursor.move(dir, 2);
            state = level.getBlockState(cursor);
        }
        cursor.move(dir, -2);
        return cursor.immutable();
    }

    private static Direction getStringDirection(BlockState state)
    {
        return state.getValue(GrapeGroundPlantOnStringBlock.AXIS) == Direction.Axis.X ? Direction.EAST : Direction.SOUTH;
    }

    public List<DeferredHolder<FoodTrait, FoodTrait>> scanAndReport()
    {
        assert level != null;

        final BlockPos brainPos = getBrainPos();
        if (!worldPosition.equals(brainPos)) // if we are not at the brain
        {
            if (level.getBlockEntity(brainPos) instanceof GrapePlantBlockEntity plant)
            {
                return plant.scanAndReport(); // ask the brain to report
            }
        }

        int dirtCount = soilData[0];
        int grassCount = soilData[1];
        int gravelCount = soilData[2];
        int countAtMinus1 = soilData[3];
        int countAtZero = soilData[4];
        boolean anyBees = hasBees;

        final Direction dir = getStringDirection(level.getBlockState(worldPosition)).getOpposite();
        final BlockPos.MutableBlockPos cursor = new BlockPos.MutableBlockPos();
        cursor.set(worldPosition);
        while (true)
        {
            cursor.move(dir, 2);
            if (level.getBlockEntity(cursor) instanceof GrapePlantBlockEntity plant)
            {
                dirtCount += plant.soilData[0];
                grassCount += plant.soilData[1];
                gravelCount += plant.soilData[2];
                countAtMinus1 += plant.soilData[3];
                countAtZero += plant.soilData[4];
                anyBees |= plant.hasBees;
            }
            else
            {
                break;
            }
        }

        final List<DeferredHolder<FoodTrait, FoodTrait>> traits = new ArrayList<>();
        if (anyBees)
        {
            traits.add(FLFoodTraits.BEE_POLLINATED);
        }
        if (dirtCount > gravelCount && dirtCount > grassCount)
        {
            traits.add(FLFoodTraits.DIRT_GROWN);
        }
        if (gravelCount > dirtCount && gravelCount > grassCount)
        {
            traits.add(FLFoodTraits.GRAVEL_GROWN);
        }
        // a little bit about the math
        // each level has 49 blocks
        // we can say there's probably a slope if there's a bit less blocks than 49 at the level below the plant
        // and if there's at least some blocks that *are* at the level of the plant
        if (countAtMinus1 < 33 && countAtZero > 20)
        {
            traits.add(FLFoodTraits.SLOPE_GROWN);
        }
        return traits;
    }

    private void recordOwnData()
    {
        assert level != null;
        final BlockPos.MutableBlockPos cursor = new BlockPos.MutableBlockPos();
        final float temp = Climate.getTemperature(level, worldPosition);

        int dirtCount = 0;
        int grassCount = 0;
        int gravelCount = 0;
        int countAtMinus1 = 0;
        int countAtZero = 0;

        boolean bees = false;

        for (int x = -3; x <= 3; x++)
        {
            for (int z = -3; z <= 3; z++)
            {
                for (int y = -1; y <= 0; y++)
                {
                    cursor.setWithOffset(worldPosition, x, y, z);
                    if (level.isLoaded(cursor))
                    {
                        final BlockState state = level.getBlockState(cursor);
                        if (!state.isAir())
                        {
                            if (y == -1)
                                countAtMinus1 += 1;
                            else
                                countAtZero += 1;
                        }
                        if (Helpers.isBlock(state, TFCTags.Blocks.GRASS))
                        {
                            grassCount += 1;
                        }
                        else if (Helpers.isBlock(state, BlockTags.DIRT))
                        {
                            dirtCount += 1;
                        }
                        else if (Helpers.isBlock(state, Tags.Blocks.GRAVELS))
                        {
                            gravelCount += 1;
                        }
                        else if (!bees && level.getBlockEntity(cursor) instanceof FLBeehiveBlockEntity hive && hive.getBee().hasQueen())
                        {
                            bees = true;
                        }
                    }

                }
            }
        }

        hasBees = bees;
        this.soilData = new int[] {dirtCount, grassCount, gravelCount, countAtMinus1, countAtZero};
    }

    public boolean isBrain()
    {
        return getBrainPos().equals(worldPosition);
    }

    public int[] debugViewOfSoilData()
    {
        return soilData;
    }

    public boolean debugHasBees()
    {
        return hasBees;
    }

    public float getGrowth()
    {
        return growth;
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
    @Deprecated
    public long getLastCalendarUpdateTick()
    {
        return lastUpdateTick;
    }

    @Override
    @Deprecated
    public void setLastCalendarUpdateTick(long tick)
    {
        lastUpdateTick = tick;
    }

    @Override
    public void loadAdditional(CompoundTag nbt, HolderLookup.Provider access)
    {
        lastUpdateTick = nbt.getLong("tick");
        lastGrowthTick = nbt.getLong("lastGrowthTick");
        growth = nbt.getFloat("growth");
        hasBees = nbt.getBoolean("hasBees");
        soilData = nbt.getIntArray("soilData");
        super.loadAdditional(nbt, access);
    }

    @Override
    public void saveAdditional(CompoundTag nbt, HolderLookup.Provider access)
    {
        nbt.putLong("tick", lastUpdateTick);
        nbt.putLong("lastGrowthTick", lastGrowthTick);
        nbt.putFloat("growth", growth);
        nbt.putBoolean("hasBees", hasBees);
        nbt.putIntArray("soilData", soilData);
        super.saveAdditional(nbt, access);
    }
}
