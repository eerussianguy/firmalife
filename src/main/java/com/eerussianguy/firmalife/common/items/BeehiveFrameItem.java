package com.eerussianguy.firmalife.common.items;

import java.util.List;

import com.eerussianguy.firmalife.common.capabilities.FLComponents;
import com.eerussianguy.firmalife.common.capabilities.bee.BeeComponent;
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
            final BeeComponent bee = stack.get(FLComponents.BEE.get());
            if (bee != null && bee.hasQueen())
            {
                slot.set(new ItemStack(this));
                ItemHandlerHelper.giveItemToPlayer(player, new ItemStack(FLItems.BEESWAX.get()));
                if (player.level() instanceof ServerLevel server)
                    other.hurtAndBreak(1, server, null, i -> {});
                return true;
            }
            return false;
        }
        return false;
    }


    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag advanced)
    {
        final BeeComponent bee = stack.get(FLComponents.BEE.get());
        if (bee != null && bee.hasQueen())
        {
            tooltip.add(Component.translatable("firmalife.bee.may_scrape"));
        }
    }

}
