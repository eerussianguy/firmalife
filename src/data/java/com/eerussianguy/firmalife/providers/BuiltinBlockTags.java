package com.eerussianguy.firmalife.providers;

import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;
import com.eerussianguy.firmalife.Accessors;
import com.eerussianguy.firmalife.FirmaLife;
import com.eerussianguy.firmalife.common.blocks.FLBlocks;
import com.eerussianguy.firmalife.common.blocks.greenhouse.Greenhouse;
import com.eerussianguy.firmalife.common.blocks.plant.FLFruitBlocks;
import com.google.common.base.Preconditions;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.packs.PackType;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagBuilder;
import net.minecraft.tags.TagEntry;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.WallBlock;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.blocks.DecorationBlockHolder;
import net.dries007.tfc.common.blocks.TFCBlocks;
import net.dries007.tfc.common.blocks.plant.Plant;
import net.dries007.tfc.common.blocks.rock.Ore;
import net.dries007.tfc.util.Metal;
import net.dries007.tfc.util.registry.IdHolder;

import static com.eerussianguy.firmalife.common.FLTags.Blocks.*;

public class BuiltinBlockTags extends TagsProvider<Block> implements Accessors
{
    private final ExistingFileHelper.IResourceType resourceType;

    public BuiltinBlockTags(GatherDataEvent event, CompletableFuture<HolderLookup.Provider> lookup)
    {
        super(event.getGenerator().getPackOutput(), Registries.BLOCK, lookup, FirmaLife.MOD_ID, event.getExistingFileHelper());
        this.resourceType = new ExistingFileHelper.ResourceType(PackType.SERVER_DATA, ".json", Registries.tagsDirPath(registryKey));
    }

