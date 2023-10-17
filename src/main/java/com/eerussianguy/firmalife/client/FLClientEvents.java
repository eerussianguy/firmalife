package com.eerussianguy.firmalife.client;

import java.util.function.Supplier;
import java.util.stream.Stream;

import com.eerussianguy.firmalife.client.model.BonsaiPlanterBlockModel;
import com.eerussianguy.firmalife.client.model.HangingPlanterBlockModel;
import com.eerussianguy.firmalife.client.model.HydroponicPlanterBlockModel;
import com.eerussianguy.firmalife.client.model.JarbnetBlockModel;
import com.eerussianguy.firmalife.client.model.LargePlanterBakedModel;
import com.eerussianguy.firmalife.client.model.PeelModel;
import com.eerussianguy.firmalife.client.model.DynamicBlockModel;
import com.eerussianguy.firmalife.client.model.QuadPlanterBlockModel;
import com.eerussianguy.firmalife.client.model.TrellisPlanterBlockModel;
import com.eerussianguy.firmalife.client.screen.StovetopGrillScreen;
import com.eerussianguy.firmalife.client.screen.StovetopPotScreen;
import com.eerussianguy.firmalife.common.FLCreativeTabs;
import com.eerussianguy.firmalife.common.util.FLFruit;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.color.block.BlockColor;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.world.item.Item;
import net.minecraftforge.client.event.*;
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
import net.dries007.tfc.client.TFCColors;
import net.dries007.tfc.client.particle.GlintParticleProvider;
import net.dries007.tfc.common.capabilities.food.FoodCapability;
import net.dries007.tfc.common.items.TFCItems;

public class FLClientEvents
{

    public static void init()
    {
        final IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();

        bus.addListener(FLClientEvents::clientSetup);
        bus.addListener(FLClientEvents::registerEntityRenderers);
        bus.addListener(FLClientEvents::onLayers);
        bus.addListener(FLClientEvents::onModelRegister);
        bus.addListener(FLClientEvents::onBlockColors);
        bus.addListener(FLClientEvents::onItemColors);
        bus.addListener(FLClientEvents::registerParticleFactories);
        bus.addListener(FLClientEvents::registerModels);
        bus.addListener(FLClientEvents::registerLoaders);
        bus.addListener(FLCreativeTabs::onBuildCreativeTab);
    }

