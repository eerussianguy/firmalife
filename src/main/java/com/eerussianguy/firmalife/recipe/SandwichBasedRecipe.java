package com.eerussianguy.firmalife.recipe;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nonnull;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.crafting.CraftingHelper;

import net.dries007.tfc.api.capability.food.CapabilityFood;
import net.dries007.tfc.api.capability.food.FoodData;
import net.dries007.tfc.api.capability.food.IFood;
import net.dries007.tfc.objects.recipes.ShapedDamageRecipe;

public abstract class SandwichBasedRecipe extends ShapedDamageRecipe
{
    public SandwichBasedRecipe(ResourceLocation group, CraftingHelper.ShapedPrimer input, @Nonnull ItemStack result, int damage)
    {
        super(group, input, result, damage);
    }

    @Override
    public boolean matches(@Nonnull InventoryCrafting inv, @Nonnull World world)
    {
        if (super.matches(inv, world))
        {
            List<FoodData> ingredients = new ArrayList<>();
            getIngredients(inv, ingredients);
            return ingredients.size() > 0;
        }
        return false;
    }

    protected void getIngredients(InventoryCrafting inv, List<FoodData> ingredients)
    {
        for (int i = 0; i < inv.getSizeInventory(); i++)
        {
            ItemStack ingredientStack = inv.getStackInSlot(i);
            IFood ingredientCap = ingredientStack.getCapability(CapabilityFood.CAPABILITY, null);
            if (ingredientCap != null)
            {
                if (ingredientCap.isRotten())
                {
                    // Found a rotten ingredient, aborting
                    ingredients.clear();
                    return;
                }
                ingredients.add(ingredientCap.getData());
            }
        }
    }
}