    @Override
    protected void addTags(HolderLookup.Provider provider)
    {
        tag(BlockTags.MINEABLE_WITH_PICKAXE)
            .addTag(OVEN_BLOCKS)
            .addTags(ALL_COPPER_GREENHOUSE, ALL_IRON_GREENHOUSE, STAINLESS_STEEL_GREENHOUSE, PLANTERS)
            .add2(FLBlocks.CHROMITE_ORES)
            .add(FLBlocks.SEALED_BRICKS, FLBlocks.CHISELED_SEALED_BRICKS, FLBlocks.POLISHED_SEALED_BRICKS, FLBlocks.TILES, FLBlocks.RUSTIC_BRICKS, FLBlocks.SEALED_WALL, FLBlocks.SEALED_TRAPDOOR, FLBlocks.SEALED_DOOR, FLBlocks.DARK_LADDER)
            .add(FLBlocks.PUMPING_STATION, FLBlocks.IRRIGATION_TANK, FLBlocks.SPRINKLER, FLBlocks.FLOOR_SPRINKLER, FLBlocks.COPPER_PIPE, FLBlocks.OXIDIZED_COPPER_PIPE, FLBlocks.COMPOST_TUMBLER)
            .addAll(FLBlocks.RUSTIC_BRICK_DECOR)
            .addAll(FLBlocks.TILE_DECOR)
            .add(FLBlocks.OVEN_COUNTERTOP)
            .add(FLBlocks.ASHTRAY, FLBlocks.JARRING_STATION, FLBlocks.PICKER, FLBlocks.SWEEPER, FLBlocks.VAT, FLBlocks.STOVETOP_GRILL, FLBlocks.STOVETOP_POT)
            .add2(FLBlocks.METALS)
            .add(FLBlocks.INSULATED_OVEN_BOTTOM)
            .add(FLBlocks.INSULATED_OVEN_TOP);
        tag(BlockTags.MINEABLE_WITH_AXE)
            .add(FLBlocks.TREATED_WOOD, FLBlocks.PLATE, FLBlocks.SOLAR_DRIER, FLBlocks.MIXING_BOWL, FLBlocks.GRAPE_TRELLIS_POST, FLBlocks.GRAPE_TRELLIS_POST_RED, FLBlocks.GRAPE_TRELLIS_POST_WHITE, FLBlocks.CLIMATE_STATION, FLBlocks.BEEHIVE, FLBlocks.WILD_BEEHIVE, FLBlocks.SKEP, FLBlocks.CENTRIFUGE)
            .add(FLBlocks.FRUIT_TREE_BRANCHES)
            .add(FLBlocks.FRUIT_TREE_GROWING_BRANCHES)
            .add(FLBlocks.GREENHOUSE_BLOCKS.get(Greenhouse.TREATED_WOOD))
            .add(FLBlocks.GREENHOUSE_BLOCKS.get(Greenhouse.WEATHERED_TREATED_WOOD))
            .addTags(FOOD_SHELVES, HANGERS, JARBNETS, KEGS, STOMPING_BARRELS, BARREL_PRESSES, WINE_SHELVES, PLANTERS);
        tag(BlockTags.MINEABLE_WITH_HOE)
            .add(FLBlocks.FRUIT_TREE_LEAVES)
            .add(FLBlocks.FRUIT_TREE_SAPLINGS)
            .add(FLBlocks.STATIONARY_BUSHES)
            .addTag(CHEESE_WHEELS)
            .add(FLBlocks.CARVED_PUMPKINS)
            .add(FLBlocks.DRYING_MAT)
            .add(FLBlocks.WOOL_STRING, FLBlocks.GRAPE_STRING, FLBlocks.GRAPE_STRING_PLANT_RED, FLBlocks.GRAPE_STRING_PLANT_WHITE, FLBlocks.GRAPE_STRING_RED, FLBlocks.GRAPE_STRING_WHITE, FLBlocks.GRAPE_FLUFF_RED, FLBlocks.GRAPE_FLUFF_WHITE, FLBlocks.WILD_BEEHIVE)
            .add(FLBlocks.WILD_RED_GRAPES, FLBlocks.WILD_WHITE_GRAPES)
            .add(FLBlocks.JACK_O_LANTERNS);
        tag(BlockTags.STAIRS).addEveryFL(b -> b instanceof StairBlock);
        tag(BlockTags.SLABS).addEveryFL(b -> b instanceof SlabBlock);
        tag(BlockTags.WALLS).addEveryFL(b -> b instanceof WallBlock);
        tag(BlockTags.TRAPDOORS).add(FLBlocks.GREENHOUSE_BLOCKS, Greenhouse.BlockType.TRAPDOOR).add(FLBlocks.SEALED_TRAPDOOR);
        tag(BlockTags.DOORS).add(FLBlocks.GREENHOUSE_BLOCKS, Greenhouse.BlockType.DOOR).add(FLBlocks.SEALED_DOOR);
        tag(BlockTags.REPLACEABLE).addEveryFL(e -> e.defaultBlockState().canBeReplaced());
        tag(BlockTags.LEAVES).add(FLBlocks.FRUIT_TREE_LEAVES);
        tag(BlockTags.FLOWER_POTS)
            .add(FLBlocks.POTTED_HERBS)
            .add(FLBlocks.FRUIT_TREE_POTTED_SAPLINGS);

        tag(BlockTags.CLIMBABLE).add(FLBlocks.DARK_LADDER);

        tag(TFCTags.Blocks.THORNY_BUSHES).add(FLBlocks.STATIONARY_BUSHES.get(FLFruitBlocks.StationaryBush.PINEAPPLE));
        tag(TFCTags.Blocks.SINGLE_BLOCK_REPLACEABLE)
            .addTag(HERBS);
        tag(TFCTags.Blocks.MINEABLE_WITH_GLASS_SAW)
            .add(FLBlocks.REINFORCED_POURED_GLASS);
        tag(TFCTags.Blocks.FOX_RAIDABLE).add(FLBlocks.STATIONARY_BUSHES);
        tag(TFCTags.Blocks.CAN_BE_SNOW_PILED)
            .add(FLBlocks.SMALL_CHROMITE);
        tag(TFCTags.Blocks.PROSPECTABLE)
            .add2(FLBlocks.CHROMITE_ORES);
        tag(TFCTags.Blocks.FRUIT_TREE_BRANCH).add(FLBlocks.FRUIT_TREE_BRANCHES);
        tag(TFCTags.Blocks.FRUIT_TREE_LEAVES).add(FLBlocks.FRUIT_TREE_LEAVES);
        tag(TFCTags.Blocks.FRUIT_TREE_SAPLING).add(FLBlocks.FRUIT_TREE_SAPLINGS);

        tag(OVEN_INSULATION)
            .add(Blocks.BRICKS, Blocks.BRICK_STAIRS, Blocks.BRICK_SLAB, FLBlocks.SEALED_BRICKS.get(), FLBlocks.CHISELED_SEALED_BRICKS.get(), FLBlocks.POLISHED_SEALED_BRICKS.get())
            .addTags(TFCTags.Blocks.CHARCOAL_FORGE_INSULATION, OVEN_BLOCKS);
        tag(PLANTERS)
            .add(FLBlocks.LARGE_PLANTER, FLBlocks.QUAD_PLANTER, FLBlocks.BONSAI_PLANTER, FLBlocks.TRELLIS_PLANTER, FLBlocks.HANGING_PLANTER, FLBlocks.HYDROPONIC_PLANTER);
        tag(BEE_RESTORATION_PLANTS)
            .addTag(HERBS)
            .add(
                TFCBlocks.PLANTS.get(Plant.GOLDENROD),
                TFCBlocks.PLANTS.get(Plant.ROSE),
                TFCBlocks.PLANTS.get(Plant.ALLIUM),
                TFCBlocks.PLANTS.get(Plant.ANTHURIUM),
                TFCBlocks.PLANTS.get(Plant.BLOOD_LILY),
                TFCBlocks.PLANTS.get(Plant.BLUE_GINGER),
                TFCBlocks.PLANTS.get(Plant.BLUE_ORCHID),
                TFCBlocks.PLANTS.get(Plant.BUTTERCUP),
                TFCBlocks.PLANTS.get(Plant.BUTTERFLY_MILKWEED),
                TFCBlocks.PLANTS.get(Plant.BLACK_ORCHID),
                TFCBlocks.PLANTS.get(Plant.CALENDULA),
                TFCBlocks.PLANTS.get(Plant.CORNFLOWER),
                TFCBlocks.PLANTS.get(Plant.DANDELION),
                TFCBlocks.PLANTS.get(Plant.DESERT_FLAME),
                TFCBlocks.PLANTS.get(Plant.EDELWEISS),
                TFCBlocks.PLANTS.get(Plant.FIELD_HORSETAIL),
                TFCBlocks.PLANTS.get(Plant.GRAPE_HYACINTH),
                TFCBlocks.PLANTS.get(Plant.HELICONIA),
                TFCBlocks.PLANTS.get(Plant.HEATHER),
                TFCBlocks.PLANTS.get(Plant.HOUSTONIA),
                TFCBlocks.PLANTS.get(Plant.NASTURTIUM),
                TFCBlocks.PLANTS.get(Plant.OXEYE_DAISY),
                TFCBlocks.PLANTS.get(Plant.POPPY),
                TFCBlocks.PLANTS.get(Plant.PULSATILLA),
                TFCBlocks.PLANTS.get(Plant.SILVER_SPURFLOWER),
                TFCBlocks.PLANTS.get(Plant.SNAPDRAGON_PINK),
                TFCBlocks.PLANTS.get(Plant.SNAPDRAGON_RED),
                TFCBlocks.PLANTS.get(Plant.SNAPDRAGON_WHITE),
                TFCBlocks.PLANTS.get(Plant.SNAPDRAGON_YELLOW),
                TFCBlocks.PLANTS.get(Plant.TRILLIUM),
                TFCBlocks.PLANTS.get(Plant.TROPICAL_MILKWEED),
                TFCBlocks.PLANTS.get(Plant.TULIP_ORANGE),
                TFCBlocks.PLANTS.get(Plant.TULIP_RED),
                TFCBlocks.PLANTS.get(Plant.TULIP_PINK),
                TFCBlocks.PLANTS.get(Plant.TULIP_WHITE),
                TFCBlocks.PLANTS.get(Plant.YELLOW_SAXIFRAGE)
            );
        tag(BEE_RESTORATION_WATER_PLANTS).add(
            TFCBlocks.PLANTS.get(Plant.WHITE_WATER_LILY),
            TFCBlocks.PLANTS.get(Plant.YELLOW_WATER_LILY),
            TFCBlocks.PLANTS.get(Plant.PURPLE_WATER_LILY),
            TFCBlocks.PLANTS.get(Plant.SARGASSUM),
            TFCBlocks.PLANTS.get(Plant.LOTUS),
            TFCBlocks.PLANTS.get(Plant.PISTIA)
        );
        tag(GREENHOUSE).add2(FLBlocks.GREENHOUSE_BLOCKS);
        tag(ALL_IRON_GREENHOUSE).addTags(IRON_GREENHOUSE, RUSTED_IRON_GREENHOUSE);
        tag(IRON_GREENHOUSE).add(FLBlocks.GREENHOUSE_BLOCKS.get(Greenhouse.IRON));
        tag(RUSTED_IRON_GREENHOUSE).add(FLBlocks.GREENHOUSE_BLOCKS.get(Greenhouse.RUSTED_IRON));
        tag(STAINLESS_STEEL_GREENHOUSE).add(FLBlocks.GREENHOUSE_BLOCKS.get(Greenhouse.STAINLESS_STEEL));
        tag(ALL_COPPER_GREENHOUSE)
            .add(FLBlocks.GREENHOUSE_BLOCKS.get(Greenhouse.COPPER))
            .add(FLBlocks.GREENHOUSE_BLOCKS.get(Greenhouse.EXPOSED_COPPER))
            .add(FLBlocks.GREENHOUSE_BLOCKS.get(Greenhouse.WEATHERED_COPPER))
            .add(FLBlocks.GREENHOUSE_BLOCKS.get(Greenhouse.OXIDIZED_COPPER));
        tag(ALL_TREATED_WOOD_GREENHOUSE)
            .add(FLBlocks.GREENHOUSE_BLOCKS.get(Greenhouse.TREATED_WOOD))
            .add(FLBlocks.GREENHOUSE_BLOCKS.get(Greenhouse.WEATHERED_TREATED_WOOD));
        tag(CELLAR_INSULATION)
            .add(FLBlocks.SEALED_BRICKS, FLBlocks.CHISELED_SEALED_BRICKS, FLBlocks.POLISHED_SEALED_BRICKS, FLBlocks.SEALED_DOOR, FLBlocks.SEALED_TRAPDOOR, FLBlocks.SEALED_WALL);
        tag(ALWAYS_VALID_GREENHOUSE_WALL)
            .addTags(BlockTags.DOORS, BlockTags.TRAPDOORS);
        tag(GRAPE_STRINGS).add(FLBlocks.GRAPE_STRING_RED, FLBlocks.GRAPE_STRING_WHITE);
        tag(GRAPE_TRELLIS_POSTS_PLANT).add(FLBlocks.GRAPE_TRELLIS_POST_RED, FLBlocks.GRAPE_TRELLIS_POST_WHITE);
        tag(BUZZING_LEAVES).add(FLBlocks.FRUIT_TREE_LEAVES.get(FLFruitBlocks.Tree.FIG));
        tag(PIPE_REPLACEABLE).addTags(BlockTags.DIRT, TFCTags.Blocks.GRASS, BlockTags.BASE_STONE_OVERWORLD, Tags.Blocks.GRAVELS, BlockTags.SAND);
        tag(GREENHOUSE_FULL_WALLS)
            .add(FLBlocks.GREENHOUSE_BLOCKS, Greenhouse.BlockType.WALL)
            .add(FLBlocks.GREENHOUSE_BLOCKS, Greenhouse.BlockType.PORT);
        tag(GREENHOUSE_PANEL_WALLS).add(FLBlocks.GREENHOUSE_BLOCKS, Greenhouse.BlockType.PANEL_WALL);
        tag(GREENHOUSE_PANEL_ROOFS).add(FLBlocks.GREENHOUSE_BLOCKS, Greenhouse.BlockType.PANEL_ROOF);

        tag(OVEN_BLOCKS)
            .add(FLBlocks.CLAY_OVEN_BOTTOM, FLBlocks.CLAY_OVEN_CHIMNEY, FLBlocks.CLAY_OVEN_HOPPER, FLBlocks.CLAY_OVEN_TOP)
            .add(FLBlocks.CURED_OVEN_BOTTOM)
            .add(FLBlocks.CURED_OVEN_HOPPER)
            .add(FLBlocks.CURED_OVEN_TOP)
            .add(FLBlocks.CURED_OVEN_CHIMNEY)
            .add(FLBlocks.OVEN_COUNTERTOP)
            .add(FLBlocks.TILES)
            .add(FLBlocks.TILE_DECOR.slab())
            .add(FLBlocks.TILE_DECOR.stair())
            .add(FLBlocks.TILE_DECOR.wall())
            .add(FLBlocks.RUSTIC_BRICKS)
            .add(FLBlocks.RUSTIC_BRICK_DECOR.slab())
            .add(FLBlocks.RUSTIC_BRICK_DECOR.stair())
            .add(FLBlocks.RUSTIC_BRICK_DECOR.wall());
        tag(FOOD_SHELVES).add(FLBlocks.FOOD_SHELVES);
        tag(HANGERS).add(FLBlocks.HANGERS);
        tag(JARBNETS).add(FLBlocks.JARBNETS);
        tag(KEGS).add(FLBlocks.KEGS);
        tag(STOMPING_BARRELS).add(FLBlocks.STOMPING_BARRELS);
        tag(BARREL_PRESSES).add(FLBlocks.BARREL_PRESSES);
        tag(WINE_SHELVES).add(FLBlocks.WINE_SHELVES);
        tag(CHEESE_WHEELS).add(FLBlocks.CHEDDAR_WHEEL, FLBlocks.CHEVRE_WHEEL, FLBlocks.FETA_WHEEL, FLBlocks.GOUDA_WHEEL, FLBlocks.RAJYA_METOK_WHEEL, FLBlocks.SHOSHA_WHEEL);
        tag(CHIMNEYS).add(FLBlocks.CURED_OVEN_CHIMNEY).add(FLBlocks.CLAY_OVEN_CHIMNEY);
        tag(HERBS).add(FLBlocks.HERBS);


        tag(TFCTags.Blocks.CAN_START_COLLAPSE).add2(FLBlocks.CHROMITE_ORES);
        tag(TFCTags.Blocks.CAN_COLLAPSE).add2(FLBlocks.CHROMITE_ORES);

        tag(Tags.Blocks.ORES).addTag(CHROMITE);
        tag(CHROMITE).addTags(POOR_CHROMITE, NORMAL_CHROMITE, RICH_CHROMITE);
        tag(POOR_CHROMITE).add(FLBlocks.CHROMITE_ORES, Ore.Grade.POOR);
        tag(NORMAL_CHROMITE).add(FLBlocks.CHROMITE_ORES, Ore.Grade.NORMAL);
        tag(RICH_CHROMITE).add(FLBlocks.CHROMITE_ORES, Ore.Grade.RICH);
        tag(BlockTags.STAIRS).add(FLBlocks.METALS, Metal.BlockType.BLOCK_STAIRS);
        tag(BlockTags.SLABS).add(FLBlocks.METALS, Metal.BlockType.BLOCK_SLAB);
    }

