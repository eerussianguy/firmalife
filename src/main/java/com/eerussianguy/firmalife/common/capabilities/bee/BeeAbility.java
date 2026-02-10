package com.eerussianguy.firmalife.common.capabilities.bee;

import java.util.Arrays;
import java.util.Locale;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.RandomSource;
import net.minecraft.util.StringRepresentable;

import net.dries007.tfc.network.StreamCodecs;

public enum BeeAbility implements StringRepresentable
{
    HARDINESS, // bees can produce in lower temperatures
    PRODUCTION, // honey production ability
    MUTANT, // traits have higher variability during swarming
    FERTILITY, // increases probability of swarming
    CROP_AFFINITY, // increases fertilization of crops
    NATURE_RESTORATION, // rejuvenates the world around the hive
    CALMNESS, // willingness of bees to not attack the keeper
    INFECTION_RESISTANCE, // ability of bees to resist infection
    ;

    public static final Codec<BeeAbility> CODEC = StringRepresentable.fromEnum(BeeAbility::values);
    public static final StreamCodec<ByteBuf, BeeAbility> STREAM_CODEC = StreamCodecs.forEnum(BeeAbility::values);

    public static float getMinTemperature(int hardiness)
    {
        return -2 * hardiness + 11;
    }

    public static int[] fresh()
    {
        final int[] ints = new int[BeeAbility.SIZE];
        Arrays.fill(ints, 0);
        return ints;
    }

    public static BeeAbility random(RandomSource random)
    {
        return VALUES[random.nextInt(SIZE)];
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
