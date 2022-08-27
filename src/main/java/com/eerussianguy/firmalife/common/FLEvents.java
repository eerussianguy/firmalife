package com.eerussianguy.firmalife.common;

import java.io.IOException;
import java.nio.file.Path;

import javax.annotation.Nonnull;

import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.metadata.pack.PackMetadataSection;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraftforge.event.AddPackFindersEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import com.eerussianguy.firmalife.FirmaLife;
import com.eerussianguy.firmalife.common.util.SafePathResourcePack;

public class FLEvents
{
    public static void init()
    {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();

        bus.addListener(FLEvents::onPackFinder);
    }

    public static void onPackFinder(AddPackFindersEvent event)
    {
        ModList.get().getMods().forEach(mod -> FirmaLife.LOGGER.info(mod.getModId()));
//        try
//        {
//            if (event.getPackType() == PackType.SERVER_DATA)
//            {
//                var modFile = ModList.get().getModFileById(FirmaLife.MOD_ID).getFile();
//                var resourcePath = modFile.getFilePath();
//                var pack = new SafePathResourcePack(modFile.getFileName() + ":overload", resourcePath) {
//                    @Nonnull
//                    @Override
//                    protected Path resolve(@Nonnull String... paths)
//                    {
//                        return modFile.findResource(paths);
//                    }
//                };
//
//                var metadata = pack.getMetadataSection(PackMetadataSection.SERIALIZER);
//                if (metadata != null)
//                {
//                    event.addRepositorySource((consumer, constructor) ->
//                        consumer.accept(constructor.create("builtin/firmalife_data", new TextComponent("Firmalife Data"), true, () -> pack, metadata, Pack.Position.TOP, PackSource.BUILT_IN, false))
//                    );
//                }
//            }
//        }
//        catch (IOException e)
//        {
//            throw new RuntimeException(e);
//        }
    }
}
