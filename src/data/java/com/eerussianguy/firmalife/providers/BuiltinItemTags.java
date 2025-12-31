package com.eerussianguy.firmalife.providers;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;
import com.eerussianguy.firmalife.Accessors;
import com.eerussianguy.firmalife.FirmaLife;
import com.eerussianguy.firmalife.common.FLHelpers;
import com.eerussianguy.firmalife.common.FLTags;
import com.eerussianguy.firmalife.common.blocks.FLBlocks;
import com.eerussianguy.firmalife.common.blocks.greenhouse.Greenhouse;
import com.eerussianguy.firmalife.common.items.FLFood;
import com.eerussianguy.firmalife.common.items.FLItems;
import com.eerussianguy.firmalife.common.util.FLFruit;
import com.eerussianguy.firmalife.common.util.FLMetal;
import com.google.common.base.Preconditions;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.packs.PackType;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagBuilder;
import net.minecraft.tags.TagEntry;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.blocks.TFCBlocks;
import net.dries007.tfc.common.items.Food;
import net.dries007.tfc.common.items.TFCItems;
import net.dries007.tfc.util.registry.IdHolder;

public class BuiltinItemTags extends TagsProvider<Item> implements Accessors
{
    private final ExistingFileHelper.IResourceType resourceType;
    private final CompletableFuture<TagsProvider.TagLookup<Block>> blockTags;
    private final Map<TagKey<Block>, TagKey<Item>> tagsToCopy = new HashMap<>();

    public BuiltinItemTags(GatherDataEvent event, CompletableFuture<HolderLookup.Provider> lookup, CompletableFuture<TagLookup<Block>> blockTags)
    {
        super(event.getGenerator().getPackOutput(), Registries.ITEM, lookup, FirmaLife.MOD_ID, event.getExistingFileHelper());
        this.blockTags = blockTags;
        this.resourceType = new ExistingFileHelper.ResourceType(PackType.SERVER_DATA, ".json", Registries.tagsDirPath(registryKey));
    }

