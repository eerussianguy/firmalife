package com.eerussianguy.firmalife.common;

import com.eerussianguy.firmalife.common.entities.FLBee;
import com.eerussianguy.firmalife.common.entities.FLEntities;
import net.minecraft.network.chat.Component;
import net.minecraft.server.packs.PackLocationInfo;
import net.minecraft.server.packs.PackSelectionConfig;
import net.minecraft.server.packs.PackType;

import java.util.Optional;

import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackSource;

import com.eerussianguy.firmalife.FirmaLife;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.event.AddPackFindersEvent;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.resource.ResourcePackLoader;
import net.neoforged.neoforgespi.language.IModFileInfo;

public class FLEvents
{
    public static void init(IEventBus bus)
    {
        bus.addListener(FLEvents::onPackFinder);
        bus.addListener(FLEvents::onEntityAttributeCreation);
    }

    public static void onPackFinder(AddPackFindersEvent event)
    {
        if (event.getPackType() == PackType.CLIENT_RESOURCES)
        {
            final IModFileInfo info = ModList.get().getModFileById(FirmaLife.MOD_ID);
            assert info != null;

            FirmaLife.LOGGER.info("Injecting firmalife override pack");
            event.addRepositorySource(consumer ->
                consumer.accept(Pack.readMetaAndCreate(new PackLocationInfo("firmalife_data", Component.literal("Firmalife Resources"), PackSource.BUILT_IN, Optional.empty()), ResourcePackLoader.createPackForMod(info), PackType.CLIENT_RESOURCES, new PackSelectionConfig(true, Pack.Position.TOP, false)))
            );
        }
    }

    public static void onEntityAttributeCreation(EntityAttributeCreationEvent event)
    {
        event.put(FLEntities.FLBEE.get(), FLBee.createAttributes().build());
    }

}
