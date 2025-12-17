package com.eerussianguy.firmalife.common.blocks;

import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.function.ToIntFunction;
import com.eerussianguy.firmalife.FirmaLife;
import com.eerussianguy.firmalife.common.FLTags;
import com.eerussianguy.firmalife.common.blockentities.BarrelPressBlockEntity;
import com.eerussianguy.firmalife.common.blockentities.CompostTumblerBlockEntity;
import com.eerussianguy.firmalife.common.blockentities.DryingMatBlockEntity;
import com.eerussianguy.firmalife.common.blockentities.FLBeehiveBlockEntity;
import com.eerussianguy.firmalife.common.blockentities.FLBlockEntities;
import com.eerussianguy.firmalife.common.blockentities.GrapePlantBlockEntity;
import com.eerussianguy.firmalife.common.blockentities.HydroponicPlanterBlockEntity;
import com.eerussianguy.firmalife.common.blockentities.JarringStationBlockEntity;
import com.eerussianguy.firmalife.common.blockentities.LargePlanterBlockEntity;
import com.eerussianguy.firmalife.common.blockentities.MixingBowlBlockEntity;
import com.eerussianguy.firmalife.common.blockentities.OvenBottomBlockEntity;
import com.eerussianguy.firmalife.common.blockentities.OvenHopperBlockEntity;
import com.eerussianguy.firmalife.common.blockentities.OvenTopBlockEntity;
import com.eerussianguy.firmalife.common.blockentities.PickerBlockEntity;
import com.eerussianguy.firmalife.common.blockentities.SprinklerBlockEntity;
import com.eerussianguy.firmalife.common.blockentities.StovetopGrillBlockEntity;
import com.eerussianguy.firmalife.common.blockentities.StovetopPotBlockEntity;
import com.eerussianguy.firmalife.common.blockentities.StringBlockEntity;
import com.eerussianguy.firmalife.common.blockentities.SweeperBlockEntity;
import com.eerussianguy.firmalife.common.blockentities.VatBlockEntity;
import com.eerussianguy.firmalife.common.blocks.greenhouse.BonsaiPlanterBlock;
import com.eerussianguy.firmalife.common.blocks.greenhouse.ClimateStationBlock;
import com.eerussianguy.firmalife.common.blocks.greenhouse.FloorSprinklerBlock;
import com.eerussianguy.firmalife.common.blocks.greenhouse.Greenhouse;
import com.eerussianguy.firmalife.common.blocks.greenhouse.HangingPlanterBlock;
import com.eerussianguy.firmalife.common.blocks.greenhouse.HydroponicPlanterBlock;
import com.eerussianguy.firmalife.common.blocks.greenhouse.LargePlanterBlock;
import com.eerussianguy.firmalife.common.blocks.greenhouse.PumpingStationBlock;
import com.eerussianguy.firmalife.common.blocks.greenhouse.QuadPlanterBlock;
import com.eerussianguy.firmalife.common.blocks.greenhouse.SprinklerBlock;
import com.eerussianguy.firmalife.common.blocks.greenhouse.SprinklerPipeBlock;
import com.eerussianguy.firmalife.common.blocks.greenhouse.TrellisPlanterBlock;
import com.eerussianguy.firmalife.common.blocks.plant.FLFruitBlocks;
import com.eerussianguy.firmalife.common.blocks.plant.GrapeFluffBlock;
import com.eerussianguy.firmalife.common.blocks.plant.GrapeGroundPlantOnStringBlock;
import com.eerussianguy.firmalife.common.blocks.plant.GrapeStringBlock;
import com.eerussianguy.firmalife.common.blocks.plant.GrapeStringWithPlantBlock;
import com.eerussianguy.firmalife.common.blocks.plant.GrapeTrellisPostBlock;
import com.eerussianguy.firmalife.common.blocks.plant.GrapeTrellisPostWithPlantBlock;
import com.eerussianguy.firmalife.common.blocks.plant.MutatingPlantBlock;
import com.eerussianguy.firmalife.common.capabilities.wine.WineType;
import com.eerussianguy.firmalife.common.items.FLFood;
import com.eerussianguy.firmalife.common.items.FLItems;
import com.eerussianguy.firmalife.common.util.Carving;
import com.eerussianguy.firmalife.common.util.ExtraFluid;
import com.eerussianguy.firmalife.common.util.FLFruit;
import com.eerussianguy.firmalife.common.util.FLMetal;
import com.eerussianguy.firmalife.common.util.FLPlant;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CarvedPumpkinBlock;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.FlowerPotBlock;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.TrapDoorBlock;
import net.minecraft.world.level.block.WallBlock;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.neoforged.neoforge.registries.DeferredRegister;

import net.dries007.tfc.common.blocks.DecorationBlockHolder;
import net.dries007.tfc.common.blocks.ExtendedBlock;
import net.dries007.tfc.common.blocks.ExtendedProperties;
import net.dries007.tfc.common.blocks.GroundcoverBlock;
import net.dries007.tfc.common.blocks.PouredGlassBlock;
import net.dries007.tfc.common.blocks.TFCBlocks.Id;
import net.dries007.tfc.common.blocks.crop.WildCropBlock;
import net.dries007.tfc.common.blocks.devices.JackOLanternBlock;
import net.dries007.tfc.common.blocks.plant.PlantBlock;
import net.dries007.tfc.common.blocks.rock.Ore;
import net.dries007.tfc.common.blocks.rock.Rock;
import net.dries007.tfc.common.blocks.wood.Wood;
import net.dries007.tfc.common.items.TFCItems;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.Metal;
import net.dries007.tfc.util.registry.RegistrationHelpers;

