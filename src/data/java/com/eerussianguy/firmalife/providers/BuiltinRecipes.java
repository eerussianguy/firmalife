package com.eerussianguy.firmalife.providers;

import java.util.Locale;
import java.util.concurrent.CompletableFuture;
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
import net.minecraft.core.HolderLookup;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.world.item.crafting.Recipe;

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
    private RecipeOutput output;
    private HolderLookup.Provider lookup;

    public BuiltinRecipes(PackOutput output, CompletableFuture<HolderLookup.Provider> registries)
    {
        super(output, registries);
    }

    @Override
    protected CompletableFuture<?> run(CachedOutput output, HolderLookup.Provider lookup)
    {
        this.lookup = lookup;
        return super.run(output, lookup);
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
    }

    @Override
    public HolderLookup.Provider lookup()
    {
        return lookup;
    }

    @Override
    public void add(String prefix, String name, Recipe<?> recipe)
    {
        output.accept(Helpers.identifier((prefix + "/" + name).toLowerCase(Locale.ROOT)), recipe, null);
    }

    @Override
    public void remove(String... names)
    {
        //TODO
    }

    @Override
    public void replace(String name, Recipe<?> recipe)
    {
        //TODO
    }
}
