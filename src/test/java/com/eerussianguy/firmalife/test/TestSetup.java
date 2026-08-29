package com.eerussianguy.firmalife.test;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import com.eerussianguy.firmalife.providers.BuiltinBlockTags;
import com.eerussianguy.firmalife.providers.BuiltinClimateRanges;
import com.eerussianguy.firmalife.providers.BuiltinFluidHeats;
import com.eerussianguy.firmalife.providers.BuiltinFluidTags;
import com.eerussianguy.firmalife.providers.BuiltinFoods;
import com.eerussianguy.firmalife.providers.BuiltinGreenhouseTypes;
import com.eerussianguy.firmalife.providers.BuiltinItemHeat;
import com.eerussianguy.firmalife.providers.BuiltinItemSizes;
import com.eerussianguy.firmalife.providers.BuiltinItemTags;
import com.eerussianguy.firmalife.providers.BuiltinKnappingTypes;
import com.eerussianguy.firmalife.providers.BuiltinPlantables;
import com.eerussianguy.firmalife.providers.BuiltinRecipes;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.tags.FluidTagsProvider;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.data.tags.VanillaBlockTagsProvider;
import net.minecraft.data.tags.VanillaItemTagsProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackLocationInfo;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraft.server.packs.resources.MultiPackResourceManager;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.tags.TagBuilder;
import net.minecraft.tags.TagKey;
import net.minecraft.tags.TagLoader;
import net.minecraft.util.profiling.InactiveProfiler;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.levelgen.RandomSupport;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.common.conditions.ICondition;
import net.neoforged.neoforge.common.data.internal.NeoForgeBlockTagsProvider;
import net.neoforged.neoforge.common.data.internal.NeoForgeFluidTagsProvider;
import net.neoforged.neoforge.common.data.internal.NeoForgeItemTagsProvider;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.neoforged.neoforge.resource.ResourcePackLoader;
import net.neoforged.neoforgespi.language.IModFileInfo;
import org.jetbrains.annotations.Nullable;
import org.junit.jupiter.api.BeforeAll;

import net.dries007.tfc.TerraFirmaCraft;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.collections.IndirectHashCollection;
import net.dries007.tfc.util.data.DataManager;
import net.dries007.tfc.util.data.DataManagers;
import net.dries007.tfc.util.data.FluidHeat;

/**
 * Bootstraps a number of useful pieces of data for unit tests. This is done using a hybrid of FirmaLife and vanilla data generation,
 * plus a number of terrible hacks to get this to work... but... it does work. And it allows fast testing of complex mechanics
 * (molds, item/fluid heat components, heating, etc.)
 * <p>
 * Unlike TFC, we only have TFC's published jar, and not the data generators that produced its content. Anything of TFC's that we
 * depend on is instead loaded from the data pack that it ships, in the same manner that a server would load it.
 */
public interface TestSetup
{
    AtomicBoolean LOADED = new AtomicBoolean(false);
    Object LOCK = new Object();

