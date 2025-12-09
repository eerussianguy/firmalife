package com.eerussianguy.firmalife.client;

import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;
import java.util.stream.Stream;
import com.eerussianguy.firmalife.FirmaLife;
import com.eerussianguy.firmalife.client.model.BonsaiPlanterBlockModel;
import com.eerussianguy.firmalife.client.model.DynamicBlockModel;
import com.eerussianguy.firmalife.client.model.FoodShelfBlockModel;
import com.eerussianguy.firmalife.client.model.HangerBlockModel;
import com.eerussianguy.firmalife.client.model.HangingPlanterBlockModel;
import com.eerussianguy.firmalife.client.model.HydroponicPlanterBlockModel;
import com.eerussianguy.firmalife.client.model.JarbnetBlockModel;
import com.eerussianguy.firmalife.client.model.JarringStationBlockModel;
import com.eerussianguy.firmalife.client.model.LargePlanterBakedModel;
import com.eerussianguy.firmalife.client.model.PeelModel;
import com.eerussianguy.firmalife.client.model.QuadPlanterBlockModel;
import com.eerussianguy.firmalife.client.model.TrellisPlanterBlockModel;
import com.eerussianguy.firmalife.client.model.WineShelfBlockModel;
import com.eerussianguy.firmalife.client.render.BarrelPressBlockEntityRenderer;
import com.eerussianguy.firmalife.client.render.CompostTumblerBlockEntityRenderer;
import com.eerussianguy.firmalife.client.render.DryingMatBlockEntityRenderer;
import com.eerussianguy.firmalife.client.render.FLBeeRenderer;
import com.eerussianguy.firmalife.client.render.HydroponicPlanterBlockEntityRenderer;
import com.eerussianguy.firmalife.client.render.MixingBowlBlockEntityRenderer;
import com.eerussianguy.firmalife.client.render.OvenBlockEntityRenderer;
import com.eerussianguy.firmalife.client.render.PickerBlockEntityRenderer;
import com.eerussianguy.firmalife.client.render.PlateBlockEntityRenderer;
import com.eerussianguy.firmalife.client.render.PumpingStationBlockEntityRenderer;
import com.eerussianguy.firmalife.client.render.StompingBarrelBlockEntityRenderer;
import com.eerussianguy.firmalife.client.render.StovetopGrillBlockEntityRenderer;
import com.eerussianguy.firmalife.client.render.StovetopPotBlockEntityRenderer;
import com.eerussianguy.firmalife.client.render.StringBlockEntityRenderer;
import com.eerussianguy.firmalife.client.render.SweeperBlockEntityRenderer;
import com.eerussianguy.firmalife.client.render.VatBlockEntityRenderer;
import com.eerussianguy.firmalife.client.screen.BarrelPressScreen;
import com.eerussianguy.firmalife.client.screen.BeehiveScreen;
import com.eerussianguy.firmalife.client.screen.BigBarrelScreen;
import com.eerussianguy.firmalife.client.screen.StovetopGrillScreen;
import com.eerussianguy.firmalife.client.screen.StovetopPotScreen;
import com.eerussianguy.firmalife.common.FLCreativeTabs;
import com.eerussianguy.firmalife.common.FLHelpers;
import com.eerussianguy.firmalife.common.blockentities.FLBlockEntities;
import com.eerussianguy.firmalife.common.blocks.FLBlocks;
import com.eerussianguy.firmalife.common.blocks.FLFluids;
import com.eerussianguy.firmalife.common.capabilities.FLComponents;
import com.eerussianguy.firmalife.common.capabilities.bee.BeeComponent;
import com.eerussianguy.firmalife.common.container.FLMenuTypes;
import com.eerussianguy.firmalife.common.entities.FLEntities;
import com.eerussianguy.firmalife.common.items.FLFoodTraits;
import com.eerussianguy.firmalife.common.items.FLItems;
import com.eerussianguy.firmalife.common.items.PeelItem;
import com.eerussianguy.firmalife.common.items.WineBottleItem;
import com.eerussianguy.firmalife.common.misc.FLParticles;
import com.eerussianguy.firmalife.common.misc.SprinklerParticle;
import com.eerussianguy.firmalife.common.util.FLFruit;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.color.block.BlockColor;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.ModelEvent;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;
import net.neoforged.neoforge.client.model.DynamicFluidContainerModel;
import net.neoforged.neoforge.client.model.geometry.IGeometryLoader;

