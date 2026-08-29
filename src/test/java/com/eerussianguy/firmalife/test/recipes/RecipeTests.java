package com.eerussianguy.firmalife.test.recipes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import com.eerussianguy.firmalife.FirmaLife;
import com.eerussianguy.firmalife.common.FLHelpers;
import com.eerussianguy.firmalife.common.items.FLItems;
import com.eerussianguy.firmalife.common.recipes.FLRecipeTypes;
import com.eerussianguy.firmalife.common.recipes.MixingBowlRecipe;
import com.eerussianguy.firmalife.common.recipes.OvenRecipe;
import com.eerussianguy.firmalife.common.recipes.VatRecipe;
import com.eerussianguy.firmalife.test.TestSetup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.neoforge.common.Tags;
import org.jetbrains.annotations.Nullable;
import org.junit.jupiter.api.Test;

import net.dries007.tfc.common.blocks.wood.Wood;
import net.dries007.tfc.common.component.food.FoodCapability;
import net.dries007.tfc.common.component.heat.HeatCapability;
import net.dries007.tfc.common.recipes.AdvancedShapedRecipe;
import net.dries007.tfc.common.recipes.AdvancedShapelessRecipe;
import net.dries007.tfc.common.recipes.AlloyRecipe;
import net.dries007.tfc.common.recipes.AnvilRecipe;
import net.dries007.tfc.common.recipes.BarrelRecipe;
import net.dries007.tfc.common.recipes.BlockRecipe;
import net.dries007.tfc.common.recipes.CastingRecipe;
import net.dries007.tfc.common.recipes.ChiselRecipe;
import net.dries007.tfc.common.recipes.GlassworkingRecipe;
import net.dries007.tfc.common.recipes.HeatingRecipe;
import net.dries007.tfc.common.recipes.ItemRecipe;
import net.dries007.tfc.common.recipes.KnappingRecipe;
import net.dries007.tfc.common.recipes.LoomRecipe;
import net.dries007.tfc.common.recipes.PotRecipe;
import net.dries007.tfc.common.recipes.TFCRecipeTypes;
import net.dries007.tfc.common.recipes.WeldingRecipe;
import net.dries007.tfc.common.recipes.outputs.DamageCraftingRemainderModifier;
import net.dries007.tfc.common.recipes.outputs.ItemStackProvider;
import net.dries007.tfc.util.Helpers;

import static org.junit.jupiter.api.Assertions.*;

public class RecipeTests implements TestSetup
{
    @Test
    public void testOvenRecipeIngredientsAreHeatable()
    {
        // Oven recipes only complete once the input reaches their temperature, which an item with no heat definition can never do
        assertIngredientsAreHeatable(FLRecipeTypes.OVEN.get(), OvenRecipe::getIngredient);
    }

    @Test
    public void testHeatingRecipeIngredientsAreHeatable()
    {
        assertIngredientsAreHeatable(TFCRecipeTypes.HEATING.get(), HeatingRecipe::getIngredient);
    }

    @Test
    public void testFoodIngredientsAreNotRotten()
    {
        // Rotten food is never a valid input to anything, and an ingredient that forgets to say so lets a player process it anyway
        // Sealing a jar requires fresh contents, but unsealing one only copies the decay of the jar onto what comes out of it
        final Set<ResourceLocation> expectedRottenInputs = Stream.concat(
            FLItems.JAM.values().stream().map(jam -> FLHelpers.identifier("crafting/" + BuiltInRegistries.ITEM.getKey(jam.get()).getPath())),
            Stream.of(
                FLHelpers.identifier("barrel_sealed/blue_mold"),
                FLHelpers.identifier("barrel_instant/wash_foods")
            )
        ).collect(Collectors.toSet());
        final List<String> errors = new ArrayList<>();

        for (RecipeHolder<?> holder : ourRecipes())
        {
            if (expectedRottenInputs.contains(holder.id())) continue;

            final @Nullable List<Ingredient> ingredients = ingredients(holder.value());
            if (ingredients == null) continue; // Reported by testEveryRecipeTypeHasKnownIngredients

            for (Ingredient ingredient : ingredients)
            {
                for (ItemStack stack : ingredient.getItems())
                {
                    if (FoodCapability.getDefinition(stack) != null && ingredient.test(FoodCapability.setRotten(stack.copy())))
                    {
                        errors.add(holder.id() + " accepts rotten " + BuiltInRegistries.ITEM.getKey(stack.getItem()));
                    }
                }
            }
        }

        assertTrue(errors.isEmpty(), "Recipes accepting rotten food: " + String.join(", ", errors));
    }

