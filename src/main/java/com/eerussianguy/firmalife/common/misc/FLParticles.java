package com.eerussianguy.firmalife.common.misc;

import java.util.function.Function;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.neoforged.neoforge.registries.DeferredRegister;

import net.dries007.tfc.client.particle.FluidParticleOption;
import net.dries007.tfc.client.particle.TFCParticles.Id;

import static com.eerussianguy.firmalife.FirmaLife.MOD_ID;

public class FLParticles
{
    public static final DeferredRegister<ParticleType<?>> PARTICLE_TYPE = DeferredRegister.create(Registries.PARTICLE_TYPE, MOD_ID);

    public static final Id<SimpleParticleType> GROWTH = register("growth");
    public static final Id<ParticleType<FluidParticleOption>> SPRINKLER = register("sprinkler", FluidParticleOption::codec, FluidParticleOption::streamCodec);

    private static <O extends ParticleOptions> Id<ParticleType<O>> register(
        final String name,
        final Function<ParticleType<O>, MapCodec<O>> codec,
        final Function<ParticleType<O>, StreamCodec<? super RegistryFriendlyByteBuf, O>> streamCodec)
    {
        return new Id<>(PARTICLE_TYPE.register(name, () -> new ParticleType<O>(false)
        {
            @Override
            public MapCodec<O> codec()
            {
                return codec.apply(this);
            }

            @Override
            public StreamCodec<? super RegistryFriendlyByteBuf, O> streamCodec()
            {
                return streamCodec.apply(this);
            }
        }));
    }


    private static Id<SimpleParticleType> register(String name)
    {
        return new Id<>(PARTICLE_TYPE.register(name, () -> new SimpleParticleType(false)));
    }
}