import net.dries007.tfc.client.ClientEventHandler;
import net.dries007.tfc.client.TFCColors;
import net.dries007.tfc.client.extensions.FluidRendererExtension;
import net.dries007.tfc.client.particle.GlintParticleProvider;
import net.dries007.tfc.client.render.blockentity.PlacedItemBlockEntityRenderer;
import net.dries007.tfc.common.component.food.FoodCapability;
import net.dries007.tfc.common.component.food.IFood;
import net.dries007.tfc.common.fluids.TFCFluids;
import net.dries007.tfc.common.items.TFCItems;

public class FLClientEvents
{

    public static void init(IEventBus bus)
    {
        bus.addListener(FLClientEvents::clientSetup);
        bus.addListener(FLClientEvents::registerEntityRenderers);
        bus.addListener(FLClientEvents::onLayers);
        bus.addListener(FLClientEvents::onMenuRegister);
        bus.addListener(FLClientEvents::onBlockColors);
        bus.addListener(FLClientEvents::onItemColors);
        bus.addListener(FLClientEvents::registerParticleFactories);
        bus.addListener(FLClientEvents::registerModels);
        bus.addListener(FLClientEvents::registerLoaders);
        bus.addListener(FLClientEvents::registerExtensions);
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
            FLBlocks.COMPOST_TUMBLER, FLBlocks.CHEDDAR_WHEEL,
            FLBlocks.RAJYA_METOK_WHEEL, FLBlocks.CHEVRE_WHEEL, FLBlocks.SHOSHA_WHEEL, FLBlocks.FETA_WHEEL, FLBlocks.GOUDA_WHEEL, FLBlocks.SMALL_CHROMITE,
            FLBlocks.MIXING_BOWL, FLBlocks.BUTTERFLY_GRASS, FLBlocks.VAT, FLBlocks.HYDROPONIC_PLANTER, FLBlocks.STOVETOP_GRILL, FLBlocks.STOVETOP_POT,
            FLBlocks.DARK_LADDER, FLBlocks.JARRING_STATION, FLBlocks.GRAPE_TRELLIS_POST_RED, FLBlocks.GRAPE_TRELLIS_POST_WHITE, FLBlocks.GRAPE_TRELLIS_POST,
            FLBlocks.GRAPE_STRING, FLBlocks.GRAPE_STRING_RED, FLBlocks.GRAPE_STRING_WHITE, FLBlocks.GRAPE_STRING_PLANT_RED, FLBlocks.GRAPE_STRING_PLANT_WHITE,
            FLBlocks.GRAPE_FLUFF_RED, FLBlocks.GRAPE_FLUFF_WHITE, FLBlocks.WILD_WHITE_GRAPES, FLBlocks.WILD_RED_GRAPES, FLBlocks.PUMPING_STATION, FLBlocks.IRRIGATION_TANK
        ).forEach(reg -> ItemBlockRenderTypes.setRenderLayer(reg.get(), cutout));

