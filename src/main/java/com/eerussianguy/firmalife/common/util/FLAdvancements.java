package com.eerussianguy.firmalife.common.util;

import com.eerussianguy.firmalife.FirmaLife;
import net.minecraft.advancements.CriterionTrigger;
import net.minecraft.core.registries.Registries;
import net.neoforged.neoforge.registries.DeferredRegister;

import net.dries007.tfc.util.advancements.GenericTrigger;
import net.dries007.tfc.util.advancements.TFCAdvancements.Id;

public final class FLAdvancements
{
    public static final DeferredRegister<CriterionTrigger<?>> TRIGGER_TYPE = DeferredRegister.create(Registries.TRIGGER_TYPE, FirmaLife.MOD_ID);

    public static final Id BIG_CELLAR = registerGeneric("big_cellar");
    public static final Id BIG_STAINLESS_GREENHOUSE = registerGeneric("big_stainless_greenhouse");
    public static final Id STOVETOP_POT = registerGeneric("stovetop_pot");
    public static final Id STOVETOP_GRILL = registerGeneric("stovetop_grill");

    public static Id registerGeneric(String name)
    {
        return new Id(TRIGGER_TYPE.register(name, GenericTrigger::new));
    }

}
