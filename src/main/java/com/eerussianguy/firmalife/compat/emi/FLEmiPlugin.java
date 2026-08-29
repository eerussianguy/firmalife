package com.eerussianguy.firmalife.compat.emi;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Supplier;
import dev.emi.emi.api.EmiEntrypoint;
import dev.emi.emi.api.EmiPlugin;
import dev.emi.emi.api.EmiRegistry;
import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.ItemLike;

import com.eerussianguy.firmalife.common.FLHelpers;
import com.eerussianguy.firmalife.common.FLTags;
import com.eerussianguy.firmalife.common.blocks.FLBlocks;
import com.eerussianguy.firmalife.common.blocks.oven.OvenType;
import com.eerussianguy.firmalife.common.items.FLItems;
import com.eerussianguy.firmalife.common.recipes.BowlPotRecipe;
import com.eerussianguy.firmalife.common.recipes.FLRecipeTypes;
import com.eerussianguy.firmalife.common.recipes.StinkySoupRecipe;
import com.eerussianguy.firmalife.compat.emi.recipe.EmiBowlPotRecipe;
import com.eerussianguy.firmalife.compat.emi.recipe.EmiItemRecipe;
import com.eerussianguy.firmalife.compat.emi.recipe.EmiMixingBowlRecipe;
import com.eerussianguy.firmalife.compat.emi.recipe.EmiOvenRecipe;
import com.eerussianguy.firmalife.compat.emi.recipe.EmiStinkySoupRecipe;
import com.eerussianguy.firmalife.compat.emi.recipe.EmiVatRecipe;
import net.dries007.tfc.common.blocks.wood.Wood;
import net.dries007.tfc.common.items.TFCItems;
import net.dries007.tfc.common.recipes.PotRecipe;
import net.dries007.tfc.common.recipes.TFCRecipeTypes;
import net.dries007.tfc.compat.emi.EmiIntegration;
import net.dries007.tfc.compat.emi.recipe.ComparableRecipe;

@EmiEntrypoint
public final class FLEmiPlugin implements EmiPlugin
{
    private static final List<EmiRecipeCategory> CATEGORIES = new ArrayList<>();

    public static final EmiRecipeCategory DRYING = createCategory("drying", FLBlocks.DRYING_MAT.get());
    public static final EmiRecipeCategory SMOKING = createCategory("smoking", TFCItems.WOOL_YARN.get());
    public static final EmiRecipeCategory STOMPING = createCategory("stomping", FLBlocks.STOMPING_BARRELS.get(Wood.MANGROVE).get());
    public static final EmiRecipeCategory PRESS = createCategory("press", FLBlocks.BARREL_PRESSES.get(Wood.MANGROVE).get());
    public static final EmiRecipeCategory CENTRIFUGE = createCategory("centrifuge", FLBlocks.CENTRIFUGE.get());
    public static final EmiRecipeCategory MIXING_BOWL = createCategory("mixing_bowl", FLBlocks.MIXING_BOWL.get());
    public static final EmiRecipeCategory OVEN = createCategory("oven", FLBlocks.CURED_OVEN_TOP.get(OvenType.BRICK).get());
    public static final EmiRecipeCategory VAT = createCategory("vat", FLBlocks.VAT.get());

    private static EmiRecipeCategory createCategory(String name, ItemLike item)
    {
        final EmiRecipeCategory category = new EmiRecipeCategory(FLHelpers.identifier(name), EmiStack.of(item));
        CATEGORIES.add(category);
        return category;
    }

    @Override
    public void register(EmiRegistry registry)
    {
        registerCategories(registry);
        registerWorkstations(registry);
        registerRecipes(registry);
    }

    private void registerCategories(EmiRegistry registry)
    {
        for (EmiRecipeCategory category : CATEGORIES)
        {
            registry.addCategory(category);
            category.sorter = basicSorter();
        }
    }

