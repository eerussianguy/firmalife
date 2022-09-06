package com.eerussianguy.firmalife.config;

import java.util.function.Function;

import org.apache.commons.lang3.tuple.Pair;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;

import net.dries007.tfc.util.Helpers;

public class FLConfig
{
    public static final FLServerConfig SERVER = register(ModConfig.Type.SERVER, FLServerConfig::new);

    public static void init() {}

    private static <C> C register(ModConfig.Type type, Function<ForgeConfigSpec.Builder, C> factory)
    {
        Pair<C, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(factory);
        if (!Helpers.BOOTSTRAP_ENVIRONMENT) ModLoadingContext.get().registerConfig(type, specPair.getRight());
        return specPair.getLeft();
    }
}