    @Override
    protected void addTags(HolderLookup.Provider provider)
    {
        tag(FLTags.Items.DYNAMIC_FOODS).add(
            FLItems.RAW_PIZZA.get(),
            FLItems.FILLED_PIE.get(),
            itemOf(FLFood.COOKED_PIE).asItem(),
            itemOf(FLFood.BURRITO).asItem(),
            itemOf(FLFood.TACO).asItem(),
            FLItems.STINKY_SOUP.get()
        );
        tag(TFCTags.Items.FLUID_ITEM_INGREDIENT_EMPTY_CONTAINERS).add(
            FLItems.HOLLOW_SHELL.asItem(),
            FLItems.WINE_GLASS.asItem()
        );
        tag(ItemTags.DOORS).add(
            FLBlocks.GREENHOUSE_BLOCKS.values().stream().map(type -> type.get(Greenhouse.BlockType.DOOR)).map(block -> block::asItem)
        );
        FLBlocks.GREENHOUSE_BLOCKS.forEach((greenhouse, entry) -> {
            var greenhouseTag = tagOf(Registries.ITEM, FLHelpers.identifier("greenhouse/" + greenhouse.name().toLowerCase(Locale.ROOT)));
            tag(greenhouseTag).add(entry.values().stream().map(block -> block::asItem));
        });
        tag(FLTags.Items.CHEESE_WHEELS).add(
            FLBlocks.RAJYA_METOK_WHEEL.asItem(),
            FLBlocks.CHEDDAR_WHEEL.asItem(),
            FLBlocks.GOUDA_WHEEL.asItem(),
            FLBlocks.FETA_WHEEL.asItem(),
            FLBlocks.CHEVRE_WHEEL.asItem(),
            FLBlocks.SHOSHA_WHEEL.asItem()
        );
        tag(TFCTags.Items.PLANTS)
            .add(FLBlocks.HERBS.values().stream().map(block -> block::asItem));
        tag(TFCTags.Items.JAM)
            .add(FLItems.JAM);
        tag(FLTags.Items.FOOD_SHELVES).add(FLBlocks.FOOD_SHELVES.values().stream().map(block -> block::asItem));
        tag(FLTags.Items.HANGERS).add(FLBlocks.HANGERS.values().stream().map(block -> block::asItem));
        tag(FLTags.Items.JARBNETS).add(FLBlocks.JARBNETS.values().stream().map(block -> block::asItem));
        tag(FLTags.Items.KEGS).add(FLBlocks.KEGS.values().stream().map(block -> block::asItem));
        tag(FLTags.Items.STOMPING_BARRELS).add(FLBlocks.STOMPING_BARRELS.values().stream().map(block -> block::asItem));
        tag(FLTags.Items.BARREL_PRESSES).add(FLBlocks.BARREL_PRESSES.values().stream().map(block -> block::asItem));
        tag(FLTags.Items.WINE_SHELVES).add(FLBlocks.WINE_SHELVES.values().stream().map(block -> block::asItem));
        tag(TFCTags.Items.FILLED_JARS)
            .add(FLItems.FRUIT_PRESERVES)
            .add(FLItems.UNSEALED_FRUIT_PRESERVES)
            .add(
                FLItems.HONEY_JAR,
                FLItems.COMPOST_JAR,
                FLItems.ROTTEN_COMPOST_JAR,
                FLItems.GUANO_JAR
            );
        tag(TFCTags.Items.EMPTY_JARS)
            .add(FLItems.EMPTY_JAR_WITH_STAINLESS_STEEL_LID);
        tag(TFCTags.Items.EMPTY_JARS_WITH_LID)
            .add(FLItems.EMPTY_JAR_WITH_STAINLESS_STEEL_LID);
        tag(TFCTags.Items.SEALED_PRESERVES).add(FLItems.FRUIT_PRESERVES);
        tag(TFCTags.Items.PRESERVES).add(FLItems.UNSEALED_FRUIT_PRESERVES);
        tag(TFCTags.Items.FOODS).add(Items.PUMPKIN_PIE).add(FLItems.FOODS);
        tag(FLTags.Items.USABLE_ON_OVEN).add(FLItems.PEEL);
        tag(TFCTags.Items.SWEETENERS).add(FLItems.FOODS.get(FLFood.RAW_HONEY));
        tag(FLTags.Items.FEEDS_YEAST).addTag(TFCTags.Items.FLOUR);
        tag(FLTags.Items.BREAD_SLICES).add(
            itemOf(FLFood.WHEAT_SLICE).asItem(),
            itemOf(FLFood.RYE_SLICE).asItem(),
            itemOf(FLFood.BARLEY_SLICE).asItem(),
            itemOf(FLFood.RICE_SLICE).asItem(),
            itemOf(FLFood.MAIZE_SLICE).asItem(),
            itemOf(FLFood.OAT_SLICE).asItem()
        );
        tag(FLTags.Items.FLATBREADS).add(
            itemOf(FLFood.WHEAT_FLATBREAD).asItem(),
            itemOf(FLFood.RYE_FLATBREAD).asItem(),
            itemOf(FLFood.BARLEY_FLATBREAD).asItem(),
            itemOf(FLFood.RICE_FLATBREAD).asItem(),
            itemOf(FLFood.MAIZE_FLATBREAD).asItem(),
            itemOf(FLFood.OAT_FLATBREAD).asItem()
        );
        tag(commonTagOf(Registries.ITEM, "foods/dough")).add(
            itemOf(FLFood.WHEAT_DOUGH).asItem(),
            itemOf(FLFood.RYE_DOUGH).asItem(),
            itemOf(FLFood.BARLEY_DOUGH).asItem(),
            itemOf(FLFood.RICE_DOUGH).asItem(),
            itemOf(FLFood.MAIZE_DOUGH).asItem(),
            itemOf(FLFood.OAT_DOUGH).asItem()
        );
        tag(FLTags.Items.PIZZA_INGREDIENTS).addTags(
            TFCTags.Items.VEGETABLES,
            TFCTags.Items.FRUITS,
            TFCTags.Items.COOKED_MEATS
        );
        tag(FLTags.Items.CHEESES).add(
            itemOf(FLFood.GOUDA).asItem(),
            itemOf(FLFood.CHEVRE).asItem(),
            itemOf(FLFood.SHOSHA).asItem(),
            itemOf(FLFood.FETA).asItem(),
            itemOf(FLFood.RAJYA_METOK).asItem(),
            itemOf(FLFood.CHEDDAR).asItem()
        );
        tag(FLTags.Items.SMOKING_FUEL).addTag(ItemTags.LOGS);
        tag(FLTags.Items.OVEN_FUEL).addTag(ItemTags.LOGS).add(TFCItems.STICK_BUNDLE);
        tag(FLTags.Items.CHOCOLATE_BLENDS).add(
            itemOf(FLFood.MILK_CHOCOLATE_BLEND).asItem(),
            itemOf(FLFood.DARK_CHOCOLATE_BLEND).asItem(),
            itemOf(FLFood.WHITE_CHOCOLATE_BLEND).asItem()
        );
        tag(FLTags.Items.CHOCOLATE).add(
            itemOf(FLFood.MILK_CHOCOLATE).asItem(),
            itemOf(FLFood.DARK_CHOCOLATE).asItem(),
            itemOf(FLFood.WHITE_CHOCOLATE).asItem()
        );
        tag(FLTags.Items.FILLED_BEEHIVE_FRAMES).add(FLItems.FILLED_BEEHIVE_FRAME, FLItems.SUGARED_BEEHIVE_FRAME, FLItems.HONEYED_BEEHIVE_FRAME);
        tag(FLTags.Items.BEEHIVE_FRAMES).addTag(FLTags.Items.FILLED_BEEHIVE_FRAMES).add(FLItems.SCRAPED_BEEHIVE_FRAME, FLItems.INSULATING_BEEHIVE_FRAME, FLItems.BEEHIVE_FRAME);
        tag(FLTags.Items.BEE_BAIT).add(FLItems.WILD_HONEYCOMB, FLItems.AROMATIC_HONEYCOMB);
        tag(TFCTags.Items.CAN_BE_SALTED).add(itemOf(FLFood.BUTTER).asItem());
        tag(TFCTags.Items.TOOL_RACK_TOOLS).add(FLItems.SPOON, FLItems.PEEL);
        tag(FLTags.Items.PUMPKIN_KNAPPING).add(TFCBlocks.PUMPKIN.asItem());
        tag(TFCTags.Items.CLAY_KNAPPING).addTag(FLTags.Items.PUMPKIN_KNAPPING);
        tag(FLTags.Items.WASHABLE_FOODS)
            .add(FLItems.FILLED_PIE, FLItems.RAW_PUMPKIN_PIE, FLItems.STINKY_SOUP)
            .add(itemOf(FLFood.COOKED_PIE).asItem(), Items.PUMPKIN_PIE);
        tag(FLTags.Items.PIE_PANS).add(FLItems.PIE_PAN);
        tag(FLTags.Items.CAN_BE_HUNG).addTag(TFCTags.Items.MEATS).add(itemOf(Food.GARLIC).asItem());
        tag(TFCTags.Items.COMPOST_GREENS_LOW).add(FLItems.FRUIT_LEAF);
        tag(TFCTags.Items.COMPOST_GREENS).add(FLItems.NIGHTSHADE_BERRY);
        tag(FLTags.Items.COOKED_MEATS_AND_SUBSTITUTES).addTag(TFCTags.Items.COOKED_MEATS).add(itemOf(FLFood.TOFU).asItem());
        tag(Tags.Items.LEATHERS).add(FLItems.PINEAPPLE_LEATHER);
        tag(FLTags.Items.USABLE_IN_STOVETOP_SOUP).addTag(TFCTags.Items.USABLE_IN_SOUP);
        tag(FLTags.Items.BEEKEEPER_ARMOR).add(
            FLItems.BEEKEEPER_HELMET,
            FLItems.BEEKEEPER_CHESTPLATE,
            FLItems.BEEKEEPER_LEGGINGS,
            FLItems.BEEKEEPER_BOOTS
        );
        tag(FLTags.Items.RAW_EGGS).add(Items.EGG);
        tag(FLTags.Items.EGG_NOODLE_FLOUR).add(
            itemOf(Food.WHEAT_FLOUR).asItem(),
            itemOf(Food.RYE_FLOUR).asItem(),
            itemOf(Food.BARLEY_FLOUR).asItem(),
            itemOf(Food.RICE_FLOUR).asItem(),
            itemOf(Food.MAIZE_FLOUR).asItem(),
            itemOf(Food.OAT_FLOUR).asItem()
        );
        tag(FLTags.Items.COOKED_POULTRY)
            .add(
                itemOf(Food.COOKED_CHICKEN).asItem(),
                itemOf(Food.COOKED_QUAIL).asItem(),
                itemOf(Food.COOKED_TURKEY).asItem(),
                itemOf(Food.COOKED_GROUSE).asItem(),
                itemOf(Food.COOKED_DUCK).asItem(),
                itemOf(Food.COOKED_PHEASANT).asItem(),
                itemOf(Food.COOKED_PEAFOWL).asItem()
            );
        tag(FLTags.Items.GRAPES).add(
            itemOf(FLFruit.RED_GRAPES).asItem(),
            itemOf(FLFruit.WHITE_GRAPES).asItem()
        );
        tag(FLTags.Items.SMASHED_GRAPES).add(
            itemOf(FLFood.SMASHED_RED_GRAPES).asItem(),
            itemOf(FLFood.SMASHED_WHITE_GRAPES).asItem()
        );
        tag(FLTags.Items.EMPTY_WINE_BOTTLES).add(
            FLItems.EMPTY_HEMATITIC_WINE_BOTTLE,
            FLItems.EMPTY_OLIVINE_WINE_BOTTLE,
            FLItems.EMPTY_VOLCANIC_WINE_BOTTLE
        );
        tag(FLTags.Items.FILLED_WINE_BOTTLES).add(
            FLItems.HEMATITIC_WINE_BOTTLE,
            FLItems.OLIVINE_WINE_BOTTLE,
            FLItems.VOLCANIC_WINE_BOTTLE
        );
        tag(FLTags.Items.WINE_BOTTLES).addTags(
            FLTags.Items.EMPTY_WINE_BOTTLES,
            FLTags.Items.FILLED_WINE_BOTTLES
        );
        tag(FLTags.Items.CAN_BE_PRESSED_LIKE_GRAPES).addTag(FLTags.Items.SMASHED_GRAPES);
        tag(TFCTags.Items.ORE_PIECES).add(FLItems.CHROMIUM_ORES);
        tag(TFCTags.Items.SMALL_ORE_PIECES).add(FLBlocks.SMALL_CHROMITE.asItem());

        for (FLMetal metal : FLMetal.values())
        {
            tag(tagOf(Registries.ITEM, FLHelpers.identifier("metal_item/" + metal.getSerializedName())))
                .add(FLItems.METAL_ITEMS.get(metal))
                .add(FLBlocks.METALS.get(metal).values().stream().map(block -> block::asItem));

            FLItems.METAL_ITEMS.get(metal).forEach((type, item) -> {
                // Specific tag e.g. c:ingot/stainless_steel

                tag(commonTagOf(Registries.ITEM, type.name().toLowerCase(Locale.ROOT) + "s/" + metal.getSerializedName())).add(
                    FLItems.METAL_ITEMS.get(metal).get(type)
                );
                // Generic tag e.g. c:ingot
                tag(commonTagOf(Registries.ITEM, type.name().toLowerCase(Locale.ROOT) + "s")).add(
                    FLItems.METAL_ITEMS.get(metal).get(type)
                );
            });
        }

        copy(FLTags.Blocks.HERBS, FLTags.Items.HERBS);
    }

