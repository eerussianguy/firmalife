package com.eerussianguy.firmalife.common.util;

import java.util.*;
import java.util.function.Predicate;
import java.util.function.Supplier;

import com.eerussianguy.firmalife.common.blockentities.ClimateStationBlockEntity;
import com.eerussianguy.firmalife.config.FLConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.BoundingBox;

import com.eerussianguy.firmalife.common.FLTags;
import com.eerussianguy.firmalife.common.blockentities.LargePlanterBlockEntity;
import com.eerussianguy.firmalife.common.blocks.FLBlocks;
import net.dries007.tfc.common.recipes.ingredients.BlockIngredient;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.calendar.Calendars;
import net.dries007.tfc.util.calendar.ICalendar;
import org.jetbrains.annotations.Nullable;
import vazkii.patchouli.api.TriPredicate;

public final class Mechanics
{
    public static Set<BlockPos> floodfill(Level level, BlockPos startPos, BlockPos.MutableBlockPos mutable, BoundingBox bounds, TriPredicate<BlockState, BlockPos, Direction> wallPredicate, Predicate<BlockState> interiorPredicate, boolean testOrigin, int lastSize, Direction[] checkDirections)
    {
        if (testOrigin && !wallPredicate.test(level.getBlockState(startPos), startPos, Direction.UP))
        {
            return Collections.emptySet();
        }

        final int maxSize = bounds.getXSpan() * bounds.getYSpan() * bounds.getZSpan();
        final Set<BlockPos> filled = lastSize == -1 ? new HashSet<>() : new HashSet<>(lastSize);
        final LinkedList<BlockPos> queue = new LinkedList<>();
        filled.add(startPos);
        queue.addFirst(startPos);

        while (!queue.isEmpty())
        {
            if (filled.size() > maxSize)
            {
                return Collections.emptySet(); // this means the floodfill failed to contain itself
            }
            BlockPos testPos = queue.removeFirst();
            for (Direction direction : checkDirections)
            {
                mutable.set(testPos).move(direction);
                if (!filled.contains(mutable))
                {
                    final BlockState stateAt = level.getBlockState(mutable);
                    if (!wallPredicate.test(stateAt, mutable, direction)) // proper walls prevent adding new blocks to the floodfill, essentially bounding it
                    {
                        if (interiorPredicate.test(stateAt)) // only add positions that match our 'inside' predicate.
                        {
                            if (!bounds.isInside(mutable))
                            {
                                return Collections.emptySet(); // we are way outside the realm of possibility...
                            }
                            else
                            {
                                // Valid flood fill location
                                BlockPos posNext = mutable.immutable();
                                queue.addFirst(posNext);
                                filled.add(posNext);
                            }
                        }
                        else
                        {
                            return Collections.emptySet(); // we ran into a block that can't be inside or outside
                        }
                    }

                }
            }
        }
        return filled;
    }

    @Nullable
    public static Set<BlockPos> getCellar(Level level, BlockPos pos, BlockState state)
    {
        return getCellar(level, pos, state, -1);
    }

    @Nullable
    public static Set<BlockPos> getCellar(Level level, BlockPos pos, BlockState state, int lastSize)
    {
        final BlockPos.MutableBlockPos mutable = new BlockPos.MutableBlockPos();
        for (Direction d : Helpers.DIRECTIONS)
        {
            mutable.setWithOffset(pos, d);
            if (CELLAR.test(level.getBlockState(mutable)))
            {
                return tryFindCellarInfo(level, pos, lastSize, mutable);
            }
        }
        if (level.getBlockEntity(pos) instanceof ClimateStationBlockEntity station && station.favoriteIsCellar())
        {
            return tryFindCellarInfo(level, pos, lastSize, mutable);
        }
        return null;
    }

    @Nullable
    private static Set<BlockPos> tryFindCellarInfo(Level level, BlockPos pos, int lastSize, BlockPos.MutableBlockPos mutable)
    {
        final BoundingBox box = new BoundingBox(pos).inflatedBy(15);
        final Set<BlockPos> filled = floodfill(level, pos, mutable, box, (s, p, dir) -> CELLAR.test(s), s -> !Helpers.isBlock(s, FLBlocks.CLIMATE_STATION.get()), false, lastSize, Helpers.DIRECTIONS);
        return filled.isEmpty() ? null : filled;
    }

    @Nullable
    public static GreenhouseInfo getGreenhouse(Level level, BlockPos pos, BlockState state)
    {
        return getGreenhouse(level, pos, state, -1);
    }

    @Nullable
    public static GreenhouseInfo getGreenhouse(Level level, BlockPos pos, BlockState state, int lastSize)
    {
        final BlockPos.MutableBlockPos mutable = new BlockPos.MutableBlockPos();
        for (Direction d : Helpers.DIRECTIONS)
        {
            mutable.setWithOffset(pos, d);
            final GreenhouseType greenhouse = GreenhouseType.get(level.getBlockState(mutable));
            if (greenhouse != null)
            {
                return tryFindGreenhouseInfo(level, pos, lastSize, mutable, greenhouse);
            }
        }
        if (level.getBlockEntity(pos) instanceof ClimateStationBlockEntity station && station.getFavoriteType() != null)
        {
            return tryFindGreenhouseInfo(level, pos, lastSize, mutable, station.getFavoriteType());
        }
        return null;
    }

