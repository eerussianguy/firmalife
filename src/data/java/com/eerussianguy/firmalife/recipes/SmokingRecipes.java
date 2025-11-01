package com.eerussianguy.firmalife.recipes;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import com.eerussianguy.firmalife.common.FLTags;
import com.eerussianguy.firmalife.common.blocks.FLBlocks;
import com.eerussianguy.firmalife.common.blocks.greenhouse.Greenhouse;
import com.eerussianguy.firmalife.common.items.FLFoodTraits;
import com.eerussianguy.firmalife.common.recipes.SmokingRecipe;
import net.minecraft.core.Holder;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Block;

import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.blocks.DecorationBlockHolder;
import net.dries007.tfc.common.component.food.FoodTraits;
import net.dries007.tfc.common.player.ChiselMode;
import net.dries007.tfc.common.recipes.ChiselRecipe;
import net.dries007.tfc.common.recipes.ingredients.BlockIngredient;
import net.dries007.tfc.common.recipes.outputs.AddTraitModifier;
import net.dries007.tfc.common.recipes.outputs.CopyInputModifier;
import net.dries007.tfc.common.recipes.outputs.ItemStackProvider;

public interface SmokingRecipes extends Recipes
{
    default void smokingRecipes() {
        smoke(
            notRotten(hasTrait(lacksTrait(Ingredient.of(TFCTags.Items.RAW_MEATS), FLFoodTraits.SMOKED), FoodTraits.BRINED)),
            ItemStackProvider.of(CopyInputModifier.INSTANCE, AddTraitModifier.of(FLFoodTraits.SMOKED))
        );
        smoke(
            notRotten(lacksTrait(Ingredient.of(FLTags.Items.CHEESES), FLFoodTraits.SMOKED)),
            ItemStackProvider.of(CopyInputModifier.INSTANCE, AddTraitModifier.of(FLFoodTraits.SMOKED))
        );
    }

    private void smoke(Ingredient input, ItemStackProvider output) {
        add(new SmokingRecipe(input, output));
    }
}
