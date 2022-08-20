package com.eerussianguy.firmalife.common.capabilities.bee;

import java.util.Arrays;
import java.util.Locale;

import net.minecraft.util.StringRepresentable;

public enum BeeAbility implements StringRepresentable
{
    HARDINESS, // bees can produce in lower temperatures
    PRODUCTION, // honey production ability
    MUTANT, // traits have higher variability during breeding
    FERTILITY, // increases probability of breeding
    CROP_AFFINITY, // increases fertilization of crops
    NATURE_RESTORATION, // rejuvenates the world around the hive
    CALMNESS; // willingness of bees to not attack the keeper

    public static float getMinTemperature(int hardiness)
    {
        return -2 * hardiness + 4;
    }

    public static int[] fresh()
    {
        final int[] ints = new int[BeeAbility.SIZE];
        Arrays.fill(ints, 0);
        return ints;
    }

    public static final BeeAbility[] VALUES = values();
    public static final int SIZE = values().length;

    private final String name;

    BeeAbility()
    {
        this.name = name().toLowerCase(Locale.ROOT);
    }

    @Override
    public String getSerializedName()
    {
        return name;
    }
}
