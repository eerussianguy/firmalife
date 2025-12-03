package com.eerussianguy.firmalife.providers;

import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import com.eerussianguy.firmalife.common.FLHelpers;
import com.eerussianguy.firmalife.recipes.AnvilRecipes;
import com.eerussianguy.firmalife.recipes.BarrelRecipes;
import com.eerussianguy.firmalife.recipes.CastingRecipes;
import com.eerussianguy.firmalife.recipes.ChiselRecipes;
import com.eerussianguy.firmalife.recipes.CollapseRecipes;
import com.eerussianguy.firmalife.recipes.CraftingRecipes;
import com.eerussianguy.firmalife.recipes.DryingRecipes;
import com.eerussianguy.firmalife.recipes.GlassworkingRecipes;
import com.eerussianguy.firmalife.recipes.HeatingRecipes;
import com.eerussianguy.firmalife.recipes.KnappingRecipes;
import com.eerussianguy.firmalife.recipes.LoomRecipes;
import com.eerussianguy.firmalife.recipes.MixingBowlRecipes;
import com.eerussianguy.firmalife.recipes.OvenRecipes;
import com.eerussianguy.firmalife.recipes.PotRecipes;
import com.eerussianguy.firmalife.recipes.QuernRecipes;
import com.eerussianguy.firmalife.recipes.Recipes;
import com.eerussianguy.firmalife.recipes.SmokingRecipes;
import com.eerussianguy.firmalife.recipes.StompingRecipes;
import com.eerussianguy.firmalife.recipes.VatRecipes;
import com.eerussianguy.firmalife.recipes.WeldingRecipes;
import com.mojang.serialization.Codec;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Unit;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.neoforged.neoforge.fluids.FluidStack;

import net.dries007.tfc.common.recipes.HeatingRecipe;
import net.dries007.tfc.common.recipes.outputs.ItemStackProvider;
import net.dries007.tfc.util.Helpers;

public class BuiltinRecipes extends RecipeProvider implements Recipes,
    AnvilRecipes,
    BarrelRecipes,
    CastingRecipes,
    ChiselRecipes,
    CollapseRecipes,
    CraftingRecipes,
    DryingRecipes,
    GlassworkingRecipes,
    HeatingRecipes,
    KnappingRecipes,
    LoomRecipes,
    MixingBowlRecipes,
    OvenRecipes,
    PotRecipes,
    QuernRecipes,
    SmokingRecipes,
    StompingRecipes,
    VatRecipes,
    WeldingRecipes
{
    final Set<ResourceLocation> removedRecipes = new HashSet<>();

    final Codec<Unit> emptyRecipeCodec = Codec.STRING.fieldOf("type")
        .codec()
        .listOf()
        .fieldOf("neoforge:conditions")
        .xmap(l -> Unit.INSTANCE, r -> List.of("neoforge:false"))
        .codec();

    private RecipeOutput output;
    private HolderLookup.Provider lookup;
    private final List<BuiltinItemHeat.MeltingRecipe> meltingRecipes;
    final CompletableFuture<?> before;

    public BuiltinRecipes(PackOutput output, CompletableFuture<HolderLookup.Provider> registries, CompletableFuture<?> before, BuiltinItemHeat itemHeat)
    {
        super(output, registries);
        this.before = CompletableFuture.allOf(before, itemHeat.output());
        this.meltingRecipes = itemHeat.meltingRecipes;
    }

    @Override
    public CompletableFuture<?> run(CachedOutput output, HolderLookup.Provider lookup)
    {
        this.lookup = lookup;
        return before.thenCompose(v -> CompletableFuture.allOf(
            super.run(output, lookup),
            CompletableFuture.allOf(removedRecipes
                .stream()
                .map(id -> DataProvider.saveStable(output, lookup, emptyRecipeCodec, Unit.INSTANCE, recipePathProvider.json(id)))
                .toArray(CompletableFuture[]::new))
        ));
    }

    @Override
    public void buildRecipes(RecipeOutput output)
    {
        this.output = output;
        anvilRecipes();
        barrelRecipes();
        castingRecipes();
        chiselRecipes();
        collapseRecipes();
        craftingRecipes();
        dryingRecipes();
        glassworkingRecipes();
        heatingRecipes();
        knappingRecipes();
        loomRecipes();
        mixingBowlRecipes();
        ovenRecipes();
        potRecipes();
        quernRecipes();
        smokingRecipes();
        stompingRecipes();
        vatRecipes();
        weldingRecipes();

        // Heat Recipes from Melting
        for (BuiltinItemHeat.MeltingRecipe melt : meltingRecipes)
        {
            add(nameOf(melt.item()), new HeatingRecipe(
                Ingredient.of(melt.item()),
                ItemStackProvider.empty(),
                new FluidStack(fluidOf(melt.metal()), melt.units()),
                temperatureOf(melt.metal()),
                false
            ));
        }
    }

    @Override
    public HolderLookup.Provider lookup()
    {
        return lookup;
    }

    @Override
    public void add(String prefix, String name, Recipe<?> recipe)
    {
        output.accept(FLHelpers.identifier((prefix + "/" + name).toLowerCase(Locale.ROOT)), recipe, null);
    }

    @Override
    public void remove(String... names)
    {
        for (String name : names)
        {
            final ResourceLocation id = Helpers.identifier(name);
            removedRecipes.add(id);
        }
    }

}
