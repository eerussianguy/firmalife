package com.eerussianguy.firmalife.common.misc;

import com.eerussianguy.firmalife.common.FLHelpers;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.damagesource.DamageSource;

import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;

public class FLDamageSources
{
    public static final ResourceKey<DamageType> OVEN = ResourceKey.create(Registries.DAMAGE_TYPE, FLHelpers.identifier("oven"));
    public static final ResourceKey<DamageType> SWARM = ResourceKey.create(Registries.DAMAGE_TYPE, FLHelpers.identifier("swarm"));

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
