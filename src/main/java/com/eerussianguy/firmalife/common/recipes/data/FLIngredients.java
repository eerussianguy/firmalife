package com.eerussianguy.firmalife.common.recipes.data;

import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.crafting.IIngredientSerializer;

import com.eerussianguy.firmalife.common.FLHelpers;

public class FLIngredients
{
    public static void init()
    {
        register("has_queen", HasQueenIngredient.Serializer.INSTANCE);
    }

    private static <T extends Ingredient> void register(String name, IIngredientSerializer<T> serializer)
    {
        CraftingHelper.register(FLHelpers.identifier(name), serializer);
    }
}
