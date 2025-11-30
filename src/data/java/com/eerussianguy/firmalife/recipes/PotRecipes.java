package com.eerussianguy.firmalife.recipes;

import java.util.Collections;
import java.util.List;
import com.eerussianguy.firmalife.common.FLHelpers;
import com.eerussianguy.firmalife.common.FLTags;
import com.eerussianguy.firmalife.common.items.FLFood;
import com.eerussianguy.firmalife.common.items.FLFoodTraits;
import com.eerussianguy.firmalife.common.items.FLItems;
import com.eerussianguy.firmalife.common.recipes.BowlPotRecipe;
import com.eerussianguy.firmalife.common.recipes.StinkySoupRecipe;
import com.eerussianguy.firmalife.common.util.ExtraFluid;
import com.eerussianguy.firmalife.common.util.FLFruit;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.crafting.SizedFluidIngredient;

import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.component.food.FoodData;
import net.dries007.tfc.common.component.food.FoodTraits;
import net.dries007.tfc.common.fluids.SimpleFluid;
import net.dries007.tfc.common.fluids.TFCFluids;
import net.dries007.tfc.common.items.Food;
import net.dries007.tfc.common.items.Powder;
import net.dries007.tfc.common.recipes.JamPotRecipe;
import net.dries007.tfc.common.recipes.PotRecipe;
import net.dries007.tfc.common.recipes.SimplePotRecipe;
import net.dries007.tfc.common.recipes.outputs.AddTraitModifier;
import net.dries007.tfc.common.recipes.outputs.CopyInputModifier;
import net.dries007.tfc.common.recipes.outputs.ItemStackProvider;
import net.dries007.tfc.util.Helpers;

public interface PotRecipes extends Recipes
{
    default void potRecipes()
    {
        pot(
            List.of(
                notRotten(itemOf(Food.BEET)),
                notRotten(itemOf(Food.BEET)),
                notRotten(itemOf(Food.BEET)),
                notRotten(itemOf(Food.BEET)),
                notRotten(itemOf(Food.BEET))
            ),
            SizedFluidIngredient.of(TFCFluids.SALT_WATER.getSource(), 100),
            List.of(
                Items.SUGAR,
                Items.SUGAR,
                Items.SUGAR
            ),
            2000,
            300
        );
        pot(
            List.of(
                notRotten(itemOf(Food.BEET)),
                notRotten(itemOf(Food.BEET)),
                notRotten(itemOf(Food.BEET)),
                notRotten(itemOf(Food.BEET)),
                Ingredient.of(itemOf(Powder.SALT))
            ),
            SizedFluidIngredient.of(Fluids.WATER, 100),
            List.of(
                Items.SUGAR,
                Items.SUGAR
            ),
            2000,
            300
        );
        pot(
            List.of(
                notRotten(itemOf(Food.SOYBEAN)),
                notRotten(itemOf(Food.SOYBEAN)),
                Ingredient.of(itemOf(Powder.SALT)),
                Ingredient.of(itemOf(Powder.SALT))
            ),
            SizedFluidIngredient.of(Fluids.WATER, 100),
            List.of(
                itemOf(FLFood.SOY_MIXTURE),
                itemOf(FLFood.SOY_MIXTURE)
            ),
            2000,
            300
        );
        pot_5(
            notRotten(itemOf(Food.MAIZE_GRAIN)),
            SizedFluidIngredient.of(fluidOf(SimpleFluid.LIMEWATER), 100),
            itemOf(FLFood.CURED_MAIZE),
            3000,
            300
        );
        pot(
            List.of(
                notRotten(itemOf(Food.TOMATO)),
                Ingredient.of(itemOf(Powder.SALT)),
                notRotten(itemOf(Food.GARLIC))
            ),
            SizedFluidIngredient.of(Fluids.WATER, 100),
            List.of(
                itemOf(FLFood.TOMATO_SAUCE),
                itemOf(FLFood.TOMATO_SAUCE),
                itemOf(FLFood.TOMATO_SAUCE),
                itemOf(FLFood.TOMATO_SAUCE),
                itemOf(FLFood.TOMATO_SAUCE)
            ),
            2000,
            300
        );
        pot(
            List.of(
                Ingredient.of(TFCTags.Items.SWEETENERS),
                notRotten(FLTags.Items.CHOCOLATE)
            ),
            SizedFluidIngredient.of(FLTags.Fluids.MILKS, 1000),
            new FluidStack(fluidOf(ExtraFluid.CHOCOLATE), 1000),
            2000,
            300
        );

        for (var fruit : FLFruit.values())
        {
            jam(fruit);
        }

        bowlPot(
            notRotten(itemOf(FLFood.RAW_EGG_NOODLES)),
            SizedFluidIngredient.of(Fluids.WATER, 100),
            itemOf(FLFood.COOKED_PASTA),
            FoodData.ofFood(4, 2, 0, 3).grain(1.5f),
            2000,
            300
        );

        bowlPot(
            notRotten(itemOf(FLFood.RAW_RICE_NOODLES)),
            SizedFluidIngredient.of(Fluids.WATER, 100),
            itemOf(FLFood.COOKED_RICE_NOODLES),
            FoodData.ofFood(4, 2, 0, 3).grain(1.5f),
            2000,
            300
        );

        for (int i = 3; i < 5; i++)
        {
            var inputs = Helpers.immutableAdd(
                Collections.nCopies(i, notRotten(TFCTags.Items.USABLE_IN_SOUP)),
                Ingredient.of(FLItems.NIGHTSHADE_BERRY)
            );
            add("stinky_" + i,
                new StinkySoupRecipe(
                    new PotRecipe(
                        inputs,
                        SizedFluidIngredient.of(Fluids.WATER, 100),
                        // 1000, 1150, 1300
                        550 + 150 * i,
                        300
                    )
                )
            );
        }
    }

