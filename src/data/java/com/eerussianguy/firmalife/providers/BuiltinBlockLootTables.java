package com.eerussianguy.firmalife.providers;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Supplier;
import com.eerussianguy.firmalife.Accessors;
import com.eerussianguy.firmalife.FirmaLife;
import com.eerussianguy.firmalife.common.blocks.CheeseWheelBlock;
import com.eerussianguy.firmalife.common.blocks.FLBlocks;
import com.eerussianguy.firmalife.common.blocks.MixingBowlBlock;
import com.eerussianguy.firmalife.common.blocks.greenhouse.Greenhouse;
import com.eerussianguy.firmalife.common.blocks.plant.FLFruitBlocks;
import com.eerussianguy.firmalife.common.items.FLFood;
import com.eerussianguy.firmalife.common.items.FLItems;
import com.eerussianguy.firmalife.common.util.FLFruit;
import com.google.common.collect.Streams;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.advancements.critereon.StatePropertiesPredicate;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.tags.TagKey;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.AlternativesEntry;
import net.minecraft.world.level.storage.loot.functions.LootItemConditionalFunction;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.predicates.AnyOfCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemBlockStatePropertyCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceCondition;
import net.minecraft.world.level.storage.loot.predicates.MatchTool;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;
import net.neoforged.neoforge.registries.DeferredHolder;
import org.jetbrains.annotations.NotNull;

import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.blocks.DecorationBlockHolder;
import net.dries007.tfc.common.blocks.TFCBlocks;
import net.dries007.tfc.common.blocks.crop.WildCropBlock;
import net.dries007.tfc.common.blocks.plant.fruit.FruitTreeLeavesBlock;
import net.dries007.tfc.common.blocks.plant.fruit.Lifecycle;
import net.dries007.tfc.common.items.TFCItems;
import net.dries007.tfc.util.Metal;

import static net.minecraft.world.level.storage.loot.LootPool.*;
import static net.minecraft.world.level.storage.loot.entries.LootItem.*;
import static net.minecraft.world.level.storage.loot.predicates.ExplosionCondition.*;
import static net.minecraft.world.level.storage.loot.predicates.LootItemBlockStatePropertyCondition.*;

public class BuiltinBlockLootTables extends BlockLootSubProvider implements Accessors
{
    private final LootItemCondition.Builder HAS_SHEARS_LIKE = MatchTool.toolMatches(ItemPredicate.Builder.item().of(commonTagOf(Registries.ITEM, "tools/shear")));
    private final LootItemCondition.Builder HAS_SHARP_TOOL = matchesTool(TFCTags.Items.TOOLS_SHARP);

    public BuiltinBlockLootTables(HolderLookup.Provider provider)
    {
        super(Set.of(), FeatureFlags.REGISTRY.allFlags(), provider);
    }

