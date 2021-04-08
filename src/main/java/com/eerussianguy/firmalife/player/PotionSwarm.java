package com.eerussianguy.firmalife.player;

import java.util.Random;
import javax.annotation.Nonnull;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import net.dries007.tfc.Constants;

public class PotionSwarm extends PotionFL
{
    public static final DamageSource SWARM = new DamageSource("swarm").setDifficultyScaled();

    public PotionSwarm()
    {
        super(true, 0xffff1a);
        setPotionName("effectFL.swarm");
        setIconIndex(0, 0);
    }

    @Override
    public void performEffect(@Nonnull EntityLivingBase entity, int amplifier)
    {
        World world = entity.getEntityWorld();
        if (world.isRemote)
        {
            BlockPos pos = entity.getPosition();
            Random rand = Constants.RNG;
            double x = pos.getX() + 0.5;
            double y = pos.getY() + 0.5;
            double z = pos.getZ() + 0.5;
            for (int i = 0; i < 3 + rand.nextInt(4); i++)
            {
                world.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, x + rand.nextFloat() - rand.nextFloat(), y + rand.nextFloat(), z + rand.nextFloat() - rand.nextFloat(),
                    0.5 * (rand.nextFloat() - rand.nextFloat()), 0.5 * (rand.nextFloat() - rand.nextFloat()), 0.5 * (rand.nextFloat() - rand.nextFloat()));
            }
        }
        else
        {
            if (!entity.isWet())
                entity.attackEntityFrom(SWARM, 1.0f + amplifier);
        }

    }
}
