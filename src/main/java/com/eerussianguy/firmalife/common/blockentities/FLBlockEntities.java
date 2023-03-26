package com.eerussianguy.firmalife.common.blockentities;

import java.util.function.Supplier;
import java.util.stream.Stream;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import com.eerussianguy.firmalife.FirmaLife;
import com.eerussianguy.firmalife.common.blocks.FLBlocks;
import net.dries007.tfc.common.blockentities.BerryBushBlockEntity;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.registry.RegistrationHelpers;

public class FLBlockEntities
{
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITIES, FirmaLife.MOD_ID);

    public static final RegistryObject<BlockEntityType<FLTickCounterBlockEntity>> TICK_COUNTER = register("tick_counter", FLTickCounterBlockEntity::new, Stream.of(
        FLBlocks.CHEDDAR_WHEEL, FLBlocks.CHEVRE_WHEEL, FLBlocks.FETA_WHEEL, FLBlocks.SHOSHA_WHEEL, FLBlocks.RAJYA_METOK_WHEEL, FLBlocks.GOUDA_WHEEL,
        FLBlocks.JACK_O_LANTERNS.values(),
        FLBlocks.FRUIT_TREE_SAPLINGS.values(),
        FLBlocks.FRUIT_TREE_GROWING_BRANCHES.values()
    ).<Supplier<? extends Block>>flatMap(Helpers::flatten));

    public static final RegistryObject<BlockEntityType<OvenBottomBlockEntity>> OVEN_BOTTOM = register("oven_bottom", OvenBottomBlockEntity::new, Stream.of(FLBlocks.OVEN_BOTTOM, FLBlocks.CURED_OVEN_BOTTOM));
    public static final RegistryObject<BlockEntityType<OvenTopBlockEntity>> OVEN_TOP = register("oven_top", OvenTopBlockEntity::new, Stream.of(FLBlocks.OVEN_TOP, FLBlocks.CURED_OVEN_TOP));
    public static final RegistryObject<BlockEntityType<DryingMatBlockEntity>> DRYING_MAT = register("drying_mat", DryingMatBlockEntity::dryingMat, FLBlocks.DRYING_MAT);
    public static final RegistryObject<BlockEntityType<DryingMatBlockEntity>> SOLAR_DRIER = register("solar_drier", DryingMatBlockEntity::solarDrier, FLBlocks.SOLAR_DRIER);
    public static final RegistryObject<BlockEntityType<FLBeehiveBlockEntity>> BEEHIVE = register("beehive", FLBeehiveBlockEntity::new, FLBlocks.BEEHIVE);
    public static final RegistryObject<BlockEntityType<IronComposterBlockEntity>> IRON_COMPOSTER = register("iron_composter", IronComposterBlockEntity::new, FLBlocks.IRON_COMPOSTER);
    public static final RegistryObject<BlockEntityType<StringBlockEntity>> STRING = register("string", StringBlockEntity::new, FLBlocks.WOOL_STRING);
    public static final RegistryObject<BlockEntityType<MixingBowlBlockEntity>> MIXING_BOWL = register("mixing_bowl", MixingBowlBlockEntity::new, FLBlocks.MIXING_BOWL);
    public static final RegistryObject<BlockEntityType<BerryBushBlockEntity>> BERRY_BUSH = register("berry_bush", FLBerryBushBlockEntity::new, Stream.of(FLBlocks.FRUIT_TREE_LEAVES.values(), FLBlocks.STATIONARY_BUSHES.values()).<Supplier<? extends Block>>flatMap(Helpers::flatten));
    public static final RegistryObject<BlockEntityType<SquirtingMoistureTransducerBlockEntity>> SQUIRTING_MOISTURE_TRANSDUCER = register("squirting_moisture_transducer", SquirtingMoistureTransducerBlockEntity::new, FLBlocks.SQUIRTING_MOISTURE_TRANSDUCER);
    public static final RegistryObject<BlockEntityType<FoodShelfBlockEntity>> FOOD_SHELF = register("food_shelf", FoodShelfBlockEntity::new, Stream.of(FLBlocks.FOOD_SHELVES.values()).<Supplier<? extends Block>>flatMap(Helpers::flatten));
    public static final RegistryObject<BlockEntityType<HangerBlockEntity>> HANGER = register("hanger", HangerBlockEntity::new, Stream.of(FLBlocks.HANGERS.values()).<Supplier<? extends Block>>flatMap(Helpers::flatten));
    public static final RegistryObject<BlockEntityType<VatBlockEntity>> VAT = register("vat", VatBlockEntity::new, FLBlocks.VAT);
    public static final RegistryObject<BlockEntityType<NutritiveBasinBlockEntity>> NUTRITIVE_BASIN = register("nutritive_basin", NutritiveBasinBlockEntity::new, FLBlocks.NUTRITIVE_BASIN);

    public static final RegistryObject<BlockEntityType<LargePlanterBlockEntity>> LARGE_PLANTER = register("large_planter", LargePlanterBlockEntity::new, FLBlocks.LARGE_PLANTER);
    public static final RegistryObject<BlockEntityType<BonsaiPlanterBlockEntity>> BONSAI_PLANTER = register("bonsai_planter", BonsaiPlanterBlockEntity::new, FLBlocks.BONSAI_PLANTER);
    public static final RegistryObject<BlockEntityType<HangingPlanterBlockEntity>> HANGING_PLANTER = register("hanging_planter", HangingPlanterBlockEntity::new, FLBlocks.HANGING_PLANTER);
    public static final RegistryObject<BlockEntityType<QuadPlanterBlockEntity>> QUAD_PLANTER = register("quad_planter", QuadPlanterBlockEntity::new, FLBlocks.QUAD_PLANTER);
    public static final RegistryObject<BlockEntityType<HydroponicPlanterBlockEntity>> HYDROPONIC_PLANTER = register("hydroponic_planter", HydroponicPlanterBlockEntity::new, FLBlocks.HYDROPONIC_PLANTER);
    public static final RegistryObject<BlockEntityType<TrellisPlanterBlockEntity>> TRELLIS_PLANTER = register("trellis_planter", TrellisPlanterBlockEntity::new, FLBlocks.TRELLIS_PLANTER);
    public static final RegistryObject<BlockEntityType<ClimateStationBlockEntity>> CLIMATE_STATION = register("climate_station", ClimateStationBlockEntity::new, FLBlocks.CLIMATE_STATION);
    public static final RegistryObject<BlockEntityType<SprinklerBlockEntity>> SPRINKLER = register("sprinkler", SprinklerBlockEntity::new, FLBlocks.SPRINKLER);

    private static <T extends BlockEntity> RegistryObject<BlockEntityType<T>> register(String name, BlockEntityType.BlockEntitySupplier<T> factory, Supplier<? extends Block> block)
    {
        return RegistrationHelpers.register(BLOCK_ENTITIES, name, factory, block);
    }

    private static <T extends BlockEntity> RegistryObject<BlockEntityType<T>> register(String name, BlockEntityType.BlockEntitySupplier<T> factory, Stream<? extends Supplier<? extends Block>> blocks)
    {
        return RegistrationHelpers.register(BLOCK_ENTITIES, name, factory, blocks);
    }
}
