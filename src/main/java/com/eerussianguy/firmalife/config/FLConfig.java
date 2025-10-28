package com.eerussianguy.firmalife.config;

import java.util.function.Function;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.ModConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

public class FLConfig
{
    public static final FLServerConfig SERVER = register(ModConfig.Type.SERVER, FLServerConfig::new);

    public static void init() {}

    private static <C> C register(ModConfig.Type type, Function<ModConfigSpec.Builder, C> factory)
    {
        Pair<C, ModConfigSpec> specPair = new ModConfigSpec.Builder().configure(factory);
        return specPair.getLeft();
    }
}