    private void bowlPot(Ingredient itemInput, SizedFluidIngredient fluidInput, ItemLike result, FoodData data, int duration, int temperature)
    {
        for (int i = 1; i < 5; i++)
        {
            var inputs = Collections.nCopies(i, itemInput);
            add(nameOf(result) + "_" + i,
                new BowlPotRecipe(
                    new PotRecipe(
                        inputs,
                        fluidInput,
                        duration,
                        temperature
                    ),
                    new ItemStack(result, i),
                    data
                )
            );
        }
    }

    private void pot_5(Ingredient input, SizedFluidIngredient fluidInput, ItemLike output, int duration, int time)
    {
        for (int i = 1; i < 5; i++)
        {
            var inputs = Collections.nCopies(i, input);
            add(nameOf(output) + "_" + i,
                new SimplePotRecipe(
                    new PotRecipe(
                        inputs,
                        fluidInput,
                        duration,
                        time
                    ),
                    FluidStack.EMPTY,
                    List.of(ItemStackProvider.of(output, i)),
                    false
                )
            );
        }
    }

    private void jam(FLFruit fruit)
    {
        var name = fruit.getSerializedName();
        // Jam boiling
        for (int i = 2; i < 4; i++)
        {
            var inputs = Helpers.immutableAdd(
                Collections.nCopies(i, notRottenWithoutTrait(Ingredient.of(itemOf(fruit)), FLFoodTraits.DRIED)),
                Ingredient.of(TFCTags.Items.SWEETENERS)
            );
            add("jam_" + name + "_" + i,
                new JamPotRecipe(
                    new PotRecipe(
                        inputs,
                        SizedFluidIngredient.of(Fluids.WATER, 100),
                        500,
                        300
                    ),
                    new ItemStack(FLItems.FL_UNSEALED_FRUIT_PRESERVES.get(fruit), i),
                    new ItemStack(FLItems.FL_FRUIT_PRESERVES.get(fruit), i),
                    FLHelpers.identifier("block/jar/" + name)
                )
            );
        }
        // Jam canning, copied from TFC and modified
        for (int n = 1; n <= 5; n++)
        {
            final Ingredient ingredient = notRottenWithoutTrait(Ingredient.of(FLItems.FL_FRUIT_PRESERVES.get(fruit)), FoodTraits.CANNED);
            add("jam_" + name + "_canning_" + n, new SimplePotRecipe(new PotRecipe(
                Collections.nCopies(n, ingredient),
                SizedFluidIngredient.of(Fluids.WATER, 100 * n),
                300, 300f),
                FluidStack.EMPTY,
                Collections.nCopies(n, ItemStackProvider.of(CopyInputModifier.INSTANCE, AddTraitModifier.of(FoodTraits.CANNED))),
                false
            ));
        }
    }

    private void pot(List<Ingredient> input, SizedFluidIngredient fluidInput, List<ItemLike> output, int duration, int time)
    {
        pot(input, fluidInput, FluidStack.EMPTY, output.stream().map(ItemStackProvider::of).toList(), duration, time);
    }

    private void pot(List<Ingredient> input, SizedFluidIngredient fluidInput, FluidStack output, int duration, int time)
    {
        pot(input, fluidInput, output, List.of(), duration, time);
    }

    private void pot(List<Ingredient> input, SizedFluidIngredient fluidInput, FluidStack fluidOutput, List<ItemStackProvider> output, int duration, int time)
    {
        String name;
        if (output.isEmpty())
        {
            name = nameOf(fluidOutput.getFluid());
        }
        else
        {
            name = nameOf(output.getFirst().getEmptyStack().getItem()) + "_" + output.size();
        }
        add(name, new SimplePotRecipe(
            new PotRecipe(
                input,
                fluidInput,
                duration,
                time
            ),
            fluidOutput,
            output,
            false
        ));
    }
}
