/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package com.eerussianguy.firmalife.providers;

import java.util.Locale;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import com.eerussianguy.firmalife.common.FLHelpers;
import com.google.common.collect.ImmutableMap;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;

import net.dries007.tfc.util.data.DataManager;

public abstract class DataManagerProvider<T> implements DataProvider
{
    private final DataManager<T> manager;
    private final CompletableFuture<HolderLookup.Provider> lookup;
    private final ImmutableMap.Builder<ResourceLocation, T> elements;
    private final PackOutput.PathProvider path;
    protected final CompletableFuture<?> contentDone;

    protected DataManagerProvider(DataManager<T> manager, PackOutput output, CompletableFuture<HolderLookup.Provider> lookup, String domain)
    {
        this.manager = manager;
        this.lookup = lookup;
        this.elements = ImmutableMap.builder();
        //TODO it would be nice to use have access to the whole ResourceLocation that is used to determine SimpleJsonResourceReloadListener#directory
        // but DataManager does not let you access the "domain". Using the correct domain required for TFC related data
        this.path = output.createPathProvider(PackOutput.Target.DATA_PACK, domain + "/" + manager.getName());
        this.contentDone = new CompletableFuture<>();
    }

    public void run(HolderLookup.Provider lookup)
    {
        addData(lookup);
        manager.bindValues(elements.buildOrThrow());
    }

    @Override
    public CompletableFuture<?> run(CachedOutput output)
    {
        return beforeRun().thenCompose(provider -> {
            addData(provider);
            final Map<ResourceLocation, T> map = elements.buildOrThrow();
            manager.bindValues(map);
            contentDone.complete(null);
            return CompletableFuture.allOf(map.entrySet()
                .stream()
                .map(e -> DataProvider.saveStable(output, provider, manager.codec(), e.getValue(), path.json(e.getKey())))
                .toArray(CompletableFuture[]::new));
        });
    }

    public CompletableFuture<?> output()
    {
        return contentDone;
    }

    @Override
    public final String getName()
    {
        return "Data Manager (" + manager.getName() + ")";
    }

    protected final void add(String name, T value)
    {
        add(FLHelpers.identifier(name.toLowerCase(Locale.ROOT)), value);
    }

    protected final void add(ResourceLocation name, T value)
    {
        elements.put(name, value);
    }

    protected final void add(DataManager.Reference<T> reference, T value)
    {
        elements.put(reference.id(), value);
    }

    protected CompletableFuture<HolderLookup.Provider> beforeRun()
    {
        return lookup;
    }

    protected abstract void addData(HolderLookup.Provider provider);
}
