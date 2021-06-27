package com.eerussianguy.firmalife.particle;

import net.minecraft.client.particle.Particle;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.world.World;

public class ParticleSprinkle extends Particle
{
    protected ParticleSprinkle(World worldIn, double x, double y, double z, double speedX, double speedY, double speedZ, int duration)
    {
        super(worldIn, x, y, z);
        particleGravity = 0.06F;
        particleMaxAge = duration + (int) (20 * Math.random());
        motionX = speedX;
        motionY = speedY;
        motionZ = speedZ;
        particleRed = 0.2F;
        particleGreen = 0.3F;
        particleBlue = 1.0F;
    }

    @Override
    public void onUpdate()
    {
        prevPosX = posX;
        prevPosY = posY;
        prevPosZ = posZ;

        motionY -= particleGravity;

        move(motionX, motionY, motionZ);
        motionX *= 0.98D;
        motionY *= 0.98D;
        motionZ *= 0.98D;

        if (particleMaxAge-- <= 0)
        {
            setExpired();
        }

        if (onGround)
        {
            setExpired();
            world.spawnParticle(EnumParticleTypes.WATER_SPLASH, posX, posY, posZ, 0.0D, 0.0D, 0.0D);
        }
    }

    @Override
    public int getFXLayer()
    {
        return 1;
    }

}