    @BeforeAll
    @SuppressWarnings({"deprecation", "UnstableApiUsage", "DataFlowIssue"})
    static void beforeAll()
    {
        synchronized (LOCK)
        {
            if (LOADED.get()) return;

            final RegistryAccess.Frozen lookup = RegistryAccess.fromRegistryOfRegistries(BuiltInRegistries.REGISTRY);
            final CompletableFuture<HolderLookup.Provider> provider = CompletableFuture.completedFuture(lookup);
            final Path path = Path.of(".");
            final PackOutput output = new PackOutput(path);
            final GatherDataEvent event = new GatherDataEvent(null, new DataGenerator(path, null, true), new GatherDataEvent.DataGeneratorConfig(Set.of(), path, Set.of(), provider, true, true, true, true, true, true, null, null, Set.of()), null);
            final CompletableFuture<?> now = CompletableFuture.completedFuture(null);
            final ResourceManager resources = packOf(TerraFirmaCraft.MOD_ID);

            final TagMap itemTagMap = new TagMap();
            final TagMap blockTagMap = new TagMap();
            final TagMap fluidTagMap = new TagMap();

            // TFC's tags have to be loaded before ours, as ours append to them
            loadTags(resources, blockTagMap, Registries.BLOCK);
            loadTags(resources, itemTagMap, Registries.ITEM);
            loadTags(resources, fluidTagMap, Registries.FLUID);

            final CompletableFuture<TagsProvider.TagLookup<Block>> blockTagLookup = CompletableFuture.completedFuture(key -> Optional.ofNullable(blockTagMap.get(key.location())));

            add(blockTagMap, new VanillaBlockTagsProvider(output, provider));
            add(itemTagMap, new VanillaItemTagsProvider(output, provider, blockTagLookup));
            add(fluidTagMap, new FluidTagsProvider(output, provider));

            add(blockTagMap, new NeoForgeBlockTagsProvider(output, provider, null));
            add(itemTagMap, new NeoForgeItemTagsProvider(output, provider, blockTagLookup, null));
            add(fluidTagMap, new NeoForgeFluidTagsProvider(output, provider, null));

            add(blockTagMap, new BuiltinBlockTags(event, provider));
            add(itemTagMap, new BuiltinItemTags(event, provider, blockTagLookup));
            add(fluidTagMap, new BuiltinFluidTags(event, provider));

            resolve(blockTagMap, BuiltInRegistries.BLOCK);
            resolve(itemTagMap, BuiltInRegistries.ITEM);
            resolve(fluidTagMap, BuiltInRegistries.FLUID);

            // Data managers are reload listeners, which are only ever populated by a data pack reload. That never happens
            // without a server, so TFC's content is loaded by reloading them here ourselves
            final Map<DataManager<?>, Map<ResourceLocation, ?>> tfcData = new HashMap<>();
            for (DataManager<?> manager : DataManagers.REGISTRY)
            {
                reload(manager, resources);
                tfcData.put(manager, Map.copyOf(manager.getElements()));
            }

            new BuiltinClimateRanges(output, provider).run(lookup);
            new BuiltinPlantables(output, provider).run(lookup);
            new BuiltinGreenhouseTypes(output, provider).run(lookup);
            new BuiltinFoods(output, provider).run(lookup);
            new BuiltinItemSizes(output, provider).run(lookup);
            new BuiltinFluidHeats(output, provider).run(lookup);
            new BuiltinKnappingTypes(output, provider).run(lookup); // Must run before recipes
            final var itemHeat = new BuiltinItemHeat(output, provider, now);
            itemHeat.run(lookup);

            // Our providers bind only the data that they generate, which replaces anything that TFC's data pack provided for
            // the same manager, so it has to be merged back in underneath what we generated
            tfcData.forEach(TestSetup::merge);

            final RecipeManager recipeManager = new RecipeManager(lookup);
            final List<RecipeHolder<?>> holders = new ArrayList<>();
            new BuiltinRecipes(output, provider, now, itemHeat).buildRecipes(new RecipeOutput() {
                @Override
                public Advancement.Builder advancement()
                {
                    return Advancement.Builder.recipeAdvancement();
                }

                @Override
                public void accept(ResourceLocation id, Recipe<?> recipe, @Nullable AdvancementHolder advancement, ICondition... conditions)
                {
                    holders.add(new RecipeHolder<>(id, recipe));
                }
            });
            recipeManager.replaceRecipes(holders);

            Helpers.setCachedRecipeManager(recipeManager);
            IndirectHashCollection.reloadAllCaches(recipeManager);
            FluidHeat.updateCache();

            LOADED.set(true);
        }
    }


    Field TAG_BUILDERS = Helpers.uncheck(() -> {
        final var field = TagsProvider.class.getDeclaredField("builders");
        field.setAccessible(true);
        return field;
    });
    Method TAG_CONTENTS_PROVIDER = Helpers.uncheck(() -> {
        final var method = TagsProvider.class.getDeclaredMethod("createContentsProvider");
        method.setAccessible(true);
        return method;
    });

