package com.eerussianguy.firmalife.common.blockentities;

import com.eerussianguy.firmalife.config.FLConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

import com.eerussianguy.firmalife.common.FLTags;
import com.eerussianguy.firmalife.common.items.FLFoodTraits;
import net.dries007.tfc.common.capabilities.food.FoodTrait;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.climate.Climate;

public class HangerBlockEntity extends FoodShelfBlockEntity
{
    private static final FoodTrait[] POSSIBLE = {FLFoodTraits.HUNG, FLFoodTraits.HUNG_2, FLFoodTraits.HUNG_3};

    public HangerBlockEntity(BlockPos pos, BlockState state)
    {
        super(FLBlockEntities.HANGER.get(), pos, state);
    }

    @Override
    public FoodTrait getFoodTrait()
    {
        if (level != null)
        {
            final float temp = Climate.getTemperature(level, getBlockPos());
            if (temp < FLConfig.SERVER.cellarLevel3Temperature.get())
            {
                return FLFoodTraits.HUNG_3;
            }
            if (temp < FLConfig.SERVER.cellarLevel2Temperature.get())
            {
                return FLFoodTraits.HUNG_2;
            }
        }
        return FLFoodTraits.HUNG;
    }

    @Override
    public boolean isItemValid(int slot, ItemStack stack)
    {
        return super.isItemValid(slot, stack) && Helpers.isItem(stack, FLTags.Items.CAN_BE_HUNG);
    }

    @Override
    public FoodTrait[] getPossibleTraits()
    {
        return POSSIBLE;
    }
}
