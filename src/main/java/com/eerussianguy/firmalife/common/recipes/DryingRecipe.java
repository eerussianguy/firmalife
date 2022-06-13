package com.eerussianguy.firmalife.common.recipes;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

import net.dries007.tfc.common.recipes.SimpleItemRecipe;
import net.dries007.tfc.common.recipes.inventory.ItemStackInventory;
import net.dries007.tfc.common.recipes.outputs.ItemStackProvider;
import net.dries007.tfc.util.collections.IndirectHashCollection;
import org.jetbrains.annotations.Nullable;

public class DryingRecipe extends SimpleItemRecipe
{
    public static final IndirectHashCollection<Item, DryingRecipe> CACHE = IndirectHashCollection.createForRecipe(DryingRecipe::getValidItems, FLRecipeTypes.DRYING);

    public DryingRecipe(ResourceLocation id, Ingredient ingredient, ItemStackProvider result)
    {
        super(id, ingredient, result);
    }

    @Nullable
    public static DryingRecipe getRecipe(Level level, ItemStackInventory wrapper)
    {
        for (DryingRecipe recipe : CACHE.getAll(wrapper.getStack().getItem()))
        {
            if (recipe.matches(wrapper, level))
            {
                return recipe;
            }
        }
        return null;
    }

    @Override
    public RecipeSerializer<?> getSerializer()
    {
        return FLRecipeSerializers.DRYING.get();
    }

    @Override
    public RecipeType<?> getType()
    {
        return FLRecipeTypes.DRYING.get();
    }
}
