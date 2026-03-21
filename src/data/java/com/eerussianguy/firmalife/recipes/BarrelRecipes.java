package com.eerussianguy.firmalife.recipes;

import com.eerussianguy.firmalife.common.FLTags;
import com.eerussianguy.firmalife.common.blocks.FLBlocks;
import com.eerussianguy.firmalife.common.items.FLFood;
import com.eerussianguy.firmalife.common.items.FLFoodTraits;
import com.eerussianguy.firmalife.common.items.FLItems;
import com.eerussianguy.firmalife.common.recipes.data.EmptyPanModifier;
import com.eerussianguy.firmalife.common.util.ExtraFluid;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.common.NeoForgeMod;

import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.fluids.SimpleFluid;
import net.dries007.tfc.common.fluids.TFCFluids;
import net.dries007.tfc.common.recipes.BarrelRecipe;
import net.dries007.tfc.common.recipes.outputs.AddTraitModifier;
import net.dries007.tfc.common.recipes.outputs.CopyInputModifier;
import net.dries007.tfc.common.recipes.outputs.ItemStackProvider;

public interface BarrelRecipes extends Recipes
{
    default void barrelRecipes()
    {
        remove(
            "barrel/curdled_milk", "barrel/food/cheese", "barrel/milk_vinegar"
        );
        barrel()
            .input(hasTrait(TFCTags.Items.FRUITS, FLFoodTraits.DRIED))
            .input(Fluids.WATER, 100)
            .output(fluidOf(ExtraFluid.YEAST_STARTER), 100)
            .sealed(72000);
        barrel("feed_yeast")
            .input(FLTags.Items.FEEDS_YEAST)
            .input(fluidOf(ExtraFluid.YEAST_STARTER), 100)
            .output(fluidOf(ExtraFluid.YEAST_STARTER), 600)
            .sealed(12000);
        barrel()
            .input(itemOf(FLFood.FROTHY_COCONUT))
            .input(fluidOf(SimpleFluid.RUM), 1000)
            .output(fluidOf(ExtraFluid.PINA_COLADA), 1000)
            .sealed(1000);
        barrel()
            .input(FLItems.RENNET)
            .input(NeoForgeMod.MILK.get(), 2000)
            .output(fluidOf(SimpleFluid.CURDLED_MILK), 2000)
            .sealed(4000);
        barrel()
            .input(FLItems.RENNET)
            .input(fluidOf(ExtraFluid.YAK_MILK), 2000)
            .output(fluidOf(ExtraFluid.CURDLED_YAK_MILK), 2000)
            .sealed(4000);
        barrel()
            .input(FLItems.RENNET)
            .input(fluidOf(ExtraFluid.GOAT_MILK), 2000)
            .output(fluidOf(ExtraFluid.CURDLED_GOAT_MILK), 2000)
            .sealed(4000);
        barrel()
            .input(FLItems.CHEESECLOTH)
            .input(fluidOf(SimpleFluid.CURDLED_MILK), 1000)
            .output(itemOf(FLFood.MILK_CURD))
            .sealed(1000);
        barrel()
            .input(FLItems.CHEESECLOTH)
            .input(fluidOf(ExtraFluid.CURDLED_GOAT_MILK), 1000)
            .output(itemOf(FLFood.GOAT_CURD))
            .sealed(1000);
        barrel()
            .input(FLItems.CHEESECLOTH)
            .input(fluidOf(ExtraFluid.CURDLED_YAK_MILK), 1000)
            .output(itemOf(FLFood.YAK_CURD))
            .sealed(1000);
        barrel()
            .input(FLItems.CHEESECLOTH)
            .input(FLTags.Fluids.MILKS, 1000)
            .output(ItemStackProvider.of(FLItems.CHEESECLOTH))
            .output(fluidOf(ExtraFluid.CREAM), 1000)
            .sealed(1000);
        barrel()
            .input(itemOf(FLFood.CURED_MAIZE))
            .input(Fluids.WATER, 100)
            .output(itemOf(FLFood.NIXTAMAL))
            .sealed(1000);
        barrel()
            .input(FLItems.FOODS.get(FLFood.RAW_HONEY))
            .input(Fluids.WATER, 100)
            .output(fluidOf(ExtraFluid.MEAD), 100)
            .sealed(72000);
        barrel("ferment_red_grapes")
            .input(lacksTrait(itemOf(FLFood.SMASHED_RED_GRAPES), FLFoodTraits.FERMENTED))
            .input(Fluids.WATER, 100)
            .output(ItemStackProvider.of(CopyInputModifier.INSTANCE, AddTraitModifier.of(FLFoodTraits.FERMENTED)))
            .sealed(120000);
        barrel("ferment_white_grapes")
            .input(lacksTrait(itemOf(FLFood.SMASHED_WHITE_GRAPES), FLFoodTraits.FERMENTED))
            .input(Fluids.WATER, 100)
            .output(ItemStackProvider.of(CopyInputModifier.INSTANCE, AddTraitModifier.of(FLFoodTraits.FERMENTED)))
            .sealed(120000);
        barrel()
            .input(FLItems.TREATED_LUMBER)
            .input(fluidOf(SimpleFluid.LIMEWATER), 1000)
            .output(ItemStackProvider.of(FLItems.CORK, 8))
            .sealed(24000);
        barrel()
            .input(itemOf(FLFood.SOYBEAN_PASTE))
            .input(Fluids.WATER, 100)
            .output(fluidOf(ExtraFluid.SOYBEAN_OIL), 250)
            .sealed(24000);
        barrel()
            .input(sized(itemOf(FLFood.YAK_CURD), 3))
            .input(TFCFluids.SALT_WATER.getSource(), 750)
            .output(FLBlocks.SHOSHA_WHEEL)
            .sealed(16000);
        barrel()
            .input(sized(itemOf(FLFood.GOAT_CURD), 3))
            .input(TFCFluids.SALT_WATER.getSource(), 750)
            .output(FLBlocks.FETA_WHEEL)
            .sealed(16000);
        barrel()
            .input(sized(itemOf(FLFood.MILK_CURD), 3))
            .input(TFCFluids.SALT_WATER.getSource(), 750)
            .output(FLBlocks.GOUDA_WHEEL)
            .sealed(16000);

        // Instant

        barrel()
            .input(TFCTags.Items.SWEETENERS)
            .input(fluidOf(ExtraFluid.YEAST_STARTER), 100)
            .output(FLItems.TIRAGE_MIXTURE)
            .instant();

        barrel("wash_foods")
            .input(FLTags.Items.WASHABLE_FOODS)
            .input(Fluids.WATER, 100)
            .output(ItemStackProvider.of(EmptyPanModifier.INSTANCE))
            .instant();
        barrel()
            .input(sized(FLItems.OLIVINE_WINE_BOTTLE))
            .input(Fluids.WATER, 100)
            .output(FLItems.EMPTY_OLIVINE_WINE_BOTTLE)
            .instant();
        barrel()
            .input(sized(FLItems.VOLCANIC_WINE_BOTTLE))
            .input(Fluids.WATER, 100)
            .output(FLItems.EMPTY_VOLCANIC_WINE_BOTTLE)
            .instant();
        barrel()
            .input(sized(FLItems.HEMATITIC_WINE_BOTTLE))
            .input(Fluids.WATER, 100)
            .output(FLItems.EMPTY_HEMATITIC_WINE_BOTTLE)
            .instant();
    }

    private BarrelRecipe.Builder barrel()
    {
        return new BarrelRecipe.Builder(this::addBarrelRecipe);
    }

    private BarrelRecipe.Builder barrel(String prefix)
    {
        return new BarrelRecipe.Builder(r -> addBarrelRecipe(r, prefix));
    }

    private void addBarrelRecipe(BarrelRecipe r)
    {
        var resultItem = r.getResultItem();
        var resultFluid = r.getOutputFluid();
        var name = resultItem.isEmpty() ? nameOf(resultFluid.getFluid()) : nameOf(resultItem.getItem());
        addBarrelRecipe(r, name);
    }

    private void addBarrelRecipe(BarrelRecipe r, String name)
    {
        add(name, r);
    }
}
