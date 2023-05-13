package com.eerussianguy.firmalife.common.misc;

import java.util.Random;

import com.eerussianguy.firmalife.common.FLTags;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

import com.eerussianguy.firmalife.config.FLConfig;
import net.dries007.tfc.util.Helpers;

public class SwarmEffect extends MobEffect
{
    public static void particles(Level level, BlockPos pos, Random rand)
    {
        final double x = pos.getX() + 0.5;
        final double y = pos.getY() + 0.5;
        final double z = pos.getZ() + 0.5;
        final int ct = 3 + rand.nextInt(4);
        for (int i = 0; i < ct; i++)
        {
            level.addParticle(ParticleTypes.SMOKE, x + Helpers.triangle(rand), y + rand.nextFloat(), z + Helpers.triangle(rand), 0.5 * (Helpers.triangle(rand)), 0.5 * (Helpers.triangle(rand)), 0.5 * (Helpers.triangle(rand)));
        }
    }

    public SwarmEffect(MobEffectCategory category, int color)
    {
        super(category, color);
    }

    @Override
    public boolean isDurationEffectTick(int time, int amp)
    {
        int j = 25 >> amp;
        if (j > 0)
        {
            return time % j == 0;
        }
        return true;
    }

    @Override
    public void applyEffectTick(LivingEntity entity, int amplifier)
    {
        if (!entity.isInWaterOrRain() && FLConfig.SERVER.enableBeeSwarm.get() && !hasBeeArmor(entity))
        {
            entity.hurt(FLDamageSources.SWARM, 0.5F);
        }
        particles(entity.getLevel(), entity.blockPosition(), entity.getRandom());
    }

    private boolean hasBeeArmor(LivingEntity entity)
    {
        return Helpers.isItem(entity.getItemBySlot(EquipmentSlot.HEAD), FLTags.Items.BEEKEEPER_ARMOR)
             && Helpers.isItem(entity.getItemBySlot(EquipmentSlot.CHEST), FLTags.Items.BEEKEEPER_ARMOR)
             && Helpers.isItem(entity.getItemBySlot(EquipmentSlot.LEGS), FLTags.Items.BEEKEEPER_ARMOR)
             && Helpers.isItem(entity.getItemBySlot(EquipmentSlot.FEET), FLTags.Items.BEEKEEPER_ARMOR);
    }
}
