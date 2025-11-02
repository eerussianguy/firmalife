package com.eerussianguy.firmalife.recipes;

import com.eerussianguy.firmalife.common.blocks.FLBlocks;
import net.minecraft.world.level.block.state.BlockState;

import net.dries007.tfc.common.blocks.TFCBlocks;
import net.dries007.tfc.common.blocks.rock.Rock;
import net.dries007.tfc.common.recipes.CollapseRecipe;
import net.dries007.tfc.common.recipes.ingredients.BlockIngredient;
import net.dries007.tfc.util.registry.RegistryHolder;

public interface CollapseRecipes extends Recipes
{
    default void collapseRecipes()
    {
        FLBlocks.CHROMITE_ORES.forEach((rock, entry) ->
            collapse(
                BlockIngredient.of(entry.values().stream().map(RegistryHolder::get)),
                TFCBlocks.ROCK_BLOCKS.get(rock).get(Rock.BlockType.COBBLE).get().defaultBlockState()
            )
        );
    }

    private void collapse(BlockIngredient ingredient, BlockState output)
    {
        add(new CollapseRecipe(ingredient, output));
    }
}
