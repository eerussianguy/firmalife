package com.eerussianguy.firmalife.common.blocks;

import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraftforge.common.SoundActions;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.fluids.ForgeFlowingFluid;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import com.eerussianguy.firmalife.common.items.FLItems;
import com.eerussianguy.firmalife.common.util.ExtraFluid;
import com.eerussianguy.firmalife.common.util.FLMetal;

import net.dries007.tfc.common.fluids.ExtendedFluidType;
import net.dries007.tfc.common.fluids.FluidRegistryObject;
import net.dries007.tfc.common.fluids.FluidTypeClientProperties;
import net.dries007.tfc.common.fluids.MixingFluid;
import net.dries007.tfc.common.fluids.MoltenFluid;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.registry.RegistrationHelpers;

import static com.eerussianguy.firmalife.FirmaLife.MOD_ID;
import static net.dries007.tfc.common.fluids.TFCFluids.*;

public class FLFluids
{
    public static final DeferredRegister<Fluid> FLUIDS = DeferredRegister.create(ForgeRegistries.FLUIDS, MOD_ID);

    public static final Map<FLMetal, FluidRegistryObject<ForgeFlowingFluid>> METALS = Helpers.mapOfKeys(FLMetal.class, metal -> register(
        "metal/" + metal.getSerializedName(),
        properties -> properties
            .block(FLBlocks.METAL_FLUIDS.get(metal))
            .bucket(FLItems.METAL_FLUID_BUCKETS.get(metal))
            .explosionResistance(100),
        lavaLike()
            .descriptionId("fluid.firmalife.metal." + metal.getSerializedName())
            .canConvertToSource(false),
        new FluidTypeClientProperties(ALPHA_MASK | metal.getColor(), MOLTEN_STILL, MOLTEN_FLOW, null, null),
        MoltenFluid.Source::new,
        MoltenFluid.Flowing::new
    ));

    public static final Map<ExtraFluid, FluidRegistryObject<ForgeFlowingFluid>> EXTRA_FLUIDS = Helpers.mapOfKeys(ExtraFluid.class, fluid -> register(
        fluid.getSerializedName(),
        properties -> properties.block(FLBlocks.EXTRA_FLUIDS.get(fluid)).bucket(FLItems.EXTRA_FLUID_BUCKETS.get(fluid)),
        waterLike()
            .descriptionId("fluid.firmalife." + fluid.getSerializedName())
            .canConvertToSource(false),
        new FluidTypeClientProperties(fluid.getColor(), WATER_STILL, WATER_FLOW, WATER_OVERLAY, null),
        MixingFluid.Source::new,
        MixingFluid.Flowing::new
    ));

    private static FluidType.Properties lavaLike()
    {
        return FluidType.Properties.create()
            .adjacentPathType(BlockPathTypes.LAVA)
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
            .adjacentPathType(BlockPathTypes.WATER)
            .sound(SoundActions.BUCKET_FILL, SoundEvents.BUCKET_FILL)
            .sound(SoundActions.BUCKET_EMPTY, SoundEvents.BUCKET_EMPTY)
            .canConvertToSource(true)
            .canDrown(true)
            .canExtinguish(true)
            .canHydrate(false)
            .canPushEntity(true)
            .canSwim(true)
            .supportsBoating(true);
    }

    private static <F extends FlowingFluid> FluidRegistryObject<F> register(String name, Consumer<ForgeFlowingFluid.Properties> builder, FluidType.Properties typeProperties, FluidTypeClientProperties clientProperties, Function<ForgeFlowingFluid.Properties, F> sourceFactory, Function<ForgeFlowingFluid.Properties, F> flowingFactory)
    {
        // Names `metal/foo` to `metal/flowing_foo`
        final int index = name.lastIndexOf('/');
        final String flowingName = index == -1 ? "flowing_" + name : name.substring(0, index) + "/flowing_" + name.substring(index + 1);

        return RegistrationHelpers.registerFluid(FLUID_TYPES, FLUIDS, name, name, flowingName, builder, () -> new ExtendedFluidType(typeProperties, clientProperties), sourceFactory, flowingFactory);
    }
}