    @Override
    protected BlockTagAppender tag(TagKey<Block> tag)
    {
        return new BlockTagAppender(getOrCreateRawBuilder(tag));
    }

    @Override
    protected TagBuilder getOrCreateRawBuilder(TagKey<Block> tag)
    {
        if (existingFileHelper != null) existingFileHelper.trackGenerated(tag.location(), resourceType);
        return this.builders.computeIfAbsent(tag.location(), key -> new TagBuilder()
        {
            @Override
            public TagBuilder add(TagEntry entry)
            {
                Preconditions.checkArgument(!entry.getId().equals(BuiltInRegistries.BLOCK.getDefaultKey()), "Adding air to block tag");
                return super.add(entry);
            }
        });
    }

    @SuppressWarnings("UnusedReturnValue")
    static class BlockTagAppender extends TagAppender<Block> implements Accessors
    {
        BlockTagAppender(TagBuilder builder)
        {
            super(builder);
        }

        BlockTagAppender add(Block... blocks)
        {
            for (Block block : blocks) add(key(block));
            return this;
        }

        BlockTagAppender add(Stream<? extends Supplier<? extends Block>> blocks)
        {
            blocks.forEach(b -> add(key(b.get())));
            return this;
        }

        @SafeVarargs
        final <T extends IdHolder<? extends Block>> BlockTagAppender add(T... blocks)
        {
            return add(Arrays.stream(blocks));
        }

