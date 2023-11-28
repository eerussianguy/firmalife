package com.eerussianguy.firmalife.common.blocks;

import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.function.ToIntFunction;
import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import com.eerussianguy.firmalife.FirmaLife;
import com.eerussianguy.firmalife.common.FLTags;
import com.eerussianguy.firmalife.common.blockentities.*;
import com.eerussianguy.firmalife.common.blocks.greenhouse.*;
import com.eerussianguy.firmalife.common.blocks.plant.FLFruitBlocks;
import com.eerussianguy.firmalife.common.blocks.plant.MutatingPlantBlock;
import com.eerussianguy.firmalife.common.items.FLFood;
import com.eerussianguy.firmalife.common.items.FLItems;
import com.eerussianguy.firmalife.common.util.*;
import net.dries007.tfc.common.blocks.DecorationBlockRegistryObject;
import net.dries007.tfc.common.blocks.ExtendedBlock;
import net.dries007.tfc.common.blocks.ExtendedProperties;
import net.dries007.tfc.common.blocks.GroundcoverBlock;
import net.dries007.tfc.common.blocks.PouredGlassBlock;
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
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, FirmaLife.MOD_ID);

    public static final Map<OvenType, RegistryObject<Block>> INSULATED_OVEN_BOTTOM = Helpers.mapOfKeys(OvenType.class, type -> register("insulated_" + type.getName() + "oven_bottom", () -> new InsulatedOvenBottomBlock(ExtendedProperties.of().strength(4.0f).randomTicks().lightLevel(FLBlocks::lightEmission).sound(SoundType.STONE).noOcclusion().blockEntity(FLBlockEntities.OVEN_BOTTOM).serverTicks(OvenBottomBlockEntity::serverTick), null)));
    public static final Map<OvenType, RegistryObject<Block>> CURED_OVEN_BOTTOM = Helpers.mapOfKeys(OvenType.class, type -> register("cured_" + type.getName() + "oven_bottom", () -> new OvenBottomBlock(ExtendedProperties.of().strength(4.0f).randomTicks().lightLevel(FLBlocks::lightEmission).sound(SoundType.STONE).noOcclusion().blockEntity(FLBlockEntities.OVEN_BOTTOM).serverTicks(OvenBottomBlockEntity::serverTick), null, INSULATED_OVEN_BOTTOM.get(type))));
    public static final Map<OvenType, RegistryObject<Block>> CURED_OVEN_TOP = Helpers.mapOfKeys(OvenType.class, type -> register("cured_" + type.getName() + "oven_top", () -> new OvenTopBlock(ExtendedProperties.of().strength(4.0f).randomTicks().sound(SoundType.STONE).noOcclusion().blockEntity(FLBlockEntities.OVEN_TOP).serverTicks(OvenTopBlockEntity::serverTick), null)));
    public static final Map<OvenType, RegistryObject<Block>> CURED_OVEN_CHIMNEY = Helpers.mapOfKeys(OvenType.class, type -> register("cured_" + type.getName() + "oven_chimney", () -> new OvenChimneyBlock(Properties.of().strength(4.0f).sound(SoundType.STONE).noOcclusion(), null)));
    public static final Map<OvenType, RegistryObject<Block>> CURED_OVEN_HOPPER = Helpers.mapOfKeys(OvenType.class, type -> register("cured_" + type.getName() + "oven_hopper", () -> new OvenHopperBlock(ExtendedProperties.of().strength(4.0f).sound(SoundType.STONE).blockEntity(FLBlockEntities.OVEN_HOPPER).serverTicks(OvenHopperBlockEntity::serverTick), null)));
    public static final Map<OvenType, RegistryObject<Block>> OVEN_COUNTERTOP = Helpers.mapOfKeys(OvenType.class, type -> register(type.getTrueName() + "_countertop", () -> new Block(Properties.of().strength(4.0f).sound(SoundType.STONE))));

    public static final RegistryObject<Block> OVEN_BOTTOM = register("oven_bottom", () -> new OvenBottomBlock(ExtendedProperties.of().strength(4.0f).randomTicks().lightLevel(FLBlocks::lightEmission).sound(SoundType.STONE).noOcclusion().blockEntity(FLBlockEntities.OVEN_BOTTOM).serverTicks(OvenBottomBlockEntity::serverTick), FLBlocks.CURED_OVEN_BOTTOM.get(OvenType.BRICK)));
    public static final RegistryObject<Block> OVEN_TOP = register("oven_top", () -> new OvenTopBlock(ExtendedProperties.of().strength(4.0f).randomTicks().sound(SoundType.STONE).noOcclusion().blockEntity(FLBlockEntities.OVEN_TOP).serverTicks(OvenTopBlockEntity::serverTick), FLBlocks.CURED_OVEN_TOP.get(OvenType.BRICK)));
    public static final RegistryObject<Block> OVEN_CHIMNEY = register("oven_chimney", () -> new OvenChimneyBlock(Properties.of().strength(4.0f).sound(SoundType.STONE).noOcclusion(), FLBlocks.CURED_OVEN_CHIMNEY.get(OvenType.BRICK)));
    public static final RegistryObject<Block> OVEN_HOPPER = register("oven_hopper", () -> new OvenHopperBlock(ExtendedProperties.of().strength(4.0f).sound(SoundType.STONE).blockEntity(FLBlockEntities.OVEN_HOPPER), FLBlocks.CURED_OVEN_HOPPER.get(OvenType.BRICK)));

    public static final RegistryObject<Block> RUSTIC_BRICKS = register("rustic_bricks", () -> new Block(Properties.of().sound(SoundType.STONE).strength(2.0f, 10).requiresCorrectToolForDrops()));
    public static final DecorationBlockRegistryObject RUSTIC_BRICK_DECOR = new DecorationBlockRegistryObject(
        register("rustic_bricks_slab", () -> new SlabBlock(brickProperties())),
        register("rustic_bricks_stairs", () -> new StairBlock(() -> RUSTIC_BRICKS.get().defaultBlockState(), brickProperties())),
        register("rustic_bricks_wall", () -> new WallBlock(brickProperties()))
    );
    public static final RegistryObject<Block> TILES = register("tiles", () -> new Block(Properties.of().sound(SoundType.STONE).strength(2.0f, 10).requiresCorrectToolForDrops()));
    public static final DecorationBlockRegistryObject TILE_DECOR = new DecorationBlockRegistryObject(
        register("tiles_slab", () -> new SlabBlock(brickProperties())),
        register("tiles_stairs", () -> new StairBlock(() -> RUSTIC_BRICKS.get().defaultBlockState(), brickProperties())),
        register("tiles_wall", () -> new WallBlock(brickProperties()))
    );

    public static final RegistryObject<Block> ASHTRAY = register("ashtray", () -> new AshtrayBlock(ExtendedProperties.of().sound(SoundType.METAL).strength(2f).randomTicks().blockEntity(FLBlockEntities.ASHTRAY)));
    public static final RegistryObject<Block> STOVETOP_GRILL = registerNoItem("stovetop_grill", () -> new StovetopGrillBlock(ExtendedProperties.of().sound(SoundType.METAL).strength(2f).blockEntity(FLBlockEntities.STOVETOP_GRILL).serverTicks(StovetopGrillBlockEntity::serverTick).noOcclusion()));
    public static final RegistryObject<Block> STOVETOP_POT = registerNoItem("stovetop_pot", () -> new StovetopPotBlock(ExtendedProperties.of().sound(SoundType.METAL).strength(2f).blockEntity(FLBlockEntities.STOVETOP_POT).serverTicks(StovetopPotBlockEntity::serverTick).noOcclusion()));
    public static final RegistryObject<Block> DRYING_MAT = register("drying_mat", () -> new DryingMatBlock(ExtendedProperties.of().strength(0.6f).sound(SoundType.AZALEA_LEAVES).flammable(60, 30).blockEntity(FLBlockEntities.DRYING_MAT).serverTicks(DryingMatBlockEntity::serverTick)));
    public static final RegistryObject<Block> SOLAR_DRIER = register("solar_drier", () -> new SolarDrierBlock(ExtendedProperties.of().strength(3.0f).sound(SoundType.WOOD).noOcclusion().flammable(60, 30).blockEntity(FLBlockEntities.SOLAR_DRIER).serverTicks(DryingMatBlockEntity::serverTick)));
    public static final RegistryObject<Block> BEEHIVE = register("beehive", () -> new FLBeehiveBlock(ExtendedProperties.of().strength(0.6f).sound(SoundType.WOOD).flammable(60, 30).randomTicks().blockEntity(FLBlockEntities.BEEHIVE).serverTicks(FLBeehiveBlockEntity::serverTick)));
    public static final RegistryObject<Block> COMPOST_TUMBLER = register("compost_tumbler", () -> new CompostTumblerBlock(ExtendedProperties.of().strength(0.6F).noOcclusion().sound(SoundType.METAL).randomTicks().blockEntity(FLBlockEntities.COMPOST_TUMBLER)));
    public static final RegistryObject<Block> WOOL_STRING = registerNoItem("wool_string", () -> new StringBlock(ExtendedProperties.of().noCollission().strength(2.0f).sound(SoundType.WOOL).randomTicks().blockEntity(FLBlockEntities.STRING).serverTicks(StringBlockEntity::serverTick), TFCItems.WOOL_YARN));
    public static final RegistryObject<Block> MIXING_BOWL = register("mixing_bowl", () -> new MixingBowlBlock(ExtendedProperties.of().sound(SoundType.STONE).strength(1f).noOcclusion().blockEntity(FLBlockEntities.MIXING_BOWL).ticks(MixingBowlBlockEntity::serverTick, MixingBowlBlockEntity::clientTick)));
    public static final RegistryObject<Block> VAT = register("vat", () -> new VatBlock(ExtendedProperties.of().sound(SoundType.METAL).strength(1f).noOcclusion().blockEntity(FLBlockEntities.VAT).serverTicks(VatBlockEntity::serverTick)));
    public static final RegistryObject<Block> JARRING_STATION = register("jarring_station", () -> new JarringStationBlock(ExtendedProperties.of().sound(SoundType.METAL).strength(1f).noOcclusion().blockEntity(FLBlockEntities.JARRING_STATION).ticks(JarringStationBlockEntity::tick)));
    public static final RegistryObject<Block> PLATE = register("plate", () -> ConsumingBlock.plate(ExtendedProperties.of().sound(SoundType.WOOD).strength(1f).noOcclusion().blockEntity(FLBlockEntities.PLATE)));
    public static final RegistryObject<Block> REINFORCED_POURED_GLASS = register("reinforced_poured_glass", () -> new PouredGlassBlock(ExtendedProperties.of().strength(0.3F).sound(SoundType.GLASS).pushReaction(PushReaction.DESTROY).noOcclusion().requiresCorrectToolForDrops(), FLItems.REINFORCED_GLASS));

    public static final RegistryObject<Block> CHEDDAR_WHEEL = register("cheddar_wheel", () -> new CheeseWheelBlock(wheelProperties(), FLItems.FOODS.get(FLFood.CHEDDAR)));
    public static final RegistryObject<Block> CHEVRE_WHEEL = register("chevre_wheel", () -> new CheeseWheelBlock(wheelProperties(), FLItems.FOODS.get(FLFood.CHEVRE)));
    public static final RegistryObject<Block> RAJYA_METOK_WHEEL = register("rajya_metok_wheel", () -> new CheeseWheelBlock(wheelProperties(), FLItems.FOODS.get(FLFood.RAJYA_METOK)));
    public static final RegistryObject<Block> GOUDA_WHEEL = register("gouda_wheel", () -> new CheeseWheelBlock(wheelProperties(), FLItems.FOODS.get(FLFood.GOUDA)));
    public static final RegistryObject<Block> FETA_WHEEL = register("feta_wheel", () -> new CheeseWheelBlock(wheelProperties(), FLItems.FOODS.get(FLFood.FETA)));
    public static final RegistryObject<Block> SHOSHA_WHEEL = register("shosha_wheel", () -> new CheeseWheelBlock(wheelProperties(), FLItems.FOODS.get(FLFood.SHOSHA)));

    public static final RegistryObject<Block> CLIMATE_STATION = register("climate_station", () -> new ClimateStationBlock(ExtendedProperties.of().strength(3.0f).sound(SoundType.WOOD).randomTicks().blockEntity(FLBlockEntities.CLIMATE_STATION).flammable(60, 30)));
    public static final RegistryObject<Block> LARGE_PLANTER = register("large_planter", () -> new LargePlanterBlock(ExtendedProperties.of().sound(SoundType.STONE).strength(1f).randomTicks().blockEntity(FLBlockEntities.LARGE_PLANTER).serverTicks(LargePlanterBlockEntity::serverTick)));
    public static final RegistryObject<Block> QUAD_PLANTER = register("quad_planter", () -> new QuadPlanterBlock(ExtendedProperties.of().sound(SoundType.STONE).strength(1f).randomTicks().blockEntity(FLBlockEntities.QUAD_PLANTER).serverTicks(LargePlanterBlockEntity::serverTick)));
    public static final RegistryObject<Block> HYDROPONIC_PLANTER = register("hydroponic_planter", () -> new HydroponicPlanterBlock(ExtendedProperties.of().sound(SoundType.STONE).strength(1f).randomTicks().blockEntity(FLBlockEntities.HYDROPONIC_PLANTER).serverTicks(LargePlanterBlockEntity::serverTick)));
    public static final RegistryObject<Block> BONSAI_PLANTER = register("bonsai_planter", () -> new BonsaiPlanterBlock(ExtendedProperties.of().sound(SoundType.STONE).strength(1f).randomTicks().blockEntity(FLBlockEntities.BONSAI_PLANTER).serverTicks(LargePlanterBlockEntity::serverTick)));
    public static final RegistryObject<Block> HANGING_PLANTER = register("hanging_planter", () -> new HangingPlanterBlock(ExtendedProperties.of().sound(SoundType.STONE).strength(1f).randomTicks().blockEntity(FLBlockEntities.HANGING_PLANTER).serverTicks(LargePlanterBlockEntity::serverTick)));
    public static final RegistryObject<Block> TRELLIS_PLANTER = register("trellis_planter", () -> new TrellisPlanterBlock(ExtendedProperties.of().sound(SoundType.STONE).strength(1f).randomTicks().blockEntity(FLBlockEntities.TRELLIS_PLANTER).serverTicks(LargePlanterBlockEntity::serverTick)));

    public static final RegistryObject<Block> SEALED_BRICKS = register("sealed_bricks", () -> new Block(Properties.of().mapColor(MapColor.COLOR_BLACK).sound(SoundType.STONE).strength(2.0f, 10).requiresCorrectToolForDrops()));
    public static final RegistryObject<Block> SEALED_DOOR = register("sealed_door", () -> new DoorBlock(Properties.of().mapColor(MapColor.COLOR_BLACK).sound(SoundType.STONE).strength(2.0f, 10).requiresCorrectToolForDrops(), BlockSetType.STONE));
    public static final RegistryObject<Block> SEALED_TRAPDOOR = register("sealed_trapdoor", () -> new TrapDoorBlock(Properties.of().mapColor(MapColor.COLOR_BLACK).sound(SoundType.STONE).strength(2.0f, 10).requiresCorrectToolForDrops(), BlockSetType.STONE));
    public static final RegistryObject<Block> SEALED_WALL = register("sealed_wall", () -> new WallBlock(Properties.of().mapColor(MapColor.COLOR_BLACK).sound(SoundType.STONE).strength(2.0f, 10).requiresCorrectToolForDrops()));
    public static final RegistryObject<Block> DARK_LADDER = register("dark_ladder", () -> new FLLadderBlock(Properties.of().mapColor(MapColor.COLOR_BLACK).strength(0.4F).sound(SoundType.LADDER).noOcclusion()));

    public static final RegistryObject<Block> HOLLOW_SHELL = registerNoItem("hollow_shell", () -> new GroundcoverBlock(ExtendedProperties.of().strength(0.05F, 0.0F).sound(SoundType.NETHER_WART).noCollission(), GroundcoverBlock.SMALL, FLItems.HOLLOW_SHELL));
    public static final RegistryObject<Block> TREATED_WOOD = register("treated_wood", () -> new ExtendedBlock(ExtendedProperties.of().mapColor(MapColor.COLOR_BROWN).sound(SoundType.WOOL).strength(2f).flammableLikePlanks()));
    public static final RegistryObject<Block> PUMPING_STATION = register("pumping_station", () -> new PumpingStationBlock(ExtendedProperties.of().mapColor(MapColor.COLOR_ORANGE).requiresCorrectToolForDrops().strength(4f).sound(SoundType.METAL).blockEntity(FLBlockEntities.PUMPING_STATION)));
    public static final RegistryObject<Block> IRRIGATION_TANK = register("irrigation_tank", () -> new Block(Properties.of().mapColor(MapColor.COLOR_ORANGE).requiresCorrectToolForDrops().strength(4f).sound(SoundType.METAL)));
    public static final RegistryObject<Block> COPPER_PIPE = register("copper_pipe", () -> new SprinklerPipeBlock(ExtendedProperties.of().strength(2f).noOcclusion().sound(SoundType.METAL)));
    public static final RegistryObject<Block> OXIDIZED_COPPER_PIPE = register("oxidized_copper_pipe", () -> new SprinklerPipeBlock(ExtendedProperties.of().strength(2f).noOcclusion().sound(SoundType.METAL)));
    public static final RegistryObject<Block> SPRINKLER = registerNoItem("sprinkler", () -> new SprinklerBlock(ExtendedProperties.of().strength(2f).noOcclusion().sound(SoundType.METAL).blockEntity(FLBlockEntities.SPRINKLER).serverTicks(SprinklerBlockEntity::serverTick)));
    public static final RegistryObject<Block> FLOOR_SPRINKLER = registerNoItem("floor_sprinkler", () -> new FloorSprinklerBlock(ExtendedProperties.of().strength(2f).noOcclusion().sound(SoundType.METAL).blockEntity(FLBlockEntities.SPRINKLER).serverTicks(SprinklerBlockEntity::serverTick)));

    public static final RegistryObject<Block> BUTTERFLY_GRASS = register("plant/butterfly_grass", () -> MutatingPlantBlock.create(FLPlant.BUTTERFLY_GRASS, FLPlant.BUTTERFLY_GRASS.nonSolidFire(), FLTags.Blocks.BUTTERFLY_GRASS_MUTANTS));
    public static final RegistryObject<Block> POTTED_BUTTERFLY_GRASS = registerNoItem("plant/potted/butterfly_grass", () -> new FlowerPotBlock(() -> (FlowerPotBlock) Blocks.FLOWER_POT, BUTTERFLY_GRASS, Properties.of().instabreak().noOcclusion()));

    public static final Map<Herb, RegistryObject<Block>> HERBS = Helpers.mapOfKeys(Herb.class, herb -> register("plant/" + herb.name(), () -> PlantBlock.create(FLPlant.HERB, FLPlant.HERB.nonSolidFire())));
    public static final Map<Herb, RegistryObject<Block>> POTTED_HERBS = Helpers.mapOfKeys(Herb.class, herb -> registerNoItem("plant/potted/" + herb.name(), () -> new FlowerPotBlock(() -> (FlowerPotBlock) Blocks.FLOWER_POT, HERBS.get(herb), Properties.of().instabreak().noOcclusion())));

    public static final Map<FLFruitBlocks.Tree, RegistryObject<Block>> FRUIT_TREE_LEAVES = Helpers.mapOfKeys(FLFruitBlocks.Tree.class, tree -> register("plant/" + tree.name() + "_leaves", tree::createLeaves));
    public static final Map<FLFruitBlocks.Tree, RegistryObject<Block>> FRUIT_TREE_BRANCHES = Helpers.mapOfKeys(FLFruitBlocks.Tree.class, tree -> registerNoItem("plant/" + tree.name() + "_branch", tree::createBranch));
    public static final Map<FLFruitBlocks.Tree, RegistryObject<Block>> FRUIT_TREE_GROWING_BRANCHES = Helpers.mapOfKeys(FLFruitBlocks.Tree.class, tree -> registerNoItem("plant/" + tree.name() + "_growing_branch", tree::createGrowingBranch));
    public static final Map<FLFruitBlocks.Tree, RegistryObject<Block>> FRUIT_TREE_SAPLINGS = Helpers.mapOfKeys(FLFruitBlocks.Tree.class, tree -> register("plant/" + tree.name() + "_sapling", tree::createSapling));
    public static final Map<FLFruitBlocks.Tree, RegistryObject<Block>> FRUIT_TREE_POTTED_SAPLINGS = Helpers.mapOfKeys(FLFruitBlocks.Tree.class, tree -> registerNoItem("plant/potted/" + tree.name() + "_sapling", tree::createPottedSapling));

    public static final Map<FLFruitBlocks.StationaryBush, RegistryObject<Block>> STATIONARY_BUSHES = Helpers.mapOfKeys(FLFruitBlocks.StationaryBush.class, bush -> register("plant/" + bush.name() + "_bush", bush::create));

    public static final Map<Wood, RegistryObject<Block>> FOOD_SHELVES = Helpers.mapOfKeys(Wood.class, wood -> register("wood/food_shelf/" + wood.getSerializedName(), () -> new FoodShelfBlock(shelfProperties())));
    public static final Map<Wood, RegistryObject<Block>> HANGERS = Helpers.mapOfKeys(Wood.class, wood -> register("wood/hanger/" + wood.getSerializedName(), () -> new HangerBlock(hangerProperties())));
    public static final Map<Wood, RegistryObject<Block>> JARBNETS = Helpers.mapOfKeys(Wood.class, wood -> register("wood/jarbnet/" + wood.getSerializedName(), () -> new JarbnetBlock(jarbnetProperties())));

    public static final Map<Carving, RegistryObject<Block>> CARVED_PUMPKINS = Helpers.mapOfKeys(Carving.class, carve ->
        register("carved_pumpkin/" + carve.getSerializedName(), () -> new CarvedPumpkinBlock(Properties.of().mapColor(MapColor.COLOR_ORANGE).strength(1.0F).sound(SoundType.WOOD).isValidSpawn(FLBlocks::always)))
    );

    public static final Map<Carving, RegistryObject<Block>> JACK_O_LANTERNS = Helpers.mapOfKeys(Carving.class, carve ->
        register("lit_pumpkin/" + carve.getSerializedName(), () -> new JackOLanternBlock(ExtendedProperties.of(Properties.of().mapColor(MapColor.COLOR_ORANGE).strength(1.0F).sound(SoundType.WOOD).randomTicks().isValidSpawn(FLBlocks::always).lightLevel(alwaysLit())).blockEntity(FLBlockEntities.TICK_COUNTER), CARVED_PUMPKINS.get(carve)))
    );

    public static final RegistryObject<Block> SMALL_CHROMITE = register("ore/small_chromite", () -> GroundcoverBlock.looseOre(Properties.of().strength(0.05F, 0.0F).sound(SoundType.NETHER_ORE).noCollission()));
    public static final Map<Rock, Map<Ore.Grade, RegistryObject<Block>>> CHROMITE_ORES = Helpers.mapOfKeys(Rock.class, rock ->
        Helpers.mapOfKeys(Ore.Grade.class, grade ->
            register(("ore/" + grade.name() + "_chromite" + "/" + rock.name()), () -> new Block(Properties.of().sound(SoundType.STONE).strength(3, 10).requiresCorrectToolForDrops()))
        )
    );

    public static final Map<Greenhouse, Map<Greenhouse.BlockType, RegistryObject<Block>>> GREENHOUSE_BLOCKS = Helpers.mapOfKeys(Greenhouse.class, greenhouse ->
        Helpers.mapOfKeys(Greenhouse.BlockType.class, type ->
            register(greenhouse.name() + "_greenhouse_" + type.name(), type.create(greenhouse), type.createBlockItem(new Item.Properties()))
        )
    );

    public static final Map<FLMetal, Map<Metal.BlockType, RegistryObject<Block>>> METALS = Helpers.mapOfKeys(FLMetal.class, metal ->
        Helpers.mapOfKeys(Metal.BlockType.class, type -> type.has(Metal.Default.BISMUTH), type ->
            register(type.createName(metal), type.create(metal), type.createBlockItem(new Item.Properties()))
        )
    );

    public static final Map<FLMetal, RegistryObject<LiquidBlock>> METAL_FLUIDS = Helpers.mapOfKeys(FLMetal.class, metal ->
        registerNoItem("fluid/metal/" + metal.name(), () -> new LiquidBlock(FLFluids.METALS.get(metal).source(), Properties.copy(Blocks.LAVA).noLootTable()))
    );

    public static final Map<ExtraFluid, RegistryObject<LiquidBlock>> EXTRA_FLUIDS = Helpers.mapOfKeys(ExtraFluid.class, fluid ->
        registerNoItem("fluid/" + fluid.getSerializedName(), () -> new LiquidBlock(FLFluids.EXTRA_FLUIDS.get(fluid).source(), Properties.copy(Blocks.WATER).noLootTable()))
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

    private static <T extends Block> RegistryObject<T> registerNoItem(String name, Supplier<T> blockSupplier)
    {
        return register(name, blockSupplier, (Function<T, ? extends BlockItem>) null);
    }

    private static <T extends Block> RegistryObject<T> register(String name, Supplier<T> blockSupplier)
    {
        return register(name, blockSupplier, block -> new BlockItem(block, new Item.Properties()));
    }

    private static <T extends Block> RegistryObject<T> register(String name, Supplier<T> blockSupplier, Item.Properties blockItemProperties)
    {
        return register(name, blockSupplier, block -> new BlockItem(block, blockItemProperties));
    }

    private static <T extends Block> RegistryObject<T> register(String name, Supplier<T> blockSupplier, @Nullable Function<T, ? extends BlockItem> blockItemFactory)
    {
        return RegistrationHelpers.registerBlock(BLOCKS, FLItems.ITEMS, name, blockSupplier, blockItemFactory);
    }

}
