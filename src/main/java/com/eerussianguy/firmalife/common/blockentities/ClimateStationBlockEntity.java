package com.eerussianguy.firmalife.common.blockentities;

import java.util.HashSet;
import java.util.Set;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.state.BlockState;

import net.dries007.tfc.common.blockentities.TFCBlockEntity;

public class ClimateStationBlockEntity extends TFCBlockEntity
{
    private Set<BlockPos> positions;
    private ClimateType type = ClimateType.GREENHOUSE;

    public ClimateStationBlockEntity(BlockPos pos, BlockState state)
    {
        super(FLBlockEntities.CLIMATE_STATION.get(), pos, state);
        positions = new HashSet<>();
    }

    @Override
    public void loadAdditional(CompoundTag nbt)
    {
        super.loadAdditional(nbt);
        type = ClimateType.byId(nbt.getInt("climateType"));
        long[] array = nbt.getLongArray("positions");
        positions.clear();
        positions = new HashSet<>(array.length);
        for (long pos : array)
        {
            positions.add(BlockPos.of(pos));
        }
    }

    @Override
    public void saveAdditional(CompoundTag nbt)
    {
        super.saveAdditional(nbt);
        nbt.putInt("climateType", type.ordinal());
        final long[] array = new long[positions.size()];
        int i = 0;
        for (BlockPos pos : positions)
        {
            array[i] = pos.asLong();
            i++;
        }
        nbt.putLongArray("positions", array);
    }

    public void updateValidity(boolean valid, int tier)
    {
        assert level != null;
        positions.forEach(pos -> {
            final ClimateReceiver receiver = ClimateReceiver.get(level, pos);
            if (receiver != null)
            {
                receiver.setValid(level, pos, valid, tier, type);
            }
        });
    }

    public void setType(ClimateType cellar)
    {
        type = cellar;
    }

    public void setPositions(Set<BlockPos> positions)
    {
        this.positions = positions;
    }
}