        /**
         * Adds every TFC-added block matching the given predicate
         */
        BlockTagAppender addEveryFL(Predicate<Block> predicate)
        {
            return add(FLBlocks.BLOCK.getEntries().stream().filter(e -> predicate.test(e.get())));
        }

        BlockTagAppender add(Map<?, ? extends IdHolder<? extends Block>> blocks)
        {
            blocks.values().forEach(this::add);
            return this;
        }

        BlockTagAppender add2(Map<?, ? extends Map<?, ? extends IdHolder<? extends Block>>> blocks)
        {
            blocks.values().forEach(m -> m.values().forEach(this::add));
            return this;
        }

        <V> BlockTagAppender add2(Map<?, ? extends Map<?, V>> blocks, Function<V, ? extends IdHolder<? extends Block>> ap)
        {
            blocks.values().forEach(m -> m.values().forEach(v -> add(ap.apply(v))));
            return this;
        }

        BlockTagAppender add3(Map<?, ? extends Map<?, ? extends Map<?, ? extends IdHolder<? extends Block>>>> blocks)
        {
            blocks.values().forEach(m1 -> m1.values().forEach(m2 -> m2.values().forEach(this::add)));
            return this;
        }

        BlockTagAppender addAll(Map<?, DecorationBlockHolder> blocks)
        {
            blocks.values().forEach(h -> add(h.slab(), h.stair(), h.wall()));
            return this;
        }

