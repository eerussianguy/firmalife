package com.eerussianguy.firmalife.common.blocks.greenhouse;

import java.util.Locale;
import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.StringRepresentable;

import net.dries007.tfc.network.StreamCodecs;

public enum PlanterType implements StringRepresentable
{
    QUAD,
    LARGE,
    HANGING,
    TRELLIS,
    BONSAI,
    HYDROPONIC,
    ;

    private final String name;

    PlanterType()
    {
        name = name().toLowerCase(Locale.ROOT);
    }

    public static final Codec<PlanterType> CODEC = StringRepresentable.fromEnum(PlanterType::values);
    public static final StreamCodec<ByteBuf, PlanterType> STREAM_CODEC = StreamCodecs.forEnum(PlanterType::values);

    @Override
    public String getSerializedName()
    {
        return name;
    }
}
