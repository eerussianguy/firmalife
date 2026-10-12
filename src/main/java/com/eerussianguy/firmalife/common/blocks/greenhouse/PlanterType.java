package com.eerussianguy.firmalife.common.blocks.greenhouse;

import java.util.Locale;
import com.eerussianguy.firmalife.common.blocks.FLBlocks;
import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.block.Block;

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

    public Block resolvePlanterBlock()
    {
        var planterBlock = switch (this)
        {
            case QUAD -> FLBlocks.QUAD_PLANTER;
            case LARGE -> FLBlocks.LARGE_PLANTER;
            case HANGING -> FLBlocks.HANGING_PLANTER;
            case TRELLIS -> FLBlocks.TRELLIS_PLANTER;
            case BONSAI -> FLBlocks.BONSAI_PLANTER;
            case HYDROPONIC -> FLBlocks.HYDROPONIC_PLANTER;
        };
        return planterBlock.get();
    }
}
