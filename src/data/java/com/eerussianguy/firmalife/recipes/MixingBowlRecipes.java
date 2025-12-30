package com.eerussianguy.firmalife.recipes;

import java.util.List;
import java.util.Optional;
import com.eerussianguy.firmalife.common.FLTags;
import com.eerussianguy.firmalife.common.items.FLFood;
import com.eerussianguy.firmalife.common.items.FLItems;
import com.eerussianguy.firmalife.common.items.Spice;
import com.eerussianguy.firmalife.common.recipes.MixingBowlRecipe;
import com.eerussianguy.firmalife.common.util.ExtraFluid;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.crafting.SizedFluidIngredient;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.items.Food;
import net.dries007.tfc.common.items.Powder;

public interface MixingBowlRecipes extends Recipes
{
    default void mixingBowlRecipes()
    {
        mix(
            itemOf(Powder.SALT),
            SizedFluidIngredient.of(fluidOf(ExtraFluid.CREAM), 1000),
            new ItemStack(itemOf(FLFood.BUTTER), 1)
        );
        mix(
            List.of(
                notRotten(itemOf(FLFood.BUTTER)),
                notRotten(TFCTags.Items.FLOUR),
                Ingredient.of(TFCTags.Items.SWEETENERS)
            ),
            SizedFluidIngredient.of(Fluids.WATER, 1000),
            new ItemStack(itemOf(FLFood.PIE_DOUGH), 1)
        );
        mix(
            List.of(
                Ingredient.of(FLTags.Items.RAW_EGGS),
                notRotten(itemOf(Food.PUMPKIN_CHUNKS)),
                notRotten(itemOf(Food.PUMPKIN_CHUNKS)),
                notRotten(itemOf(FLFood.SPICED_FLOUR)),
                Ingredient.of(TFCTags.Items.SWEETENERS)
            ),
            SizedFluidIngredient.of(Fluids.WATER, 1000),
            new ItemStack(itemOf(FLFood.PUMPKIN_PIE_DOUGH), 1)
        );
        mix(
            List.of(
                notRotten(TFCTags.Items.DOUGH),
                Ingredient.of(itemOf(Powder.SALT)),
                Ingredient.of(itemOf(Spice.BASIL_LEAVES))
            ),
            SizedFluidIngredient.of(FLTags.Fluids.OILS, 100),
            new ItemStack(itemOf(FLFood.PIZZA_DOUGH), 4)
        );
        mix(
            List.of(
                Ingredient.of(TFCTags.Items.SWEETENERS),
                notRotten(itemOf(FLFood.COCOA_POWDER)),
                notRotten(itemOf(FLFood.COCOA_POWDER))
            ),
            SizedFluidIngredient.of(FLTags.Fluids.MILKS, 1000),
            new ItemStack(itemOf(FLFood.DARK_CHOCOLATE_BLEND), 2)
        );
        mix(
            List.of(
                Ingredient.of(TFCTags.Items.SWEETENERS),
                notRotten(itemOf(FLFood.COCOA_BUTTER)),
                notRotten(itemOf(FLFood.COCOA_BUTTER))
            ),
            SizedFluidIngredient.of(FLTags.Fluids.MILKS, 1000),
            new ItemStack(itemOf(FLFood.WHITE_CHOCOLATE_BLEND), 2)
        );
        mix(
            List.of(
                Ingredient.of(TFCTags.Items.SWEETENERS),
                notRotten(itemOf(FLFood.COCOA_BUTTER)),
                notRotten(itemOf(FLFood.COCOA_POWDER))
            ),
            SizedFluidIngredient.of(FLTags.Fluids.MILKS, 1000),
            new ItemStack(itemOf(FLFood.MILK_CHOCOLATE_BLEND), 2)
        );
        mix(
            List.of(
                Ingredient.of(TFCTags.Items.SWEETENERS),
                Ingredient.of(itemOf(Spice.VANILLA)),
                Ingredient.of(FLItems.ICE_SHAVINGS)
            ),
            SizedFluidIngredient.of(fluidOf(ExtraFluid.CREAM), 1000),
            new ItemStack(itemOf(FLFood.VANILLA_ICE_CREAM), 2)
        );
        mix(
            notRotten(itemOf(FLFood.VANILLA_ICE_CREAM)),
            SizedFluidIngredient.of(fluidOf(ExtraFluid.CHOCOLATE), 1000),
            new ItemStack(itemOf(FLFood.CHOCOLATE_ICE_CREAM), 1)
        );
        mix(
            List.of(
                notRotten(itemOf(FLFood.VANILLA_ICE_CREAM)),
                notRotten(itemOf(Food.STRAWBERRY)),
                notRotten(itemOf(Food.STRAWBERRY))
            ),
            new ItemStack(itemOf(FLFood.STRAWBERRY_ICE_CREAM), 1)
        );
        mix(
            List.of(
                notRotten(FLTags.Items.RAW_EGGS),
                Ingredient.of(itemOf(Spice.VANILLA)),
                notRotten(itemOf(FLFood.BUTTER)),
                Ingredient.of(TFCTags.Items.SWEETENERS),
                notRotten(TFCTags.Items.FLOUR)
            ),
            new ItemStack(itemOf(FLFood.COOKIE_DOUGH), 4)
        );
        mix(
            List.of(
                notRotten(FLTags.Items.CHOCOLATE),
                notRotten(itemOf(FLFood.COOKIE_DOUGH)),
                notRotten(itemOf(FLFood.COOKIE_DOUGH)),
                notRotten(itemOf(FLFood.COOKIE_DOUGH)),
                notRotten(itemOf(FLFood.COOKIE_DOUGH))
            ),
            new ItemStack(itemOf(FLFood.CHOCOLATE_CHIP_COOKIE_DOUGH), 4)
        );
        mix(
            List.of(
                notRotten(TFCTags.Items.FLOUR),
                Ingredient.of(itemOf(Powder.SALT))
            ),
            SizedFluidIngredient.of(Fluids.WATER, 1000),
            new ItemStack(itemOf(FLFood.HARDTACK_DOUGH), 4)
        );
        mix(
            List.of(
                notRotten(FLTags.Items.EGG_NOODLE_FLOUR),
                Ingredient.of(itemOf(Powder.SALT)),
                Ingredient.of(Items.EGG)
            ),
            SizedFluidIngredient.of(FLTags.Fluids.MILKS, 1000),
            new ItemStack(itemOf(FLFood.RAW_EGG_NOODLES), 1)
        );
        mix(
            List.of(
                notRotten(itemOf(Food.RICE_FLOUR)),
                notRotten(itemOf(Food.MAIZE_FLOUR)),
                Ingredient.of(itemOf(Powder.SALT))
            ),
            SizedFluidIngredient.of(FLTags.Fluids.MILKS, 1000),
            new ItemStack(itemOf(FLFood.RAW_RICE_NOODLES), 2)
        );
    }

    private void mix(ItemLike input, SizedFluidIngredient inputFluid, ItemStack output)
    {
        mix(List.of(Ingredient.of(input)), inputFluid, output, FluidStack.EMPTY);
    }

    private void mix(Ingredient input, SizedFluidIngredient inputFluid, ItemStack output)
    {
        mix(List.of(input), inputFluid, output, FluidStack.EMPTY);
    }

    private void mix(List<Ingredient> inputs, ItemStack output)
    {
        mix(inputs, null, output, FluidStack.EMPTY);
    }

    private void mix(List<Ingredient> inputs, SizedFluidIngredient inputFluid, ItemStack outputStack)
    {
        mix(inputs, inputFluid, outputStack, FluidStack.EMPTY);
    }

    private void mix(List<Ingredient> inputs, @Nullable SizedFluidIngredient inputFluid, ItemStack outputStack, FluidStack outputFluid)
    {
        add(new MixingBowlRecipe(inputs, Optional.ofNullable(inputFluid), outputStack, outputFluid));
    }
}
