package com.eerussianguy.firmalife.common.util;

import java.util.Arrays;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.Material;

import net.dries007.tfc.common.blocks.ExtendedProperties;
import net.dries007.tfc.common.blocks.TFCBlockStateProperties;
import net.dries007.tfc.common.blocks.plant.Plant;
import net.dries007.tfc.util.calendar.Month;
import net.dries007.tfc.util.registry.RegistryPlant;
import org.jetbrains.annotations.Nullable;

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

    @Override
    public int stageFor(Month month)
    {
        assert stagesByMonth != null;
        return stagesByMonth.length < month.ordinal() ? 0 : stagesByMonth[month.ordinal()];
    }

    @Override
    @Nullable
    public IntegerProperty getStageProperty()
    {
        return property;
    }

    public BlockBehaviour.Properties solid()
    {
        return Block.Properties.of(Material.REPLACEABLE_PLANT).noOcclusion().sound(SoundType.GRASS).randomTicks();
    }

    public BlockBehaviour.Properties nonSolid()
    {
        return solid().instabreak().speedFactor(speedFactor).noCollission();
    }

    public ExtendedProperties nonSolidFire()
    {
        return fire(nonSolid());
    }

    private ExtendedProperties fire(BlockBehaviour.Properties properties)
    {
        return ExtendedProperties.of(properties).flammable(60, 30);
    }
}
