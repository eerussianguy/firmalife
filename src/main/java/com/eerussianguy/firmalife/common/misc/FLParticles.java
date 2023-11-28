package com.eerussianguy.firmalife.common.misc;

import java.util.function.Function;
import com.mojang.serialization.Codec;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import net.dries007.tfc.client.particle.FluidParticleOption;

import static com.eerussianguy.firmalife.FirmaLife.MOD_ID;

public class FLParticles
{
    public static final DeferredRegister<ParticleType<?>> PARTICLE_TYPES = DeferredRegister.create(ForgeRegistries.PARTICLE_TYPES, MOD_ID);

    public static final RegistryObject<SimpleParticleType> GROWTH = register("growth");
    public static final RegistryObject<ParticleType<FluidParticleOption>> SPRINKLER = register("sprinkler", FluidParticleOption.DESERIALIZER, FluidParticleOption::getCodec);

    @SuppressWarnings("deprecation")
    private static <T extends ParticleOptions> RegistryObject<ParticleType<T>> register(String name, ParticleOptions.Deserializer<T> deserializer, final Function<ParticleType<T>, Codec<T>> codec)
    {
        return PARTICLE_TYPES.register(name, () -> new ParticleType<>(false, deserializer) {
            @Override
            public Codec<T> codec()
            {
                return codec.apply(this);
            }
        });
    }

    private static RegistryObject<SimpleParticleType> register(String name)
    {
        return PARTICLE_TYPES.register(name, () -> new SimpleParticleType(false));
    }
}