    @SuppressWarnings("deprecation")
    public static void clientSetup(FMLClientSetupEvent event)
    {
        // Render Types
        final RenderType solid = RenderType.solid();
        final RenderType cutout = RenderType.cutout();
        final RenderType cutoutMipped = RenderType.cutoutMipped();
        final RenderType translucent = RenderType.translucent();

        Stream.of(FLBlocks.OVEN_BOTTOM, FLBlocks.OVEN_TOP, FLBlocks.OVEN_CHIMNEY, FLBlocks.OVEN_HOPPER,
            FLBlocks.QUAD_PLANTER, FLBlocks.LARGE_PLANTER, FLBlocks.HANGING_PLANTER, FLBlocks.BONSAI_PLANTER, FLBlocks.TRELLIS_PLANTER,
            FLBlocks.IRON_COMPOSTER, FLBlocks.CHEDDAR_WHEEL,
            FLBlocks.RAJYA_METOK_WHEEL, FLBlocks.CHEVRE_WHEEL, FLBlocks.SHOSHA_WHEEL, FLBlocks.FETA_WHEEL, FLBlocks.GOUDA_WHEEL, FLBlocks.SMALL_CHROMITE,
            FLBlocks.MIXING_BOWL, FLBlocks.BUTTERFLY_GRASS, FLBlocks.SPRINKLER, FLBlocks.DRIBBLER, FLBlocks.VAT, FLBlocks.HYDROPONIC_PLANTER, FLBlocks.STOVETOP_GRILL, FLBlocks.STOVETOP_POT,
            FLBlocks.DARK_LADDER, FLBlocks.JARRING_STATION
        ).forEach(reg -> ItemBlockRenderTypes.setRenderLayer(reg.get(), cutout));

        ItemBlockRenderTypes.setRenderLayer(FLBlocks.SOLAR_DRIER.get(), translucent);
        ItemBlockRenderTypes.setRenderLayer(FLBlocks.NUTRITIVE_BASIN.get(), translucent);

        FLBlocks.CHROMITE_ORES.values().forEach(map -> map.values().forEach(reg -> ItemBlockRenderTypes.setRenderLayer(reg.get(), cutout)));
        FLBlocks.GREENHOUSE_BLOCKS.values().forEach(map -> map.values().forEach(reg -> ItemBlockRenderTypes.setRenderLayer(reg.get(), cutout)));
        FLBlocks.FRUIT_TREE_LEAVES.values().forEach(reg -> ItemBlockRenderTypes.setRenderLayer(reg.get(), layer -> Minecraft.useFancyGraphics() ? layer == cutoutMipped : layer == solid));
        FLBlocks.FRUIT_TREE_SAPLINGS.values().forEach(reg -> ItemBlockRenderTypes.setRenderLayer(reg.get(), cutout));
        FLBlocks.FRUIT_TREE_POTTED_SAPLINGS.values().forEach(reg -> ItemBlockRenderTypes.setRenderLayer(reg.get(), cutout));
        FLBlocks.STATIONARY_BUSHES.values().forEach(bush -> ItemBlockRenderTypes.setRenderLayer(bush.get(), cutoutMipped));
        FLBlocks.HERBS.values().forEach(reg -> ItemBlockRenderTypes.setRenderLayer(reg.get(), cutout));
        FLBlocks.POTTED_HERBS.values().forEach(reg -> ItemBlockRenderTypes.setRenderLayer(reg.get(), cutout));
        FLBlocks.FOOD_SHELVES.values().forEach(reg -> ItemBlockRenderTypes.setRenderLayer(reg.get(), cutout));
        FLBlocks.HANGERS.values().forEach(reg -> ItemBlockRenderTypes.setRenderLayer(reg.get(), cutout));
        FLBlocks.JARBNETS.values().forEach(reg -> ItemBlockRenderTypes.setRenderLayer(reg.get(), cutout));
        FLBlocks.CURED_OVEN_BOTTOM.values().forEach(reg -> ItemBlockRenderTypes.setRenderLayer(reg.get(), cutout));
        FLBlocks.CURED_OVEN_TOP.values().forEach(reg -> ItemBlockRenderTypes.setRenderLayer(reg.get(), cutout));
        FLBlocks.CURED_OVEN_HOPPER.values().forEach(reg -> ItemBlockRenderTypes.setRenderLayer(reg.get(), cutout));
        FLBlocks.CURED_OVEN_CHIMNEY.values().forEach(reg -> ItemBlockRenderTypes.setRenderLayer(reg.get(), cutout));
        FLBlocks.INSULATED_OVEN_BOTTOM.values().forEach(reg -> ItemBlockRenderTypes.setRenderLayer(reg.get(), cutout));

        event.enqueueWork(() -> {
            MenuScreens.register(FLContainerTypes.BEEHIVE.get(), BeehiveScreen::new);
            MenuScreens.register(FLContainerTypes.STOVETOP_GRILL.get(), StovetopGrillScreen::new);
            MenuScreens.register(FLContainerTypes.STOVETOP_POT.get(), StovetopPotScreen::new);

            TFCItems.FOOD.forEach((food, item) -> {
                if (FLItems.TFC_FRUITS.contains(food))
                {
                    registerDryProperty(item);
                }
            });
            FLItems.FRUITS.forEach((food, item) -> registerDryProperty(item));
        });
    }

    public static void onBlockColors(RegisterColorHandlersEvent.Block event)
    {
        final BlockColor tallGrassColor = (state, level, pos, tintIndex) -> TFCColors.getTallGrassColor(pos, tintIndex);

        event.register(tallGrassColor, FLBlocks.BUTTERFLY_GRASS.get());
        event.register(tallGrassColor, FLBlocks.POTTED_BUTTERFLY_GRASS.get());
    }

