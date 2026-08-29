package com.eerussianguy.firmalife.common.util;

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
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LiquidBlock;
import org.slf4j.Logger;

import net.dries007.tfc.common.blocks.PouredGlassBlock;
import net.dries007.tfc.common.blocks.plant.BodyPlantBlock;
import net.dries007.tfc.common.blocks.plant.BranchingCactusBlock;
import net.dries007.tfc.common.blocks.plant.GrowingBranchingCactusBlock;
import net.dries007.tfc.common.blocks.plant.fruit.GrowingFruitTreeBranchBlock;
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

    private static Stream<Block> getBlocks()
    {
        return FLBlocks.BLOCK.getEntries().stream().map(Holder::value);
    }

}
