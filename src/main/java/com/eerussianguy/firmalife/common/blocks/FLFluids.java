package com.eerussianguy.firmalife.common.blocks;

import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import org.apache.commons.lang3.mutable.Mutable;
import org.apache.commons.lang3.mutable.MutableObject;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.common.util.Lazy;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.ForgeFlowingFluid;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import com.eerussianguy.firmalife.common.items.FLItems;
import com.eerussianguy.firmalife.common.util.FLMetal;
import com.mojang.datafixers.util.Pair;
import net.dries007.tfc.common.fluids.MoltenFluid;
import net.dries007.tfc.util.Helpers;

import static com.eerussianguy.firmalife.Firmalife.MOD_ID;
import static net.dries007.tfc.common.fluids.TFCFluids.*;

public class FLFluids
{
    public static final DeferredRegister<Fluid> FLUIDS = DeferredRegister.create(ForgeRegistries.FLUIDS, MOD_ID);

    public static final Map<FLMetal, FluidPair<ForgeFlowingFluid>> METALS = Helpers.mapOfKeys(FLMetal.class, metal -> register(
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

    private static <F extends FlowingFluid> FluidPair<F> register(String sourceName, String flowingName, Consumer<ForgeFlowingFluid.Properties> builder, FluidAttributes.Builder attributes, Function<ForgeFlowingFluid.Properties, F> sourceFactory, Function<ForgeFlowingFluid.Properties, F> flowingFactory)
    {
        final Mutable<Lazy<ForgeFlowingFluid.Properties>> propertiesBox = new MutableObject<>();
        final RegistryObject<F> source = register(sourceName, () -> sourceFactory.apply(propertiesBox.getValue().get()));
        final RegistryObject<F> flowing = register(flowingName, () -> flowingFactory.apply(propertiesBox.getValue().get()));

        propertiesBox.setValue(Lazy.of(() -> {
            ForgeFlowingFluid.Properties lazyProperties = new ForgeFlowingFluid.Properties(source, flowing, attributes);
            builder.accept(lazyProperties);
            return lazyProperties;
        }));

        return new FluidPair<>(flowing, source);
    }

    private static <F extends Fluid> RegistryObject<F> register(String name, Supplier<F> factory)
    {
        return FLUIDS.register(name, factory);
    }

    public static class FluidPair<F extends FlowingFluid> extends Pair<RegistryObject<F>, RegistryObject<F>>
    {
        private FluidPair(RegistryObject<F> first, RegistryObject<F> second)
        {
            super(first, second);
        }

        public F getFlowing()
        {
            return getFirst().get();
        }

        public F getSource()
        {
            return getSecond().get();
        }

        public BlockState getSourceBlock()
        {
            return getSource().defaultFluidState().createLegacyBlock();
        }
    }
}