@SuppressWarnings("unused")
public class FLBlocks
{
    public static final DeferredRegister<Block> BLOCK = DeferredRegister.create(Registries.BLOCK, FirmaLife.MOD_ID);

    public static final Map<OvenType, Id<Block>> INSULATED_OVEN_BOTTOM = Helpers.mapOf(OvenType.class, type -> register("insulated_" + type.getName() + "oven_bottom", () -> new InsulatedOvenBottomBlock(ExtendedProperties.of().strength(4.0f).randomTicks().lightLevel(FLBlocks::lightEmission).sound(SoundType.STONE).noOcclusion().blockEntity(FLBlockEntities.OVEN_BOTTOM).serverTicks(OvenBottomBlockEntity::serverTick), null)));
    public static final Map<OvenType, Id<Block>> CURED_OVEN_BOTTOM = Helpers.mapOf(OvenType.class, type -> register("cured_" + type.getName() + "oven_bottom", () -> new OvenBottomBlock(ExtendedProperties.of().strength(4.0f).randomTicks().lightLevel(FLBlocks::lightEmission).sound(SoundType.STONE).noOcclusion().blockEntity(FLBlockEntities.OVEN_BOTTOM).serverTicks(OvenBottomBlockEntity::serverTick), null, INSULATED_OVEN_BOTTOM.get(type))));
    public static final Map<OvenType, Id<Block>> CURED_OVEN_TOP = Helpers.mapOf(OvenType.class, type -> register("cured_" + type.getName() + "oven_top", () -> new OvenTopBlock(ExtendedProperties.of().strength(4.0f).randomTicks().sound(SoundType.STONE).noOcclusion().blockEntity(FLBlockEntities.OVEN_TOP).serverTicks(OvenTopBlockEntity::serverTick), null)));
    public static final Map<OvenType, Id<Block>> CURED_OVEN_CHIMNEY = Helpers.mapOf(OvenType.class, type -> register("cured_" + type.getName() + "oven_chimney", () -> new OvenChimneyBlock(Properties.of().strength(4.0f).sound(SoundType.STONE).noOcclusion(), null)));
    public static final Map<OvenType, Id<Block>> CURED_OVEN_HOPPER = Helpers.mapOf(OvenType.class, type -> register("cured_" + type.getName() + "oven_hopper", () -> new OvenHopperBlock(ExtendedProperties.of().strength(4.0f).sound(SoundType.STONE).blockEntity(FLBlockEntities.OVEN_HOPPER).serverTicks(OvenHopperBlockEntity::serverTick), null)));
    public static final Map<OvenType, Id<Block>> OVEN_COUNTERTOP = Helpers.mapOf(OvenType.class, type -> register(type.getTrueName() + "_countertop", () -> new Block(Properties.of().strength(4.0f).sound(SoundType.STONE))));

    public static final Id<Block> OVEN_BOTTOM = register("oven_bottom", () -> new OvenBottomBlock(ExtendedProperties.of().strength(4.0f).randomTicks().lightLevel(FLBlocks::lightEmission).sound(SoundType.STONE).noOcclusion().blockEntity(FLBlockEntities.OVEN_BOTTOM).serverTicks(OvenBottomBlockEntity::serverTick), FLBlocks.CURED_OVEN_BOTTOM.get(OvenType.BRICK)));
    public static final Id<Block> OVEN_TOP = register("oven_top", () -> new OvenTopBlock(ExtendedProperties.of().strength(4.0f).randomTicks().sound(SoundType.STONE).noOcclusion().blockEntity(FLBlockEntities.OVEN_TOP).serverTicks(OvenTopBlockEntity::serverTick), FLBlocks.CURED_OVEN_TOP.get(OvenType.BRICK)));
    public static final Id<Block> OVEN_CHIMNEY = register("oven_chimney", () -> new OvenChimneyBlock(Properties.of().strength(4.0f).sound(SoundType.STONE).noOcclusion(), FLBlocks.CURED_OVEN_CHIMNEY.get(OvenType.BRICK)));
    public static final Id<Block> OVEN_HOPPER = register("oven_hopper", () -> new OvenHopperBlock(ExtendedProperties.of().strength(4.0f).sound(SoundType.STONE).blockEntity(FLBlockEntities.OVEN_HOPPER), FLBlocks.CURED_OVEN_HOPPER.get(OvenType.BRICK)));

