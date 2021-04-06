package com.eerussianguy.firmalife.init;

import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.registries.IForgeRegistry;

import com.eerussianguy.firmalife.recipe.*;

/**
 * This is where we initialize our registry instances!
 */
public class RegistriesFL
{
    public static final IForgeRegistry<OvenRecipe> OVEN = GameRegistry.findRegistry(OvenRecipe.class);
    public static final IForgeRegistry<DryingRecipe> DRYING = GameRegistry.findRegistry(DryingRecipe.class);
    public static final IForgeRegistry<PlanterRecipe> PLANTER_QUAD = GameRegistry.findRegistry(PlanterRecipe.class);
    public static final IForgeRegistry<NutRecipe> NUT_TREES = GameRegistry.findRegistry(NutRecipe.class);
    public static final IForgeRegistry<CrackingRecipe> CRACKING = GameRegistry.findRegistry(CrackingRecipe.class);
    public static final IForgeRegistry<StrainingRecipe> STRAINING = GameRegistry.findRegistry(StrainingRecipe.class);
}
