package com.eerussianguy.firmalife.providers;

import com.eerussianguy.firmalife.common.misc.FLDamageTypes;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.damagesource.DamageEffects;
import net.minecraft.world.damagesource.DamageScaling;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.damagesource.DeathMessageType;

public class BuiltinDamageTypes
{
    private final BootstrapContext<DamageType> context;

    public BuiltinDamageTypes(BootstrapContext<DamageType> context)
    {
        this.context = context;

        register(FLDamageTypes.OVEN, 0.1f, DamageEffects.BURNING);
        register(FLDamageTypes.SWARM, 0.1f, DamageEffects.HURT);
    }

    private void register(ResourceKey<DamageType> type, float exhaustion, DamageEffects effects)
    {
        context.register(type, new DamageType(type.location().getNamespace() + "." + type.location().getPath(), DamageScaling.WHEN_CAUSED_BY_LIVING_NON_PLAYER, exhaustion, effects, DeathMessageType.DEFAULT));
    }

}
