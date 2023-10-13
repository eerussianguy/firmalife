package com.eerussianguy.firmalife.common.recipes.data;

import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;

import com.eerussianguy.firmalife.common.FLTags;
import com.eerussianguy.firmalife.common.items.FLFoodTraits;

import net.dries007.tfc.common.capabilities.food.DynamicBowlHandler;
import net.dries007.tfc.common.capabilities.food.FoodCapability;
import net.dries007.tfc.common.recipes.RecipeHelpers;
import net.dries007.tfc.util.Helpers;

public enum PieModifier implements CustomFoodModifier<PieModifier>
{
    INSTANCE;

    @Override
    public ItemStack apply(ItemStack stack, ItemStack input)
    {
        ItemStack ret = CustomFoodModifier.super.apply(stack, input);
        FoodCapability.applyTrait(ret, FLFoodTraits.RAW);

        CraftingContainer inv = RecipeHelpers.getCraftingContainer();
        if (inv != null)
        {
            for (int i = 0; i < inv.getContainerSize(); i++)
            {
                ItemStack item = inv.getItem(i);
                if (Helpers.isItem(item, FLTags.Items.PIE_PANS))
                {
                    ret.getCapability(FoodCapability.CAPABILITY).ifPresent(cap -> {
                        if (cap instanceof DynamicBowlHandler handler)
                        {
                            handler.setBowl(item);
                        }
                    });
                }
            }
        }
        return ret;
    }

    @Override
    public float water()
    {
        return 0.5f;
    }

    @Override
    public float[] nutrients(float[] input)
    {
        input[G] = 1f;
        input[D] = 0.5f;
        input[F] = 1.5f;
        return input;
    }

    @Override
    public PieModifier instance()
    {
        return INSTANCE;
    }
}