    public static final Id<Block> RUSTIC_BRICKS = register("rustic_bricks", () -> new Block(Properties.of().sound(SoundType.STONE).strength(2.0f, 10).requiresCorrectToolForDrops()));
    public static final DecorationBlockHolder RUSTIC_BRICK_DECOR = registerDecorations(
        "rustic_bricks",
        () -> new SlabBlock(brickProperties()),
        () -> new StairBlock(RUSTIC_BRICKS.get().defaultBlockState(), brickProperties()),
        () -> new WallBlock(brickProperties()),
        new Item.Properties()
    );
    public static final Id<Block> TILES = register("tiles", () -> new Block(Properties.of().sound(SoundType.STONE).strength(2.0f, 10).requiresCorrectToolForDrops()));
    public static final DecorationBlockHolder TILE_DECOR = registerDecorations(
        "tiles",
        () -> new SlabBlock(brickProperties()),
        () -> new StairBlock(RUSTIC_BRICKS.get().defaultBlockState(), brickProperties()),
        () -> new WallBlock(brickProperties()),
        new Item.Properties()
    );

    public static final Id<Block> ASHTRAY = register("ashtray", () -> new AshtrayBlock(ExtendedProperties.of().sound(SoundType.METAL).strength(2f).randomTicks().blockEntity(FLBlockEntities.ASHTRAY)));
    public static final Id<Block> STOVETOP_GRILL = registerNoItem("stovetop_grill", () -> new StovetopGrillBlock(ExtendedProperties.of().sound(SoundType.METAL).strength(2f).blockEntity(FLBlockEntities.STOVETOP_GRILL).serverTicks(StovetopGrillBlockEntity::serverTick).noOcclusion()));
    public static final Id<Block> STOVETOP_POT = registerNoItem("stovetop_pot", () -> new StovetopPotBlock(ExtendedProperties.of().sound(SoundType.METAL).strength(2f).blockEntity(FLBlockEntities.STOVETOP_POT).serverTicks(StovetopPotBlockEntity::serverTick).noOcclusion()));
    public static final Id<Block> DRYING_MAT = register("drying_mat", () -> new DryingMatBlock(ExtendedProperties.of().strength(0.6f).sound(SoundType.AZALEA_LEAVES).flammable(60, 30).pushReaction(PushReaction.DESTROY).blockEntity(FLBlockEntities.DRYING_MAT).serverTicks(DryingMatBlockEntity::serverTick)));
    public static final Id<Block> SOLAR_DRIER = register("solar_drier", () -> new SolarDrierBlock(ExtendedProperties.of().strength(3.0f).sound(SoundType.WOOD).noOcclusion().flammable(60, 30).pushReaction(PushReaction.DESTROY).blockEntity(FLBlockEntities.SOLAR_DRIER).serverTicks(DryingMatBlockEntity::serverTick)));
    public static final Id<Block> BEEHIVE = register("beehive", () -> new WoodenBeehiveBlock(ExtendedProperties.of().strength(0.6f).sound(SoundType.WOOD).flammable(60, 30).randomTicks().blockEntity(FLBlockEntities.BEEHIVE).serverTicks(FLBeehiveBlockEntity::serverTick)));
    public static final Id<Block> SKEP = register("skep", () -> new SkepBlock(ExtendedProperties.of().strength(0.6f).sound(SoundType.MOSS).flammable(60, 30).randomTicks().noOcclusion().blockEntity(FLBlockEntities.BEEHIVE).serverTicks(FLBeehiveBlockEntity::serverTick)));
    public static final Id<Block> WILD_BEEHIVE = register("wild_beehive", () -> new WildBeehiveBlock(ExtendedProperties.of().strength(0.6f).noOcclusion().sound(SoundType.MOSS).flammable(60, 30).randomTicks()));
    public static final Id<Block> COMPOST_TUMBLER = register("compost_tumbler", () -> new CompostTumblerBlock(ExtendedProperties.of().strength(0.6F).noOcclusion().sound(SoundType.METAL).blockEntity(FLBlockEntities.COMPOST_TUMBLER).serverTicks(CompostTumblerBlockEntity::serverTick)));
    public static final Id<Block> WOOL_STRING = registerNoItem("wool_string", () -> new StringBlock(ExtendedProperties.of().noCollission().strength(1.0f).sound(SoundType.WOOL).randomTicks().blockEntity(FLBlockEntities.STRING).serverTicks(StringBlockEntity::serverTick), TFCItems.WOOL_YARN));
    public static final Id<Block> MIXING_BOWL = register("mixing_bowl", () -> new MixingBowlBlock(ExtendedProperties.of().sound(SoundType.STONE).strength(1f).noOcclusion().blockEntity(FLBlockEntities.MIXING_BOWL).ticks(MixingBowlBlockEntity::serverTick, MixingBowlBlockEntity::clientTick)));
    public static final Id<Block> VAT = register("vat", () -> new VatBlock(ExtendedProperties.of().sound(SoundType.METAL).strength(1f).noOcclusion().blockEntity(FLBlockEntities.VAT).serverTicks(VatBlockEntity::serverTick)));
    public static final Id<Block> JARRING_STATION = register("jarring_station", () -> new JarringStationBlock(ExtendedProperties.of().sound(SoundType.METAL).strength(1f).noOcclusion().blockEntity(FLBlockEntities.JARRING_STATION).ticks(JarringStationBlockEntity::tick)));
    public static final Id<Block> PLATE = register("plate", () -> ConsumingBlock.plate(ExtendedProperties.of().sound(SoundType.WOOD).strength(1f).noOcclusion().blockEntity(FLBlockEntities.PLATE)));
    public static final Id<Block> REINFORCED_POURED_GLASS = register("reinforced_poured_glass", () -> new PouredGlassBlock(ExtendedProperties.of().strength(0.3F).sound(SoundType.GLASS).pushReaction(PushReaction.DESTROY).noOcclusion().requiresCorrectToolForDrops(), FLItems.REINFORCED_GLASS));
    public static final Id<Block> PICKER = register("picker", () -> new PickerBlock(ExtendedProperties.of().strength(0.3f).sound(SoundType.METAL).pushReaction(PushReaction.BLOCK).noOcclusion().requiresCorrectToolForDrops().blockEntity(FLBlockEntities.PICKER).ticks(PickerBlockEntity::tick)));
    public static final Id<Block> SWEEPER = register("sweeper", () -> new SweeperBlock(ExtendedProperties.of().strength(0.3f).sound(SoundType.METAL).pushReaction(PushReaction.BLOCK).noOcclusion().requiresCorrectToolForDrops().blockEntity(FLBlockEntities.SWEEPER).serverTicks(SweeperBlockEntity::serverTick)));