    @Override
    protected CompletableFuture<HolderLookup.Provider> createContentsProvider()
    {
        return super.createContentsProvider().thenCombine(blockTags, (lookup, tagLookup) -> {
            tagsToCopy.forEach((blockTag, itemTag) -> {
                tagLookup.apply(blockTag)
                    .map(TagBuilder::build)
                    .filter(e -> !e.isEmpty())
                    .ifPresentOrElse(content -> {
                        // N.B. Only copy the tag if the original is non-empty. We do this since we copy all vanilla tags by default,
                        // and we only really want to include the ones that we are adding to
                        final TagBuilder builder = getOrCreateRawBuilder(itemTag);
                        content.forEach(builder::add);
                    }, () -> {
                        // Throw an error if we try and copy a TFC tag that didn't exist
                        if (blockTag.location().getNamespace().equals("tfc")) throw new IllegalArgumentException("Copying empty or missing tag " + blockTag.location());
                    });
            });
            return lookup;
        });
    }

    @Override
    protected ItemTagAppender tag(TagKey<Item> tag)
    {
        return new ItemTagAppender(getOrCreateRawBuilder(tag));
    }

    @Override
    protected TagBuilder getOrCreateRawBuilder(TagKey<Item> tag)
    {
        if (existingFileHelper != null) existingFileHelper.trackGenerated(tag.location(), resourceType);
        return this.builders.computeIfAbsent(tag.location(), key -> new TagBuilder()
        {
            @Override
            public TagBuilder add(TagEntry entry)
            {
                Preconditions.checkArgument(!entry.getId().equals(BuiltInRegistries.ITEM.getDefaultKey()), "Adding air to item tag");
                return super.add(entry);
            }
        });
    }

