package com.eerussianguy.firmalife.client;

import java.util.Random;

import com.eerussianguy.firmalife.common.FLHelpers;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;

import org.jetbrains.annotations.Nullable;

public final class FLClientHelpers
{
    public static ModelLayerLocation modelIdentifier(String name, String part)
    {
        return new ModelLayerLocation(FLHelpers.identifier(name), part);
    }

    public static ModelLayerLocation modelIdentifier(String name)
    {
        return modelIdentifier(name, "main");
    }

    public static void randomParticle(ParticleOptions particle, Random random, BlockPos pos, Level level, float ySpeed)
    {
        final double x = pos.getX() + Mth.nextFloat(random, 0.125f, 0.875f);
        final double y = pos.getY() + Mth.nextFloat(random, 0.125f, 0.875f);
        final double z = pos.getZ() + Mth.nextFloat(random, 0.125f, 0.875f);
        level.addParticle(particle, x, y, z, 0, ySpeed, 0f);
    }

    @Nullable
    public static BlockHitResult getTargetedLocation()
    {
        final Minecraft mc = Minecraft.getInstance();
        if (mc.level != null && mc.hitResult instanceof BlockHitResult hit)
        {
            return hit;
        }
        return null;
    }
}
