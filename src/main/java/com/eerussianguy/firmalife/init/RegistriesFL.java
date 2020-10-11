package com.eerussianguy.firmalife.init;

import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.registries.IForgeRegistry;

/**
 * This is where we initialize our registry instances!
 */
public class RegistriesFL
{
    public static final IForgeRegistry<OvenRecipe> OVEN = GameRegistry.findRegistry(OvenRecipe.class);
}
