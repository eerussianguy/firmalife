package com.eerussianguy.firmalife.common.util;

import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;
import java.util.function.Predicate;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.BoundingBox;

import com.eerussianguy.firmalife.common.FLHelpers;
import net.dries007.tfc.util.Helpers;
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
                Set<BlockPos> filled = floodfill(level, pos, mutable, box, greenhouse.ingredient, s -> true, false, lastSize, FLHelpers.NOT_DOWN);
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
}