        BlockTagAppender addAll(DecorationBlockHolder blocks)
        {
            add(blocks.slab(), blocks.stair(), blocks.wall());
            return this;
        }

        BlockTagAppender addAll2(Map<?, ? extends Map<?, DecorationBlockHolder>> blocks)
        {
            blocks.values().forEach(m -> m.values().forEach(h -> add(h.slab(), h.stair(), h.wall())));
            return this;
        }

        <T1, T2, V extends IdHolder<? extends Block>> BlockTagAppender add(Map<T1, Map<T2, V>> blocks, T2 key)
        {
            return add(pivot(blocks, key));
        }

        <T, V extends IdHolder<? extends Block>> BlockTagAppender addOnly(Map<T, V> blocks, Predicate<T> key)
        {
            blocks.forEach((k, v) -> {if (key.test(k)) add(v);});
            return this;
        }

        <T1, T2, V extends IdHolder<? extends Block>> BlockTagAppender addOnly2(Map<T1, Map<T2, V>> blocks, Predicate<T2> key)
        {
            blocks.values().forEach(m -> addOnly(m, key));
            return this;
        }

        @SafeVarargs
        @SuppressWarnings("unchecked")
        final <K> BlockTagAppender addTags(Function<K, TagKey<Block>> apply, K... values)
        {
            return addTags(Arrays.stream(values).map(apply).toArray(TagKey[]::new));
        }

        @Override
        public BlockTagAppender addTag(TagKey<Block> tag)
        {
            return (BlockTagAppender) super.addTag(tag);
        }

        @Override
        @SafeVarargs
        public final BlockTagAppender addTags(TagKey<Block>... values)
        {
            return (BlockTagAppender) super.addTags(values);
        }

        BlockTagAppender remove(Block... blocks)
        {
            for (Block block : blocks) remove(key(block));
            return this;
        }

        private ResourceKey<Block> key(Block block)
        {
            return BuiltInRegistries.BLOCK.getResourceKey(block).orElseThrow();
        }
    }
}
