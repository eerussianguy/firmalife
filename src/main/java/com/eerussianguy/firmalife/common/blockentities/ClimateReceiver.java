package com.eerussianguy.firmalife.common.blockentities;

import com.eerussianguy.firmalife.common.blocks.greenhouse.ClimateStationBlock;
import com.eerussianguy.firmalife.common.misc.FLPOIs;
import com.eerussianguy.firmalife.config.FLConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.village.poi.PoiManager;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public interface ClimateReceiver
{
    @Nullable
    static ClimateReceiver get(Level level, BlockPos pos)
    {
        if (level.getBlockEntity(pos) instanceof ClimateReceiver receiver)
        {
            return receiver;
        }
        else if (level.getBlockState(pos).getBlock() instanceof ClimateReceiver receiver)
        {
            return receiver;
        }
        return null;
    }

    default boolean addWater(float amount)
    {
        return addWater(amount, null);
    }

    default boolean addWater(float amount, @Nullable Direction direction)
    {
        return false;
    }

    void setValid(Level level, BlockPos pos, boolean valid, int tier, ClimateType climate);

    default void findClimateStation(Level level, BlockPos origin)
    {
        if (level instanceof ServerLevel server)
        {
            server.getPoiManager()
                .findAll(h -> h.value().equals(FLPOIs.CLIMATE_STATIONS.value()), p -> !p.equals(origin), origin, FLConfig.SERVER.greenhouseRadius.get() * 2, PoiManager.Occupancy.ANY)
                .forEach(pos -> ClimateStationBlock.check(level, pos, level.getBlockState(pos)));
        }
    }
}
