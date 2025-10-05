package com.eerussianguy.firmalife.common.capabilities;

import com.eerussianguy.firmalife.FirmaLife;
import com.eerussianguy.firmalife.common.capabilities.bee.BeeComponent;
import com.eerussianguy.firmalife.common.capabilities.wine.WineComponent;
import com.mojang.serialization.Codec;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.neoforged.neoforge.registries.DeferredRegister;

import net.dries007.tfc.common.component.TFCComponents.Id;

public final class FLComponents
{
    public static final DeferredRegister<DataComponentType<?>> COMPONENT = DeferredRegister.create(Registries.DATA_COMPONENT_TYPE, FirmaLife.MOD_ID);

    public static final Id<BeeComponent> BEE = register("bee", BeeComponent.CODEC, BeeComponent.STREAM_CODEC);
    public static final Id<WineComponent> WINE = register("wine", WineComponent.CODEC, WineComponent.STREAM_CODEC);

    private static <T> Id<T> register(String name, Codec<T> codec, StreamCodec<? super RegistryFriendlyByteBuf, T> streamCodec)
    {
        return new Id<>(COMPONENT.register(name, () -> new DataComponentType.Builder<T>()
            .persistent(codec)
            .networkSynchronized(streamCodec)
            .build()));
    }
}
