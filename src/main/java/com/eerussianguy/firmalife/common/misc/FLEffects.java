package com.eerussianguy.firmalife.common.misc;

import java.util.function.Supplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.neoforged.neoforge.registries.DeferredRegister;

import net.dries007.tfc.common.effect.TFCEffects.Id;

import static com.eerussianguy.firmalife.FirmaLife.MOD_ID;

public class FLEffects
{
    public static final DeferredRegister<MobEffect> EFFECT = DeferredRegister.create(Registries.MOB_EFFECT, MOD_ID);

    public static final Id<SwarmEffect> SWARM = register("swarm", () -> new SwarmEffect(MobEffectCategory.HARMFUL, 0xffff1a));

    public static <T extends MobEffect> Id<T> register(String name, Supplier<T> supplier)
    {
        return new Id<>(EFFECT.register(name, supplier));
    }

}
