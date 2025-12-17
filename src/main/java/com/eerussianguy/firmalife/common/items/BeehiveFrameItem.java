package com.eerussianguy.firmalife.common.items;

import java.util.List;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.neoforged.neoforge.items.ItemHandlerHelper;

import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.util.Helpers;

public class BeehiveFrameItem extends Item
{
    public BeehiveFrameItem(Properties properties)
    {
        super(properties);
    }

    @Override
    public boolean overrideOtherStackedOnMe(ItemStack stack, ItemStack other, Slot slot, ClickAction action, Player player, SlotAccess carried)
    {
        if (action == ClickAction.SECONDARY && Helpers.isItem(other, TFCTags.Items.TOOLS_KNIFE))
        {
            slot.set(new ItemStack(FLItems.BEEHIVE_FRAME));
            ItemHandlerHelper.giveItemToPlayer(player, new ItemStack(FLItems.BEESWAX.get()));
            if (player.level() instanceof ServerLevel server)
                other.hurtAndBreak(1, server, null, i -> {});
            return true;
        }
        return false;
    }


    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag advanced)
    {
        tooltip.add(Component.translatable("firmalife.bee.may_scrape").withStyle(ChatFormatting.GRAY).withStyle(ChatFormatting.ITALIC));
    }

}
