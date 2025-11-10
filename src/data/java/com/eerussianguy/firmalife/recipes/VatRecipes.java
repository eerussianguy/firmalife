package com.eerussianguy.firmalife.recipes;

import java.util.Optional;
import com.eerussianguy.firmalife.common.FLHelpers;
import com.eerussianguy.firmalife.common.FLTags;
import com.eerussianguy.firmalife.common.items.FLFood;
import com.eerussianguy.firmalife.common.items.FLFoodTraits;
import com.eerussianguy.firmalife.common.items.FLItems;
import com.eerussianguy.firmalife.common.recipes.VatRecipe;
import com.eerussianguy.firmalife.common.util.ExtraFluid;
import com.eerussianguy.firmalife.common.util.FLFruit;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.common.crafting.SizedIngredient;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.crafting.SizedFluidIngredient;

import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.fluids.SimpleFluid;
import net.dries007.tfc.common.fluids.TFCFluids;
import net.dries007.tfc.common.items.Food;
import net.dries007.tfc.common.items.Powder;
import net.dries007.tfc.common.items.TFCItems;
import net.dries007.tfc.common.recipes.ingredients.AndIngredient;
import net.dries007.tfc.common.recipes.ingredients.LacksTraitIngredient;
import net.dries007.tfc.common.recipes.ingredients.NotRottenIngredient;
import net.dries007.tfc.common.recipes.outputs.ItemStackProvider;
import net.dries007.tfc.util.Helpers;

public interface VatRecipes extends Recipes
{
    default void vatRecipes()
    {

        //TODO none of these recipes had length or temperature listed
        vat(
            sized(TFCItems.OLIVE_PASTE),
            sized(Fluids.WATER, 200),
            new FluidStack(fluidOf(SimpleFluid.OLIVE_OIL_WATER), 200)
        );
        vat(
            sized(TFCItems.BLUBBER),
            sized(Fluids.WATER, 200),
            new FluidStack(fluidOf(SimpleFluid.TALLOW), 200)
        );
        vat(
            sized(itemOf(Powder.WOOD_ASH)),
            sized(Fluids.WATER, 200),
            new FluidStack(fluidOf(SimpleFluid.LYE), 200)
        );
        vat(
            sized(notRotten(itemOf(Food.RICE_GRAIN))),
            sized(Fluids.WATER, 200),
            ItemStackProvider.of(itemOf(Food.COOKED_RICE))
        );
        vat(
            sized(notRotten(FLTags.Items.RAW_EGGS)),
            sized(Fluids.WATER, 200),
            ItemStackProvider.of(itemOf(Food.BOILED_EGG))
        );
        for (var color : DyeColor.values())
        {
            vat(sized(color.getTag()), sized(Fluids.WATER, 1000), new FluidStack(fluidOf(color), 1000));
        }
        vat(
            sized(notRotten(itemOf(Food.BEET)), 5),
            sized(fluidOf(TFCFluids.SALT_WATER), 1000),
            ItemStackProvider.of(Items.SUGAR, 3)
        );
        vat(
            sized(notRotten(itemOf(Food.SOYBEAN))),
            sized(fluidOf(TFCFluids.SALT_WATER), 1000),
            ItemStackProvider.of(itemOf(FLFood.SOY_MIXTURE))
        );
        vat(
            sized(notRotten(itemOf(Food.MAIZE_GRAIN))),
            sized(fluidOf(SimpleFluid.LIMEWATER), 100),
            ItemStackProvider.of(itemOf(FLFood.CURED_MAIZE))
        );
        vat(
            sized(notRotten(itemOf(FLFood.TOMATO_SAUCE_MIX))),
            sized(Fluids.WATER, 200),
            ItemStackProvider.of(itemOf(FLFood.TOMATO_SAUCE))
        );
        vat(
            sized(TFCTags.Items.SWEETENERS),
            sized(Fluids.WATER, 1000),
            new FluidStack(fluidOf(ExtraFluid.SUGAR_WATER), 500)
        );

        for (var fruit : FLFruit.values())
        {
            vat(
                sized(AndIngredient.of(Ingredient.of(itemOf(fruit)), LacksTraitIngredient.of(FLFoodTraits.DRIED), NotRottenIngredient.INSTANCE)),
                sized(fluidOf(ExtraFluid.SUGAR_WATER), 500),
                new ItemStack(FLItems.FL_FRUIT_PRESERVES.get(fruit)),
                Optional.of(FLHelpers.identifier("block/jar/" + fruit.name().toLowerCase()))
            );
        }
        for (var fruit : FLItems.TFC_FRUITS)
        {
            vat(
                sized(AndIngredient.of(Ingredient.of(itemOf(fruit)), LacksTraitIngredient.of(FLFoodTraits.DRIED), NotRottenIngredient.INSTANCE)),
                sized(fluidOf(ExtraFluid.SUGAR_WATER), 500),
                new ItemStack(TFCItems.FRUIT_PRESERVES.get(fruit)),
                Optional.of(Helpers.identifier("block/jar/" + fruit.name().toLowerCase()))
            );
        }
    }


    private void vat(SizedIngredient ingredient, SizedFluidIngredient fluidInput, ItemStackProvider output, FluidStack outputFluid)
    {
        //TODO this needs length and temperature
        vat(ingredient, fluidInput, Optional.of(output), Optional.of(outputFluid), 1, 1, ItemStack.EMPTY, Optional.empty());
    }

    private void vat(SizedIngredient ingredient, SizedFluidIngredient fluidInput, ItemStack jarOutput, Optional<ResourceLocation> outputTexture)
    {
        //TODO this needs length and temperature
        vat(ingredient, fluidInput, Optional.empty(), Optional.empty(), 1, 1, jarOutput, outputTexture);
    }

    private void vat(SizedIngredient ingredient, SizedFluidIngredient fluidInput, ItemStackProvider output)
    {
        //TODO this needs length and temperature
        vat(ingredient, fluidInput, Optional.of(output), Optional.empty(), 1, 1, ItemStack.EMPTY, Optional.empty());
    }

    private void vat(SizedIngredient ingredient, SizedFluidIngredient fluidInput, FluidStack outputFluid)
    {
        //TODO this needs length and temperature
        vat(ingredient, fluidInput, Optional.empty(), Optional.of(outputFluid), 1, 1, ItemStack.EMPTY, Optional.empty());
    }


    private void vat(SizedIngredient ingredient, SizedFluidIngredient fluidInput, Optional<ItemStackProvider> output, Optional<FluidStack> outputFluid, int length, float temperature, ItemStack jarOutput, Optional<ResourceLocation> outputTexture)
    {
        String name;
        if (output.isPresent())
        {
            name = nameOf(output.get().getEmptyStack().getItem());
        }
        else if (outputFluid.isPresent() && !outputFluid.get().isEmpty())
        {
            name = nameOf(outputFluid.get().getFluid());
        }
        else if (!jarOutput.isEmpty())
        {
            name = nameOf(jarOutput.getItem());
        }
        else
        {
            name = nameOf(ingredient.ingredient());
        }
        add(name, new VatRecipe(ingredient, fluidInput, output, outputFluid, length, temperature, Optional.empty(), outputTexture));
    }
}
