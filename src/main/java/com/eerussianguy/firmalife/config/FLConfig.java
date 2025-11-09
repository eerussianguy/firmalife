package com.eerussianguy.firmalife.config;

import java.util.function.Function;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.ModConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

import net.dries007.tfc.config.BaseConfig;

public class FLConfig
{
    public static final FLServerConfig SERVER = register(ModConfig.Type.SERVER, FLServerConfig::new);

    public static void init() {}

    private static <C extends BaseConfig> C register(ModConfig.Type type, Function<ModConfigSpec.Builder, C> factory)
    {
        Pair<C, ModConfigSpec> specPair = new ModConfigSpec.Builder().configure(factory);
        specPair.getKey().updateSpec(specPair.getValue());
        return specPair.getLeft();
    }
}
