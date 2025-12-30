package com.eerussianguy.firmalife.common.capabilities.bee;

import java.util.Arrays;
import java.util.Locale;
import java.util.Map;
import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.RandomSource;
import net.minecraft.util.StringRepresentable;
import net.minecraft.util.random.SimpleWeightedRandomList;

import net.dries007.tfc.network.StreamCodecs;

public enum BeeSpecies implements StringRepresentable
{
    WESTERN(Map.of(BeeAbility.CALMNESS, 4, BeeAbility.PRODUCTION, 3), 5f, 20f, 100f, 400f, 15, false),
    ASIAN(Map.of(BeeAbility.FERTILITY, 3, BeeAbility.NATURE_RESTORATION, 1), 10f, 30f, 250f, 400f, 10, false),
    GIANT(Map.of(BeeAbility.FERTILITY, 1), 15f, 30f, 300f, 450f, 10, false),
    DWARF(Map.of(BeeAbility.FERTILITY, 5, BeeAbility.PRODUCTION, 2), 20f, 30f, 300f, 400f, 10, false),
    BLACK_DWARF(Map.of(BeeAbility.MUTANT, 1, BeeAbility.NATURE_RESTORATION, 2), 12f, 22f, 150f, 350f, 5, false),
    KOSCHEVNIKOV(Map.of(BeeAbility.HARDINESS, 2, BeeAbility.PRODUCTION, 1, BeeAbility.NATURE_RESTORATION, 1), 12f, 16f, 200f, 375f, 2, false),
    HIMALAYAN_GIANT(Map.of(BeeAbility.HARDINESS, 3, BeeAbility.MUTANT, 1, BeeAbility.PRODUCTION, 1), 10f, 19f, 150f, 250f, 5, true),
    PHILIPPINE(Map.of(BeeAbility.CALMNESS, 1, BeeAbility.CROP_AFFINITY, 1), 15f, 20f, 350f, 450f, 2, false),
    BORNEO_MOUNTAIN(Map.of(BeeAbility.HARDINESS, 3, BeeAbility.INFECTION_RESISTANCE, 1), 13f, 22f, 150f, 400f, 10, true),
    INDONESIAN_GIANT(Map.of(BeeAbility.NATURE_RESTORATION, 1, BeeAbility.FERTILITY, 1, BeeAbility.INFECTION_RESISTANCE, 1), 16f, 20f, 350f, 420f, 5, false),
    AFRICANIZED(Map.of(BeeAbility.HARDINESS, 2), 10f, 25f, 100f, 320f, 15, false),
    ;

    public static final BeeSpecies[] CAN_BE_FOUND = values();
    public static final Codec<BeeSpecies> CODEC = StringRepresentable.fromEnum(BeeSpecies::values);
    public static final StreamCodec<ByteBuf, BeeSpecies> STREAM_CODEC = StreamCodecs.forEnum(BeeSpecies::values);

    private final String serializedName;
    private final Map<BeeAbility, Integer> defaults;
    private final float minTemp;
    private final float maxTemp;
    private final float minRain;
    private final float maxRain;
    private final int weight;
    private final boolean mountain;

    BeeSpecies(Map<BeeAbility, Integer> defaults, float minTemp, float maxTemp, float minRain, float maxRain, int weight, boolean mountain)
    {
        this.serializedName = name().toLowerCase(Locale.ROOT);
        this.defaults = defaults;
        this.minTemp = minTemp;
        this.maxTemp = maxTemp;
        this.minRain = minRain;
        this.maxRain = maxRain;
        this.weight = weight;
        this.mountain = mountain;
    }

    @Override
    public String getSerializedName()
    {
        return serializedName;
    }

    public Map<BeeAbility, Integer> getDefaults()
    {
        return defaults;
    }

    public static BeeSpecies choose(float temp, float rain, BlockPos pos, RandomSource random)
    {
        final SimpleWeightedRandomList.Builder<BeeSpecies> builder = SimpleWeightedRandomList.builder();
        Arrays.stream(CAN_BE_FOUND)
            .filter(s -> temp > s.minTemp && temp < s.maxTemp && rain > s.minRain && rain < s.maxRain && (!s.mountain || pos.getY() > 96))
            .forEach(s -> builder.add(s, s.weight));
        return builder.build().getRandomValue(random).orElse(WESTERN);
    }
}
