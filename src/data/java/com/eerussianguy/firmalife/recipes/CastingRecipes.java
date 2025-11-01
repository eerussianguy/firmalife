package com.eerussianguy.firmalife.recipes;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import com.eerussianguy.firmalife.common.blocks.FLBlocks;
import com.eerussianguy.firmalife.common.blocks.FLFluids;
import com.eerussianguy.firmalife.common.blocks.greenhouse.Greenhouse;
import com.eerussianguy.firmalife.common.items.FLItems;
import com.eerussianguy.firmalife.common.util.FLMetal;
import net.minecraft.core.Holder;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.fluids.crafting.SizedFluidIngredient;

import net.dries007.tfc.common.blocks.DecorationBlockHolder;
import net.dries007.tfc.common.items.TFCItems;
import net.dries007.tfc.common.player.ChiselMode;
import net.dries007.tfc.common.recipes.CastingRecipe;
import net.dries007.tfc.common.recipes.ChiselRecipe;
import net.dries007.tfc.common.recipes.ingredients.BlockIngredient;
import net.dries007.tfc.common.recipes.outputs.ItemStackProvider;
import net.dries007.tfc.util.Metal;

public interface CastingRecipes extends Recipes
{
    default void castingRecipes() {
        for (var metal : FLMetal.values()) {
            casting(
                TFCItems.MOLDS.get(Metal.ItemType.INGOT),
                SizedFluidIngredient.of(fluidOf(metal), 100),
                ItemStackProvider.of(FLItems.METAL_ITEMS.get(metal).get(FLMetal.ItemType.INGOT)),
                0.1f
            );
            casting(
                TFCItems.FIRE_INGOT_MOLD,
                SizedFluidIngredient.of(fluidOf(metal), 100),
                ItemStackProvider.of(FLItems.METAL_ITEMS.get(metal).get(FLMetal.ItemType.INGOT)),
                0.01f
            );
        }
    }

    private void casting(ItemLike mold, SizedFluidIngredient fluid, ItemStackProvider result, float breakChance) {
        add(new CastingRecipe(Ingredient.of(mold), fluid, result, breakChance));
    }

}
