package com.eerussianguy.firmalife.client;

import java.util.List;
import com.eerussianguy.firmalife.common.FLTags;
import com.eerussianguy.firmalife.common.blocks.greenhouse.GreenhousePanelWallBlock;
import com.eerussianguy.firmalife.common.capabilities.FLComponents;
import com.eerussianguy.firmalife.common.capabilities.bee.BeeComponent;
import com.eerussianguy.firmalife.common.capabilities.wine.WineComponent;
import com.eerussianguy.firmalife.common.util.Plantable;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;

import net.dries007.tfc.common.items.PlantableInfo;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.SelfTests;

public class FLClientForgeEvents
{
    public static void init()
    {
        final IEventBus bus = NeoForge.EVENT_BUS;

        bus.addListener(FLClientForgeEvents::onSelfTest);
        bus.addListener(FLClientForgeEvents::onTooltip);
        bus.addListener(EventPriority.LOW, FLClientForgeEvents::onTooltipLater);
    }

    private static void onSelfTest(SelfTests.ClientSelfTestEvent event)
    {
        FLClientSelfTests.runClientSelfTests();
    }

    private static void onTooltip(ItemTooltipEvent event)
    {
        final ItemStack stack = event.getItemStack();
        final List<Component> text = event.getToolTip();
        if (!stack.isEmpty())
        {
            final List<Component> tooltip = event.getToolTip();

            final WineComponent wine = stack.get(FLComponents.WINE.get());
            if (wine != null)
            {
                wine.addTooltipInfo(tooltip::add, stack);
            }
            final BeeComponent bee = stack.get(FLComponents.BEE.get());
            if (bee != null)
            {
                bee.addTooltipInfo(tooltip::add);
            }
            if (Helpers.isItem(stack, FLTags.Items.BEEKEEPER_ARMOR))
            {
                text.add(Component.translatable("firmalife.tooltip.beekeeper_armor"));
            }
            if (stack.getItem() instanceof BlockItem blockItem && blockItem.getBlock() instanceof GreenhousePanelWallBlock)
            {
                text.add(Component.translatable("firmalife.tooltip.greenhouse_panel_corner").withStyle(ChatFormatting.GRAY));
            }
        }
    }

    private static void onTooltipLater(ItemTooltipEvent event)
    {
        final ItemStack stack = event.getItemStack();
        final List<Component> text = event.getToolTip();
        final Plantable plantable = Plantable.get(stack);
        if (plantable != null)
        {
            plantable.addTooltipInfo(text, stack.getItem() instanceof PlantableInfo info ? info : null);
        }
    }
}
