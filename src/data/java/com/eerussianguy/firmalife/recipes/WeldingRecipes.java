package com.eerussianguy.firmalife.recipes;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import com.eerussianguy.firmalife.common.blocks.FLBlocks;
import com.eerussianguy.firmalife.common.blocks.greenhouse.Greenhouse;
import com.eerussianguy.firmalife.common.items.FLItems;
import com.eerussianguy.firmalife.common.util.FLMetal;
import net.minecraft.core.Holder;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;

import net.dries007.tfc.common.blocks.DecorationBlockHolder;
import net.dries007.tfc.common.items.TFCItems;
import net.dries007.tfc.common.player.ChiselMode;
import net.dries007.tfc.common.recipes.ChiselRecipe;
import net.dries007.tfc.common.recipes.WeldingRecipe;
import net.dries007.tfc.common.recipes.ingredients.BlockIngredient;
import net.dries007.tfc.common.recipes.outputs.ItemStackProvider;

public interface WeldingRecipes extends Recipes
{
    default void weldingRecipes() {
        // TODO move this? make somewhere that has metal tiers and not welding tiers and use that?
        Map<FLMetal, Integer> metalTiers = new HashMap<>();
        metalTiers.put(FLMetal.STAINLESS_STEEL, 3);
        metalTiers.put(FLMetal.CHROMIUM, 3);

        for(var metal : FLMetal.values()) {
            weld(
                FLItems.METAL_ITEMS.get(metal).get(FLMetal.ItemType.INGOT),
                FLItems.METAL_ITEMS.get(metal).get(FLMetal.ItemType.INGOT),
                FLItems.METAL_ITEMS.get(metal).get(FLMetal.ItemType.DOUBLE_INGOT),
                metalTiers.getOrDefault(metal, 0)
            );
            weld(
                FLItems.METAL_ITEMS.get(metal).get(FLMetal.ItemType.SHEET),
                FLItems.METAL_ITEMS.get(metal).get(FLMetal.ItemType.SHEET),
                FLItems.METAL_ITEMS.get(metal).get(FLMetal.ItemType.DOUBLE_SHEET),
                metalTiers.getOrDefault(metal, 0)
            );
        }
    }

    private void weld(ItemLike first, ItemLike second, ItemLike result, int tier) {
        add(new WeldingRecipe(Ingredient.of(first), Ingredient.of(second), tier, ItemStackProvider.of(result), WeldingRecipe.Behavior.IGNORE));
    }

}
