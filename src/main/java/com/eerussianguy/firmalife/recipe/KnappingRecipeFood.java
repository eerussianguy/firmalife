package com.eerussianguy.firmalife.recipe;

import net.minecraft.item.ItemStack;

import net.dries007.tfc.api.capability.food.CapabilityFood;
import net.dries007.tfc.api.recipes.knapping.KnappingRecipe;
import net.dries007.tfc.api.recipes.knapping.KnappingType;

public class KnappingRecipeFood extends KnappingRecipe
{
    private final ItemStack output;

    public KnappingRecipeFood(KnappingType type, boolean outsideSlotRequired, ItemStack output, String... pattern)
    {
        super(type, outsideSlotRequired, pattern);
        this.output = output;
    }

    @Override
    public ItemStack getOutput(ItemStack input)
    {
        ItemStack candidate = output.copy();
        return CapabilityFood.updateFoodFromPrevious(input, candidate);
    }
}
