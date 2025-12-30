package com.eerussianguy.firmalife.common.entities;

import java.util.Locale;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;

import com.eerussianguy.firmalife.FirmaLife;
import net.neoforged.neoforge.registries.DeferredRegister;

import net.dries007.tfc.common.entities.TFCEntities.Id;

public class FLEntities
{
    public static final DeferredRegister<EntityType<?>> ENTITY = DeferredRegister.create(Registries.ENTITY_TYPE, FirmaLife.MOD_ID);

    public static final Id<FLBee> FLBEE = register("bee", EntityType.Builder.<FLBee>of(FLBee::new, MobCategory.CREATURE).sized(0.2F, 0.2F).clientTrackingRange(2));

    public static <E extends Entity> Id<E> register(String name, EntityType.Builder<E> builder)
    {
        return register(name, builder, true);
    }

    public static <E extends Entity> Id<E> register(String name, EntityType.Builder<E> builder, boolean serialize)
    {
        final String id = name.toLowerCase(Locale.ROOT);
        return new Id<>(ENTITY.register(id, () -> {
            if (!serialize) builder.noSave();
            return builder.build(FirmaLife.MOD_ID + ":" + id);
        }));
    }
}