    private void copy(TagKey<Block> blockTag, TagKey<Item> itemTag)
    {
        this.tagsToCopy.put(blockTag, itemTag);
    }

    static class ItemTagAppender extends TagAppender<Item> implements Accessors
    {
        ItemTagAppender(TagBuilder builder)
        {
            super(builder);
        }

        ItemTagAppender add(Item... items)
        {
            for (Item item : items) add(key(item));
            return this;
        }

        ItemTagAppender add(Stream<? extends Supplier<? extends Item>> items)
        {
            items.forEach(b -> add(key(b.get())));
            return this;
        }

        @SafeVarargs
        final <T extends IdHolder<? extends Item>> ItemTagAppender add(T... items)
        {
            return add(Arrays.stream(items));
        }

        /**
         * Adds every FL-added item matching the given predicate
         */
        ItemTagAppender addEveryFL(Predicate<Item> predicate)
        {
            return add(FLItems.ITEM.getEntries().stream().filter(e -> predicate.test(e.get())));
        }

        ItemTagAppender add(Map<?, ? extends IdHolder<? extends Item>> items)
        {
            items.values().forEach(this::add);
            return this;
        }

        ItemTagAppender add2(Map<?, ? extends Map<?, ? extends IdHolder<? extends Item>>> items)
        {
            items.values().forEach(m -> m.values().forEach(this::add));
            return this;
        }

