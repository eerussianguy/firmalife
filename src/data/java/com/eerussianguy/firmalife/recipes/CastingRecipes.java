package com.eerussianguy.firmalife.recipes;

import com.eerussianguy.firmalife.common.items.FLItems;
import com.eerussianguy.firmalife.common.util.FLMetal;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.crafting.SizedFluidIngredient;

import net.dries007.tfc.common.items.TFCItems;
import net.dries007.tfc.common.recipes.CastingRecipe;
import net.dries007.tfc.common.recipes.outputs.ItemStackProvider;
import net.dries007.tfc.util.Metal;

public interface CastingRecipes extends Recipes
{
    default void castingRecipes()
    {
        for (var metal : FLMetal.values())
        {
            casting(
                TFCItems.MOLDS.get(Metal.ItemType.INGOT),
                new FluidStack(fluidOf(metal), 100),
                ItemStackProvider.of(FLItems.METAL_ITEMS.get(metal).get(FLMetal.ItemType.INGOT)),
                0.1f
            );
            casting(
                TFCItems.FIRE_INGOT_MOLD,
                new FluidStack(fluidOf(metal), 100),
                ItemStackProvider.of(FLItems.METAL_ITEMS.get(metal).get(FLMetal.ItemType.INGOT)),
                0.01f
            );
        }
    }

    private void casting(ItemLike mold, FluidStack fluid, ItemStackProvider result, float breakChance)
    {
        add(
            nameOf(mold) + "_" + nameOf(fluid.getFluid()),
            new CastingRecipe(
                Ingredient.of(mold),
                SizedFluidIngredient.of(fluid.getFluid(), fluid.getAmount()),
                result,
                breakChance
            )
        );
    }

}
