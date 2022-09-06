package com.eerussianguy.firmalife.common.misc;

import net.minecraft.world.damagesource.DamageSource;

import com.eerussianguy.firmalife.FirmaLife;

public class FLDamageSources
{
    public static final DamageSource OVEN = create("oven").setIsFire();
    public static final DamageSource SWARM = create("swarm").bypassArmor();

    private static DamageSource create(String key)
    {
        return new DamageSource(FirmaLife.MOD_ID + "." + key);
    }
}
