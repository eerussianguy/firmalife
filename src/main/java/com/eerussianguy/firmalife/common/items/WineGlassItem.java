package com.eerussianguy.firmalife.common.items;

import java.util.function.Supplier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;

import net.dries007.tfc.common.items.JugItem;

public class WineGlassItem extends JugItem
{
    public WineGlassItem(Properties properties, Supplier<Integer> capacity, TagKey<Fluid> whitelist)
    {
        super(properties, capacity, whitelist);
    }

    @Override
    protected InteractionResultHolder<ItemStack> afterFillFailed(IFluidHandler handler, Level level, Player player, ItemStack stack, InteractionHand hand)
    {
        return InteractionResultHolder.pass(stack);
    }
}
