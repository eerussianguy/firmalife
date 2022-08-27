package com.eerussianguy.firmalife.common.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;

import com.google.common.base.Joiner;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraftforge.common.ForgeConfig;
import net.minecraftforge.resource.PathResourcePack;
import net.minecraftforge.resource.ResourceCacheManager;

public class SafePathResourcePack extends PathResourcePack
{
    private final ResourceCacheManager cacheManager = new ResourceCacheManager(true, ForgeConfig.COMMON.indexModPackCachesOnThread, (packType, namespace) -> resolve(packType.getDirectory(), namespace).toAbsolutePath());

    public SafePathResourcePack(String packName, Path source)
    {
        super(packName, source);
    }

    @Override
    public Collection<ResourceLocation> getResources(PackType type, String resourceNamespace, String pathIn, int maxDepth, Predicate<String> filter)
    {
        try
        {
            Path root = resolve(type.getDirectory(), resourceNamespace).toAbsolutePath();
            Path inputPath = root.getFileSystem().getPath(pathIn);

            if (ResourceCacheManager.shouldUseCache() && this.cacheManager.hasCached(type, resourceNamespace))
            {
                return this.cacheManager.getResources(type, resourceNamespace, inputPath, filter);
            }

            return Files.walk(root)
                .map(root::relativize)
                .filter(path -> path.getNameCount() <= maxDepth && !path.toString().endsWith(".mcmeta") && path.startsWith(inputPath))
                .filter(path -> filter.test(path.getFileName().toString()))
                // backporting fix for resource location path validity
                .filter(path -> ResourceLocation.isValidPath(Joiner.on('/').join(path)))
                .map(path -> new ResourceLocation(resourceNamespace, Joiner.on('/').join(path)))
                .collect(Collectors.toList());
        }
        catch (IOException e)
        {
            return Collections.emptyList();
        }
    }

    @Override
    public void initForNamespace(final String namespace)
    {
        if (ResourceCacheManager.shouldUseCache())
        {
            this.cacheManager.index(namespace);
        }
    }

    @Override
    public Set<String> getNamespaces(PackType type)
    {
        if (ResourceCacheManager.shouldUseCache())
        {
            return this.cacheManager.getNamespaces(type);
        }

        return getNamespacesFromDisk(type);
    }
}