    public static final Id<Block> CHEDDAR_WHEEL = register("cheddar_wheel", () -> new CheeseWheelBlock(wheelProperties(), FLItems.FOODS.get(FLFood.CHEDDAR)));
    public static final Id<Block> CHEVRE_WHEEL = register("chevre_wheel", () -> new CheeseWheelBlock(wheelProperties(), FLItems.FOODS.get(FLFood.CHEVRE)));
    public static final Id<Block> RAJYA_METOK_WHEEL = register("rajya_metok_wheel", () -> new CheeseWheelBlock(wheelProperties(), FLItems.FOODS.get(FLFood.RAJYA_METOK)));
    public static final Id<Block> GOUDA_WHEEL = register("gouda_wheel", () -> new CheeseWheelBlock(wheelProperties(), FLItems.FOODS.get(FLFood.GOUDA)));
    public static final Id<Block> FETA_WHEEL = register("feta_wheel", () -> new CheeseWheelBlock(wheelProperties(), FLItems.FOODS.get(FLFood.FETA)));
    public static final Id<Block> SHOSHA_WHEEL = register("shosha_wheel", () -> new CheeseWheelBlock(wheelProperties(), FLItems.FOODS.get(FLFood.SHOSHA)));

    public static final Id<Block> CLIMATE_STATION = register("climate_station", () -> new ClimateStationBlock(ExtendedProperties.of().strength(3.0f).sound(SoundType.WOOD).randomTicks().blockEntity(FLBlockEntities.CLIMATE_STATION).flammable(60, 30)));
    public static final Id<Block> LARGE_PLANTER = register("large_planter", () -> new LargePlanterBlock(ExtendedProperties.of().sound(SoundType.STONE).strength(1f).randomTicks().blockEntity(FLBlockEntities.LARGE_PLANTER).serverTicks(LargePlanterBlockEntity::serverTick)));
    public static final Id<Block> QUAD_PLANTER = register("quad_planter", () -> new QuadPlanterBlock(ExtendedProperties.of().sound(SoundType.STONE).strength(1f).randomTicks().blockEntity(FLBlockEntities.QUAD_PLANTER).serverTicks(LargePlanterBlockEntity::serverTick)));
    public static final Id<Block> HYDROPONIC_PLANTER = register("hydroponic_planter", () -> new HydroponicPlanterBlock(ExtendedProperties.of().sound(SoundType.STONE).strength(1f).randomTicks().blockEntity(FLBlockEntities.HYDROPONIC_PLANTER).serverTicks(HydroponicPlanterBlockEntity::hydroponicServerTick)));
    public static final Id<Block> BONSAI_PLANTER = register("bonsai_planter", () -> new BonsaiPlanterBlock(ExtendedProperties.of().sound(SoundType.STONE).strength(1f).randomTicks().blockEntity(FLBlockEntities.BONSAI_PLANTER).serverTicks(LargePlanterBlockEntity::serverTick)));
    public static final Id<Block> HANGING_PLANTER = register("hanging_planter", () -> new HangingPlanterBlock(ExtendedProperties.of().sound(SoundType.STONE).strength(1f).randomTicks().blockEntity(FLBlockEntities.HANGING_PLANTER).serverTicks(LargePlanterBlockEntity::serverTick)));
    public static final Id<Block> TRELLIS_PLANTER = register("trellis_planter", () -> new TrellisPlanterBlock(ExtendedProperties.of().sound(SoundType.STONE).strength(1f).randomTicks().blockEntity(FLBlockEntities.TRELLIS_PLANTER).serverTicks(LargePlanterBlockEntity::serverTick)));