    @Override
    protected void generate()
    {
        FLBlocks.METALS.forEach((metal, entry) -> {});
        FLBlocks.GREENHOUSE_BLOCKS.forEach(((greenhouse, entry) -> {
            entry.forEach((type, block) -> {
                if (type == Greenhouse.BlockType.DOOR)
                {
                    add(block.get(), createDoorTable(block.get()));
                }
                else
                {
                    dropSelf(block.get());
                }
            });
        }));
        FLBlocks.METALS.forEach((metal, entry) -> {
            entry.forEach((type, block) -> {
                if (type == Metal.BlockType.BLOCK_SLAB)
                {
                    add(block.get(), createSlabItemTable(block.get()));
                }
                else
                {
                    dropSelf(block.get());
                }
            });
        });
        FLBlocks.CHROMITE_ORES.forEach((rock, entry) -> {
            entry.forEach((grade, block) -> {
                dropOther(block, FLItems.CHROMIUM_ORES.get(grade));
            });
        });
        dropSelf(FLBlocks.SMALL_CHROMITE);
        dropSelf(FLBlocks.BARREL_PRESSES);
        dropSelf(FLBlocks.WINE_SHELVES);
        dropSelf(FLBlocks.STOMPING_BARRELS);
        dropSelf(FLBlocks.BIG_BARRELS);
        dropSelf(FLBlocks.CARVED_PUMPKINS);
        dropSelf(FLBlocks.FOOD_SHELVES);
        dropSelf(FLBlocks.INSULATED_OVEN_BOTTOM);
        dropSelf(FLBlocks.CURED_OVEN_BOTTOM);
        dropSelf(FLBlocks.CURED_OVEN_CHIMNEY);
        dropSelf(FLBlocks.CURED_OVEN_HOPPER);
        dropSelf(FLBlocks.CURED_OVEN_TOP);
        dropSelf(FLBlocks.OVEN_COUNTERTOP);
        dropSelf(FLBlocks.OVEN_BOTTOM);
        dropSelf(FLBlocks.OVEN_TOP);
        dropSelf(FLBlocks.OVEN_CHIMNEY);
        dropSelf(FLBlocks.OVEN_HOPPER);
        dropSelf(FLBlocks.RUSTIC_BRICKS);
        dropSelf(FLBlocks.RUSTIC_BRICK_DECOR);
        dropSelf(FLBlocks.TILES);
        dropSelf(FLBlocks.TILE_DECOR);
        dropSelf(FLBlocks.ASHTRAY);
        dropOther(FLBlocks.STOVETOP_GRILL, TFCItems.WROUGHT_IRON_GRILL);
        dropOther(FLBlocks.STOVETOP_POT, TFCItems.POT);
        dropSelf(FLBlocks.DRYING_MAT);
        dropSelf(FLBlocks.SOLAR_DRIER);
        dropSelf(FLBlocks.BEEHIVE);
        dropSelf(FLBlocks.COMPOST_TUMBLER);
        dropSelf(FLBlocks.CLIMATE_STATION);
        dropOther(FLBlocks.WOOL_STRING, TFCItems.WOOL_YARN);
        dropOther(FLBlocks.GRAPE_STRING, TFCItems.JUTE_FIBER);
        dropOther(FLBlocks.GRAPE_STRING_WHITE, TFCItems.JUTE_FIBER);
        dropOther(FLBlocks.GRAPE_STRING_RED, TFCItems.JUTE_FIBER);
        dropItems(FLBlocks.GRAPE_STRING_PLANT_RED, TFCItems.JUTE_FIBER, FLItems.RED_GRAPE_SEEDS);
        dropItems(FLBlocks.GRAPE_STRING_PLANT_WHITE, TFCItems.JUTE_FIBER, FLItems.WHITE_GRAPE_SEEDS);
        dropSelf(FLBlocks.GRAPE_TRELLIS_POST);
        dropOther(FLBlocks.GRAPE_TRELLIS_POST_RED, FLBlocks.GRAPE_TRELLIS_POST);
        dropOther(FLBlocks.GRAPE_TRELLIS_POST_WHITE, FLBlocks.GRAPE_TRELLIS_POST);

        dropSelf(FLBlocks.VAT);
        dropSelf(FLBlocks.JARRING_STATION);
        dropSelf(FLBlocks.PLATE);
        dropSelf(FLBlocks.REINFORCED_POURED_GLASS);
        dropSelf(FLBlocks.PICKER);
        dropSelf(FLBlocks.SWEEPER);
        dropSelf(FLBlocks.LARGE_PLANTER);
        dropSelf(FLBlocks.QUAD_PLANTER);
        dropSelf(FLBlocks.HYDROPONIC_PLANTER);
        dropSelf(FLBlocks.BONSAI_PLANTER);
        dropSelf(FLBlocks.HANGING_PLANTER);
        dropSelf(FLBlocks.TRELLIS_PLANTER);
        dropSelf(FLBlocks.SEALED_BRICKS);
        dropSelf(FLBlocks.POLISHED_SEALED_BRICKS);
        dropSelf(FLBlocks.CHISELED_SEALED_BRICKS);
        add(FLBlocks.SEALED_DOOR.get(), createDoorTable(FLBlocks.SEALED_DOOR.get()));
        dropSelf(FLBlocks.SEALED_TRAPDOOR);
        dropSelf(FLBlocks.SEALED_WALL);
        dropSelf(FLBlocks.HOLLOW_SHELL);
        dropSelf(FLBlocks.TREATED_WOOD);
        dropSelf(FLBlocks.PUMPING_STATION);
        dropSelf(FLBlocks.IRRIGATION_TANK);
        dropSelf(FLBlocks.COPPER_PIPE);
        dropSelf(FLBlocks.OXIDIZED_COPPER_PIPE);
        dropSelf(FLBlocks.SPRINKLER);
        dropSelf(FLBlocks.FLOOR_SPRINKLER);
        dropSelf(FLBlocks.DARK_LADDER);
        dropSelf(FLBlocks.HANGERS);
        dropSelf(FLBlocks.JARBNETS);
        dropSelf(FLBlocks.JACK_O_LANTERNS);

        add(FLBlocks.BUTTERFLY_GRASS.get(), createTFCGrass(FLBlocks.BUTTERFLY_GRASS));
        add(FLBlocks.POTTED_BUTTERFLY_GRASS.get(), createPotFlowerItemTable(FLBlocks.BUTTERFLY_GRASS.asItem()));
        add(FLBlocks.WILD_RED_GRAPES.get(), createTFCWildCrop(FLBlocks.WILD_RED_GRAPES, FLItems.RED_GRAPE_SEEDS, itemOf(FLFruit.RED_GRAPES)));
        add(FLBlocks.WILD_WHITE_GRAPES.get(), createTFCWildCrop(FLBlocks.WILD_WHITE_GRAPES, FLItems.WHITE_GRAPE_SEEDS, itemOf(FLFruit.WHITE_GRAPES)));

        FLBlocks.FRUIT_TREE_POTTED_SAPLINGS.forEach((tree, block) -> {
            add(block.get(), createPotFlowerItemTable(FLBlocks.FRUIT_TREE_SAPLINGS.get(tree)));
        });
        FLBlocks.POTTED_HERBS.forEach((herb, block) -> {
            add(block.get(), createPotFlowerItemTable(itemOf(herb)));
        });
        FLBlocks.HERBS.forEach((herb, block) -> {
            dropRequiresTool(block, itemOf(herb), TFCTags.Items.TOOLS_KNIFE);
        });
        FLBlocks.STATIONARY_BUSHES.forEach((bush, block) -> {
            dropRequiresTool(block, block.asItem(), TFCTags.Items.TOOLS_SHARP);
        });

        cheeseWheel(FLBlocks.CHEDDAR_WHEEL, itemOf(FLFood.CHEDDAR));
        cheeseWheel(FLBlocks.CHEVRE_WHEEL, itemOf(FLFood.CHEVRE));
        cheeseWheel(FLBlocks.RAJYA_METOK_WHEEL, itemOf(FLFood.RAJYA_METOK));
        cheeseWheel(FLBlocks.GOUDA_WHEEL, itemOf(FLFood.GOUDA));
        cheeseWheel(FLBlocks.FETA_WHEEL, itemOf(FLFood.FETA));
        cheeseWheel(FLBlocks.SHOSHA_WHEEL, itemOf(FLFood.SHOSHA));

        Map<FLFruitBlocks.Tree, ItemLike> fruitTrees = new HashMap<>();
        fruitTrees.put(FLFruitBlocks.Tree.COCOA, itemOf(FLFood.COCOA_BEANS));
        fruitTrees.put(FLFruitBlocks.Tree.FIG, itemOf(FLFruit.FIG));
        fruitTrees.forEach((tree, fruit) -> {
            var leaves = FLBlocks.FRUIT_TREE_LEAVES.get(tree);
            var sapling = FLBlocks.FRUIT_TREE_SAPLINGS.get(tree);
            var branch = FLBlocks.FRUIT_TREE_BRANCHES.get(tree);
            var growingBranch = FLBlocks.FRUIT_TREE_GROWING_BRANCHES.get(tree);
            add(
                leaves.get(),
                LootTable.lootTable()
                    .withPool(
                        lootPool()
                            .add(
                                lootTableItem(fruit).when(hasProperty(leaves.get(), FruitTreeLeavesBlock.LIFECYCLE, Lifecycle.FRUITING))
                            )
                            .when(survivesExplosion())
                    )
                    .withPool(
                        lootPool()
                            .add(
                                lootTableItem(leaves).when(AnyOfCondition.anyOf(hasSilkTouch(), HAS_SHEARS_LIKE))
                            ).when(survivesExplosion())
                    )
                    .withPool(
                        lootPool()
                            .add(
                                lootTableItem(Items.STICK)
                                    .when(LootItemRandomChanceCondition.randomChance(0.2f))
                                    .when(HAS_SHARP_TOOL)
                                    .apply(setCount(1, 2))
                            ).when(survivesExplosion())
                    )
                    .withPool(
                        lootPool()
                            .add(
                                lootTableItem(Items.STICK)
                                    .when(LootItemRandomChanceCondition.randomChance(0.05f))
                                    .apply(setCount(1, 2))
                            ).when(survivesExplosion())
                    )
            );
            //TODO temp
            dropSelf(branch);
            dropSelf(growingBranch);
            dropSelf(sapling);
        });

        //TODO replace this last one?
        build(FLBlocks.MIXING_BOWL)
            .add(FLBlocks.MIXING_BOWL)
            .add(FLItems.SPOON, MixingBowlBlock.SPOON, true)
            .create();

        // -----------------------

        var missing = FLBlocks.BLOCK.getEntries().stream().filter(Objects::nonNull).filter(block -> {
            if (block.get() instanceof LiquidBlock) return false;
            var table = block.get().getLootTable();
            if (table == BuiltInLootTables.EMPTY) return false;
            return !this.map.containsKey(table);
        }).map(DeferredHolder::getId).toList();

        if (!missing.isEmpty())
        {
            FirmaLife.LOGGER.info("{} of {} blocks have loot tables.", this.map.size(), Streams.stream(getKnownBlocks()).count());
            FirmaLife.LOGGER.info("The following blocks are missing loot tables:");
            missing.forEach(id -> FirmaLife.LOGGER.info("- {}", id));
        }
    }

