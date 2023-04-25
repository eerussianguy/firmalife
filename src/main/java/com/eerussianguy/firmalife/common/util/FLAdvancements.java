package com.eerussianguy.firmalife.common.util;

import com.eerussianguy.firmalife.common.FLHelpers;
import net.minecraft.advancements.CriteriaTriggers;

import net.dries007.tfc.util.advancements.GenericTrigger;

public final class FLAdvancements
{
    public static void init() {}

    public static final GenericTrigger BIG_CELLAR = registerGeneric("big_cellar");
    public static final GenericTrigger BIG_STAINLESS_GREENHOUSE = registerGeneric("big_stainless_greenhouse");
    public static final GenericTrigger STOVETOP_POT = registerGeneric("stovetop_pot");
    public static final GenericTrigger STOVETOP_GRILL = registerGeneric("stovetop_grill");

    public static GenericTrigger registerGeneric(String name)
    {
        return CriteriaTriggers.register(new GenericTrigger(FLHelpers.identifier(name)));
    }

}
