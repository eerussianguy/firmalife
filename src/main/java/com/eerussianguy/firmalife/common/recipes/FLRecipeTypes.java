package com.eerussianguy.firmalife.common.recipes;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;

import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import net.dries007.tfc.common.recipes.TFCRecipeTypes.Id;
import net.dries007.tfc.common.recipes.outputs.PotOutput;

import static com.eerussianguy.firmalife.FirmaLife.MOD_ID;

public class FLRecipeTypes
{
    public static final DeferredRegister<RecipeType<?>> RECIPE_TYPE = DeferredRegister.create(Registries.RECIPE_TYPE, MOD_ID);
    public static final DeferredRegister<PotOutput.OutputType> POT_OUTPUT_TYPE = DeferredRegister.create(PotOutput.KEY, MOD_ID);

    public static final Id<DryingRecipe> DRYING = register("drying");
    public static final Id<SmokingRecipe> SMOKING = register("smoking");
    public static final Id<MixingBowlRecipe> MIXING_BOWL = register("mixing_bowl");
    public static final Id<OvenRecipe> OVEN = register("oven");
    public static final Id<VatRecipe> VAT = register("vat");
    public static final Id<StompingRecipe> STOMPING = register("stomping");
    public static final Id<PressRecipe> PRESS = register("press");
    public static final Id<CentrifugeRecipe> CENTRIFUGE = register("centrifuge");

    public static final DeferredHolder<PotOutput.OutputType, PotOutput.OutputType> STINKY_SOUP = POT_OUTPUT_TYPE.register("stinky_soup", () -> StinkySoupRecipe.OUTPUT_TYPE);
    public static final DeferredHolder<PotOutput.OutputType, PotOutput.OutputType> BOWL = POT_OUTPUT_TYPE.register("bowl", () -> BowlPotRecipe.OUTPUT_TYPE);

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