    @Nullable
    private static GreenhouseInfo tryFindGreenhouseInfo(Level level, BlockPos pos, int lastSize, BlockPos.MutableBlockPos mutable, GreenhouseType greenhouse)
    {
        final BoundingBox box = new BoundingBox(pos).inflatedBy(15);
        final TriPredicate<BlockState, BlockPos, Direction> predicate = (wallState, wallPos, direction) -> {
            if (direction == Direction.DOWN)
                return !wallState.isAir();
            if (!greenhouse.ingredient.test(wallState))
                return false;
            if (direction == Direction.UP && wallState.getBlock() instanceof SlabBlock)
                return true;
            if (Helpers.isBlock(wallState, FLTags.Blocks.ALWAYS_VALID_GREENHOUSE_WALL))
                return true; // short circuit for stuff we know will pass (plus exempt doors)
            return wallState.isFaceSturdy(level, wallPos, direction.getOpposite());
        };
        Set<BlockPos> filled = floodfill(level, pos, mutable, box, predicate, s -> !Helpers.isBlock(s, FLBlocks.CLIMATE_STATION.get()), false, lastSize, Helpers.DIRECTIONS);
        if (filled.isEmpty())
        {
            return null;
        }
        if (level.getBlockEntity(pos) instanceof ClimateStationBlockEntity station)
        {
            station.setFavorite(greenhouse);
        }
        return new GreenhouseInfo(greenhouse, filled);
    }

    public record GreenhouseInfo(GreenhouseType type, Set<BlockPos> positions) { }

    public static final Predicate<BlockState> CELLAR = state -> Helpers.isBlock(state, FLTags.Blocks.CELLAR_INSULATION);
    private static final int UPDATE_INTERVAL = ICalendar.TICKS_IN_DAY;

    public static final Supplier<Float> GROWTH_FACTOR = () -> 1f / (FLConfig.SERVER.greenhouseGrowthDays.get().floatValue() * ICalendar.TICKS_IN_DAY); // same as tfc
    public static final Supplier<Float> NUTRIENT_CONSUMPTION = () -> 1f / (FLConfig.SERVER.greenhouseNutrientDays.get().floatValue() * ICalendar.TICKS_IN_DAY); //  12 -> 8 days
    public static final Supplier<Float> WATER_CONSUMPTION = () -> 1f / (FLConfig.SERVER.greenhouseWaterDays.get().floatValue() * ICalendar.TICKS_IN_DAY); // 12 days
    public static final float NUTRIENT_GROWTH_FACTOR = 0.5f;

    public static boolean growthTick(Level level, BlockPos pos, BlockState state, LargePlanterBlockEntity planter)
    {
        final long firstTick = planter.getLastGrowthTick(), thisTick = Calendars.SERVER.getTicks();
        long tick = firstTick + UPDATE_INTERVAL, lastTick = firstTick;
        for (; tick < thisTick; tick += UPDATE_INTERVAL)
        {
            if (!growthTickStep(level, level.getRandom(), lastTick, tick, planter))
            {
                return false;
            }
            lastTick = tick;
        }
        return lastTick >= thisTick || growthTickStep(level, level.getRandom(), lastTick, thisTick, planter);
    }

    public static boolean growthTickStep(Level level, RandomSource random, long fromTick, long toTick, LargePlanterBlockEntity planter)
    {
        // Calculate invariants
        final ICalendar calendar = Calendars.get(level);
        final long tickDelta = toTick - fromTick;
        final boolean growing = planter.checkValid();

        for (int slot = 0; slot < planter.slots(); slot++)
        {
            Plantable plant = planter.getPlantable(slot);
            if (plant != null)
            {
                // Nutrients are consumed first, since they are independent of growth or health.
                // As long as the crop exists it consumes nutrients.
                float nutrientsConsumed = planter.consumeNutrientAndResupplyOthers(plant.getPrimaryNutrient(), NUTRIENT_CONSUMPTION.get() * tickDelta);

                // Total growth is based on the ticks and the nutrients consumed. It is then allocated to actual growth.
                float totalGrowthDelta = Helpers.uniform(random, 0.9f, 1.1f) * tickDelta * GROWTH_FACTOR.get() + nutrientsConsumed * NUTRIENT_GROWTH_FACTOR;
                float growth = planter.getGrowth(slot);

                if (totalGrowthDelta > 0 && growing)
                {
                    // Allocate to growth
                    final float delta = Mth.clamp(totalGrowthDelta, 0, 1);
                    growth += delta;

                    planter.drainWater(tickDelta * WATER_CONSUMPTION.get());
                }

                planter.setGrowth(slot, Mth.clamp(growth, 0f, 1f));
            }
            else
            {
                planter.setGrowth(slot, 0);
            }
        }
        planter.setLastGrowthTick(calendar.getTicks());
        planter.afterGrowthTickStep(growing);
        return true;
    }

}
