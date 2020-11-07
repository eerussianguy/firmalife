package com.eerussianguy.firmalife.items;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.event.world.BlockEvent;

import net.dries007.tfc.api.capability.food.FoodData;
import net.dries007.tfc.api.capability.food.FoodHandler;
import net.dries007.tfc.api.capability.food.IItemFoodTFC;
import net.dries007.tfc.objects.blocks.BlockPlacedItem;
import net.dries007.tfc.objects.blocks.BlockPlacedItemFlat;
import net.dries007.tfc.objects.blocks.BlocksTFC;
import net.dries007.tfc.objects.te.TEPlacedItemFlat;

public class ItemFoodFL extends ItemFood implements IItemFoodTFC
{
    public FoodData data;

    public ItemFoodFL(FoodData data)
    {
        super(0, 0.0F, false);
        this.setMaxDamage(0);
        this.data = data;
    }

    @Override
    public ICapabilityProvider getCustomFoodHandler()
    {
        return new FoodHandler(null, data);
    }

    @Override
    public EnumActionResult onItemUse(EntityPlayer player, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
    {
        if(!worldIn.isRemote && hand == EnumHand.MAIN_HAND && worldIn.isAirBlock(pos.offset(EnumFacing.UP)) && worldIn.getBlockState(pos).isFullBlock())
        {
            worldIn.setBlockState(pos.offset(EnumFacing.UP), BlocksTFC.PLACED_ITEM_FLAT.getDefaultState(), 2);
            TEPlacedItemFlat te = (TEPlacedItemFlat) worldIn.getTileEntity(pos.offset(EnumFacing.UP));
            te.setStack(player.getHeldItem(hand).splitStack(1));

            return EnumActionResult.SUCCESS;
        }
        return EnumActionResult.FAIL;
    }
}
