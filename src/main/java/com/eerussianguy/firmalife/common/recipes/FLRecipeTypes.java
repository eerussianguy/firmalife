package com.eerussianguy.firmalife.common.recipes;

import net.minecraft.core.Registry;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

import com.eerussianguy.firmalife.common.FLHelpers;
import net.dries007.tfc.common.recipes.KnappingRecipe;
import net.dries007.tfc.common.recipes.PotRecipe;

import static com.eerussianguy.firmalife.FirmaLife.MOD_ID;

public class FLRecipeTypes
{
    public static final DeferredRegister<RecipeType<?>> RECIPE_TYPES = DeferredRegister.create(Registry.RECIPE_TYPE_REGISTRY, MOD_ID);

    public static final RegistryObject<RecipeType<DryingRecipe>> DRYING = register("scraping");
    public static final RegistryObject<RecipeType<SmokingRecipe>> SMOKING = register("smoking");
    public static final RegistryObject<RecipeType<MixingBowlRecipe>> MIXING_BOWL = register("mixing_bowl");
    public static final RegistryObject<RecipeType<KnappingRecipe>> PUMPKIN_KNAPPING = register("pumpkin_knapping");
    public static final RegistryObject<RecipeType<OvenRecipe>> OVEN = register("oven");
    public static final RegistryObject<RecipeType<VatRecipe>> VAT = register("vat");

    public static void init()
    {
        PotRecipe.register(FLHelpers.identifier("stinky_soup"), StinkySoupRecipe.OUTPUT_TYPE);
    }

    private static <R extends Recipe<?>> RegistryObject<RecipeType<R>> register(String name)
    {
        return RECIPE_TYPES.register(name, () -> new RecipeType<>() {
            @Override
            public String toString()
            {
                return FLHelpers.identifier(name).toString();
            }
        });
    }
}
