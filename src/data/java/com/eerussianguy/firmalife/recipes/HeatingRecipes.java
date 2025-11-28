package com.eerussianguy.firmalife.recipes;

import com.eerussianguy.firmalife.common.FLTags;
import com.eerussianguy.firmalife.common.blocks.FLBlocks;
import com.eerussianguy.firmalife.common.blocks.FLFluids;
import com.eerussianguy.firmalife.common.items.FLFood;
import com.eerussianguy.firmalife.common.items.FLItems;
import com.eerussianguy.firmalife.common.util.FLMetal;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.fluids.FluidStack;

import net.dries007.tfc.common.blocks.rock.Ore;
import net.dries007.tfc.common.recipes.HeatingRecipe;
import net.dries007.tfc.common.recipes.outputs.ItemStackProvider;
import net.dries007.tfc.util.Metal;

public interface HeatingRecipes extends Recipes
{
    default void heatingRecipes()
    {
        cookFood(FLFood.WHEAT_DOUGH, FLFood.WHEAT_FLATBREAD);
        cookFood(FLFood.RYE_DOUGH, FLFood.RYE_FLATBREAD);
        cookFood(FLFood.BARLEY_DOUGH, FLFood.BARLEY_FLATBREAD);
        cookFood(FLFood.RICE_DOUGH, FLFood.RICE_FLATBREAD);
        cookFood(FLFood.MAIZE_DOUGH, FLFood.MAIZE_FLATBREAD);
        cookFood(FLFood.OAT_DOUGH, FLFood.OAT_FLATBREAD);
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
