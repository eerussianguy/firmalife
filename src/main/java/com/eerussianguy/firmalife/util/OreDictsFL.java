package com.eerussianguy.firmalife.util;

import net.minecraftforge.oredict.OreDictionary;

import com.eerussianguy.firmalife.registry.ItemsFL;

public class OreDictsFL
{
    public static void addStaticOres()
    {
        // I don't care enough to do this to everything right now, but
        // this should be used instead of checking for a held item
        // because it's better mod compat
        // Also anything that is an IIngredient input should eventually be moved to ore dicts
        // Requires some more thinking
        OreDictionary.registerOre("peel", ItemsFL.PEEL);
    }
}
