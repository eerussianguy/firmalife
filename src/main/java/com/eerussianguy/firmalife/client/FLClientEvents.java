package com.eerussianguy.firmalife.client;

import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import com.eerussianguy.firmalife.client.render.*;
import com.eerussianguy.firmalife.client.screen.BeehiveScreen;
import com.eerussianguy.firmalife.common.FLHelpers;
import com.eerussianguy.firmalife.common.blockentities.FLBlockEntities;
import com.eerussianguy.firmalife.common.blocks.FLBlocks;
import com.eerussianguy.firmalife.common.container.FLContainerTypes;
import com.eerussianguy.firmalife.common.items.FLFoodTraits;
import com.eerussianguy.firmalife.common.items.FLItems;
import net.dries007.tfc.common.capabilities.food.FoodCapability;
import net.dries007.tfc.common.items.TFCItems;

public class FLClientEvents
{

    public static void init()
    {
        final IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();

        bus.addListener(FLClientEvents::clientSetup);
        bus.addListener(FLClientEvents::registerEntityRenderers);
        bus.addListener(FLClientEvents::onTextureStitch);
    }

    public static void clientSetup(FMLClientSetupEvent event)
    {
        // Render Types
        final RenderType solid = RenderType.solid();
        final RenderType cutout = RenderType.cutout();
        final RenderType cutoutMipped = RenderType.cutoutMipped();
        final RenderType translucent = RenderType.translucent();

        ItemBlockRenderTypes.setRenderLayer(FLBlocks.OVEN_TOP.get(), cutout);
        ItemBlockRenderTypes.setRenderLayer(FLBlocks.OVEN_BOTTOM.get(), cutout);
        ItemBlockRenderTypes.setRenderLayer(FLBlocks.OVEN_CHIMNEY.get(), cutout);
        ItemBlockRenderTypes.setRenderLayer(FLBlocks.CURED_OVEN_TOP.get(), cutout);
        ItemBlockRenderTypes.setRenderLayer(FLBlocks.CURED_OVEN_BOTTOM.get(), cutout);
        ItemBlockRenderTypes.setRenderLayer(FLBlocks.CURED_OVEN_CHIMNEY.get(), cutout);
        ItemBlockRenderTypes.setRenderLayer(FLBlocks.QUAD_PLANTER.get(), cutout);
        ItemBlockRenderTypes.setRenderLayer(FLBlocks.LARGE_PLANTER.get(), cutout);
        ItemBlockRenderTypes.setRenderLayer(FLBlocks.HANGING_PLANTER.get(), cutout);
        ItemBlockRenderTypes.setRenderLayer(FLBlocks.BONSAI_PLANTER.get(), cutout);
        ItemBlockRenderTypes.setRenderLayer(FLBlocks.IRON_COMPOSTER.get(), cutout);
        ItemBlockRenderTypes.setRenderLayer(FLBlocks.COMPOST_JAR.get(), translucent);
        ItemBlockRenderTypes.setRenderLayer(FLBlocks.HONEY_JAR.get(), translucent);
        ItemBlockRenderTypes.setRenderLayer(FLBlocks.ROTTEN_COMPOST_JAR.get(), translucent);
        ItemBlockRenderTypes.setRenderLayer(FLBlocks.GUANO_JAR.get(), translucent);
        ItemBlockRenderTypes.setRenderLayer(FLBlocks.CHEDDAR_WHEEL.get(), cutout);
        ItemBlockRenderTypes.setRenderLayer(FLBlocks.RAJYA_METOK_WHEEL.get(), cutout);
        ItemBlockRenderTypes.setRenderLayer(FLBlocks.CHEVRE_WHEEL.get(), cutout);
        ItemBlockRenderTypes.setRenderLayer(FLBlocks.SHOSHA_WHEEL.get(), cutout);
        ItemBlockRenderTypes.setRenderLayer(FLBlocks.FETA_WHEEL.get(), cutout);
        ItemBlockRenderTypes.setRenderLayer(FLBlocks.GOUDA_WHEEL.get(), cutout);


        FLBlocks.FRUIT_PRESERVES.values().forEach(reg -> ItemBlockRenderTypes.setRenderLayer(reg.get(), translucent));
        FLBlocks.GREENHOUSE_BLOCKS.values().forEach(map -> map.values().forEach(reg -> ItemBlockRenderTypes.setRenderLayer(reg.get(), cutout)));

        event.enqueueWork(() -> {
            MenuScreens.register(FLContainerTypes.BEEHIVE.get(), BeehiveScreen::new);

            TFCItems.FOOD.forEach((food, item) -> {
                if (FLItems.TFC_FRUITS.contains(food))
                {
                    ItemProperties.register(item.get(), FLHelpers.identifier("dry"), (stack, a, b, c) -> {
                        return stack.getCapability(FoodCapability.CAPABILITY).map(cap -> cap.getTraits().contains(FLFoodTraits.DRIED)).orElse(false) ? 1f : 0f;
                    });
                }
            });
        });



    }

    public static void registerEntityRenderers(EntityRenderersEvent.RegisterRenderers event)
    {
        event.registerBlockEntityRenderer(FLBlockEntities.OVEN_TOP.get(), ctx -> new OvenBlockEntityRenderer());
        event.registerBlockEntityRenderer(FLBlockEntities.DRYING_MAT.get(), ctx -> new DryingMatBlockEntityRenderer());
        event.registerBlockEntityRenderer(FLBlockEntities.STRING.get(), ctx -> new StringBlockEntityRenderer());
        event.registerBlockEntityRenderer(FLBlockEntities.LARGE_PLANTER.get(), ctx -> new LargePlanterBlockEntityRenderer());
        event.registerBlockEntityRenderer(FLBlockEntities.QUAD_PLANTER.get(), ctx -> new QuadPlanterBlockEntityRenderer());
        event.registerBlockEntityRenderer(FLBlockEntities.TRELLIS_PLANTER.get(), ctx -> new TrellisPlanterBlockEntityRenderer());
        event.registerBlockEntityRenderer(FLBlockEntities.BONSAI_PLANTER.get(), ctx -> new BonsaiPlanterBlockEntityRenderer());
        event.registerBlockEntityRenderer(FLBlockEntities.HANGING_PLANTER.get(), ctx -> new HangingPlanterBlockEntityRenderer());
    }

    public static void onTextureStitch(TextureStitchEvent.Pre event)
    {
        for (String name : new String[] {"green_bean", "maize", "jute", "tomato", "sugarcane"})
        {
            for (int i = 0; i < 5; i++)
            {
                event.addSprite(FLHelpers.identifier("block/crop/" + name + "_" + i));
            }
        }
        for (String name : new String[] {"squash", "banana"})
        {
            for (int i = 0; i < 5; i++)
            {
                event.addSprite(FLHelpers.identifier("block/crop/" + name + "_" + i));
            }
            event.addSprite(FLHelpers.identifier("block/crop/" + name + "_fruit"));
        }
    }
}
