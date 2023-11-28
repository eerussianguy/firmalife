package com.eerussianguy.firmalife.common.blockentities;

import java.util.ArrayDeque;
import java.util.Queue;
import java.util.Set;
import com.eerussianguy.firmalife.common.blocks.FLBlocks;
import com.eerussianguy.firmalife.common.blocks.greenhouse.AbstractSprinklerBlock;
import com.eerussianguy.firmalife.common.blocks.greenhouse.PumpingStationBlock;
import com.eerussianguy.firmalife.common.blocks.greenhouse.SprinklerBlock;
import com.eerussianguy.firmalife.common.blocks.greenhouse.SprinklerPipeBlock;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.common.blockentities.TFCBlockEntity;
import net.dries007.tfc.common.blocks.DirectionPropertyBlock;
import net.dries007.tfc.common.capabilities.FluidTankCallback;
import net.dries007.tfc.util.Helpers;


public class SprinklerBlockEntity extends TFCBlockEntity implements FluidTankCallback, ClimateReceiver
{
    public static void serverTick(Level level, BlockPos pos, BlockState state, SprinklerBlockEntity sprinkler)
    {
        if (level.getGameTime() % (80 + (pos.getZ() % 4)) == 0 && sprinkler.valid && state.getBlock() instanceof AbstractSprinklerBlock block)
        {
            final Fluid fluid = searchForFluid(level, pos, block instanceof SprinklerBlock ? Direction.UP : Direction.DOWN);
            final boolean valid = fluid != null;
            if (valid)
            {
                for (BlockPos waterPos : block.getPathMaker().apply(pos))
                {
                    final ClimateReceiver receiver = ClimateReceiver.get(level, waterPos);
                    if (receiver != null)
                    {
                        receiver.addWater(0.01f, waterPos.getY() > pos.getY() ? Direction.UP : Direction.DOWN);
                    }
                }
            }
            if (state.getValue(AbstractSprinklerBlock.STASIS) != valid)
            {
                level.setBlockAndUpdate(pos, state.cycle(AbstractSprinklerBlock.STASIS));
            }
        }
    }

    /**
     * Attempts to find the fluid that this pump can source, via traversing the network of pipes.
     * @return A fluid if found, otherwise {@code null}
     */
    @Nullable
    private static Fluid searchForFluid(Level level, BlockPos start, Direction pipeDirection)
    {
        final BlockPos.MutableBlockPos cursor = new BlockPos.MutableBlockPos();
        final Queue<Path> queue = new ArrayDeque<>();
        final Set<BlockPos> seen = new ObjectOpenHashSet<>(64);

        final BlockPos above = start.relative(pipeDirection);
        final BlockState stateAbove = level.getBlockState(above);

        if (!isPipe(stateAbove))
        {
            return null;
        }

        enqueueConnections(cursor, level, new Path(stateAbove, above, 1), seen, queue);

        while (!queue.isEmpty())
        {
            final Path prev = queue.poll();
            final Fluid fluid = enqueueConnections(cursor, level, prev, seen, queue);
            if (fluid != null)
            {
                return fluid;
            }
        }
        return null;
    }

    @Nullable
    private static Fluid enqueueConnections(BlockPos.MutableBlockPos cursor, Level level, Path prev, Set<BlockPos> seen, Queue<Path> queue)
    {
        for (final Direction direction : Helpers.DIRECTIONS)
        {
            cursor.setWithOffset(prev.pos, direction);

            if (!seen.contains(cursor))
            {
                final BlockState stateAdj = level.getBlockState(cursor);
                if (isPipe(stateAdj)) // If there are two adjacent pipes, we know they connect as per expected behavior, and so don't have to check the property
                {
                    if (prev.cost < MAX_COST)
                    {
                        final BlockPos posAdj = cursor.immutable();

                        queue.add(new Path(stateAdj, posAdj, 1 + prev.cost));
                        seen.add(posAdj);
                    }
                }
                else if (
                    direction.getAxis().isHorizontal() && // be horizontal
                    prev.state.getValue(DirectionPropertyBlock.getProperty(direction)) && // The current pipe still connects in this direction (to nothing)
                    stateAdj.getBlock() == FLBlocks.PUMPING_STATION.get() || stateAdj.getBlock() == FLBlocks.IRRIGATION_TANK.get() && // next to port
                    PumpingStationBlock.hasConnection(level, cursor)
                )
                {
                    // Then we can pull from this fluid
                    return stateAdj.getFluidState().getType();
                }
            }
        }
        return null;
    }

    private static boolean isPipe(BlockState state)
    {
        return state.getBlock() instanceof SprinklerPipeBlock;
    }

    private static final int MAX_COST = 32;

    private boolean valid = false;

    public SprinklerBlockEntity(BlockPos pos, BlockState state)
    {
        this(FLBlockEntities.SPRINKLER.get(), pos, state);
    }

    public SprinklerBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state)
    {
        super(type, pos, state);
    }

    public boolean isActive()
    {
        return valid;
    }

    @Override
    public void loadAdditional(CompoundTag tag)
    {
        this.valid = tag.getBoolean("valid");
        super.loadAdditional(tag);
    }

    @Override
    public void saveAdditional(CompoundTag tag)
    {
        tag.putBoolean("valid", valid);
        super.saveAdditional(tag);
    }

    @Override
    public void setValid(Level level, BlockPos pos, boolean valid, int tier, boolean cellar)
    {
        if (valid != this.valid)
        {
            markForSync();
        }
        this.valid = valid;
    }

    public boolean isValid()
    {
        return valid;
    }

    private record Path(BlockState state, BlockPos pos, int cost) {}
}
