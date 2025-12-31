package com.eerussianguy.firmalife.common.util;

import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;
import java.util.function.Predicate;
import java.util.function.Supplier;
import com.eerussianguy.firmalife.common.FLTags;
import com.eerussianguy.firmalife.common.blockentities.ClimateStationBlockEntity;
import com.eerussianguy.firmalife.common.blockentities.LargePlanterBlockEntity;
import com.eerussianguy.firmalife.common.blocks.FLBlocks;
import com.eerussianguy.firmalife.common.blocks.greenhouse.GreenhousePanelWallBlock;
import com.eerussianguy.firmalife.config.FLConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import org.jetbrains.annotations.Nullable;
import vazkii.patchouli.api.TriPredicate;

import net.dries007.tfc.common.blockentities.FarmlandBlockEntity;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.calendar.Calendars;
import net.dries007.tfc.util.calendar.ICalendar;

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
        GreenhousePanelWallBlock.WallSegmentLocator panelSegmentLocator = new GreenhousePanelWallBlock.WallSegmentLocator();
        final TriPredicate<BlockState, BlockPos, Direction> predicate = (wallState, wallPos, direction) -> {
            if (Helpers.isBlock(wallState, FLTags.Blocks.ALWAYS_VALID_GREENHOUSE_WALL))
                return true; // short circuit for stuff we know will pass (plus exempt doors)
            if (direction == Direction.DOWN)
                return !wallState.isAir();
            if (!greenhouse.ingredient().test(wallState))
                return false;
            if (direction == Direction.UP && wallState.getBlock() instanceof SlabBlock)
                return true;
            if (wallState.getBlock() instanceof GreenhousePanelWallBlock)
            {
                Direction panelFacing = wallState.getValue(GreenhousePanelWallBlock.FACING);
                if (direction == panelFacing.getOpposite())
                {
                    return true;
                }
                var wallSegment = panelSegmentLocator.scan(level, wallPos, panelFacing);
                var left = wallSegment.getFirst().getSecond();
                var right = wallSegment.getSecond().getSecond();
                boolean leftMatches;
                boolean rightMatches;
                if (left.getBlock() instanceof GreenhousePanelWallBlock)
                {
                    Set<Direction> leftWalls = GreenhousePanelWallBlock.getWallStates2(left);
                    leftMatches = leftWalls.contains(panelFacing) && leftWalls.size() == 2;
                }
                else
                {
                    //TODO this may require a more robust check to determine valid blocks at the end of wall segments
                    // Could be any kind of block (doors, trapdoors, roof panels, etc.)
                    leftMatches = Helpers.isBlock(left, FLTags.Blocks.ALWAYS_VALID_GREENHOUSE_WALL) || Helpers.isBlock(left, FLTags.Blocks.GREENHOUSE_PANEL_ROOFS);
                }
                if (right.getBlock() instanceof GreenhousePanelWallBlock)
                {
                    Set<Direction> rightWalls = GreenhousePanelWallBlock.getWallStates2(right);
                    rightMatches = rightWalls.contains(panelFacing) && rightWalls.size() == 2;
                }
                else
                {
                    //TODO same as above
                    rightMatches = Helpers.isBlock(right, FLTags.Blocks.ALWAYS_VALID_GREENHOUSE_WALL) || Helpers.isBlock(right, FLTags.Blocks.GREENHOUSE_PANEL_ROOFS);
                }
                return leftMatches && rightMatches;
            }
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

    public record GreenhouseInfo(GreenhouseType type, Set<BlockPos> positions) {}

    public static final Predicate<BlockState> CELLAR = state -> Helpers.isBlock(state, FLTags.Blocks.CELLAR_INSULATION);
    private static final int UPDATE_INTERVAL = ICalendar.CALENDAR_TICKS_IN_DAY;

    public static final Supplier<Float> NUTRIENT_CONSUMPTION = () -> 1f / (FLConfig.SERVER.greenhouseNutrientDays.get().floatValue() * ICalendar.CALENDAR_TICKS_IN_DAY); //  12 -> 8 days
    public static final Supplier<Float> WATER_CONSUMPTION = () -> 1f / (FLConfig.SERVER.greenhouseWaterDays.get().floatValue() * ICalendar.CALENDAR_TICKS_IN_DAY); // 14 days
    public static final float GROWTH_FACTOR = 1f / (16 * ICalendar.CALENDAR_TICKS_IN_DAY);
    public static final float NUTRIENT_GROWTH_FACTOR = 0.5f;
    public static final float YIELD_MIN = 0.2f;
    public static final float YIELD_LIMIT = 1f;

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
                // Nutrients are consumed first, since they are independent of growth or health.
                // As long as the crop exists it consumes nutrients.

                // Nutrients required for 100% yield multiplier
                // Negative values are nutrients restored to soil
                final float nForGrowth = plant.nutrient().nitrogen();
                final float pForGrowth = plant.nutrient().phosphorous();
                final float kForGrowth = plant.nutrient().potassium();

                final float posNForGrowth = Math.max(0, nForGrowth);
                final float posPForGrowth = Math.max(0, pForGrowth);
                final float posKForGrowth = Math.max(0, kForGrowth);

                float nutrientsForGrowth = posNForGrowth + posPForGrowth + posKForGrowth;

                // Required nutrients for this growth tick
                final float nRequired = NUTRIENT_CONSUMPTION.get() * tickDelta * nForGrowth;
                final float pRequired = NUTRIENT_CONSUMPTION.get() * tickDelta * pForGrowth;
                final float kRequired = NUTRIENT_CONSUMPTION.get() * tickDelta * kForGrowth;

                final float nutrientsRequired = Math.max(0, nRequired) + Math.max(0, pRequired) + Math.max(0, kRequired);

                // Consumed nutrients for this growth tick
                float nutrientsConsumed = 0;
                float nutrientsAvailable = 0;

                // How many nutrients were absorbed relative to the crop's capacity
                if (nutrientsForGrowth > 0)
                {
                    // Sum of all nutrients available for growth
                    nutrientsAvailable = (
                        Math.min(posNForGrowth, planter.getNutrient(FarmlandBlockEntity.NutrientType.NITROGEN))
                            + Math.min(posPForGrowth, planter.getNutrient(FarmlandBlockEntity.NutrientType.PHOSPHOROUS))
                            + Math.min(posKForGrowth, planter.getNutrient(FarmlandBlockEntity.NutrientType.POTASSIUM))
                    );

                    // Won't consume a nutrient beyond the amount required by the crop
                    final float maxNToConsume = nForGrowth - planter.getNAbsorbed();
                    final float maxPToConsume = pForGrowth - planter.getPAbsorbed();
                    final float maxKToConsume = kForGrowth - planter.getKAbsorbed();

                    final float nConsumed = planter.consumeNutrients(Math.min(nRequired, maxNToConsume), FarmlandBlockEntity.NutrientType.NITROGEN);
                    final float pConsumed = planter.consumeNutrients(Math.min(pRequired, maxPToConsume), FarmlandBlockEntity.NutrientType.PHOSPHOROUS);
                    final float kConsumed = planter.consumeNutrients(Math.min(kRequired, maxKToConsume), FarmlandBlockEntity.NutrientType.POTASSIUM);

                    // Adds new nutrients back to the crop
                    planter.addNutrients(nConsumed, pConsumed, kConsumed);

                    nutrientsConsumed += nConsumed + pConsumed + kConsumed;
                }
                else
                {
                    // Avoids division by zero
                    nutrientsForGrowth = 1f;
                }

                final float growthModifier = FLConfig.SERVER.greenhouseGrowthModifier.get().floatValue(); // Higher = Slower growth

                // Total growth is based on the ticks and the nutrients consumed. It is then allocated to actual growth or expiry based on other factors.
                final float totalGrowthDelta = (1f / growthModifier) * Helpers.uniform(random, 0.9f, 1.1f) * tickDelta * GROWTH_FACTOR + nutrientsConsumed / nutrientsForGrowth * NUTRIENT_GROWTH_FACTOR;
                final float initialGrowth = planter.getGrowth(slot);
                float growth = initialGrowth, actualYield = planter.getYield(slot);

                final float growthLimit = 1f;
                if (totalGrowthDelta > 0 && growing && growth < growthLimit)
                {
                    // Allocate to growth
                    final float delta = Math.min(totalGrowthDelta, growthLimit - growth);
                    planter.drainWater(Helpers.uniform(random, 0.9f, 1.1f) * tickDelta * WATER_CONSUMPTION.get());

                    growth += delta;
                }

                // Add nutrients back to soil. Must happen after we determine the growth delta to prevent extra nutrients being added
                final float growthDelta = growth - initialGrowth;
                final float percentOfNutrientsSatisfied = nutrientsRequired > 0 ? nutrientsConsumed / nutrientsRequired : 0f;
                final float bonus = 1f; // soil contribution

                planter.produceNutrients(nForGrowth * bonus, FarmlandBlockEntity.NutrientType.NITROGEN, percentOfNutrientsSatisfied, growthDelta);
                planter.produceNutrients(pForGrowth * bonus, FarmlandBlockEntity.NutrientType.PHOSPHOROUS, percentOfNutrientsSatisfied, growthDelta);
                planter.produceNutrients(kForGrowth * bonus, FarmlandBlockEntity.NutrientType.POTASSIUM, percentOfNutrientsSatisfied, growthDelta);
                // Calculate yield, which depends on the nutrient satisfaction, which is a measure of nutrient consumption over the growth time.
                final float nutrientSatisfaction;

                if (growthDelta <= 0)
                {
                    nutrientSatisfaction = 1; // Either condition causes the below formula to result in NaN
                }
                else if (nutrientsRequired <= 0)
                {
                    nutrientSatisfaction = 0; // No yield bonuses for plants that don't absorb nutrients
                }
                else
                {
                    nutrientSatisfaction = Math.min(1, (totalGrowthDelta / growthDelta) * (nutrientsAvailable / nutrientsRequired));
                }

                actualYield += growthDelta * Helpers.lerp(nutrientSatisfaction, YIELD_MIN, YIELD_LIMIT);

                planter.setGrowth(slot, growth);
                planter.setYield(slot, actualYield);
            }
            else
            {
                planter.setGrowth(slot, 0);
                planter.setYield(slot, 0);
                planter.setLastGrowthTick(calendar.getTicks());
                return false;
            }
        }
        planter.setLastGrowthTick(calendar.getTicks());
        planter.afterGrowthTickStep(growing);
        return true;
    }

}
