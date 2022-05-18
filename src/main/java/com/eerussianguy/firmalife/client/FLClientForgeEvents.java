package com.eerussianguy.firmalife.client;

import com.google.common.base.Stopwatch;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;

import com.eerussianguy.firmalife.Firmalife;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.SelfTests;

public class FLClientForgeEvents
{
    public static void init()
    {
        final IEventBus bus = MinecraftForge.EVENT_BUS;

        bus.addListener(FLClientForgeEvents::onSelfTest);
    }

    public static void onSelfTest(SelfTests.ClientSelfTestEvent event)
    {
        if (Helpers.detectAssertionsEnabled())
        {
            final Stopwatch tick = Stopwatch.createStarted();
            FLClientSelfTests.validateModels();
            Firmalife.LOGGER.info("Client self tests passed in {}", tick.stop());
        }
    }
}
