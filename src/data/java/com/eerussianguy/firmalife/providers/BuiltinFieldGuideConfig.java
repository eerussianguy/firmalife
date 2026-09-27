package com.eerussianguy.firmalife.providers;

import java.util.concurrent.CompletableFuture;
import com.eerussianguy.firmalife.common.FLHelpers;
import com.eerussianguy.firmalife.compat.patchouli.FLConfigTextFunction;
import com.google.gson.JsonObject;
import com.mojang.serialization.JsonOps;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.network.chat.ComponentSerialization;

/**
 * Dumps the default value of every {@code $(flcfg:<name>)} the field guide is allowed to reference. This doubles as a
 * check that each registered name actually resolves.
 */
public class BuiltinFieldGuideConfig implements DataProvider
{
    private final PackOutput.PathProvider path;
    private final CompletableFuture<?> climateRanges;

    public BuiltinFieldGuideConfig(PackOutput output, CompletableFuture<?> climateRanges)
    {
        this.path = output.createPathProvider(PackOutput.Target.RESOURCE_PACK, "field_guide");
        this.climateRanges = climateRanges;
    }

    @Override
    public CompletableFuture<?> run(CachedOutput cache)
    {
        // Climate range references have to be bound before they can be resolved
        return climateRanges.thenCompose(ignored -> {
            final JsonObject json = new JsonObject();
            FLConfigTextFunction.values().forEach((name, value) -> json.add(name, ComponentSerialization.CODEC
                .encodeStart(JsonOps.INSTANCE, value.get())
                .getOrThrow()));
            return DataProvider.saveStable(cache, json, path.json(FLHelpers.identifier("config_defaults")));
        });
    }

    @Override
    public String getName()
    {
        return "Field Guide Config Defaults";
    }
}
