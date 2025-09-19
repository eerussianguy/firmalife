package com.eerussianguy.firmalife.common.blocks;

import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

import com.eerussianguy.firmalife.common.capabilities.wine.WineType;
import net.minecraft.core.registries.Registries;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;

import com.eerussianguy.firmalife.common.items.FLItems;
import com.eerussianguy.firmalife.common.util.ExtraFluid;
import com.eerussianguy.firmalife.common.util.FLMetal;
import net.minecraft.world.level.pathfinder.PathType;
import net.neoforged.neoforge.common.SoundActions;
import net.neoforged.neoforge.fluids.BaseFlowingFluid;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import net.dries007.tfc.common.fluids.FluidHolder;
import net.dries007.tfc.common.fluids.MixingFluid;
import net.dries007.tfc.common.fluids.MoltenFluid;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.registry.RegistrationHelpers;

import static com.eerussianguy.firmalife.FirmaLife.MOD_ID;
import static net.dries007.tfc.common.fluids.TFCFluids.*;

public class FLFluids
{
    public static final DeferredRegister<Fluid> FLUID = DeferredRegister.create(Registries.FLUID, MOD_ID);
    public static final DeferredRegister<FluidType> FLUID_TYPES = DeferredRegister.create(NeoForgeRegistries.FLUID_TYPES, MOD_ID);


    public static final Map<FLMetal, FluidHolder<BaseFlowingFluid>> METALS = Helpers.mapOf(FLMetal.class, metal -> register(
        "metal/" + metal.getSerializedName(),
        properties -> properties
            .block(FLBlocks.METAL_FLUIDS.get(metal))
            .bucket(FLItems.METAL_FLUID_BUCKETS.get(metal))
            .explosionResistance(100),
        lavaLike()
            .descriptionId("fluid.firmalife.metal." + metal.getSerializedName())
            .canConvertToSource(false),
        MoltenFluid.Source::new,
        MoltenFluid.Flowing::new
    ));

    public static final Map<ExtraFluid, FluidHolder<BaseFlowingFluid>> EXTRA_FLUIDS = Helpers.mapOf(ExtraFluid.class, fluid -> register(
        fluid.getSerializedName(),
        properties -> properties.block(FLBlocks.EXTRA_FLUIDS.get(fluid)).bucket(FLItems.EXTRA_FLUID_BUCKETS.get(fluid)),
        waterLike()
            .descriptionId("fluid.firmalife." + fluid.getSerializedName())
            .canConvertToSource(false),
        MixingFluid.Source::new,
        MixingFluid.Flowing::new
    ));

    public static final Map<WineType, FluidHolder<BaseFlowingFluid>> WINE_FLUIDS = Helpers.mapOf(WineType.class, fluid -> register(
        fluid.getSerializedName(),
        properties -> properties.block(FLBlocks.WINE_FLUIDS.get(fluid)).bucket(FLItems.WINE_FLUID_BUCKETS.get(fluid)),
        waterLike()
            .descriptionId("fluid.firmalife." + fluid.getSerializedName())
            .canConvertToSource(false),
        MixingFluid.Source::new,
        MixingFluid.Flowing::new
    ));

    private static FluidType.Properties lavaLike()
    {
        return FluidType.Properties.create()
            .adjacentPathType(PathType.LAVA)
            .sound(SoundActions.BUCKET_FILL, SoundEvents.BUCKET_FILL)
            .sound(SoundActions.BUCKET_EMPTY, SoundEvents.BUCKET_EMPTY_LAVA)
            .lightLevel(15)
            .density(3000)
            .viscosity(6000)
            .temperature(1300)
            .canConvertToSource(false)
            .canDrown(false)
            .canExtinguish(false)
            .canHydrate(false)
            .canPushEntity(false)
            .canSwim(false)
            .supportsBoating(false);
    }

    private static FluidType.Properties waterLike()
    {
        return FluidType.Properties.create()
            .adjacentPathType(PathType.WATER)
            .sound(SoundActions.BUCKET_FILL, SoundEvents.BUCKET_FILL)
            .sound(SoundActions.BUCKET_EMPTY, SoundEvents.BUCKET_EMPTY)
            .canConvertToSource(true)
            .canDrown(true)
            .canExtinguish(true)
            .canHydrate(true)
            .canPushEntity(true)
            .canSwim(true)
            .supportsBoating(true);
    }

    private static <F extends FlowingFluid> FluidHolder<F> register(String name, Consumer<BaseFlowingFluid.Properties> builder, FluidType.Properties typeProperties, Function<BaseFlowingFluid.Properties, F> sourceFactory, Function<BaseFlowingFluid.Properties, F> flowingFactory)
    {
        // Names `metal/foo` to `metal/flowing_foo`
        final int index = name.lastIndexOf('/');
        final String flowingName = index == -1 ? "flowing_" + name : name.substring(0, index) + "/flowing_" + name.substring(index + 1);

        return RegistrationHelpers.registerFluid(FLUID_TYPES, FLUIDS, name, name, flowingName, builder, () -> new FluidType(typeProperties), sourceFactory, flowingFactory);
    }

}