    public static final Id<Block> SEALED_BRICKS = register("sealed_bricks", () -> new Block(Properties.of().mapColor(MapColor.COLOR_BLACK).sound(SoundType.STONE).strength(2.0f, 10).requiresCorrectToolForDrops()));
    public static final Id<Block> POLISHED_SEALED_BRICKS = register("polished_sealed_bricks", () -> new Block(Properties.of().mapColor(MapColor.COLOR_BLACK).sound(SoundType.STONE).strength(2.0f, 10).requiresCorrectToolForDrops()));
    public static final Id<Block> CHISELED_SEALED_BRICKS = register("chiseled_sealed_bricks", () -> new Block(Properties.of().mapColor(MapColor.COLOR_BLACK).sound(SoundType.STONE).strength(2.0f, 10).requiresCorrectToolForDrops()));
    public static final Id<Block> SEALED_DOOR = register("sealed_door", () -> new DoorBlock(BlockSetType.STONE, Properties.of().mapColor(MapColor.COLOR_BLACK).sound(SoundType.STONE).strength(2.0f, 10).requiresCorrectToolForDrops()));
    public static final Id<Block> SEALED_TRAPDOOR = register("sealed_trapdoor", () -> new TrapDoorBlock(BlockSetType.STONE, Properties.of().mapColor(MapColor.COLOR_BLACK).sound(SoundType.STONE).strength(2.0f, 10).requiresCorrectToolForDrops()));
    public static final Id<Block> SEALED_WALL = register("sealed_wall", () -> new WallBlock(Properties.of().mapColor(MapColor.COLOR_BLACK).sound(SoundType.STONE).strength(2.0f, 10).requiresCorrectToolForDrops()));
    public static final Id<Block> DARK_LADDER = register("dark_ladder", () -> new FLLadderBlock(Properties.of().mapColor(MapColor.COLOR_BLACK).strength(0.4F).sound(SoundType.LADDER).noOcclusion()));

    public static final Id<Block> HOLLOW_SHELL = registerNoItem("hollow_shell", () -> new GroundcoverBlock(ExtendedProperties.of().strength(0.05F, 0.0F).sound(SoundType.NETHER_WART).noCollission(), GroundcoverBlock.SMALL));
    public static final Id<Block> TREATED_WOOD = register("treated_wood", () -> new ExtendedBlock(ExtendedProperties.of().mapColor(MapColor.COLOR_BROWN).sound(SoundType.WOOL).strength(2f).flammableLikePlanks()));
    public static final Id<Block> PUMPING_STATION = register("pumping_station", () -> new PumpingStationBlock(ExtendedProperties.of().mapColor(MapColor.COLOR_ORANGE).requiresCorrectToolForDrops().noOcclusion().strength(4f).sound(SoundType.METAL).blockEntity(FLBlockEntities.PUMPING_STATION)));
    public static final Id<Block> IRRIGATION_TANK = register("irrigation_tank", () -> new Block(Properties.of().mapColor(MapColor.COLOR_ORANGE).requiresCorrectToolForDrops().noOcclusion().strength(4f).sound(SoundType.METAL)));
    public static final Id<Block> COPPER_PIPE = register("copper_pipe", () -> new SprinklerPipeBlock(ExtendedProperties.of().strength(2f).noOcclusion().sound(SoundType.METAL)));
    public static final Id<Block> OXIDIZED_COPPER_PIPE = register("oxidized_copper_pipe", () -> new SprinklerPipeBlock(ExtendedProperties.of().strength(2f).noOcclusion().sound(SoundType.METAL)));
    public static final Id<Block> SPRINKLER = registerNoItem("sprinkler", () -> new SprinklerBlock(ExtendedProperties.of().strength(2f).noOcclusion().sound(SoundType.METAL).blockEntity(FLBlockEntities.SPRINKLER).serverTicks(SprinklerBlockEntity::serverTick)));
    public static final Id<Block> FLOOR_SPRINKLER = registerNoItem("floor_sprinkler", () -> new FloorSprinklerBlock(ExtendedProperties.of().strength(2f).noOcclusion().sound(SoundType.METAL).blockEntity(FLBlockEntities.SPRINKLER).serverTicks(SprinklerBlockEntity::serverTick)));

    public static final Id<Block> BUTTERFLY_GRASS = register("plant/butterfly_grass", () -> MutatingPlantBlock.create(FLPlant.BUTTERFLY_GRASS, FLPlant.BUTTERFLY_GRASS.nonSolidFire(), FLTags.Blocks.BUTTERFLY_GRASS_MUTANTS));
    public static final Id<Block> POTTED_BUTTERFLY_GRASS = registerNoItem("plant/potted/butterfly_grass", () -> new FlowerPotBlock(() -> (FlowerPotBlock) Blocks.FLOWER_POT, BUTTERFLY_GRASS, Properties.of().instabreak().noOcclusion()));
    public static final Id<Block> WILD_WHITE_GRAPES = register("plant/wild_white_grapes", () -> new WildCropBlock(ExtendedProperties.of(MapColor.PLANT).noCollission().randomTicks().strength(0.4F).sound(SoundType.CROP).flammable(60, 30).randomTicks()));
    public static final Id<Block> WILD_RED_GRAPES = register("plant/wild_red_grapes", () -> new WildCropBlock(ExtendedProperties.of(MapColor.PLANT).noCollission().randomTicks().strength(0.4F).sound(SoundType.CROP).flammable(60, 30).randomTicks()));

    public static final Map<Herb, Id<Block>> HERBS = Helpers.mapOf(Herb.class, herb -> register("plant/" + herb.name(), () -> PlantBlock.create(FLPlant.HERB, FLPlant.HERB.nonSolidFire())));
    public static final Map<Herb, Id<Block>> POTTED_HERBS = Helpers.mapOf(Herb.class, herb -> registerNoItem("plant/potted/" + herb.name(), () -> new FlowerPotBlock(() -> (FlowerPotBlock) Blocks.FLOWER_POT, HERBS.get(herb), Properties.of().instabreak().noOcclusion())));

