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
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.Tags;

import net.dries007.tfc.common.blocks.DecorationBlockHolder;
import net.dries007.tfc.common.component.forge.ForgeRule;
import net.dries007.tfc.common.player.ChiselMode;
import net.dries007.tfc.common.recipes.AnvilRecipe;
import net.dries007.tfc.common.recipes.ChiselRecipe;
import net.dries007.tfc.common.recipes.ingredients.BlockIngredient;
import net.dries007.tfc.common.recipes.outputs.ItemStackProvider;
import net.dries007.tfc.util.Metal;

public interface AnvilRecipes extends Recipes
{
    default void anvilRecipes() {
        // TODO move this? make somewhere that has metal tiers and use that?
        Map<FLMetal, Integer> metalTiers = new HashMap<>();
        metalTiers.put(FLMetal.STAINLESS_STEEL, 4);
        metalTiers.put(FLMetal.CHROMIUM, 4);

        var stainlessSteelIngots = commonTagOf(Registries.ITEM, "ingots/stainless_steel");
        var copperSheets = commonTagOf(Registries.ITEM, "sheets/copper");
        var castIronSheets = commonTagOf(Registries.ITEM, "sheets/cast_iron");

        anvil(
            castIronSheets,
            1,
            List.of(
                ForgeRule.HIT_LAST,
                ForgeRule.HIT_SECOND_LAST,
                ForgeRule.DRAW_THIRD_LAST
            ),
            ItemStackProvider.of(FLItems.PIE_PAN, 4)
        );
        anvil(
            copperSheets,
            1,
            List.of(
                ForgeRule.HIT_LAST,
                ForgeRule.HIT_SECOND_LAST,
                ForgeRule.PUNCH_THIRD_LAST
            ),
            ItemStackProvider.of(FLItems.SPRINKLER)
        );
        anvil(
            copperSheets,
            1,
            List.of(
                ForgeRule.DRAW_LAST,
                ForgeRule.BEND_NOT_LAST
            ),
            ItemStackProvider.of(FLBlocks.COPPER_PIPE, 8)
        );
        anvil(
            stainlessSteelIngots,
            4,
            List.of(
                ForgeRule.HIT_LAST,
                ForgeRule.HIT_SECOND_LAST,
                ForgeRule.PUNCH_THIRD_LAST
            ),
            ItemStackProvider.of(FLItems.STAINLESS_STEEL_JAR_LID, 16)
        );

        for(var metal : FLMetal.values()) {
            anvil(
                FLItems.METAL_ITEMS.get(metal).get(FLMetal.ItemType.DOUBLE_INGOT),
                metalTiers.get(metal),
                List.of(
                    ForgeRule.HIT_LAST,
                    ForgeRule.HIT_SECOND_LAST,
                    ForgeRule.HIT_THIRD_LAST
                ),
                FLItems.METAL_ITEMS.get(metal).get(FLMetal.ItemType.SHEET)
            );
            anvil(
                FLItems.METAL_ITEMS.get(metal).get(FLMetal.ItemType.INGOT),
                metalTiers.get(metal),
                List.of(
                    ForgeRule.BEND_LAST,
                    ForgeRule.DRAW_SECOND_LAST,
                    ForgeRule.DRAW_THIRD_LAST
                ),
                ItemStackProvider.of(FLItems.METAL_ITEMS.get(metal).get(FLMetal.ItemType.ROD), 2)
            );
        }
    }

    private void anvil(TagKey<Item> input, int tier, List<ForgeRule> rules, ItemStackProvider output) {
        anvil(Ingredient.of(input), tier, rules, output);
    }
    private void anvil(ItemLike input, int tier, List<ForgeRule> rules, ItemLike output) {
        anvil(Ingredient.of(input), tier, rules, ItemStackProvider.of(output));
    }
    private void anvil(ItemLike input, int tier, List<ForgeRule> rules, ItemStackProvider output) {
        anvil(Ingredient.of(input), tier, rules, output);
    }
    private void anvil(Ingredient input, int tier, List<ForgeRule> rules, ItemStackProvider output) {
        add(new AnvilRecipe(input, tier, rules, false, output));
    }
}