    private static ResourceManager packOf(String modId)
    {
        final IModFileInfo mod = Objects.requireNonNull(ModList.get().getModFileById(modId), () -> "No mod file for " + modId);
        final Pack.ResourcesSupplier supplier = ResourcePackLoader.createPackForMod(mod);
        final PackResources pack = supplier.openPrimary(new PackLocationInfo(modId, Component.literal(modId), PackSource.BUILT_IN, Optional.empty()));

        return new MultiPackResourceManager(PackType.SERVER_DATA, List.of(pack));
    }

    private static void loadTags(ResourceManager resources, TagMap map, ResourceKey<? extends Registry<?>> registry)
    {
        new TagLoader<>(id -> Optional.empty(), Registries.tagsDirPath(registry))
            .load(resources)
            .forEach((id, entries) -> {
                final TagBuilder builder = map.computeIfAbsent(id, key -> TagBuilder.create());
                entries.forEach(entry -> builder.add(entry.entry()));
            });
    }

    private static void reload(DataManager<?> manager, ResourceManager resources)
    {
        final PreparableReloadListener.PreparationBarrier barrier = new PreparableReloadListener.PreparationBarrier() {
            @Override
            public <T> CompletableFuture<T> wait(T value)
            {
                return CompletableFuture.completedFuture(value);
            }
        };
        manager.reload(barrier, resources, InactiveProfiler.INSTANCE, InactiveProfiler.INSTANCE, Runnable::run, Runnable::run).join();
    }

    @SuppressWarnings("unchecked")
    private static <T> void merge(DataManager<T> manager, Map<ResourceLocation, ?> loaded)
    {
        final Map<ResourceLocation, T> elements = new HashMap<>((Map<ResourceLocation, T>) loaded);
        elements.putAll(manager.getElements());
        manager.bindValues(elements);
    }

    private static <T extends TagsProvider<?>> void add(TagMap map, T provider)
    {
        Helpers.uncheck(() -> {
            TAG_BUILDERS.set(provider, map);
            ((CompletableFuture<?>) TAG_CONTENTS_PROVIDER.invoke(provider)).get();
        });
    }

    private static <T> void resolve(TagMap map, Registry<T> registry)
    {
        final TagResolver<T> resolver = new TagResolver<>(map, registry);
        registry.bindTags(map.keySet()
            .stream()
            .collect(Collectors.toMap(
                e -> TagKey.create(registry.key(), e),
                e -> resolver.resolve(e).toList()
            )));
    }

    record TagResolver<T>(Map<ResourceLocation, TagBuilder> builder, Registry<T> registry)
    {
        Stream<Holder<T>> resolve(ResourceLocation id)
        {
            return Objects.requireNonNull(builder.get(id), () -> "No tag for " + id + " in registry " + registry.key().location())
                .build()
                .stream()
                .flatMap(e -> e.isTag()
                    ? e.isRequired() || builder.containsKey(e.getId())
                        ? resolve(e.getId())
                        : Stream.empty()
                    : resolveElement(e.getId(), e.isRequired()));
        }

        private Stream<Holder<T>> resolveElement(ResourceLocation id, boolean required)
        {
            final ResourceKey<T> key = ResourceKey.create(registry.key(), id);
            return !required && !registry.containsKey(key)
                ? Stream.empty()
                : Stream.of(registry.wrapAsHolder(registry.getOrThrow(key)));
        }
    }

    class TagMap extends LinkedHashMap<ResourceLocation, TagBuilder>
    {
        @Override
        public void clear() {} // No-op
    }

    default long seed()
    {
        final long seed = RandomSupport.generateUniqueSeed();
        System.out.printf("Seed: %d\n", seed);
        return seed;
    }
}
