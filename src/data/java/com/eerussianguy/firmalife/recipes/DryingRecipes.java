package com.eerussianguy.firmalife.recipes;

import com.eerussianguy.firmalife.common.blocks.Herb;
import com.eerussianguy.firmalife.common.items.FLFood;
import com.eerussianguy.firmalife.common.items.FLFoodTraits;
import com.eerussianguy.firmalife.common.items.FLItems;
import com.eerussianguy.firmalife.common.items.Spice;
import com.eerussianguy.firmalife.common.recipes.DryingRecipe;
import com.eerussianguy.firmalife.common.util.FLFruit;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;

import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.blocks.TFCBlocks;
import net.dries007.tfc.common.blocks.soil.SoilBlockType;
import net.dries007.tfc.common.items.Food;
import net.dries007.tfc.common.items.TFCItems;
import net.dries007.tfc.common.recipes.QuernRecipe;
import net.dries007.tfc.common.recipes.outputs.AddTraitModifier;
import net.dries007.tfc.common.recipes.outputs.CopyInputModifier;
import net.dries007.tfc.common.recipes.outputs.ItemStackProvider;

public interface DryingRecipes extends Recipes
{
    default void dryingRecipes()
    {
        drying(
            notRotten(lacksTrait(Ingredient.of(TFCTags.Items.FRUITS), FLFoodTraits.DRIED)),
            ItemStackProvider.of(CopyInputModifier.INSTANCE, AddTraitModifier.of(FLFoodTraits.DRIED))
        );
        drying(FLItems.CINNAMON_BARK, itemOf(Spice.CINNAMON));
        //TODO no dead grass groundcover? whats a good replacement?
        //drying(TFCBlocks.THATCH, ItemStackProvider.of(GROUNDCOVER/DEAD_GRASS));
        drying(itemOf(FLFood.SOY_MIXTURE), copyFood(itemOf(FLFood.TOFU)));
        drying(itemOf(Herb.VANILLA), ItemStackProvider.of(itemOf(Spice.VANILLA)));
        drying(notRotten(itemOf(Food.SOYBEAN)), copyFood(itemOf(FLFood.DEHYDRATED_SOYBEANS)));
        drying(itemOf(FLFood.MILK_CHOCOLATE_BLEND), ItemStackProvider.of(itemOf(FLFood.MILK_CHOCOLATE)));
        drying(itemOf(FLFood.WHITE_CHOCOLATE_BLEND), ItemStackProvider.of(itemOf(FLFood.WHITE_CHOCOLATE)));
        drying(itemOf(FLFood.DARK_CHOCOLATE_BLEND), ItemStackProvider.of(itemOf(FLFood.DARK_CHOCOLATE)));
        for (var type : SoilBlockType.Variant.values()) {
            drying(Ingredient.of(type.getBlock(SoilBlockType.MUD).get()), ItemStackProvider.of(type.getBlock(SoilBlockType.DIRT).get()));
        }
    }
    private void drying(ItemLike input, ItemStackProvider output) {
        drying(Ingredient.of(input), output);
    }

    private void drying(ItemLike input, ItemLike output) {
        drying(Ingredient.of(input), ItemStackProvider.of(output));
    }

    private void drying(Ingredient input, ItemLike output) {
        drying(input, ItemStackProvider.of(output));
    }

    private void drying(Ingredient input, ItemStackProvider output) {
        add(new DryingRecipe(input, output));
    }
}