        ItemBlockRenderTypes.setRenderLayer(FLBlocks.SOLAR_DRIER.get(), translucent);

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
        FLBlocks.STOMPING_BARRELS.values().forEach(reg -> ItemBlockRenderTypes.setRenderLayer(reg.get(), cutout));
        FLBlocks.BARREL_PRESSES.values().forEach(reg -> ItemBlockRenderTypes.setRenderLayer(reg.get(), cutout));
        FLBlocks.WINE_SHELVES.values().forEach(reg -> ItemBlockRenderTypes.setRenderLayer(reg.get(), cutout));
        FLBlocks.CURED_OVEN_BOTTOM.values().forEach(reg -> ItemBlockRenderTypes.setRenderLayer(reg.get(), cutout));
        FLBlocks.CURED_OVEN_TOP.values().forEach(reg -> ItemBlockRenderTypes.setRenderLayer(reg.get(), cutout));
        FLBlocks.CURED_OVEN_HOPPER.values().forEach(reg -> ItemBlockRenderTypes.setRenderLayer(reg.get(), cutout));
        FLBlocks.CURED_OVEN_CHIMNEY.values().forEach(reg -> ItemBlockRenderTypes.setRenderLayer(reg.get(), cutout));
        FLBlocks.INSULATED_OVEN_BOTTOM.values().forEach(reg -> ItemBlockRenderTypes.setRenderLayer(reg.get(), cutout));
        ItemBlockRenderTypes.setRenderLayer(FLBlocks.REINFORCED_POURED_GLASS.get(), translucent);

