package com.eerussianguy.firmalife.common.capabilities.bee;

import java.util.Locale;
import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.StringRepresentable;

import net.dries007.tfc.network.StreamCodecs;

public enum GeneticDisease implements StringRepresentable
{
    NONE,
    MALFORMED_RECTUM,
    MALPHIGIAN_TUBULE_IRIDESCENCE,
    RECTAL_STONES,
    POOR_OSMOREGULATION,
    NOSEMOSIS,
    BROKEN_WINGS;

    public static final Codec<GeneticDisease> CODEC = StringRepresentable.fromEnum(GeneticDisease::values);
    public static final StreamCodec<ByteBuf, GeneticDisease> STREAM_CODEC = StreamCodecs.forEnum(GeneticDisease::values);
    public static final GeneticDisease[] VALUES = values();

    private final String name;

    GeneticDisease()
    {
        name = name().toLowerCase(Locale.ROOT);
    }

    @Override
    public String getSerializedName()
    {
        return name;
    }
}