    public static final Map<FLFruitBlocks.Tree, Id<Block>> FRUIT_TREE_LEAVES = Helpers.mapOf(FLFruitBlocks.Tree.class, tree -> register("plant/" + tree.name() + "_leaves", tree::createLeaves));
    public static final Map<FLFruitBlocks.Tree, Id<Block>> FRUIT_TREE_BRANCHES = Helpers.mapOf(FLFruitBlocks.Tree.class, tree -> registerNoItem("plant/" + tree.name() + "_branch", tree::createBranch));
    public static final Map<FLFruitBlocks.Tree, Id<Block>> FRUIT_TREE_GROWING_BRANCHES = Helpers.mapOf(FLFruitBlocks.Tree.class, tree -> registerNoItem("plant/" + tree.name() + "_growing_branch", tree::createGrowingBranch));
    public static final Map<FLFruitBlocks.Tree, Id<Block>> FRUIT_TREE_SAPLINGS = Helpers.mapOf(FLFruitBlocks.Tree.class, tree -> register("plant/" + tree.name() + "_sapling", tree::createSapling));
    public static final Map<FLFruitBlocks.Tree, Id<Block>> FRUIT_TREE_POTTED_SAPLINGS = Helpers.mapOf(FLFruitBlocks.Tree.class, tree -> registerNoItem("plant/potted/" + tree.name() + "_sapling", tree::createPottedSapling));

    public static final Map<FLFruitBlocks.StationaryBush, Id<Block>> STATIONARY_BUSHES = Helpers.mapOf(FLFruitBlocks.StationaryBush.class, bush -> register("plant/" + bush.name() + "_bush", bush::create));

    public static final Map<Wood, Id<Block>> FOOD_SHELVES = Helpers.mapOf(Wood.class, wood -> register("wood/food_shelf/" + wood.getSerializedName(), () -> new FoodShelfBlock(shelfProperties().mapColor(wood.woodColor()))));
    public static final Map<Wood, Id<Block>> HANGERS = Helpers.mapOf(Wood.class, wood -> register("wood/hanger/" + wood.getSerializedName(), () -> new HangerBlock(hangerProperties().mapColor(wood.woodColor()))));
    public static final Map<Wood, Id<Block>> JARBNETS = Helpers.mapOf(Wood.class, wood -> register("wood/jarbnet/" + wood.getSerializedName(), () -> new JarbnetBlock(jarbnetProperties().mapColor(wood.woodColor()))));
    public static final Map<Wood, Id<Block>> KEGS = Helpers.mapOf(Wood.class, wood -> register("wood/keg/" + wood.getSerializedName(), () -> new KegBlock(ExtendedProperties.of().mapColor(wood.woodColor()).sound(SoundType.WOOD).noOcclusion().strength(10f).pushReaction(PushReaction.BLOCK).flammableLikeLogs().blockEntity(FLBlockEntities.KEG))));
    public static final Map<Wood, Id<Block>> WINE_SHELVES = Helpers.mapOf(Wood.class, wood -> register("wood/wine_shelf/" + wood.getSerializedName(), () -> new WineShelfBlock(ExtendedProperties.of().mapColor(wood.woodColor()).sound(SoundType.WOOD).noOcclusion().strength(4f).pushReaction(PushReaction.BLOCK).flammableLikeLogs().blockEntity(FLBlockEntities.WINE_SHELF))));
    public static final Map<Wood, Id<Block>> STOMPING_BARRELS = Helpers.mapOf(Wood.class, wood -> register("wood/stomping_barrel/" + wood.getSerializedName(), () -> new StompingBarrelBlock(ExtendedProperties.of().mapColor(wood.woodColor()).sound(SoundType.WOOD).noOcclusion().strength(4f).pushReaction(PushReaction.BLOCK).flammableLikeLogs().blockEntity(FLBlockEntities.STOMPING_BARREL))));
    public static final Map<Wood, Id<Block>> BARREL_PRESSES = Helpers.mapOf(Wood.class, wood -> register("wood/barrel_press/" + wood.getSerializedName(), () -> new BarrelPressBlock(ExtendedProperties.of().mapColor(wood.woodColor()).sound(SoundType.WOOD).noOcclusion().strength(4f).pushReaction(PushReaction.BLOCK).flammableLikeLogs().blockEntity(FLBlockEntities.BARREL_PRESS).ticks(BarrelPressBlockEntity::tick))));

