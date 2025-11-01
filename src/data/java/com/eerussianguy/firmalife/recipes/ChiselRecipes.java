package com.eerussianguy.firmalife.recipes;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import com.eerussianguy.firmalife.common.blocks.FLBlocks;
import com.eerussianguy.firmalife.common.blocks.greenhouse.Greenhouse;
import net.minecraft.core.Holder;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import org.apache.logging.log4j.core.pattern.AbstractStyleNameConverter;

import net.dries007.tfc.common.blocks.DecorationBlockHolder;
import net.dries007.tfc.common.player.ChiselMode;
import net.dries007.tfc.common.recipes.ChiselRecipe;
import net.dries007.tfc.common.recipes.ingredients.BlockIngredient;
import net.dries007.tfc.common.recipes.outputs.ItemStackProvider;

public interface ChiselRecipes extends Recipes
{
    default void chiselRecipes() {
        Map<Greenhouse, Greenhouse> cleaningPairs = new HashMap<>();
        cleaningPairs.put(Greenhouse.RUSTED_IRON, Greenhouse.IRON);
        cleaningPairs.put(Greenhouse.EXPOSED_COPPER, Greenhouse.COPPER);
        cleaningPairs.put(Greenhouse.WEATHERED_COPPER, Greenhouse.COPPER);
        cleaningPairs.put(Greenhouse.OXIDIZED_COPPER, Greenhouse.COPPER);
        cleaningPairs.put(Greenhouse.WEATHERED_TREATED_WOOD, Greenhouse.TREATED_WOOD);

        chiselSlabStairs(FLBlocks.TILES, FLBlocks.TILE_DECOR);
        chiselSlabStairs(FLBlocks.RUSTIC_BRICKS, FLBlocks.RUSTIC_BRICK_DECOR);

        for (var entry : cleaningPairs.entrySet()) {
            var dirty = entry.getKey();
            var clean = entry.getValue();
            for (Greenhouse.BlockType type : Greenhouse.BlockType.values()) {
                chisel(
                    List.of(FLBlocks.GREENHOUSE_BLOCKS.get(dirty).get(type)),
                    FLBlocks.GREENHOUSE_BLOCKS.get(clean).get(type),
                    ChiselMode.SMOOTH,
                    ItemStackProvider.empty()
                );
            }
        }
    }

    private void chiselSlabStairs(Supplier<? extends Block> input, DecorationBlockHolder deco) {
        chisel(List.of(input), deco.stair(), ChiselMode.STAIR, ItemStackProvider.empty());
        chisel(List.of(input), deco.slab(), ChiselMode.SLAB, ItemStackProvider.of(deco.slab()));
    }

    private void chisel(Supplier<? extends Block> input, Supplier<? extends Block> output)
    {
        chisel(List.of(input), output);
    }

    private void chisel(List<? extends Supplier<? extends Block>> input, Supplier<? extends Block> output)
    {
        chisel(input, output, ChiselMode.SMOOTH, ItemStackProvider.empty());
    }

    private void chisel(List<? extends Supplier<? extends Block>> input, Supplier<? extends Block> output, Holder<ChiselMode> mode, ItemStackProvider outputItem)
    {
        add(new ChiselRecipe(
            BlockIngredient.of(input.stream().map(Supplier::get)),
            output.get().defaultBlockState(),
            mode.value(),
            outputItem
        ));
    }
}
