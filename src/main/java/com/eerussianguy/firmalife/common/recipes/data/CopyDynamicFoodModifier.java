package com.eerussianguy.firmalife.common.recipes.data;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import net.dries007.tfc.common.component.Bowl;
import net.dries007.tfc.common.component.TFCComponents;
import net.dries007.tfc.common.component.food.FoodCapability;
import net.dries007.tfc.common.component.food.IFood;
import net.dries007.tfc.common.component.item.ItemComponent;
import net.dries007.tfc.common.recipes.outputs.ItemStackModifier;
import net.dries007.tfc.common.recipes.outputs.ItemStackModifierType;

//TODO is this even needed? use a combination of CopyBowlModifier & a modifier for copying creation date?
public enum CopyDynamicFoodModifier implements ItemStackModifier
{
    INSTANCE;

    @Override
    public ItemStack apply(ItemStack stack, ItemStack input, Context context)
    {
        IFood inputFood = FoodCapability.get(input);
        ItemComponent inputBowl = input.get(TFCComponents.BOWL);

        if (inputFood != null)
        {
            FoodCapability.setFoodForDynamicItemOnCreate(stack, inputFood.getData());
            FoodCapability.setCreationDate(stack, inputFood.getCreationDate());
        }
        if (inputBowl != null)
        {
            if (!inputBowl.stack().isEmpty())
            {
                stack.set(TFCComponents.BOWL, inputBowl);
            }
            else
            {
                stack.set(TFCComponents.BOWL, Bowl.of(new ItemStack(Items.BOWL)));
            }
        }
        return stack;
    }

    @Override
    public ItemStackModifierType<?> type()
    {
        return FLItemStackModifiers.COPY_DYNAMIC_FOOD.get();
    }
}
