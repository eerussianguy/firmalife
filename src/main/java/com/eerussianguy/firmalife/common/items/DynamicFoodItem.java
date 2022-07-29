package com.eerussianguy.firmalife.common.items;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import net.dries007.tfc.common.capabilities.food.FoodCapability;
import net.dries007.tfc.common.items.SandwichItem;

public class DynamicFoodItem extends SandwichItem
{
    public DynamicFoodItem(Properties properties)
    {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand)
    {
        ItemStack stack = player.getItemInHand(hand);
        return stack.getCapability(FoodCapability.CAPABILITY).map(food -> {
            if (food.getTraits().contains(FLFoodTraits.RAW))
            {
                return InteractionResultHolder.pass(stack);
            }
            return super.use(level, player, hand);
        }).orElse(super.use(level, player, hand));
    }
}
