package com.eerussianguy.firmalife.common.blocks;

import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.ForgeFlowingFluid;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import com.eerussianguy.firmalife.common.items.FLItems;
import com.eerussianguy.firmalife.common.util.ExtraFluid;
import com.eerussianguy.firmalife.common.util.FLMetal;
import net.dries007.tfc.common.blocks.TFCBlocks;
import net.dries007.tfc.common.fluids.FlowingFluidRegistryObject;
import net.dries007.tfc.common.fluids.MixingFluid;
import net.dries007.tfc.common.fluids.MoltenFluid;
import net.dries007.tfc.common.fluids.SimpleFluid;
import net.dries007.tfc.common.items.TFCItems;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.registry.RegistrationHelpers;

import static com.eerussianguy.firmalife.Firmalife.MOD_ID;
import static net.dries007.tfc.common.fluids.TFCFluids.*;

public class FLFluids
{
    public static final DeferredRegister<Fluid> FLUIDS = DeferredRegister.create(ForgeRegistries.FLUIDS, MOD_ID);

    public static final Map<FLMetal, FlowingFluidRegistryObject<ForgeFlowingFluid>> METALS = Helpers.mapOfKeys(FLMetal.class, metal -> register(
        "metal/" + metal.getSerializedName(),
        "metal/flowing_" + metal.getSerializedName(),
        properties -> properties.block(FLBlocks.METAL_FLUIDS.get(metal)).bucket(FLItems.METAL_FLUID_BUCKETS.get(metal)).explosionResistance(100),
        FluidAttributes.builder(MOLTEN_STILL, MOLTEN_FLOW)
            .translationKey("fluid.firmalife.metal." + metal.getSerializedName())
            .color(ALPHA_MASK | metal.getColor())
            .rarity(metal.getRarity())
            .luminosity(15)
            .density(3000)
            .viscosity(6000)
            .temperature(1300)
            .sound(SoundEvents.BUCKET_FILL_LAVA, SoundEvents.BUCKET_EMPTY_LAVA),
        MoltenFluid.Source::new,
        MoltenFluid.Flowing::new
    ));

    public static final Map<ExtraFluid, FlowingFluidRegistryObject<ForgeFlowingFluid>> EXTRA_FLUIDS = Helpers.mapOfKeys(ExtraFluid.class, fluid -> register(
        fluid.getSerializedName(),
        "flowing_" + fluid.getSerializedName(),
        properties -> properties.block(FLBlocks.EXTRA_FLUIDS.get(fluid)).bucket(FLItems.EXTRA_FLUID_BUCKETS.get(fluid)),
        FluidAttributes.builder(WATER_STILL, WATER_FLOW)
            .translationKey("fluid.firmalife." + fluid.getSerializedName())
            .color(fluid.getColor())
            .overlay(WATER_OVERLAY)
            .sound(SoundEvents.BUCKET_FILL, SoundEvents.BUCKET_EMPTY),
        MixingFluid.Source::new,
        MixingFluid.Flowing::new
    ));

    private static <F extends FlowingFluid> FlowingFluidRegistryObject<F> register(String sourceName, String flowingName, Consumer<ForgeFlowingFluid.Properties> builder, FluidAttributes.Builder attributes, Function<ForgeFlowingFluid.Properties, F> sourceFactory, Function<ForgeFlowingFluid.Properties, F> flowingFactory)
    {
        return RegistrationHelpers.registerFluid(FLUIDS, sourceName, flowingName, builder, attributes, sourceFactory, flowingFactory);
    }
}