    public static void onItemColors(RegisterColorHandlersEvent.Item event)
    {
        final ItemColor grassColor = (stack, tintIndex) -> TFCColors.getGrassColor(null, tintIndex);

        Stream.of(FLBlocks.BUTTERFLY_GRASS).forEach(reg -> event.register(grassColor, reg.get()));
    }

    public static void registerParticleFactories(RegisterParticleProvidersEvent event)
    {
        event.registerSpriteSet(FLParticles.GROWTH.get(), set -> new GlintParticleProvider(set, ChatFormatting.GREEN));
    }

    public static void registerModels(ModelEvent.RegisterAdditional event)
    {
        for (FLFruit fruit : FLFruit.values())
        {
            event.register(FLHelpers.identifier("block/jar/" + fruit.getSerializedName()));
            event.register(FLHelpers.identifier("block/jar/" + fruit.getSerializedName() + "_unsealed"));
        }
        event.register(FLHelpers.identifier("block/jar/compost"));
        event.register(FLHelpers.identifier("block/jar/rotten_compost"));
        event.register(FLHelpers.identifier("block/jar/guano"));
        event.register(FLHelpers.identifier("block/jar/honey"));
    }

    public static void registerLoaders(ModelEvent.RegisterGeometryLoaders event)
    {
        event.register("large_planter", new DynamicBlockModel.Loader(LargePlanterBakedModel::new));
        event.register("hanging_planter", new DynamicBlockModel.Loader(HangingPlanterBlockModel::new));
        event.register("bonsai_planter", new DynamicBlockModel.Loader(BonsaiPlanterBlockModel::new));
        event.register("quad_planter", new DynamicBlockModel.Loader(QuadPlanterBlockModel::new));
        event.register("hydroponic_planter", new DynamicBlockModel.Loader(HydroponicPlanterBlockModel::new));
        event.register("trellis_planter", new DynamicBlockModel.Loader(TrellisPlanterBlockModel::new));
        event.register("jarbnet", new DynamicBlockModel.Loader(JarbnetBlockModel::new));
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
        event.registerBlockEntityRenderer(FLBlockEntities.VAT.get(), ctx -> new VatBlockEntityRenderer());
        event.registerBlockEntityRenderer(FLBlockEntities.MIXING_BOWL.get(), ctx -> new MixingBowlBlockEntityRenderer());
        event.registerBlockEntityRenderer(FLBlockEntities.HANGER.get(), ctx -> new HangerBlockEntityRenderer());
        event.registerBlockEntityRenderer(FLBlockEntities.FOOD_SHELF.get(), ctx -> new FoodShelfBlockEntityRenderer());
        event.registerBlockEntityRenderer(FLBlockEntities.STOVETOP_GRILL.get(), ctx -> new StovetopGrillBlockEntityRenderer());
        event.registerBlockEntityRenderer(FLBlockEntities.STOVETOP_POT.get(), ctx -> new StovetopPotBlockEntityRenderer());
        event.registerBlockEntityRenderer(FLBlockEntities.ICE_FISHING_STATION.get(), ctx -> new IceFishingStationBlockEntityRenderer());
        event.registerBlockEntityRenderer(FLBlockEntities.JARRING_STATION.get(), ctx -> new JarringStationBlockEntityRenderer());
        event.registerBlockEntityRenderer(FLBlockEntities.PLATE.get(), ctx -> new PlateBlockEntityRenderer());
        event.registerBlockEntityRenderer(FLBlockEntities.HYDROPONIC_PLANTER.get(), ctx -> new HydroponicPlanterBlockEntityRenderer());

        event.registerEntityRenderer(FLEntities.SEED_BALL.get(), ThrownItemRenderer::new);
    }

    public static void onLayers(EntityRenderersEvent.RegisterLayerDefinitions event)
    {
        event.registerLayerDefinition(FLClientHelpers.modelIdentifier("peel"), PeelModel::createBodyLayer);
    }


    public static void onModelRegister(ModelEvent.RegisterAdditional event)
    {
        event.register(MixingBowlBlockEntityRenderer.SPOON_LOCATION);
        event.register(JarbnetBlockModel.JUG_LOCATION);
        event.register(JarringStationBlockEntityRenderer.EMPTY_JAR_LOCATION);
    }
}
