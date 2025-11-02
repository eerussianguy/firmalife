package com.eerussianguy.firmalife.recipes;

import java.util.Optional;
import com.eerussianguy.firmalife.common.FLHelpers;
import com.eerussianguy.firmalife.common.blocks.FLBlocks;
import com.eerussianguy.firmalife.common.util.Carving;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.common.items.Food;
import net.dries007.tfc.common.recipes.KnappingRecipe;
import net.dries007.tfc.util.DataGenerationHelpers;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.data.KnappingPattern;
import net.dries007.tfc.util.data.KnappingType;

public interface KnappingRecipes extends Recipes
{
    //TODO move these
    ResourceLocation CLAY = Helpers.identifier("clay");
    ResourceLocation PUMPKIN = FLHelpers.identifier("pumpkin");

    default void knappingRecipes()
    {
        clayKnapping(FLBlocks.OVEN_TOP, "XXXXX", "XX XX", "X   X", "X   X", "XXXXX");
        clayKnapping(FLBlocks.OVEN_BOTTOM, "XX XX", "X   X", "X   X", "XX XX", "XXXXX");
        clayKnapping(FLBlocks.OVEN_CHIMNEY, "XX XX", "XX XX", "XX XX");
        pumpkinKnapping(FLBlocks.CARVED_PUMPKINS.get(Carving.NONE), "XXXXX", "XXXXX", "XXXXX", "X   X", "XXXXX");
        pumpkinKnapping(FLBlocks.CARVED_PUMPKINS.get(Carving.CIRCLE), "XXXXX", "X   X", "X   X", "X   X", "XXXXX");
        pumpkinKnapping(FLBlocks.CARVED_PUMPKINS.get(Carving.CREEPER), "XXXXX", "X X X", "XX XX", "X X X", "X X X");
        pumpkinKnapping(FLBlocks.CARVED_PUMPKINS.get(Carving.AXE), "XXXXX", "X  XX", "X   X", "X  XX", "XXXXX");
        pumpkinKnapping(FLBlocks.CARVED_PUMPKINS.get(Carving.HAMMER), "XXXXX", "X   X", "X   X", "XX XX", "XX XX");
        pumpkinKnapping(FLBlocks.CARVED_PUMPKINS.get(Carving.PICKAXE), "XXXXX", "XX XX", "X X X", "XX XX", "XX XX");
        pumpkinKnapping(FLBlocks.CARVED_PUMPKINS.get(Carving.LEFT), "XXXXX", "X XXX", "X XXX", "X   X", "XXXXX");
        pumpkinKnapping(FLBlocks.CARVED_PUMPKINS.get(Carving.RIGHT), "XXXXX", "X   X", "XXX X", "XXX X", "XXXXX");
        pumpkinKnapping(Items.CARVED_PUMPKIN, "XXXXX", "X X X", "XXXXX", "X   X", "XXXXX");
        pumpkinKnapping(itemOf(Food.PUMPKIN_CHUNKS), 4, " X X ", "X X X", " X X ", "X X X", " X X ");
    }


    private void pumpkinKnapping(ItemLike output, String... pattern)
    {
        pumpkinKnapping(output, 1, pattern);
    }

    private void pumpkinKnapping(ItemLike output, int count, String... pattern)
    {
        knapping(PUMPKIN, pattern, output, count);
    }


    // Copied from TFC
    private void clayKnapping(ItemLike output, String... pattern)
    {
        clayKnapping(output, 1, pattern);
    }

    private void clayKnapping(ItemLike output, int count, String... pattern)
    {
        clayKnapping("", output, count, false, pattern);
    }

    private void clayKnapping(String suffix, ItemLike output, int count, boolean defaultOn, String... pattern)
    {
        add(nameOf(output) + (suffix.isEmpty() ? "" : "_" + suffix), new KnappingRecipe(
            KnappingType.MANAGER.getCheckedReference(CLAY),
            KnappingPattern.from(defaultOn, pattern),
            Optional.empty(),
            new ItemStack(output, count)
        ));
        // Un-crafting, only for non-suffixed recipes
        if (suffix.isEmpty()) new DataGenerationHelpers.Builder((name, r) -> add(nameOf(output) + "_to_clay", r))
            .input(output)
            .shapeless(Items.CLAY_BALL, 5 / count);
    }

    private void knapping(ResourceLocation knappingType, String[] pattern, ItemLike output, int count)
    {
        knapping(knappingType, pattern, new ItemStack(output, count), null);
    }

    private void knapping(ResourceLocation knappingType, String[] pattern, ItemStack output, @Nullable String name)
    {
        final KnappingRecipe recipe = new KnappingRecipe(KnappingType.MANAGER.getCheckedReference(knappingType), KnappingPattern.from(true, pattern), Optional.empty(), output);
        if (name == null)
        {
            add(recipe);
        }
        else
        {
            add(name, recipe);
        }
    }
}