    public static final Id<Block> GRAPE_FLUFF_RED = registerNoItem("grape_fluff_red", () -> new GrapeFluffBlock(ExtendedProperties.of().noLootTable().noOcclusion().noCollission().strength(1.0f).sound(SoundType.CROP).flammableLikeLeaves()));
    public static final Id<Block> GRAPE_FLUFF_WHITE = registerNoItem("grape_fluff_white", () -> new GrapeFluffBlock(ExtendedProperties.of().noLootTable().noOcclusion().noCollission().strength(1.0f).sound(SoundType.CROP).flammableLikeLeaves()));
    public static final Id<Block> GRAPE_TRELLIS_POST = register("grape_trellis_post", () -> new GrapeTrellisPostBlock(ExtendedProperties.of().noOcclusion().strength(4f).sound(SoundType.WOOD).mapColor(MapColor.COLOR_BROWN).flammableLikeLogs()));
    public static final Id<Block> GRAPE_TRELLIS_POST_RED = registerNoItem("grape_trellis_post_red", () -> new GrapeTrellisPostWithPlantBlock(ExtendedProperties.of().noOcclusion().strength(4f).sound(SoundType.WOOD).mapColor(MapColor.COLOR_BROWN).flammableLikeLogs(), FLItems.FRUITS.get(FLFruit.RED_GRAPES)));
    public static final Id<Block> GRAPE_TRELLIS_POST_WHITE = registerNoItem("grape_trellis_post_white", () -> new GrapeTrellisPostWithPlantBlock(ExtendedProperties.of().noOcclusion().strength(4f).sound(SoundType.WOOD).mapColor(MapColor.COLOR_BROWN).flammableLikeLogs(), FLItems.FRUITS.get(FLFruit.WHITE_GRAPES)));
    public static final Id<Block> GRAPE_STRING = registerNoItem("grape_string", () -> new GrapeStringBlock(ExtendedProperties.of().noOcclusion().strength(1.0f).sound(SoundType.WOOL).flammableLikeLogs()));
    public static final Id<Block> GRAPE_STRING_RED = registerNoItem("grape_string_red", () -> new GrapeStringWithPlantBlock(ExtendedProperties.of().noOcclusion().strength(1.0f).sound(SoundType.WOOL).flammableLikeLogs(), GRAPE_TRELLIS_POST_RED, GRAPE_FLUFF_RED, FLItems.FRUITS.get(FLFruit.RED_GRAPES)));
    public static final Id<Block> GRAPE_STRING_WHITE = registerNoItem("grape_string_white", () -> new GrapeStringWithPlantBlock(ExtendedProperties.of().noOcclusion().strength(1.0f).sound(SoundType.WOOL).flammableLikeLogs(), GRAPE_TRELLIS_POST_WHITE, GRAPE_FLUFF_WHITE, FLItems.FRUITS.get(FLFruit.WHITE_GRAPES)));
    public static final Id<Block> GRAPE_STRING_PLANT_RED = registerNoItem("grape_string_plant_red", () -> new GrapeGroundPlantOnStringBlock(ExtendedProperties.of().randomTicks().noOcclusion().strength(1.0f).sound(SoundType.CROP).flammableLikeLogs().blockEntity(FLBlockEntities.GRAPE_PLANT).serverTicks(GrapePlantBlockEntity::serverTick), GRAPE_STRING_RED));
    public static final Id<Block> GRAPE_STRING_PLANT_WHITE = registerNoItem("grape_string_plant_white", () -> new GrapeGroundPlantOnStringBlock(ExtendedProperties.of().randomTicks().noOcclusion().strength(1.0f).sound(SoundType.CROP).flammableLikeLogs().blockEntity(FLBlockEntities.GRAPE_PLANT).serverTicks(GrapePlantBlockEntity::serverTick), GRAPE_STRING_WHITE));

    public static final Map<Carving, Id<Block>> CARVED_PUMPKINS = Helpers.mapOf(Carving.class, carve ->
        register("carved_pumpkin/" + carve.getSerializedName(), () -> new CarvedPumpkinBlock(Properties.of().mapColor(MapColor.COLOR_ORANGE).strength(1.0F).sound(SoundType.WOOD).isValidSpawn(FLBlocks::always)))
    );

    public static final Map<Carving, Id<Block>> JACK_O_LANTERNS = Helpers.mapOf(Carving.class, carve ->
        register("lit_pumpkin/" + carve.getSerializedName(), () -> new JackOLanternBlock(ExtendedProperties.of(Properties.of().mapColor(MapColor.COLOR_ORANGE).strength(1.0F).sound(SoundType.WOOD).randomTicks().isValidSpawn(FLBlocks::always).lightLevel(alwaysLit())).blockEntity(FLBlockEntities.TICK_COUNTER), CARVED_PUMPKINS.get(carve)))
    );

    public static final Id<Block> SMALL_CHROMITE = register("ore/small_chromite", () -> GroundcoverBlock.looseOre(Properties.of().strength(0.05F, 0.0F).sound(SoundType.NETHER_ORE).noCollission()));
    public static final Map<Rock, Map<Ore.Grade, Id<Block>>> CHROMITE_ORES = Helpers.mapOf(Rock.class, rock ->
        Helpers.mapOf(Ore.Grade.class, grade ->
            register(("ore/" + grade.name() + "_chromite" + "/" + rock.name()), () -> new Block(Properties.of().sound(SoundType.STONE).strength(3, 10).requiresCorrectToolForDrops()))
        )
    );

    public static final Map<Greenhouse, Map<Greenhouse.BlockType, Id<Block>>> GREENHOUSE_BLOCKS = Helpers.mapOf(Greenhouse.class, greenhouse ->
        Helpers.mapOf(Greenhouse.BlockType.class, type ->
            register(greenhouse.name() + "_greenhouse_" + type.name(), type.create(greenhouse), type.createBlockItem(new Item.Properties()))
        )
    );

