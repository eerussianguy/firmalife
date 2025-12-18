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
    CHALKBROOD,//
    STONEBROOD,//
    FOULBROOD,
    WAX_MOTHS,//
    HIVE_BEETLES,//
    VARROA;

    public static final Codec<ParasiticInfection> CODEC = StringRepresentable.fromEnum(ParasiticInfection::values);
    public static final StreamCodec<ByteBuf, ParasiticInfection> STREAM_CODEC = StreamCodecs.forEnum(ParasiticInfection::values);
    public static final ParasiticInfection[] VALUES = values();

    public static ParasiticInfection valueOf(int i)
    {
        return i >= VALUES.length || i < 0 ? NONE : VALUES[i];
    }

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
