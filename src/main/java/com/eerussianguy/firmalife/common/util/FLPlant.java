package com.eerussianguy.firmalife.common.util;

import java.util.Arrays;

import net.minecraft.world.level.block.state.properties.IntegerProperty;

import net.dries007.tfc.common.blocks.TFCBlockStateProperties;
import net.dries007.tfc.util.calendar.Month;
import net.dries007.tfc.util.registry.RegistryPlant;
import org.jetbrains.annotations.Nullable;

public enum FLPlant implements RegistryPlant
{
    BUTTERFLY_GRASS(0.8F, new int[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0});

    private final float speedFactor;
    @Nullable private final IntegerProperty property;
    private final int @Nullable [] stagesByMonth;

    FLPlant(float speedFactor, int @Nullable [] stagesByMonth)
    {
        this.speedFactor = speedFactor;
        this.stagesByMonth = stagesByMonth;

        int maxStage = 1;
        if (stagesByMonth != null)
        {
            maxStage = Arrays.stream(stagesByMonth).max().orElse(1);
        }
        this.property = maxStage > 0 ? TFCBlockStateProperties.getStageProperty(maxStage) : TFCBlockStateProperties.getStageProperty(1);
    }

    public float getSpeedFactor()
    {
        return speedFactor;
    }

    @Override
    public int stageFor(Month month)
    {
        assert stagesByMonth != null;
        return stagesByMonth.length < month.ordinal() ? 0 : stagesByMonth[month.ordinal()];
    }

    @Override
    public IntegerProperty getStageProperty()
    {
        assert property != null;
        return property;
    }
}
