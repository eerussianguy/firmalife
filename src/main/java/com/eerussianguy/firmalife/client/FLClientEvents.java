package com.eerussianguy.firmalife.client;

import java.util.function.Supplier;
import java.util.stream.Stream;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.color.block.BlockColor;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.client.color.item.ItemColors;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.world.item.Item;
import net.minecraftforge.client.event.*;
import net.minecraftforge.client.model.ForgeModelBakery;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import com.eerussianguy.firmalife.client.render.*;
import com.eerussianguy.firmalife.client.screen.BeehiveScreen;
import com.eerussianguy.firmalife.common.FLHelpers;
import com.eerussianguy.firmalife.common.blockentities.FLBlockEntities;
import com.eerussianguy.firmalife.common.blocks.FLBlocks;
import com.eerussianguy.firmalife.common.container.FLContainerTypes;
import com.eerussianguy.firmalife.common.entities.FLEntities;
import com.eerussianguy.firmalife.common.entities.FLParticles;
import com.eerussianguy.firmalife.common.items.FLFoodTraits;
import com.eerussianguy.firmalife.common.items.FLItems;
import com.eerussianguy.firmalife.common.util.FLMetal;
import net.dries007.tfc.client.TFCColors;
import net.dries007.tfc.client.particle.GlintParticleProvider;
import net.dries007.tfc.client.screen.KnappingScreen;
import net.dries007.tfc.common.blocks.TFCBlocks;
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
        bus.addListener(FLClientEvents::onModelRegister);
        bus.addListener(FLClientEvents::onBlockColors);
        bus.addListener(FLClientEvents::onItemColors);
        bus.addListener(FLClientEvents::registerParticleFactories);
    }

    public static void clientSetup(FMLClientSetupEvent event)
    {
        // Render Types
        final RenderType solid = RenderType.solid();
        final RenderType cutout = RenderType.cutout();
        final RenderType cutoutMipped = RenderType.cutoutMipped();
        final RenderType translucent = RenderType.translucent();

        Stream.of(FLBlocks.OVEN_TOP, FLBlocks.OVEN_BOTTOM, FLBlocks.OVEN_CHIMNEY, FLBlocks.CURED_OVEN_TOP, FLBlocks.CURED_OVEN_BOTTOM,
            FLBlocks.CURED_OVEN_CHIMNEY, FLBlocks.QUAD_PLANTER, FLBlocks.LARGE_PLANTER, FLBlocks.HANGING_PLANTER, FLBlocks.BONSAI_PLANTER,
            FLBlocks.IRON_COMPOSTER, FLBlocks.COMPOST_JAR, FLBlocks.HONEY_JAR, FLBlocks.ROTTEN_COMPOST_JAR, FLBlocks.GUANO_JAR, FLBlocks.CHEDDAR_WHEEL,
            FLBlocks.RAJYA_METOK_WHEEL, FLBlocks.CHEVRE_WHEEL, FLBlocks.SHOSHA_WHEEL, FLBlocks.FETA_WHEEL, FLBlocks.GOUDA_WHEEL, FLBlocks.SMALL_CHROMITE,
            FLBlocks.MIXING_BOWL, FLBlocks.BUTTERFLY_GRASS, FLBlocks.SPRINKLER, FLBlocks.DRIBBLER, FLBlocks.VAT, FLBlocks.HYDROPONIC_PLANTER, FLBlocks.NUTRITIVE_BASIN
        ).forEach(reg -> ItemBlockRenderTypes.setRenderLayer(reg.get(), cutout));

        ItemBlockRenderTypes.setRenderLayer(FLBlocks.SOLAR_DRIER.get(), translucent);

        FLBlocks.CHROMITE_ORES.values().forEach(map -> map.values().forEach(reg -> ItemBlockRenderTypes.setRenderLayer(reg.get(), cutout)));
        FLBlocks.FRUIT_PRESERVES.values().forEach(reg -> ItemBlockRenderTypes.setRenderLayer(reg.get(), translucent));
        FLBlocks.FL_FRUIT_PRESERVES.values().forEach(reg -> ItemBlockRenderTypes.setRenderLayer(reg.get(), translucent));
        FLBlocks.GREENHOUSE_BLOCKS.values().forEach(map -> map.values().forEach(reg -> ItemBlockRenderTypes.setRenderLayer(reg.get(), cutout)));
        FLBlocks.FRUIT_TREE_LEAVES.values().forEach(reg -> ItemBlockRenderTypes.setRenderLayer(reg.get(), layer -> Minecraft.useFancyGraphics() ? layer == cutoutMipped : layer == solid));
        FLBlocks.FRUIT_TREE_SAPLINGS.values().forEach(reg -> ItemBlockRenderTypes.setRenderLayer(reg.get(), cutout));
        FLBlocks.FRUIT_TREE_POTTED_SAPLINGS.values().forEach(reg -> ItemBlockRenderTypes.setRenderLayer(reg.get(), cutout));
        FLBlocks.STATIONARY_BUSHES.values().forEach(bush -> ItemBlockRenderTypes.setRenderLayer(bush.get(), cutoutMipped));
        FLBlocks.HERBS.values().forEach(reg -> ItemBlockRenderTypes.setRenderLayer(reg.get(), cutout));
        FLBlocks.POTTED_HERBS.values().forEach(reg -> ItemBlockRenderTypes.setRenderLayer(reg.get(), cutout));
        FLBlocks.FOOD_SHELVES.values().forEach(reg -> ItemBlockRenderTypes.setRenderLayer(reg.get(), cutout));
        FLBlocks.HANGERS.values().forEach(reg -> ItemBlockRenderTypes.setRenderLayer(reg.get(), cutout));

        event.enqueueWork(() -> {
            MenuScreens.register(FLContainerTypes.BEEHIVE.get(), BeehiveScreen::new);
            MenuScreens.register(FLContainerTypes.PUMPKIN.get(), KnappingScreen::new);

            TFCItems.FOOD.forEach((food, item) -> {
                if (FLItems.TFC_FRUITS.contains(food))
                {
                    registerDryProperty(item);
                }
            });
            FLItems.FRUITS.forEach((food, item) -> registerDryProperty(item));
        });
    }

    public static void onBlockColors(ColorHandlerEvent.Block event)
    {
        final BlockColors registry = event.getBlockColors();
        final BlockColor grassColor = (state, level, pos, tintIndex) -> TFCColors.getGrassColor(pos, tintIndex);
        final BlockColor tallGrassColor = (state, level, pos, tintIndex) -> TFCColors.getTallGrassColor(pos, tintIndex);

        registry.register(tallGrassColor, FLBlocks.BUTTERFLY_GRASS.get());
        registry.register(tallGrassColor, FLBlocks.POTTED_BUTTERFLY_GRASS.get());
    }

    public static void onItemColors(ColorHandlerEvent.Item event)
    {
        final ItemColors registry = event.getItemColors();
        final ItemColor grassColor = (stack, tintIndex) -> TFCColors.getGrassColor(null, tintIndex);

        Stream.of(FLBlocks.BUTTERFLY_GRASS).forEach(reg -> registry.register(grassColor, reg.get()));
    }

    public static void registerParticleFactories(ParticleFactoryRegisterEvent event)
    {
        ParticleEngine engine = Minecraft.getInstance().particleEngine;
        engine.register(FLParticles.GROWTH.get(), set -> new GlintParticleProvider(set, ChatFormatting.GREEN));
    }

    private static void registerDryProperty(Supplier<Item> item)
    {
        ItemProperties.register(item.get(), FLHelpers.identifier("dry"), (stack, a, b, c) ->
            stack.getCapability(FoodCapability.CAPABILITY)
                .map(cap -> cap.getTraits().contains(FLFoodTraits.DRIED))
                .orElse(false) ? 1f : 0f);
    }


    public static void registerEntityRenderers(EntityRenderersEvent.RegisterRenderers event)
    {
        event.registerBlockEntityRenderer(FLBlockEntities.OVEN_TOP.get(), ctx -> new OvenBlockEntityRenderer());
        event.registerBlockEntityRenderer(FLBlockEntities.DRYING_MAT.get(), ctx -> new DryingMatBlockEntityRenderer(2f / 16));
        event.registerBlockEntityRenderer(FLBlockEntities.SOLAR_DRIER.get(), ctx -> new DryingMatBlockEntityRenderer(1f / 16));
        event.registerBlockEntityRenderer(FLBlockEntities.STRING.get(), ctx -> new StringBlockEntityRenderer());
        event.registerBlockEntityRenderer(FLBlockEntities.LARGE_PLANTER.get(), ctx -> new LargePlanterBlockEntityRenderer());
        event.registerBlockEntityRenderer(FLBlockEntities.QUAD_PLANTER.get(), ctx -> new QuadPlanterBlockEntityRenderer<>());
        event.registerBlockEntityRenderer(FLBlockEntities.HYDROPONIC_PLANTER.get(), ctx -> new HydroponicPlanterBlockEntityRenderer());
        event.registerBlockEntityRenderer(FLBlockEntities.TRELLIS_PLANTER.get(), ctx -> new TrellisPlanterBlockEntityRenderer());
        event.registerBlockEntityRenderer(FLBlockEntities.BONSAI_PLANTER.get(), ctx -> new BonsaiPlanterBlockEntityRenderer());
        event.registerBlockEntityRenderer(FLBlockEntities.HANGING_PLANTER.get(), ctx -> new HangingPlanterBlockEntityRenderer());
        event.registerBlockEntityRenderer(FLBlockEntities.VAT.get(), ctx -> new VatBlockEntityRenderer());
        event.registerBlockEntityRenderer(FLBlockEntities.MIXING_BOWL.get(), ctx -> new MixingBowlBlockEntityRenderer());
        event.registerBlockEntityRenderer(FLBlockEntities.HANGER.get(), ctx -> new HangerBlockEntityRenderer());
        event.registerBlockEntityRenderer(FLBlockEntities.FOOD_SHELF.get(), ctx -> new FoodShelfBlockEntityRenderer());
        event.registerBlockEntityRenderer(FLBlockEntities.NUTRITIVE_BASIN.get(), ctx -> new NutritiveBasinBlockEntityRenderer());

        event.registerEntityRenderer(FLEntities.SEED_BALL.get(), ThrownItemRenderer::new);
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
        for (String name : new String[] {"cranberry"})
        {
            for (int i = 0; i < 4; i++)
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
        for (String name : new String[] {"melon", "pumpkin"})
        {
            for (int i = 0; i < 7; i++)
            {
                event.addSprite(FLHelpers.identifier("block/crop/" + name + "_" + i));
            }
            event.addSprite(FLHelpers.identifier("block/crop/" + name + "_fruit"));
        }
        for (FLMetal metal : FLMetal.values())
        {
            event.addSprite(metal.getSheet());
        }
        event.addSprite(FLHelpers.identifier("block/pineapple"));
    }

    public static void onModelRegister(ModelRegistryEvent event)
    {
        ForgeModelBakery.addSpecialModel(MixingBowlBlockEntityRenderer.SPOON_LOCATION);
    }
}
