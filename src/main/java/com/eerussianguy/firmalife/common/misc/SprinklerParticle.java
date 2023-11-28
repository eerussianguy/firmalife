package com.eerussianguy.firmalife.common.misc;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.world.level.material.Fluid;

import net.dries007.tfc.client.particle.FluidDripParticle;
import net.dries007.tfc.client.particle.FluidParticleOption;

public class SprinklerParticle extends FluidDripParticle.FluidFallAndLandParticle
{
    public SprinklerParticle(ClientLevel level, double x, double y, double z, Fluid fluid)
    {
        super(level, x, y, z, fluid);
    }

    public static ParticleProvider<FluidParticleOption> provider(SpriteSet set, FluidParticleFactory factory)
    {
        return (type, level, x, y, z, dx, dy, dz) -> {
            FluidDripParticle particle = factory.create(level, x, y, z, type.getFluid());
            particle.pickSprite(set);
            particle.setParticleSpeed(dx, dy, dz);
            return particle;
        };
    }
}
