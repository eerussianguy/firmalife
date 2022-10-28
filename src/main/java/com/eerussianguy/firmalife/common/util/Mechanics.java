package com.eerussianguy.firmalife.common.util;

import java.util.*;
import java.util.function.Predicate;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.BoundingBox;

import com.eerussianguy.firmalife.common.FLHelpers;
import com.eerussianguy.firmalife.common.FLTags;
import com.eerussianguy.firmalife.common.blockentities.LargePlanterBlockEntity;
import com.eerussianguy.firmalife.common.blocks.FLBlocks;
import net.dries007.tfc.common.recipes.ingredients.BlockIngredient;
import net.dries007.tfc.common.recipes.ingredients.BlockIngredients;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.calendar.Calendars;
import net.dries007.tfc.util.calendar.ICalendar;
import org.jetbrains.annotations.Nullable;

public final class Mechanics
{
    public static Set<BlockPos> floodfill(Level level, BlockPos startPos, BlockPos.MutableBlockPos mutable, BoundingBox bounds, Predicate<BlockState> wallPredicate, Predicate<BlockState> interiorPredicate, boolean testOrigin, int lastSize, Direction[] checkDirections)
    {
        if (testOrigin && !wallPredicate.test(level.getBlockState(startPos)))
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
                    if (!wallPredicate.test(stateAt)) // proper walls prevent adding new blocks to the floodfill, essentially bounding it
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
                final BoundingBox box = new BoundingBox(pos).inflatedBy(15);
                Set<BlockPos> filled = floodfill(level, pos, mutable, box, CELLAR, s -> !Helpers.isBlock(s, FLBlocks.CLIMATE_STATION.get()), false, lastSize, Helpers.DIRECTIONS);
                return filled.isEmpty() ? null : filled;
            }
        }
        return null;
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
            GreenhouseType greenhouse = GreenhouseType.get(level.getBlockState(mutable));
            if (greenhouse != null)
            {
                final BoundingBox box = new BoundingBox(pos).inflatedBy(15);
                Set<BlockPos> filled = floodfill(level, pos, mutable, box, greenhouse.ingredient, s -> !Helpers.isBlock(s, FLBlocks.CLIMATE_STATION.get()), false, lastSize, FLHelpers.NOT_DOWN);
                if (filled.isEmpty())
                {
                    return null;
                }
                return new GreenhouseInfo(greenhouse, filled);
            }
        }
        return null;
    }

    public record GreenhouseInfo(GreenhouseType type, Set<BlockPos> positions) { }

    private static final BlockIngredient CELLAR = BlockIngredients.of(FLTags.Blocks.CELLAR_INSULATION);
    private static final int UPDATE_INTERVAL = ICalendar.TICKS_IN_DAY;

    public static final float GROWTH_FACTOR = 1f / (16 * ICalendar.TICKS_IN_DAY); // 24 -> 16 days
    public static final float NUTRIENT_CONSUMPTION = 1f / (8 * ICalendar.TICKS_IN_DAY); //  12 -> 8 days
    public static final float WATER_CONSUMPTION = 1f / (12 * ICalendar.TICKS_IN_DAY); // 12 days
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

    public static boolean growthTickStep(Level level, Random random, long fromTick, long toTick, LargePlanterBlockEntity planter)
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
                float nutrientsConsumed = planter.consumeNutrientAndResupplyOthers(plant.getPrimaryNutrient(), NUTRIENT_CONSUMPTION * tickDelta);

                // Total growth is based on the ticks and the nutrients consumed. It is then allocated to actual growth.
                float totalGrowthDelta = Helpers.uniform(random, 0.9f, 1.1f) * tickDelta * GROWTH_FACTOR + nutrientsConsumed * NUTRIENT_GROWTH_FACTOR;
                float growth = planter.getGrowth(slot);

                if (totalGrowthDelta > 0 && growing)
                {
                    // Allocate to growth
                    final float delta = Mth.clamp(totalGrowthDelta, 0, 1);
                    growth += delta;

                    planter.drainWater(tickDelta * WATER_CONSUMPTION);
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
