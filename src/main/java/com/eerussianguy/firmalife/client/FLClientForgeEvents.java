package com.eerussianguy.firmalife.client;

import java.util.List;

import com.google.common.base.Stopwatch;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.IEventBus;

import com.eerussianguy.firmalife.Firmalife;
import com.eerussianguy.firmalife.common.FLHelpers;
import com.eerussianguy.firmalife.common.capabilities.bee.BeeCapability;
import net.dries007.tfc.util.SelfTests;

public class FLClientForgeEvents
{
    public static void init()
    {
        final IEventBus bus = MinecraftForge.EVENT_BUS;

        bus.addListener(FLClientForgeEvents::onSelfTest);
        bus.addListener(FLClientForgeEvents::onTooltip);
    }

    public static void onSelfTest(SelfTests.ClientSelfTestEvent event)
    {
        if (FLHelpers.ASSERTIONS_ENABLED)
        {
            final Stopwatch tick = Stopwatch.createStarted();
            FLClientSelfTests.validateModels();
            Firmalife.LOGGER.info("Client self tests passed in {}", tick.stop());
        }
    }

    public static void onTooltip(ItemTooltipEvent event)
    {
        final ItemStack stack = event.getItemStack();
        final List<Component> text = event.getToolTip();
        if (!stack.isEmpty())
        {
            stack.getCapability(BeeCapability.CAPABILITY).ifPresent(cap -> cap.addTooltipInfo(text));
        }
    }
}
