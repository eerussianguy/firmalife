package com.eerussianguy.firmalife.common.recipes;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;

import com.eerussianguy.firmalife.common.FLHelpers;
import net.neoforged.neoforge.registries.DeferredRegister;

import net.dries007.tfc.common.recipes.PotRecipe;
import net.dries007.tfc.common.recipes.TFCRecipeTypes.Id;

import static com.eerussianguy.firmalife.FirmaLife.MOD_ID;

public class FLRecipeTypes
{
    public static final DeferredRegister<RecipeType<?>> RECIPE_TYPE = DeferredRegister.create(Registries.RECIPE_TYPE, MOD_ID);

    public static final Id<DryingRecipe> DRYING = register("scraping");
    public static final Id<SmokingRecipe> SMOKING = register("smoking");
    public static final Id<MixingBowlRecipe> MIXING_BOWL = register("mixing_bowl");
    public static final Id<OvenRecipe> OVEN = register("oven");
    public static final Id<VatRecipe> VAT = register("vat");
    public static final Id<StompingRecipe> STOMPING = register("stomping");
    public static final Id<PressRecipe> PRESS = register("press");

    public static void init()
    {
        PotRecipe.register(FLHelpers.identifier("stinky_soup"), StinkySoupRecipe.OUTPUT_TYPE);
        PotRecipe.register(FLHelpers.identifier("bowl"), BowlPotRecipe.OUTPUT_TYPE);
    }

    private static <R extends Recipe<?>> Id<R> register(String name)
    {
        return new Id<>(RECIPE_TYPE.register(name, () -> new RecipeType<>() {
            @Override
            public String toString()
            {
                return name;
            }
        }));
    }

}
