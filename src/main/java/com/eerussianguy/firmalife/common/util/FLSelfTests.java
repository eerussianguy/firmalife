package com.eerussianguy.firmalife.common.util;

import java.util.List;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import com.eerussianguy.firmalife.common.FLHelpers;
import com.eerussianguy.firmalife.common.blocks.FLBlocks;
import com.google.common.base.Stopwatch;
import com.google.common.collect.ImmutableSet;
import com.mojang.logging.LogUtils;
import net.minecraft.core.Holder;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.WallBlock;
import net.neoforged.neoforge.registries.DeferredHolder;
import org.slf4j.Logger;

import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.blocks.PouredGlassBlock;
import net.dries007.tfc.common.blocks.plant.BodyPlantBlock;
import net.dries007.tfc.common.blocks.plant.BranchingCactusBlock;
import net.dries007.tfc.common.blocks.plant.GrowingBranchingCactusBlock;
import net.dries007.tfc.common.blocks.plant.fruit.GrowingFruitTreeBranchBlock;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.SelfTests;

import static net.dries007.tfc.util.SelfTests.*;

public class FLSelfTests
{
    private static final Logger LOGGER = LogUtils.getLogger();
    private static boolean EXTERNAL_ERROR = false;

    public static void reportExternalError()
    {
        EXTERNAL_ERROR = true;
    }

    public static void runServerSelfTests(MinecraftServer server)
    {
        if (FLHelpers.ASSERTIONS_ENABLED)
        {
            final Stopwatch tick = Stopwatch.createStarted();
            SelfTests.throwIfAny(
                validateOwnBlockLootTables(server),
                validateOwnBlockMineableTags(server),
                validateOwnBlockTags(server),
                EXTERNAL_ERROR
            );
            LOGGER.info("Server self tests passed in {}", tick.stop());
        }
    }

    private static boolean validateOwnBlockLootTables(MinecraftServer server)
    {
        final Set<? extends Block> expectedNoLootTableBlocks = Stream.of(FLBlocks.GRAPE_FLUFF_RED, FLBlocks.GRAPE_FLUFF_WHITE)
            .map(Supplier::get)
            .collect(Collectors.toSet());
        final Set<Class<?>> expectedNoLootTableClasses = ImmutableSet.of(BodyPlantBlock.class, GrowingFruitTreeBranchBlock.class, LiquidBlock.class, BranchingCactusBlock.class, GrowingBranchingCactusBlock.class, PouredGlassBlock.class);
        return validateBlockLootTables(server, getBlocks()
            .filter(b -> !expectedNoLootTableBlocks.contains(b)).filter(b -> !expectedNoLootTableClasses.contains(b.getClass())).toList(), LOGGER);
    }

    private static boolean validateOwnBlockMineableTags(MinecraftServer server)
    {
        final Set<Block> expectedNotMineableBlocks = Stream.of(FLBlocks.SMALL_CHROMITE, FLBlocks.HOLLOW_SHELL)
            .map(Supplier::get)
            .collect(Collectors.toSet());
        final Set<TagKey<Block>> mineableTags = Set.of(
            BlockTags.MINEABLE_WITH_AXE, BlockTags.MINEABLE_WITH_HOE, BlockTags.MINEABLE_WITH_PICKAXE, BlockTags.MINEABLE_WITH_SHOVEL,
            TFCTags.Blocks.MINEABLE_WITH_PROPICK, TFCTags.Blocks.MINEABLE_WITH_HAMMER, TFCTags.Blocks.MINEABLE_WITH_KNIFE, TFCTags.Blocks.MINEABLE_WITH_SCYTHE, TFCTags.Blocks.MINEABLE_WITH_CHISEL, TFCTags.Blocks.MINEABLE_WITH_GLASS_SAW
        );
        // All non-fluid, non-exceptional, blocks with hardness > 0, < infinity, should define a tool
        final List<? extends Block> missingTag = FLBlocks.BLOCK.getEntries().stream()
            .map(DeferredHolder::get)
            .filter(b -> !(b instanceof LiquidBlock)
                && b.defaultDestroyTime() > 0
                && !expectedNotMineableBlocks.contains(b)
                && mineableTags.stream().noneMatch(t -> Helpers.isBlock(b, t)))
            .toList();

        return logRegistryErrors("{} non-fluid blocks have no mineable_with_<tool> tag.", missingTag, LOGGER);
    }

    private static boolean validateOwnBlockTags(MinecraftServer server)
    {
        return validateBlocksHaveTag(getBlocks().filter(b -> b instanceof WallBlock), BlockTags.WALLS)
            | validateBlocksHaveTag(getBlocks().filter(b -> b instanceof StairBlock), BlockTags.STAIRS)
            | validateBlocksHaveTag(getBlocks().filter(b -> b instanceof SlabBlock), BlockTags.SLABS);
    }

    private static boolean validateBlocksHaveTag(Stream<Block> blocks, TagKey<Block> tag)
    {
        return logRegistryErrors("{} blocks are missing the #" + tag.location() + " tag", blocks.filter(b -> !Helpers.isBlock(b, tag)).toList(), FLSelfTests.LOGGER);
    }

    private static Stream<Block> getBlocks()
    {
        return FLBlocks.BLOCK.getEntries().stream().map(Holder::value);
    }

}
