package com.eerussianguy.firmalife.common.items;

import java.util.function.Supplier;

import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.TagKey;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.capability.IFluidHandler;

import com.eerussianguy.firmalife.common.misc.FLSounds;
import net.dries007.tfc.common.items.DiscreteFluidContainerItem;

public class HollowShellItem extends DiscreteFluidContainerItem
{
    public HollowShellItem(Properties properties, Supplier<Integer> capacity, TagKey<Fluid> whitelist, boolean canPlaceSourceBlocks)
    {
        super(properties, capacity, whitelist, canPlaceSourceBlocks);
    }

    @Override
    protected InteractionResultHolder<ItemStack> afterFillFailed(IFluidHandler handler, Level level, Player player, ItemStack stack, InteractionHand hand)
    {
        level.playSound(player, player.blockPosition(), FLSounds.HOLLOW_SHELL_BLOW.get(), SoundSource.PLAYERS, 1.0F, 0.8F + (float)(player.getLookAngle().y / 2.0));
        return InteractionResultHolder.success(stack);
    }
}
