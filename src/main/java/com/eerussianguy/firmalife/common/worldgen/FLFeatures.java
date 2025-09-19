package com.eerussianguy.firmalife.common.worldgen;

import java.util.function.Function;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;

import com.eerussianguy.firmalife.FirmaLife;
import com.mojang.serialization.Codec;
import net.neoforged.neoforge.registries.DeferredRegister;

import net.dries007.tfc.world.Codecs;
import net.dries007.tfc.world.feature.TFCFeatures.Id;

public class FLFeatures
{
    public static final DeferredRegister<Feature<?>> FEATURE = DeferredRegister.create(Registries.FEATURE, FirmaLife.MOD_ID);

    public static final Id<FLFruitTreeFeature> FRUIT_TREE = register("fruit_trees", FLFruitTreeFeature::new, Codecs.BLOCK_STATE_CONFIG);

    private static <C extends FeatureConfiguration, F extends Feature<C>> Id<F> register(String name, Function<Codec<C>, F> factory, Codec<C> codec)
    {
        return new Id<>(FEATURE.register(name, () -> factory.apply(codec)));
    }

}
