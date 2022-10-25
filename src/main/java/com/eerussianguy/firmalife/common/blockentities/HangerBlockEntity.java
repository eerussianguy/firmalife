package com.eerussianguy.firmalife.common.blockentities;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

import com.eerussianguy.firmalife.common.FLTags;
import com.eerussianguy.firmalife.common.items.FLFoodTraits;
import net.dries007.tfc.common.capabilities.food.FoodTrait;
import net.dries007.tfc.util.Helpers;

public class HangerBlockEntity extends FoodShelfBlockEntity
{
    public HangerBlockEntity(BlockPos pos, BlockState state)
    {
        super(FLBlockEntities.HANGER.get(), pos, state);
    }

    @Override
    public FoodTrait getFoodTrait()
    {
        return FLFoodTraits.HUNG;
    }

    @Override
    public boolean isItemValid(int slot, ItemStack stack)
    {
        return super.isItemValid(slot, stack) && Helpers.isItem(stack, FLTags.Items.CAN_BE_HUNG);
    }
}