    private void cheeseWheel(Supplier<Block> block, ItemLike cheese)
    {
        var table = LootTable.lootTable();
        var pool = lootPool();
        var entry = AlternativesEntry.alternatives();
        for (int i = 1; i < 4; i++)
        {
            entry.otherwise(
                lootTableItem(cheese)
                    .when(hasProperty(block.get(), CheeseWheelBlock.COUNT, i))
                    .apply(setCount(i))
            );
        }
        add(
            block.get(),
            table.withPool(pool.add(entry))
        );
    }

    private void dropItems(Supplier<Block> block, ItemLike... items)
    {
        add(block.get(), createItemList(items));
    }

    private static LootTable.Builder createTFCWildCrop(Supplier<Block> block, ItemLike seed, ItemLike result)
    {
        return LootTable.lootTable()
            .withPool(
                lootPool()
                    .add(
                        lootTableItem(seed).apply(setCount(1, 3))
                    )
                    .when(survivesExplosion())
            )
            .withPool(
                lootPool()
                    .add(
                        lootTableItem(result)
                            .when(hasProperty(block.get(), WildCropBlock.MATURE, true))
                    )
                    .when(survivesExplosion())
            );
    }

    private LootTable.Builder createTFCGrass(Supplier<Block> block)
    {
        return LootTable.lootTable()
            .withPool(
                lootPool().add(
                    AlternativesEntry.alternatives(
                        lootTableItem(block.get()).when(HAS_SHEARS_LIKE),
                        lootTableItem(TFCItems.STRAW).when(HAS_SHARP_TOOL)
                    )
                ).when(survivesExplosion())
            );
    }

