package com.eerussianguy.firmalife.common.blockentities;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

import com.eerussianguy.firmalife.common.items.FLFoodTraits;
import net.dries007.tfc.common.capabilities.food.FoodTrait;

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
}