    private void registerWorkstations(EmiRegistry registry)
    {
        registry.addWorkstation(DRYING, EmiStack.of(FLBlocks.DRYING_MAT.get()));
        registry.addWorkstation(DRYING, EmiStack.of(FLBlocks.SOLAR_DRIER.get()));
        registry.addWorkstation(SMOKING, EmiStack.of(TFCItems.WOOL_YARN.get()));
        registry.addWorkstation(STOMPING, EmiIngredient.of(FLTags.Items.STOMPING_BARRELS));
        registry.addWorkstation(PRESS, EmiIngredient.of(FLTags.Items.BARREL_PRESSES));
        registry.addWorkstation(CENTRIFUGE, EmiStack.of(FLBlocks.CENTRIFUGE.get()));
        registry.addWorkstation(MIXING_BOWL, EmiStack.of(FLBlocks.MIXING_BOWL.get()));
        registry.addWorkstation(MIXING_BOWL, EmiStack.of(FLItems.SPOON.get()));
        registry.addWorkstation(VAT, EmiStack.of(FLBlocks.VAT.get()));
        for (var oven : FLBlocks.CURED_OVEN_TOP.values())
        {
            registry.addWorkstation(OVEN, EmiStack.of(oven.get()));
        }

        // Kegs run the same recipes as a TFC barrel, and pot recipes below are added to TFC's pot category
        registry.addWorkstation(EmiIntegration.BARREL, EmiIngredient.of(FLTags.Items.KEGS));
    }

    private void registerRecipes(EmiRegistry registry)
    {
        basicRecipeMapping(registry, FLRecipeTypes.DRYING, (id, recipe) -> EmiItemRecipe.of(DRYING, id, recipe));
        basicRecipeMapping(registry, FLRecipeTypes.SMOKING, (id, recipe) -> EmiItemRecipe.of(SMOKING, id, recipe));
        basicRecipeMapping(registry, FLRecipeTypes.STOMPING, (id, recipe) -> EmiItemRecipe.of(STOMPING, id, recipe));
        basicRecipeMapping(registry, FLRecipeTypes.PRESS, (id, recipe) -> EmiItemRecipe.of(PRESS, id, recipe));
        basicRecipeMapping(registry, FLRecipeTypes.CENTRIFUGE, (id, recipe) -> EmiItemRecipe.of(CENTRIFUGE, id, recipe));
        basicRecipeMapping(registry, FLRecipeTypes.MIXING_BOWL, EmiMixingBowlRecipe::new);
        basicRecipeMapping(registry, FLRecipeTypes.OVEN, EmiOvenRecipe::new);
        basicRecipeMapping(registry, FLRecipeTypes.VAT, EmiVatRecipe::new);

        for (RecipeHolder<PotRecipe> entry : recipes(registry.getRecipeManager(), TFCRecipeTypes.POT))
        {
            final PotRecipe recipe = entry.value();
            if (recipe instanceof BowlPotRecipe bowl)
            {
                registry.addRecipe(new EmiBowlPotRecipe(entry.id(), bowl));
            }
            else if (recipe instanceof StinkySoupRecipe stinky)
            {
                registry.addRecipe(new EmiStinkySoupRecipe(entry.id(), stinky));
            }
        }
    }

    private static <C extends RecipeInput, T extends Recipe<C>> List<RecipeHolder<T>> recipes(RecipeManager manager, Supplier<RecipeType<T>> type)
    {
        return manager.getAllRecipesFor(type.get()).stream().toList();
    }

    private static <C extends RecipeInput, T extends Recipe<C>> void basicRecipeMapping(EmiRegistry registry, Supplier<RecipeType<T>> type, BiFunction<ResourceLocation, T, EmiRecipe> mapper)
    {
        for (RecipeHolder<T> recipe : recipes(registry.getRecipeManager(), type))
        {
            registry.addRecipe(mapper.apply(recipe.id(), recipe.value()));
        }
    }

    private static Comparator<EmiRecipe> basicSorter()
    {
        return (o1, o2) -> o1 instanceof ComparableRecipe recipe ? recipe.compareTo(o2) : 0;
    }
}