    @Test
    public void testEveryRecipeTypeHasKnownIngredients()
    {
        // Guards the above, which can only check the recipe types that it knows how to read ingredients out of
        final List<String> types = ourRecipes()
            .stream()
            .filter(holder -> ingredients(holder.value()) == null)
            .map(holder -> String.valueOf(BuiltInRegistries.RECIPE_TYPE.getKey(holder.value().getType())))
            .distinct()
            .toList();

        assertTrue(types.isEmpty(), "Recipe types with no ingredients defined in " + getClass().getSimpleName() + ": " + String.join(", ", types));
    }

    @Test
    public void testCraftingRecipesWithToolsDamageInputs()
    {
        final RecipeManager manager = Helpers.getUnsafeRecipeManager();

        final List<String> recipes = manager
            .getAllRecipesFor(RecipeType.CRAFTING)
            .stream()
            .filter(holder -> {
                final CraftingRecipe recipe = holder.value();
                final Optional<ItemStackProvider> remainder = recipe instanceof AdvancedShapedRecipe shaped ? shaped.getRemainder()
                    : recipe instanceof AdvancedShapelessRecipe shapeless ? shapeless.getRemainder() : Optional.empty();

                if (remainder.isPresent() && remainder.get().modifiers().stream().anyMatch(modifier ->  modifier instanceof DamageCraftingRemainderModifier))
                {
                    return false;
                }

                final Stream<ItemStack> stacks = recipe
                    .getIngredients()
                    .stream()
                    .flatMap(ingredient -> Arrays.stream(ingredient.getItems()));

                return stacks.anyMatch(stack -> stack.isDamageableItem() && stack.is(Tags.Items.TOOLS));
            })
            .map(holder -> holder.id().toString())
            .toList();

        assertTrue(recipes.isEmpty(), "Recipes with tools do not damage inputs: " + String.join("\n", recipes));
    }

    @Test
    public void testAdvancedShapelessRecipesHavePrimaryInput()
    {
        final RecipeManager manager = Helpers.getUnsafeRecipeManager();

        final List<String> recipes = manager
            .getAllRecipesFor(RecipeType.CRAFTING)
            .stream()
            .filter(holder -> {
                final CraftingRecipe recipe = holder.value();
                if (recipe instanceof AdvancedShapelessRecipe advancedShapeless)
                {
                    return advancedShapeless.getPrimaryIngredient().isEmpty();
                }
                else
                {
                    return false;
                }
            })
            .map(holder -> holder.id().toString())
            .toList();

        assertTrue(recipes.isEmpty(), "Advanced shapeless crafting recipes do not have primary inputs: " + String.join("\n", recipes));
    }


    private List<RecipeHolder<?>> ourRecipes()
    {
        return Helpers.getUnsafeRecipeManager()
            .getRecipes()
            .stream()
            .filter(holder -> FirmaLife.MOD_ID.equals(holder.id().getNamespace()))
            .toList();
    }

    @Nullable
    private List<Ingredient> ingredients(Recipe<?> recipe)
    {
        return switch (recipe)
        {
            // Ours
            case ItemRecipe r -> List.of(r.getIngredient()); // Drying, smoking, stomping, press, centrifuge, and TFC's quern
            case OvenRecipe r -> List.of(r.getIngredient());
            case MixingBowlRecipe r -> r.getItemIngredients();
            case VatRecipe r -> List.of(r.getInputItem().ingredient());

            // TFC's, which we also add recipes to
            case AnvilRecipe r -> List.of(r.getInput());
            case BarrelRecipe r -> List.of(r.getInputItem().ingredient());
            case CastingRecipe r -> List.of(r.getIngredient());
            case CraftingRecipe r -> r.getIngredients();
            case GlassworkingRecipe r -> List.of(r.batchItem());
            case HeatingRecipe r -> List.of(r.getIngredient());
            case KnappingRecipe r -> r.getIngredient() == null ? List.of() : List.of(r.getIngredient());
            case LoomRecipe r -> List.of(r.getItemStackIngredient().ingredient());
            case PotRecipe r -> r.getItemIngredients();
            case WeldingRecipe r -> List.of(r.getFirstInput(), r.getSecondInput());

            // Recipes with no item ingredient to speak of
            case AlloyRecipe r -> List.of();
            case BlockRecipe r -> List.of(); // Collapse
            case ChiselRecipe r -> List.of();

            default -> null;
        };
    }

    private <C extends RecipeInput, R extends Recipe<C>> void assertIngredientsAreHeatable(RecipeType<R> type, Function<R, Ingredient> ingredient)
    {
        final List<String> items = Helpers.getUnsafeRecipeManager()
            .getAllRecipesFor(type)
            .stream()
            .flatMap(holder -> Arrays.stream(ingredient.apply(holder.value()).getItems()))
            .filter(stack -> HeatCapability.getDefinition(stack) == null)
            .map(ItemStack::getItem)
            .map(BuiltInRegistries.ITEM::getKey)
            .map(Object::toString)
            .distinct()
            .toList();

        assertTrue(items.isEmpty(), "Ingredients to " + type + " recipes with no heat definition: " + String.join("\n", items));
    }
}
