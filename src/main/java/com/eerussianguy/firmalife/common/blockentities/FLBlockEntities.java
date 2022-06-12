package com.eerussianguy.firmalife.common.blockentities;

import java.util.function.Supplier;
import java.util.stream.Stream;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import com.eerussianguy.firmalife.common.blocks.FLBlocks;
import net.dries007.tfc.util.registry.RegistrationHelpers;

public class FLBlockEntities
{
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITIES, com.eerussianguy.firmalife.Firmalife.MOD_ID);

    public static final RegistryObject<BlockEntityType<OvenBottomBlockEntity>> OVEN_BOTTOM = register("oven_bottom", OvenBottomBlockEntity::new, Stream.of(FLBlocks.OVEN_BOTTOM, FLBlocks.CURED_OVEN_BOTTOM));
    public static final RegistryObject<BlockEntityType<OvenTopBlockEntity>> OVEN_TOP = register("oven_top", OvenTopBlockEntity::new, Stream.of(FLBlocks.OVEN_TOP, FLBlocks.CURED_OVEN_TOP));
    public static final RegistryObject<BlockEntityType<DryingMatBlockEntity>> DRYING_MAT = register("drying_mat", DryingMatBlockEntity::new, FLBlocks.DRYING_MAT);
    public static final RegistryObject<BlockEntityType<FLBeehiveBlockEntity>> BEEHIVE = register("beehive", FLBeehiveBlockEntity::new, FLBlocks.BEEHIVE);
    public static final RegistryObject<BlockEntityType<IronComposterBlockEntity>> IRON_COMPOSTER = register("iron_composter", IronComposterBlockEntity::new, FLBlocks.IRON_COMPOSTER);

    public static final RegistryObject<BlockEntityType<LargePlanterBlockEntity>> LARGE_PLANTER = register("large_planter", LargePlanterBlockEntity::new, FLBlocks.LARGE_PLANTER);
    public static final RegistryObject<BlockEntityType<BonsaiPlanterBlockEntity>> BONSAI_PLANTER = register("bonsai_planter", BonsaiPlanterBlockEntity::new, FLBlocks.BONSAI_PLANTER);
    public static final RegistryObject<BlockEntityType<HangingPlanterBlockEntity>> HANGING_PLANTER = register("hanging_planter", HangingPlanterBlockEntity::new, FLBlocks.HANGING_PLANTER);
    public static final RegistryObject<BlockEntityType<QuadPlanterBlockEntity>> QUAD_PLANTER = register("quad_planter", QuadPlanterBlockEntity::new, FLBlocks.QUAD_PLANTER);
    public static final RegistryObject<BlockEntityType<ClimateStationBlockEntity>> CLIMATE_STATION = register("climate_station", ClimateStationBlockEntity::new, FLBlocks.CLIMATE_STATION);

    private static <T extends BlockEntity> RegistryObject<BlockEntityType<T>> register(String name, BlockEntityType.BlockEntitySupplier<T> factory, Supplier<? extends Block> block)
    {
        return RegistrationHelpers.register(BLOCK_ENTITIES, name, factory, block);
    }

    private static <T extends BlockEntity> RegistryObject<BlockEntityType<T>> register(String name, BlockEntityType.BlockEntitySupplier<T> factory, Stream<? extends Supplier<? extends Block>> blocks)
    {
        return RegistrationHelpers.register(BLOCK_ENTITIES, name, factory, blocks);
    }
}
