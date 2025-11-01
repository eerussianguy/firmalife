package com.eerussianguy.firmalife.recipes;

import com.eerussianguy.firmalife.common.blocks.FLBlocks;
import com.eerussianguy.firmalife.common.items.FLItems;
import com.eerussianguy.firmalife.common.util.FLMetal;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.fluids.crafting.SizedFluidIngredient;

import net.dries007.tfc.common.blocks.TFCBlocks;
import net.dries007.tfc.common.blocks.rock.Rock;
import net.dries007.tfc.common.items.TFCItems;
import net.dries007.tfc.common.recipes.CastingRecipe;
import net.dries007.tfc.common.recipes.CollapseRecipe;
import net.dries007.tfc.common.recipes.ingredients.BlockIngredient;
import net.dries007.tfc.common.recipes.outputs.ItemStackProvider;
import net.dries007.tfc.util.Metal;
import net.dries007.tfc.util.registry.RegistryHolder;

public interface CollapseRecipes extends Recipes
{
    default void collapseRecipes() {
        FLBlocks.CHROMITE_ORES.forEach((rock, entry)->
            collapse(
                BlockIngredient.of(entry.values().stream().map(RegistryHolder::get)),
                TFCBlocks.ROCK_BLOCKS.get(rock).get(Rock.BlockType.COBBLE).get().defaultBlockState()
            )
        );
    }

    private void collapse(BlockIngredient ingredient, BlockState output) {
        add(new CollapseRecipe(ingredient, output));
    }
}
