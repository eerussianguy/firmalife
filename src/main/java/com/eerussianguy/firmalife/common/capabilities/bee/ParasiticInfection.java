package com.eerussianguy.firmalife.common.capabilities.bee;

import java.util.Locale;
import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.StringRepresentable;

import net.dries007.tfc.network.StreamCodecs;

public enum ParasiticInfection implements StringRepresentable
{
    NONE,
    CHALKBROOD,
    STONEBROOD,
    FOULBROOD,
    WAX_MOTHS,
    HIVE_BEETLES,
    MITES;

    public static final Codec<ParasiticInfection> CODEC = StringRepresentable.fromEnum(ParasiticInfection::values);
    public static final StreamCodec<ByteBuf, ParasiticInfection> STREAM_CODEC = StreamCodecs.forEnum(ParasiticInfection::values);
    public static final ParasiticInfection[] VALUES = values();

    private final String name;

    ParasiticInfection()
    {
        name = name().toLowerCase(Locale.ROOT);
    }

    @Override
    public String getSerializedName()
    {
        return name;
    }
}
