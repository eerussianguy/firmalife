package com.eerussianguy.firmalife.recipes;

import com.eerussianguy.firmalife.common.blocks.Herb;
import com.eerussianguy.firmalife.common.items.FLFood;
import com.eerussianguy.firmalife.common.items.FLFoodTraits;
import com.eerussianguy.firmalife.common.items.FLItems;
import com.eerussianguy.firmalife.common.items.Spice;
import com.eerussianguy.firmalife.common.recipes.DryingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;

import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.blocks.soil.SoilBlockType;
import net.dries007.tfc.common.items.Food;
import net.dries007.tfc.common.recipes.outputs.AddTraitModifier;
import net.dries007.tfc.common.recipes.outputs.CopyInputModifier;
import net.dries007.tfc.common.recipes.outputs.ItemStackProvider;

public interface DryingRecipes extends Recipes
{
    default void dryingRecipes()
    {
        drying(
            "dry_fruits",
            notRottenWithoutTrait(TFCTags.Items.FRUITS, FLFoodTraits.DRIED),
            ItemStackProvider.of(CopyInputModifier.INSTANCE, AddTraitModifier.of(FLFoodTraits.DRIED))
        );
        drying(FLItems.CINNAMON_BARK, itemOf(Spice.CINNAMON));
        dryingCopyFood(itemOf(FLFood.SOY_MIXTURE), itemOf(FLFood.TOFU));
        drying(itemOf(Herb.VANILLA), itemOf(Spice.VANILLA));
        dryingCopyFood(notRotten(itemOf(Food.SOYBEAN)), itemOf(FLFood.DEHYDRATED_SOYBEANS));
        drying(itemOf(FLFood.MILK_CHOCOLATE_BLEND), itemOf(FLFood.MILK_CHOCOLATE));
        drying(itemOf(FLFood.WHITE_CHOCOLATE_BLEND), itemOf(FLFood.WHITE_CHOCOLATE));
        drying(itemOf(FLFood.DARK_CHOCOLATE_BLEND), itemOf(FLFood.DARK_CHOCOLATE));
        for (var type : SoilBlockType.Variant.values())
        {
            drying(Ingredient.of(type.getBlock(SoilBlockType.MUD).get()), type.getBlock(SoilBlockType.DIRT).get());
        }
    }

    private void dryingCopyFood(ItemLike input, ItemLike output)
    {
        drying(nameOf(output), Ingredient.of(input), copyFood(output));
    }

    private void dryingCopyFood(Ingredient input, ItemLike output)
    {
        drying(nameOf(output), input, copyFood(output));
    }

    private void drying(ItemLike input, ItemLike output)
    {
        drying(nameOf(output), Ingredient.of(input), ItemStackProvider.of(output));
    }

    private void drying(Ingredient input, ItemLike output)
    {
        drying(null, input, ItemStackProvider.of(output));
    }

    private void drying(String name, Ingredient input, ItemStackProvider output)
    {
        if (name != null)
        {
            add(name, new DryingRecipe(input, output));
        }
        else
        {
            add(new DryingRecipe(input, output));
        }
    }
}
