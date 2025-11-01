package com.eerussianguy.firmalife.recipes;

import java.util.List;
import java.util.function.Supplier;
import com.eerussianguy.firmalife.common.FLTags;
import com.eerussianguy.firmalife.common.blocks.FLBlocks;
import com.eerussianguy.firmalife.common.items.FLFood;
import com.eerussianguy.firmalife.common.items.FLFoodTraits;
import com.eerussianguy.firmalife.common.items.FLItems;
import com.eerussianguy.firmalife.common.recipes.data.EmptyPanModifier;
import com.eerussianguy.firmalife.common.util.ExtraFluid;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.common.NeoForgeMod;

import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.component.glass.GlassOperation;
import net.dries007.tfc.common.fluids.SimpleFluid;
import net.dries007.tfc.common.fluids.TFCFluids;
import net.dries007.tfc.common.items.TFCItems;
import net.dries007.tfc.common.recipes.BarrelRecipe;
import net.dries007.tfc.common.recipes.GlassworkingRecipe;
import net.dries007.tfc.common.recipes.outputs.AddTraitModifier;
import net.dries007.tfc.common.recipes.outputs.CopyInputModifier;
import net.dries007.tfc.common.recipes.outputs.ItemStackProvider;

public interface GlassworkingRecipes extends Recipes
{
    default void glassworkingRecipes()
    {
        glass(
            List.of(
                GlassOperation.FLATTEN,
                GlassOperation.SODA_ASH,
                GlassOperation.TABLE_POUR
            ),
            TFCItems.SILICA_GLASS_BATCH,
            new ItemStack(FLBlocks.REINFORCED_POURED_GLASS, 1)
        );
        glass(
            List.of(
                GlassOperation.BLOW,
                GlassOperation.BLOW,
                GlassOperation.PINCH,
                GlassOperation.SAW
            ),
            TFCItems.OLIVINE_GLASS_BATCH,
            new ItemStack(FLItems.EMPTY_OLIVINE_WINE_BOTTLE, 1)
        );
        glass(
            List.of(
                GlassOperation.BLOW,
                GlassOperation.BLOW,
                GlassOperation.PINCH,
                GlassOperation.SAW
            ),
            TFCItems.HEMATITIC_GLASS_BATCH,
            new ItemStack(FLItems.EMPTY_HEMATITIC_WINE_BOTTLE, 1)
        );
        glass(
            List.of(
                GlassOperation.BLOW,
                GlassOperation.BLOW,
                GlassOperation.PINCH,
                GlassOperation.SAW
            ),
            TFCItems.VOLCANIC_GLASS_BATCH,
            new ItemStack(FLItems.EMPTY_VOLCANIC_WINE_BOTTLE, 1)
        );
        glass(
            List.of(
                GlassOperation.BLOW,
                GlassOperation.BLOW,
                GlassOperation.PINCH,
                GlassOperation.SAW
            ),
            TFCItems.SILICA_GLASS_BATCH,
            new ItemStack(FLItems.WINE_GLASS, 2)
        );
    }

    private void glass(List<Supplier<GlassOperation>> operations, ItemLike input, ItemStack result) {
        add(new GlassworkingRecipe(operations.stream().map(Supplier::get).toList(), Ingredient.of(input), result));
    }
}