    private static LootTable.Builder createItemList(ItemLike[] items)
    {
        var table = LootTable.lootTable();
        for (var item : items)
        {
            table.withPool(
                lootPool()
                    .setRolls(ConstantValue.exactly(1))
                    .add(lootTableItem(item))
                    .when(survivesExplosion())
            );
        }
        return table;
    }

    private void dropRequiresTool(Supplier<Block> block, ItemLike item, TagKey<Item> tag)
    {
        add(
            block.get(),
            LootTable.lootTable().withPool(
                lootPool().add(
                        lootTableItem(item).when(
                            matchesTool(tag)
                        )
                    )
                    .when(survivesExplosion())
            )
        );
    }

    private static LootItemCondition.@NotNull Builder matchesTool(TagKey<Item> tag)
    {
        return MatchTool.toolMatches(ItemPredicate.Builder.item().of(tag));
    }

    private void dropOther(Supplier<Block> block, ItemLike out)
    {
        dropOther(block.get(), out);
    }

    private void dropSelf(DecorationBlockHolder deco)
    {
        dropSelf(deco.wall().get());
        dropSelf(deco.stair().get());
        var slab = deco.slab().get();
        add(slab, createSlabItemTable(slab));
    }

    private void dropSelf(Supplier<Block> block)
    {
        dropSelf(block.get());
    }

