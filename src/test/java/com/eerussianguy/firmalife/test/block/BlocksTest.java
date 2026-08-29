package com.eerussianguy.firmalife.test.block;

import java.util.List;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import com.eerussianguy.firmalife.common.blocks.FLBlocks;
import com.eerussianguy.firmalife.test.TestSetup;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.WallBlock;
import net.neoforged.neoforge.registries.DeferredHolder;
import org.junit.jupiter.api.Test;

import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.util.Helpers;

import static org.junit.jupiter.api.Assertions.*;

public class BlocksTest implements TestSetup
{
    @Test
    public void testBlocksAllHaveMineableTags()
    {
        final Set<Block> expectedNotMineableBlocks = Stream.of(FLBlocks.SMALL_CHROMITE, FLBlocks.HOLLOW_SHELL)
            .map(Supplier::get)
            .collect(Collectors.toSet());

        final List<TagKey<Block>> mineableTags = List.of(
            BlockTags.MINEABLE_WITH_AXE,
            BlockTags.MINEABLE_WITH_HOE,
            BlockTags.MINEABLE_WITH_PICKAXE,
            BlockTags.MINEABLE_WITH_SHOVEL,
            TFCTags.Blocks.MINEABLE_WITH_PROPICK,
            TFCTags.Blocks.MINEABLE_WITH_HAMMER,
            TFCTags.Blocks.MINEABLE_WITH_KNIFE,
            TFCTags.Blocks.MINEABLE_WITH_SCYTHE,
            TFCTags.Blocks.MINEABLE_WITH_CHISEL,
            TFCTags.Blocks.MINEABLE_WITH_GLASS_SAW
        );

        // All non-fluid, non-exceptional, blocks with hardness > 0, < infinity, should define a tool
        final var blocks = FLBlocks.BLOCK.getEntries()
            .stream()
            .filter(holder -> {
                final Block block = holder.value();
                return !(block instanceof LiquidBlock)
                    && block.defaultDestroyTime() > 0
                    && !expectedNotMineableBlocks.contains(block)
                    && mineableTags.stream().noneMatch(t -> Helpers.isBlock(block, t));
            })
            .map(holder -> holder.getId().toString())
            .toList();

        assertTrue(blocks.isEmpty(), "Missing mineable tags on blocks: " + String.join("\n", blocks));
    }

    @Test
    public void testWallsHaveWallTag()
    {
        assertBlocksHaveTag(block -> block instanceof WallBlock, BlockTags.WALLS);
    }

    @Test
    public void testStairsHaveStairTag()
    {
        assertBlocksHaveTag(block -> block instanceof StairBlock, BlockTags.STAIRS);
    }

    @Test
    public void testSlabsHaveSlabTag()
    {
        assertBlocksHaveTag(block -> block instanceof SlabBlock, BlockTags.SLABS);
    }

    private void assertBlocksHaveTag(java.util.function.Predicate<Block> filter, TagKey<Block> tag)
    {
        final var blocks = FLBlocks.BLOCK.getEntries()
            .stream()
            .filter(holder -> filter.test(holder.value()) && !Helpers.isBlock(holder.value(), tag))
            .map(DeferredHolder::getId)
            .map(Object::toString)
            .toList();

        assertTrue(blocks.isEmpty(), "Missing #" + tag.location() + " tag on blocks: " + String.join("\n", blocks));
    }
}