        <V> ItemTagAppender add2(Map<?, ? extends Map<?, V>> items, Function<V, ? extends IdHolder<? extends Item>> ap)
        {
            items.values().forEach(m -> m.values().forEach(v -> add(ap.apply(v))));
            return this;
        }

        ItemTagAppender add3(Map<?, ? extends Map<?, ? extends Map<?, ? extends IdHolder<? extends Item>>>> items)
        {
            items.values().forEach(m1 -> m1.values().forEach(m2 -> m2.values().forEach(this::add)));
            return this;
        }

        <T1, T2, V extends IdHolder<? extends Item>> ItemTagAppender add(Map<T1, Map<T2, V>> items, T2 key)
        {
            return add(pivot(items, key));
        }

        <T, V extends IdHolder<? extends Item>> ItemTagAppender addOnly(Map<T, V> items, Predicate<T> key)
        {
            items.forEach((k, v) -> {if (key.test(k)) add(v);});
            return this;
        }

        <T1, T2, V extends IdHolder<? extends Item>> ItemTagAppender addOnly2(Map<T1, Map<T2, V>> items, Predicate<T2> key)
        {
            items.values().forEach(m -> addOnly(m, key));
            return this;
        }

        @SafeVarargs
        @SuppressWarnings("unchecked")
        final <K> ItemTagAppender addTags(Function<K, TagKey<Item>> apply, K... values)
        {
            return addTags(Arrays.stream(values).map(apply).toArray(TagKey[]::new));
        }

        @Override
        public ItemTagAppender addTag(TagKey<Item> tag)
        {
            return (ItemTagAppender) super.addTag(tag);
        }

        @Override
        @SafeVarargs
        public final ItemTagAppender addTags(TagKey<Item>... values)
        {
            return (ItemTagAppender) super.addTags(values);
        }

        ItemTagAppender remove(Item... items)
        {
            for (Item item : items) remove(key(item));
            return this;
        }

        private ResourceKey<Item> key(Item item)
        {
            return BuiltInRegistries.ITEM.getResourceKey(item).orElseThrow();
        }
    }
}
