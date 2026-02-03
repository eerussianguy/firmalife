package com.eerussianguy.firmalife.recipes;

import com.eerussianguy.firmalife.common.FLTags;
import com.eerussianguy.firmalife.common.blocks.FLBlocks;
import com.eerussianguy.firmalife.common.blocks.FLFluids;
import com.eerussianguy.firmalife.common.items.FLFood;
import com.eerussianguy.firmalife.common.items.FLItems;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.fluids.FluidStack;

import net.dries007.tfc.common.items.Food;
import net.dries007.tfc.common.recipes.HeatingRecipe;
import net.dries007.tfc.common.recipes.outputs.ItemStackProvider;
import net.dries007.tfc.util.Metal;

public interface HeatingRecipes extends Recipes
{
    default void heatingRecipes()
    {
        cookFood(FLFood.WHEAT_DOUGH, Food.WHEAT_BREAD);
        cookFood(FLFood.RYE_DOUGH, Food.RYE_BREAD);
        cookFood(FLFood.BARLEY_DOUGH, Food.BARLEY_BREAD);
        cookFood(FLFood.RICE_DOUGH, Food.RICE_BREAD);
        cookFood(FLFood.MAIZE_DOUGH, Food.MAIZE_BREAD);
        cookFood(FLFood.OAT_DOUGH, Food.OAT_BREAD);

        cookFood(Food.WHEAT_DOUGH, FLFood.WHEAT_FLATBREAD);
        cookFood(Food.RYE_DOUGH, FLFood.RYE_FLATBREAD);
        cookFood(Food.BARLEY_DOUGH, FLFood.BARLEY_FLATBREAD);
        cookFood(Food.RICE_DOUGH, FLFood.RICE_FLATBREAD);
        cookFood(Food.MAIZE_DOUGH, FLFood.MAIZE_FLATBREAD);
        cookFood(Food.OAT_DOUGH, FLFood.OAT_FLATBREAD);

        remove("heating/food/wheat_bread");
        remove("heating/food/rye_bread");
        remove("heating/food/barley_bread");
        remove("heating/food/rice_bread");
        remove("heating/food/maize_bread");
        remove("heating/food/oat_bread");

        cookFood(FLFood.MASA, FLFood.CORN_TORTILLA);
        cookFood(FLFood.BACON, FLFood.COOKED_BACON);
        cookFood(Ingredient.of(FLTags.Items.BREAD_SLICES), FLFood.TOAST);

        // Misc metal items
        heat(
            FLBlocks.COPPER_PIPE,
            new FluidStack(fluidOf(Metal.COPPER), 25),
            1080
        );
        heat(
            FLBlocks.OXIDIZED_COPPER_PIPE,
            new FluidStack(fluidOf(Metal.COPPER), 25),
            1080
        );

        // Metal blocks & items
        FLItems.METAL_ITEMS.forEach((metal, items) -> items.forEach((type, item) ->
            heat(
                ingredientOf(metal, type),
                new FluidStack(FLFluids.METALS.get(metal).getSource(), units(type)),
                temperatureOf(metal)
            )
        ));
        FLBlocks.METALS.forEach((metal, blocks) -> blocks.forEach((type, block) ->
            heat(
                ingredientOf(metal, type),
                new FluidStack(FLFluids.METALS.get(metal).getSource(), units(type)),
                temperatureOf(metal)
            )
        ));
    }

    private void cookFood(Ingredient input, FLFood output)
    {
        heat(
            nameOf(input),
            notRotten(input),
            copyFood(itemOf(output)),
            FluidStack.EMPTY,
            200
        );
    }

    private void cookFood(FLFood input, FLFood output)
    {
        heat(
            nameOf(itemOf(input)),
            notRotten(itemOf(input)),
            copyFood(itemOf(output)),
            FluidStack.EMPTY,
            200
        );
    }

    private void cookFood(Food input, FLFood output)
    {
        heat(
            nameOf(itemOf(input)) + "_tfc",
            notRotten(itemOf(input)),
            copyFood(itemOf(output)),
            FluidStack.EMPTY,
            200
        );
    }


    private void cookFood(FLFood input, Food output)
    {
        heat(
            nameOf(itemOf(input)),
            notRotten(itemOf(input)),
            copyFood(itemOf(output)),
            FluidStack.EMPTY,
            200
        );
    }

    private void heat(ItemLike input, FluidStack output, float temperature)
    {
        heat(nameOf(input), Ingredient.of(input), ItemStackProvider.empty(), output, temperature);
    }

    private void heat(Ingredient input, FluidStack output, float temperature)
    {
        heat(nameOf(input), input, ItemStackProvider.empty(), output, temperature);
    }

    private void heat(ItemLike input, ItemStackProvider output, float temperature)
    {
        heat(nameOf(input), Ingredient.of(input), output, FluidStack.EMPTY, temperature);
    }

    private void heat(Ingredient input, ItemStackProvider output, float temperature)
    {
        heat(nameOf(input), input, output, FluidStack.EMPTY, temperature);
    }

    private void heat(String name, Ingredient input, ItemStackProvider outputItem, FluidStack outputFluid, float temperature)
    {
        add(name, new HeatingRecipe(input, outputItem, outputFluid, temperature, false));
    }
}
