package com.eerussianguy.firmalife.client;

import java.util.Random;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;

import net.dries007.tfc.util.Helpers;

public final class FLClientHelpers
{
    public static void randomParticle(ParticleOptions particle, Random random, BlockPos pos, Level level, float ySpeed)
    {
        final double x = pos.getX() + Mth.nextFloat(random, 0.125f, 0.875f);
        final double y = pos.getY() + Mth.nextFloat(random, 0.125f, 0.875f);
        final double z = pos.getZ() + Mth.nextFloat(random, 0.125f, 0.875f);
        level.addParticle(particle, x, y, z, Helpers.triangle(random), ySpeed, Helpers.triangle(random));
    }
}