    private void dropSelf(Map<?, TFCBlocks.Id<Block>> map)
    {
        map.forEach((type, block) -> dropSelf(block.get()));
    }

    @Override
    protected Iterable<Block> getKnownBlocks()
    {
        return FLBlocks.BLOCK.getEntries().stream().map(block -> (Block) block.get()).filter(block -> !(block instanceof LiquidBlock)).toList();
    }

    private static LootItemConditionalFunction.Builder<?> setCount(int count)
    {
        return SetItemCountFunction.setCount(ConstantValue.exactly(count));
    }

    private static LootItemConditionalFunction.Builder<?> setCount(int min, int max)
    {
        return SetItemCountFunction.setCount(UniformGenerator.between(min, max));
    }

    private static <T extends Comparable<T> & StringRepresentable> LootItemBlockStatePropertyCondition.Builder hasProperty(Block block, Property<T> property, T value)
    {
        return hasBlockStateProperties(block).setProperties(
            StatePropertiesPredicate.Builder.properties().hasProperty(property, value)
        );
    }

    private static <T extends Comparable<T> & StringRepresentable> LootItemBlockStatePropertyCondition.Builder hasProperty(Block block, Property<Integer> property, int value)
    {
        return hasBlockStateProperties(block).setProperties(
            StatePropertiesPredicate.Builder.properties().hasProperty(property, value)
        );
    }

    private static <T extends Comparable<T> & StringRepresentable> LootItemBlockStatePropertyCondition.Builder hasProperty(Block block, Property<Boolean> property, boolean value)
    {
        return hasBlockStateProperties(block).setProperties(
            StatePropertiesPredicate.Builder.properties().hasProperty(property, value)
        );
    }

    private TableHelper build(Block block)
    {
        return new TableHelper(block, this::add);
    }

    private TableHelper build(Supplier<Block> block)
    {
        return new TableHelper(block.get(), this::add);
    }

    private static class TableHelper
    {
        private Block block;
        private LootTable.Builder table;
        private BiConsumer<Block, LootTable.Builder> register;

        public TableHelper(Block block, BiConsumer<Block, LootTable.Builder> register)
        {
            this.block = block;
            this.register = register;
            this.table = LootTable.lootTable();
        }

        private LootPool.Builder addInternal(ItemLike item)
        {
            var pool = lootPool()
                .setRolls(ConstantValue.exactly(1))
                .add(lootTableItem(item))
                .when(survivesExplosion());
            table.withPool(pool);
            return pool;
        }

        public TableHelper add(ItemLike item)
        {
            addInternal(item);
            return this;
        }

        public <T extends Comparable<T> & StringRepresentable> TableHelper add(ItemLike item, Property<T> property, T value)
        {
            var pool = addInternal(item);
            pool.when(
                hasBlockStateProperties(block)
                    .setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(property, value))
            );
            return this;
        }

        public TableHelper add(ItemLike item, Property<Boolean> property, boolean value)
        {
            var pool = addInternal(item);
            pool.when(
                hasBlockStateProperties(block)
                    .setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(property, value))
            );
            return this;
        }


        public void create()
        {
            register.accept(block, table);
        }
    }
}
