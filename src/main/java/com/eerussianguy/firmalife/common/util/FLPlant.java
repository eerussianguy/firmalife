package com.eerussianguy.firmalife.common.util;

import java.util.Arrays;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.IntegerProperty;

import net.dries007.tfc.common.blocks.ExtendedProperties;
import net.dries007.tfc.common.blocks.TFCBlockStateProperties;
import net.dries007.tfc.util.calendar.Month;
import net.dries007.tfc.util.registry.RegistryPlant;
import org.jetbrains.annotations.Nullable;

//TODO this needs plant properties defined
public enum FLPlant implements RegistryPlant
{
    BUTTERFLY_GRASS(0.8F),
    HERB(0.9F, new int[] {0, 0, 0, 0, 1, 1, 1, 1, 0, 0, 0, 0});

    private final float speedFactor;
    @Nullable private final IntegerProperty property;
    private final int @Nullable [] stagesByMonth;

    FLPlant(float speedFactor)
    {
        this(speedFactor, null);
    }

    FLPlant(float speedFactor, int @Nullable [] stagesByMonth)
    {
        this.speedFactor = speedFactor;
        this.stagesByMonth = stagesByMonth;
        int maxStage = 0;
        if (stagesByMonth != null)
        {
            maxStage = Arrays.stream(stagesByMonth).max().orElse(0);
        }

        this.property = maxStage > 0 ? TFCBlockStateProperties.getStageProperty(maxStage) : null;
    }

    public float getSpeedFactor()
    {
        return speedFactor;
    }

    public int stageFor(Month month)
    {
        assert stagesByMonth != null;
        return stagesByMonth.length < month.ordinal() ? 0 : stagesByMonth[month.ordinal()];
    }

    public BlockBehaviour.Properties solid()
    {
        return Block.Properties.of().replaceable().noOcclusion().sound(SoundType.GRASS).randomTicks();
    }

    public BlockBehaviour.Properties nonSolid()
    {
        return solid().instabreak().speedFactor(speedFactor).noCollission();
    }

    public ExtendedProperties nonSolidFire()
    {
        return fire(nonSolid());
    }

    @Override
    public boolean isWetSeasonBlooming()
    {
        return false;
    }

    @Override
    public int getStartTime()
    {
        return 0;
    }

    @Override
    public int getEndTime()
    {
        return 0;
    }

    @Override
    public float getBloomOffset()
    {
        return 0;
    }

    @Override
    public float getBloomingEnd()
    {
        return 0;
    }

    @Override
    public float getSeedingEnd()
    {
        return 0;
    }

    @Override
    public float getDyingEnd()
    {
        return 0;
    }

    @Override
    public float getDormantEnd()
    {
        return 0;
    }

    @Override
    public float getSproutingEnd()
    {
        return 0;
    }

    @Override
    public @Nullable IntegerProperty getAgeProperty()
    {
        return property;
    }

    private ExtendedProperties fire(BlockBehaviour.Properties properties)
    {
        return ExtendedProperties.of(properties).flammable(60, 30);
    }
}