        event.enqueueWork(() -> {
            TFCItems.FOOD.forEach((food, item) -> {
                if (FLItems.TFC_FRUITS.contains(food))
                {
                    registerDryProperty(item);
                }
            });
            FLItems.FRUITS.forEach((food, item) -> registerDryProperty(item));

            ItemProperties.register(FLItems.BEEHIVE_FRAME.get(), FLHelpers.identifier("queen"), (stack, a, b, c) -> {
                final BeeComponent bee = stack.get(FLComponents.BEE.get());
                return bee != null && bee.hasQueen() ? 1f : 0f;
            });

            PlacedItemBlockEntityRenderer.MODELS.putAll(Map.of(
                FLItems.HONEY_JAR.get(), translucent("block/jar/honey"),
                FLItems.COMPOST_JAR.get(), translucent("block/jar/compost"),
                FLItems.GUANO_JAR.get(), translucent("block/jar/guano"),
                FLItems.ROTTEN_COMPOST_JAR.get(), translucent("block/jar/rotten_compost"),
                FLItems.EMPTY_JAR_WITH_STAINLESS_STEEL_LID.get(), translucent("block/jar")
            ));
            FLItems.FRUIT_PRESERVES.forEach((fruit, item) -> PlacedItemBlockEntityRenderer.MODELS.put(item.get(), translucent("block/jar/" + fruit.getSerializedName())));
            FLItems.UNSEALED_FRUIT_PRESERVES.forEach((fruit, item) -> PlacedItemBlockEntityRenderer.MODELS.put(item.get(), translucent("block/jar/" + fruit.getSerializedName() + "_unsealed")));
        });
    }

    private static PlacedItemBlockEntityRenderer.Provider translucent(String model)
    {
        return new PlacedItemBlockEntityRenderer.Provider(ModelResourceLocation.standalone(FLHelpers.identifier(model)), RenderType.translucent());
    }

    public static void onMenuRegister(RegisterMenuScreensEvent event)
    {
        event.register(FLMenuTypes.BEEHIVE.get(), BeehiveScreen::new);
        event.register(FLMenuTypes.BARREL_PRESS.get(), BarrelPressScreen::new);
        event.register(FLMenuTypes.STOVETOP_GRILL.get(), StovetopGrillScreen::new);
        event.register(FLMenuTypes.STOVETOP_POT.get(), StovetopPotScreen::new);
        event.register(FLMenuTypes.KEG.get(), BigBarrelScreen::new);
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


        BuiltInRegistries.FLUID.entrySet().forEach(entry -> {
            if (Objects.requireNonNull(entry.getKey().location().getNamespace()).equals(FirmaLife.MOD_ID))
            {
                event.register(new DynamicFluidContainerModel.Colors(), entry.getValue().getBucket());
            }
        });

        event.register(new DynamicFluidContainerModel.Colors(), FLItems.HOLLOW_SHELL.get());
        event.register(new DynamicFluidContainerModel.Colors(), FLItems.WINE_GLASS.get());
    }

    public static void registerParticleFactories(RegisterParticleProvidersEvent event)
    {
        event.registerSpriteSet(FLParticles.GROWTH.get(), set -> new GlintParticleProvider(set, ChatFormatting.GREEN));
        event.registerSpriteSet(FLParticles.SPRINKLER.get(), set -> SprinklerParticle.provider(set, SprinklerParticle::new));
    }

    public static void registerModels(ModelEvent.RegisterAdditional event)
    {
        for (FLFruit fruit : FLFruit.values())
        {
            register(event, FLHelpers.identifier("block/jar/" + fruit.getSerializedName()));
            register(event, FLHelpers.identifier("block/jar/" + fruit.getSerializedName() + "_unsealed"));
        }
        register(event, FLHelpers.identifier("block/jar/compost"));
        register(event, FLHelpers.identifier("block/jar/rotten_compost"));
        register(event, FLHelpers.identifier("block/jar/guano"));
        register(event, FLHelpers.identifier("block/jar/honey"));

        BuiltInRegistries.ITEM.forEach(item -> {
            if (item instanceof WineBottleItem wine)
            {
                event.register(wine.getModelLocation());
            }
        });


        event.register(MixingBowlBlockEntityRenderer.SPOON_LOCATION);
        event.register(CompostTumblerBlockEntityRenderer.OPEN_MODEL);
        event.register(CompostTumblerBlockEntityRenderer.CLOSED_MODEL);
        event.register(BarrelPressBlockEntityRenderer.PRESS);
        event.register(SweeperBlockEntityRenderer.ARM);
        event.register(PickerBlockEntityRenderer.ARMS);
    }

    private static void register(ModelEvent.RegisterAdditional event, ResourceLocation id)
    {
        event.register(ModelResourceLocation.standalone(id));
    }

    public static void registerLoaders(ModelEvent.RegisterGeometryLoaders event)
    {
        register(event, "large_planter", new DynamicBlockModel.Loader(LargePlanterBakedModel::new));
        register(event, "hanging_planter", new DynamicBlockModel.Loader(HangingPlanterBlockModel::new));
        register(event, "bonsai_planter", new DynamicBlockModel.Loader(BonsaiPlanterBlockModel::new));
        register(event, "quad_planter", new DynamicBlockModel.Loader(QuadPlanterBlockModel::new));
        register(event, "hydroponic_planter", new DynamicBlockModel.Loader(HydroponicPlanterBlockModel::new));
        register(event, "trellis_planter", new DynamicBlockModel.Loader(TrellisPlanterBlockModel::new));
        register(event, "jarbnet", new DynamicBlockModel.Loader(JarbnetBlockModel::new));
        register(event, "jarring_station", new DynamicBlockModel.Loader(JarringStationBlockModel::new));
        register(event, "food_shelf", new DynamicBlockModel.Loader(FoodShelfBlockModel::new));
        register(event, "hanger", new DynamicBlockModel.Loader(HangerBlockModel::new));
        register(event, "wine_shelf", new DynamicBlockModel.Loader(WineShelfBlockModel::new));
    }

    private static void register(ModelEvent.RegisterGeometryLoaders event, String id, IGeometryLoader<?> loader)
    {
        event.register(FLHelpers.identifier(id), loader);
    }

    private static void registerDryProperty(Supplier<Item> item)
    {
        ItemProperties.register(item.get(), FLHelpers.identifier("dry"), (stack, a, b, c) -> {
            final IFood food = FoodCapability.get(stack);
            return food != null && food.hasTrait(FLFoodTraits.DRIED) ? 1f : 0f;
        });
    }


    public static void registerEntityRenderers(EntityRenderersEvent.RegisterRenderers event)
    {
        event.registerBlockEntityRenderer(FLBlockEntities.OVEN_TOP.get(), ctx -> new OvenBlockEntityRenderer());
        event.registerBlockEntityRenderer(FLBlockEntities.DRYING_MAT.get(), ctx -> new DryingMatBlockEntityRenderer(2f / 16));
        event.registerBlockEntityRenderer(FLBlockEntities.SOLAR_DRIER.get(), ctx -> new DryingMatBlockEntityRenderer(1f / 16));
        event.registerBlockEntityRenderer(FLBlockEntities.STRING.get(), ctx -> new StringBlockEntityRenderer());
        event.registerBlockEntityRenderer(FLBlockEntities.VAT.get(), ctx -> new VatBlockEntityRenderer());
        event.registerBlockEntityRenderer(FLBlockEntities.MIXING_BOWL.get(), ctx -> new MixingBowlBlockEntityRenderer());
        event.registerBlockEntityRenderer(FLBlockEntities.STOVETOP_GRILL.get(), ctx -> new StovetopGrillBlockEntityRenderer());
        event.registerBlockEntityRenderer(FLBlockEntities.STOVETOP_POT.get(), ctx -> new StovetopPotBlockEntityRenderer());
        event.registerBlockEntityRenderer(FLBlockEntities.PLATE.get(), ctx -> new PlateBlockEntityRenderer());
        event.registerBlockEntityRenderer(FLBlockEntities.HYDROPONIC_PLANTER.get(), ctx -> new HydroponicPlanterBlockEntityRenderer());
        event.registerBlockEntityRenderer(FLBlockEntities.COMPOST_TUMBLER.get(), ctx -> new CompostTumblerBlockEntityRenderer());
        event.registerBlockEntityRenderer(FLBlockEntities.STOMPING_BARREL.get(), ctx -> new StompingBarrelBlockEntityRenderer());
        event.registerBlockEntityRenderer(FLBlockEntities.BARREL_PRESS.get(), ctx -> new BarrelPressBlockEntityRenderer());
        event.registerBlockEntityRenderer(FLBlockEntities.PUMPING_STATION.get(), ctx -> new PumpingStationBlockEntityRenderer());
        event.registerBlockEntityRenderer(FLBlockEntities.PICKER.get(), ctx -> new PickerBlockEntityRenderer());
        event.registerBlockEntityRenderer(FLBlockEntities.SWEEPER.get(), ctx -> new SweeperBlockEntityRenderer());

        event.registerEntityRenderer(FLEntities.SEED_BALL.get(), ThrownItemRenderer::new);
        event.registerEntityRenderer(FLEntities.FLBEE.get(), FLBeeRenderer::new);
    }

    public static void onLayers(EntityRenderersEvent.RegisterLayerDefinitions event)
    {
        event.registerLayerDefinition(FLClientHelpers.modelIdentifier("peel"), PeelModel::createBodyLayer);
    }

    public static void registerExtensions(RegisterClientExtensionsEvent event)
    {
        event.registerItem(PeelItem.Extension.INSTANCE, FLItems.PEEL.get());
        //TODO check these for correctness, they might need tweaking. Also change from using TFC textures here?
        FLFluids.METALS.forEach((metal, holder) -> event.registerFluidType(
            new FluidRendererExtension(TFCFluids.ALPHA_MASK | metal.getColor(), ClientEventHandler.MOLTEN_STILL, ClientEventHandler.MOLTEN_FLOW, null, null),
            holder.getType()
        ));
        FLFluids.EXTRA_FLUIDS.forEach((fluid, holder) -> event.registerFluidType(
            new FluidRendererExtension(fluid.getColor(), ClientEventHandler.WATER_STILL, ClientEventHandler.WATER_FLOW, ClientEventHandler.WATER_OVERLAY, ClientEventHandler.UNDERWATER_LOCATION),
            holder.getType()
        ));
        FLFluids.WINE_FLUIDS.forEach((fluid, holder) -> event.registerFluidType(
            new FluidRendererExtension(fluid.getColor(), ClientEventHandler.WATER_STILL, ClientEventHandler.WATER_FLOW, ClientEventHandler.WATER_OVERLAY, ClientEventHandler.UNDERWATER_LOCATION),
            holder.getType()
        ));
    }

}