    public static final Map<FLMetal, Map<Metal.BlockType, Id<Block>>> METALS = Helpers.mapOf(FLMetal.class, metal ->
        Helpers.mapOf(Metal.BlockType.class, type -> type.has(Metal.BISMUTH), type ->
            register(type.createName(metal), type.create(metal), type.createBlockItem(new Item.Properties()))
        )
    );

    public static final Map<FLMetal, Id<LiquidBlock>> METAL_FLUIDS = Helpers.mapOf(FLMetal.class, metal ->
        registerNoItem("fluid/metal/" + metal.name(), () -> new LiquidBlock(FLFluids.METALS.get(metal).source().get(), Properties.ofFullCopy(Blocks.LAVA).noLootTable()))
    );

    public static final Map<ExtraFluid, Id<LiquidBlock>> EXTRA_FLUIDS = Helpers.mapOf(ExtraFluid.class, fluid ->
        registerNoItem("fluid/" + fluid.getSerializedName(), () -> new LiquidBlock(FLFluids.EXTRA_FLUIDS.get(fluid).source().get(), Properties.ofFullCopy(Blocks.WATER).noLootTable()))
    );

    public static final Map<WineType, Id<LiquidBlock>> WINE_FLUIDS = Helpers.mapOf(WineType.class, fluid ->
        registerNoItem("fluid/" + fluid.getSerializedName(), () -> new LiquidBlock(FLFluids.WINE_FLUIDS.get(fluid).source().get(), Properties.ofFullCopy(Blocks.WATER).noLootTable()))
    );


    public static void registerFlowerPotFlowers()
    {
        FlowerPotBlock pot = (FlowerPotBlock) Blocks.FLOWER_POT;
        FRUIT_TREE_POTTED_SAPLINGS.forEach((plant, reg) -> pot.addPlant(FRUIT_TREE_SAPLINGS.get(plant).getId(), reg));
        POTTED_HERBS.forEach((herb, reg) -> pot.addPlant(HERBS.get(herb).getId(), reg));
        pot.addPlant(BUTTERFLY_GRASS.getId(), POTTED_BUTTERFLY_GRASS);
    }

    public static Properties brickProperties()
    {
        return Properties.of().sound(SoundType.STONE).strength(2.0f, 10).requiresCorrectToolForDrops();
    }

    public static ExtendedProperties shelfProperties()
    {
        return ExtendedProperties.of().strength(0.3F).sound(SoundType.WOOD).noOcclusion().blockEntity(FLBlockEntities.FOOD_SHELF);
    }

    public static ExtendedProperties hangerProperties()
    {
        return ExtendedProperties.of().strength(0.3F).sound(SoundType.WOOD).noOcclusion().blockEntity(FLBlockEntities.HANGER);
    }

    public static ExtendedProperties jarbnetProperties()
    {
        return ExtendedProperties.of().strength(0.3F).sound(SoundType.WOOD).noOcclusion().randomTicks().lightLevel(s -> s.getValue(JarbnetBlock.LIT) ? 11 : 0).blockEntity(FLBlockEntities.JARBNET);
    }

    public static ExtendedProperties wheelProperties()
    {
        return ExtendedProperties.of().sound(SoundType.WART_BLOCK).strength(2f).randomTicks().blockEntity(FLBlockEntities.TICK_COUNTER);
    }

    public static ExtendedProperties jarProperties()
    {
        return ExtendedProperties.of(Properties.of().noCollission().noOcclusion().instabreak().sound(SoundType.GLASS).randomTicks());
    }

    private static ToIntFunction<BlockState> alwaysLit()
    {
        return s -> 15;
    }

    private static boolean always(BlockState state, BlockGetter level, BlockPos pos, EntityType<?> type)
    {
        return true;
    }

    public static int lightEmission(BlockState state)
    {
        return state.getValue(BlockStateProperties.LIT) ? 15 : 0;
    }

    private static <T1 extends SlabBlock, T2 extends StairBlock, T3 extends WallBlock> DecorationBlockHolder registerDecorations(String baseName, Supplier<T1> slab, Supplier<T2> stair, Supplier<T3> wall, Item.Properties properties)
    {
        return new DecorationBlockHolder(
            register(baseName + "_slab", slab, b -> new BlockItem(b, properties)),
            register(baseName + "_stairs", stair, b -> new BlockItem(b, properties)),
            register(baseName + "_wall", wall, b -> new BlockItem(b, properties))
        );
    }


    private static <T extends Block> Id<T> registerNoItem(String name, Supplier<T> blockSupplier)
    {
        return register(name, blockSupplier, (Function<T, ? extends BlockItem>) null);
    }

    private static <T extends Block> Id<T> register(String name, Supplier<T> blockSupplier)
    {
        return register(name, blockSupplier, block -> new BlockItem(block, new Item.Properties()));
    }

    private static <T extends Block> Id<T> register(String name, Supplier<T> blockSupplier, Item.Properties blockItemProperties)
    {
        return register(name, blockSupplier, block -> new BlockItem(block, blockItemProperties));
    }

    private static <T extends Block> Id<T> register(String name, Supplier<T> blockSupplier, @Nullable Function<T, ? extends BlockItem> blockItemFactory)
    {
        return new Id<>(RegistrationHelpers.registerBlock(BLOCK, FLItems.ITEM, name, blockSupplier, blockItemFactory));
    }

}
