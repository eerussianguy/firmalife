package com.eerussianguy.firmalife.common.blockentities;

import java.util.function.Supplier;
import java.util.stream.Stream;
import com.eerussianguy.firmalife.FirmaLife;
import com.eerussianguy.firmalife.common.blocks.FLBlocks;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.registries.DeferredRegister;

import net.dries007.tfc.common.blockentities.BerryBushBlockEntity;
import net.dries007.tfc.common.blockentities.TFCBlockEntities.Id;
import net.dries007.tfc.util.registry.RegistrationHelpers;

public class FLBlockEntities
{
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, FirmaLife.MOD_ID);

    public static final Id<FLTickCounterBlockEntity> TICK_COUNTER = register("tick_counter", FLTickCounterBlockEntity::new, Stream.of(
        Stream.of(FLBlocks.CHEDDAR_WHEEL, FLBlocks.CHEVRE_WHEEL, FLBlocks.FETA_WHEEL, FLBlocks.SHOSHA_WHEEL, FLBlocks.RAJYA_METOK_WHEEL, FLBlocks.GOUDA_WHEEL),
        FLBlocks.JACK_O_LANTERNS.values().stream(),
        FLBlocks.FRUIT_TREE_SAPLINGS.values().stream(),
        FLBlocks.FRUIT_TREE_GROWING_BRANCHES.values().stream()
    ).flatMap(e -> e));

    public static final Id<OvenBottomBlockEntity> OVEN_BOTTOM = register("oven_bottom", OvenBottomBlockEntity::new, Stream.concat(Stream.concat(Stream.of(FLBlocks.CLAY_OVEN_BOTTOM), FLBlocks.CURED_OVEN_BOTTOM.values().stream()), FLBlocks.INSULATED_OVEN_BOTTOM.values().stream()));
    public static final Id<OvenTopBlockEntity> OVEN_TOP = register("oven_top", OvenTopBlockEntity::new, Stream.concat(Stream.of(FLBlocks.CLAY_OVEN_TOP), Stream.concat(FLBlocks.INSULATED_OVEN_TOP.values().stream(), FLBlocks.CURED_OVEN_TOP.values().stream())));
    public static final Id<OvenHopperBlockEntity> OVEN_HOPPER = register("oven_hopper", OvenHopperBlockEntity::new, Stream.concat(Stream.of(FLBlocks.CLAY_OVEN_HOPPER), FLBlocks.CURED_OVEN_HOPPER.values().stream()));
    public static final Id<DryingMatBlockEntity> DRYING_MAT = register("drying_mat", DryingMatBlockEntity::dryingMat, FLBlocks.DRYING_MAT);
    public static final Id<DryingMatBlockEntity> SOLAR_DRIER = register("solar_drier", DryingMatBlockEntity::solarDrier, FLBlocks.SOLAR_DRIER);
    public static final Id<WoodenBeehiveBlockEntity> BEEHIVE = register("beehive", WoodenBeehiveBlockEntity::new, FLBlocks.BEEHIVE);
    public static final Id<CentrifugeBlockEntity> CENTRIFUGE = register("centrifuge", CentrifugeBlockEntity::new, FLBlocks.CENTRIFUGE);
    public static final Id<SkepBlockEntity> SKEP = register("skep", SkepBlockEntity::new, FLBlocks.SKEP);
    public static final Id<CompostTumblerBlockEntity> COMPOST_TUMBLER = register("compost_tumbler", CompostTumblerBlockEntity::new, FLBlocks.COMPOST_TUMBLER);
    public static final Id<StringBlockEntity> STRING = register("string", StringBlockEntity::new, Stream.of(FLBlocks.WOOL_STRING, FLBlocks.PINEAPPLE_YARN));
    public static final Id<MixingBowlBlockEntity> MIXING_BOWL = register("mixing_bowl", MixingBowlBlockEntity::new, FLBlocks.MIXING_BOWL);
    public static final Id<BerryBushBlockEntity> BERRY_BUSH = register("berry_bush", FLBerryBushBlockEntity::new, Stream.of(FLBlocks.FRUIT_TREE_LEAVES.values().stream(), FLBlocks.STATIONARY_BUSHES.values().stream()).flatMap(e -> e));
    public static final Id<FoodShelfBlockEntity> FOOD_SHELF = register("food_shelf", FoodShelfBlockEntity::new, FLBlocks.FOOD_SHELVES.values().stream());
    public static final Id<HangerBlockEntity> HANGER = register("hanger", HangerBlockEntity::new, FLBlocks.HANGERS.values().stream());
    public static final Id<JarbnetBlockEntity> JARBNET = register("jarbnet", JarbnetBlockEntity::new, FLBlocks.JARBNETS.values().stream());
    public static final Id<KegBlockEntity> KEG = register("keg", KegBlockEntity::new, FLBlocks.KEGS.values().stream());
    public static final Id<KegSubBlockEntity> KEG_SUB = register("keg_sub", KegSubBlockEntity::new, FLBlocks.KEG_SUBS.values().stream());
    public static final Id<WineShelfBlockEntity> WINE_SHELF = register("wine_shelf", WineShelfBlockEntity::new, FLBlocks.WINE_SHELVES.values().stream());
    public static final Id<StompingBarrelBlockEntity> STOMPING_BARREL = register("stomping_barrel", StompingBarrelBlockEntity::new, FLBlocks.STOMPING_BARRELS.values().stream());
    public static final Id<BarrelPressBlockEntity> BARREL_PRESS = register("barrel_press", BarrelPressBlockEntity::new, FLBlocks.BARREL_PRESSES.values().stream());
    public static final Id<VatBlockEntity> VAT = register("vat", VatBlockEntity::new, FLBlocks.VAT);
    public static final Id<AshTrayBlockEntity> ASHTRAY = register("ashtray", AshTrayBlockEntity::new, FLBlocks.ASHTRAY);
    public static final Id<StovetopGrillBlockEntity> STOVETOP_GRILL = register("stovetop_grill", StovetopGrillBlockEntity::new, FLBlocks.STOVETOP_GRILL);
    public static final Id<StovetopPotBlockEntity> STOVETOP_POT = register("stovetop_pot", StovetopPotBlockEntity::new, FLBlocks.STOVETOP_POT);
    public static final Id<JarringStationBlockEntity> JARRING_STATION = register("jarring_station", JarringStationBlockEntity::new, FLBlocks.JARRING_STATION);
    public static final Id<PlateBlockEntity> PLATE = register("plate", PlateBlockEntity::new, FLBlocks.PLATE);
    public static final Id<GrapePlantBlockEntity> GRAPE_PLANT = register("grape_plant", GrapePlantBlockEntity::new, Stream.of(FLBlocks.GRAPE_STRING_PLANT_RED, FLBlocks.GRAPE_STRING_PLANT_WHITE));

    public static final Id<LargePlanterBlockEntity> LARGE_PLANTER = register("large_planter", LargePlanterBlockEntity::new, FLBlocks.LARGE_PLANTER);
    public static final Id<BonsaiPlanterBlockEntity> BONSAI_PLANTER = register("bonsai_planter", BonsaiPlanterBlockEntity::new, FLBlocks.BONSAI_PLANTER);
    public static final Id<HangingPlanterBlockEntity> HANGING_PLANTER = register("hanging_planter", HangingPlanterBlockEntity::new, FLBlocks.HANGING_PLANTER);
    public static final Id<QuadPlanterBlockEntity> QUAD_PLANTER = register("quad_planter", QuadPlanterBlockEntity::new, FLBlocks.QUAD_PLANTER);
    public static final Id<HydroponicPlanterBlockEntity> HYDROPONIC_PLANTER = register("hydroponic_planter", HydroponicPlanterBlockEntity::new, FLBlocks.HYDROPONIC_PLANTER);
    public static final Id<TrellisPlanterBlockEntity> TRELLIS_PLANTER = register("trellis_planter", TrellisPlanterBlockEntity::new, FLBlocks.TRELLIS_PLANTER);
    public static final Id<ClimateStationBlockEntity> CLIMATE_STATION = register("climate_station", ClimateStationBlockEntity::new, FLBlocks.CLIMATE_STATION);
    public static final Id<SprinklerBlockEntity> SPRINKLER = register("sprinkler", SprinklerBlockEntity::new, Stream.of(FLBlocks.SPRINKLER, FLBlocks.FLOOR_SPRINKLER));
    public static final Id<PumpingStationBlockEntity> PUMPING_STATION = register("pumping_station", PumpingStationBlockEntity::new, Stream.of(FLBlocks.PUMPING_STATION));
    public static final Id<PickerBlockEntity> PICKER = register("picker", PickerBlockEntity::new, Stream.of(FLBlocks.PICKER));
    public static final Id<SweeperBlockEntity> SWEEPER = register("sweeper", SweeperBlockEntity::new, Stream.of(FLBlocks.SWEEPER));

    private static <T extends BlockEntity> Id<T> register(String name, BlockEntityType.BlockEntitySupplier<T> factory, Supplier<? extends Block> block)
    {
        return new Id<>(RegistrationHelpers.register(BLOCK_ENTITY, name, factory, block));
    }

    private static <T extends BlockEntity> Id<T> register(String name, BlockEntityType.BlockEntitySupplier<T> factory, Stream<? extends Supplier<? extends Block>> blocks)
    {
        return new Id<>(RegistrationHelpers.register(BLOCK_ENTITY, name, factory, blocks));
    }
}
