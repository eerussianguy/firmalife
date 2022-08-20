package com.eerussianguy.firmalife.common.entities;

import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import static com.eerussianguy.firmalife.FirmaLife.MOD_ID;

public class FLParticles
{
    public static final DeferredRegister<ParticleType<?>> PARTICLE_TYPES = DeferredRegister.create(ForgeRegistries.PARTICLE_TYPES, MOD_ID);

    public static final RegistryObject<ParticleType<SimpleParticleType>> GROWTH = PARTICLE_TYPES.register("growth", () -> new SimpleParticleType(false));
}
