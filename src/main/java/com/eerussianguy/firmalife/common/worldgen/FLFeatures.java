package com.eerussianguy.firmalife.common.worldgen;

import java.util.function.Function;

import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.BlockStateConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import com.eerussianguy.firmalife.FirmaLife;
import com.mojang.serialization.Codec;
import net.dries007.tfc.world.Codecs;

public class FLFeatures
{
    public static final DeferredRegister<Feature<?>> FEATURES = DeferredRegister.create(ForgeRegistries.FEATURES, FirmaLife.MOD_ID);

    public static final RegistryObject<Feature<BlockStateConfiguration>> FRUIT_TREE = register("fruit_trees", FLFruitTreeFeature::new, Codecs.BLOCK_STATE_CONFIG);

    private static <C extends FeatureConfiguration, F extends Feature<C>> RegistryObject<F> register(String name, Function<Codec<C>, F> factory, Codec<C> codec)
    {
        return FEATURES.register(name, () -> factory.apply(codec));
    }

}
