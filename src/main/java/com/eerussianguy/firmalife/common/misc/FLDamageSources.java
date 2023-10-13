package com.eerussianguy.firmalife.common.misc;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.damagesource.DamageSource;

import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;

import net.dries007.tfc.util.Helpers;

public class FLDamageSources
{
//    public static final DamageSource OVEN = create("oven").setIsFire();
//    public static final DamageSource SWARM = create("swarm").bypassArmor();
    public static final ResourceKey<DamageType> OVEN = ResourceKey.create(Registries.DAMAGE_TYPE, Helpers.identifier("ash"));
    public static final ResourceKey<DamageType> SWARM = ResourceKey.create(Registries.DAMAGE_TYPE, Helpers.identifier("ash"));

    public static void oven(Entity entity, float amount)
    {
        entity.hurt(new DamageSource(fetch(OVEN, entity.level())), amount);
    }

    public static void swarm(Entity entity, float amount)
    {
        entity.hurt(new DamageSource(fetch(SWARM, entity.level())), amount);
    }

    private static Holder<DamageType> fetch(ResourceKey<DamageType> type, Level level)
    {
        return level.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(type);
    }
}
