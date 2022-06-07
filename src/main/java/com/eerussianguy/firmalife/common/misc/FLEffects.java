package com.eerussianguy.firmalife.common.misc;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import static com.eerussianguy.firmalife.Firmalife.MOD_ID;

public class FLEffects
{
    public static final DeferredRegister<MobEffect> EFFECTS = DeferredRegister.create(ForgeRegistries.MOB_EFFECTS, MOD_ID);

    public static final RegistryObject<SwarmEffect> SWARM = EFFECTS.register("swarm", () -> new SwarmEffect(MobEffectCategory.HARMFUL, 0xffff1a));

}
