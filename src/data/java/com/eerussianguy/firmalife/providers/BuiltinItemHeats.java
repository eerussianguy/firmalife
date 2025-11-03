package com.eerussianguy.firmalife.providers;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CompletableFuture;
import com.eerussianguy.firmalife.Accessors;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;

import net.dries007.tfc.common.component.heat.HeatCapability;
import net.dries007.tfc.common.component.heat.HeatDefinition;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.Metal;
import net.dries007.tfc.util.data.FluidHeat;

public class BuiltinItemHeats extends DataManagerProvider<HeatDefinition> implements Accessors
{
    public static final float FLUID_HEAT_CAPACITY = 0.003f;
    
    public final List<MeltingRecipe> meltingRecipes = new ArrayList<>();
    private final CompletableFuture<?> before;

    public BuiltinItemHeats(PackOutput output, CompletableFuture<HolderLookup.Provider> lookup, CompletableFuture<?> before)
    {
        super(HeatCapability.MANAGER, output, lookup);
        this.before = before;
    }

    @Override
    protected CompletableFuture<HolderLookup.Provider> beforeRun()
    {
        return before.thenCompose(v -> super.beforeRun());
    }


    @Override
    protected void addData(HolderLookup.Provider provider)
    {

    }

    private void addAndMeltIron(ItemLike item, int units)
    {
        meltingRecipes.add(new MeltingRecipe(item, Metal.CAST_IRON, units));
        add(nameOf(item), Ingredient.of(item), Metal.WROUGHT_IRON, units);
    }

    private void addAndMelt(ItemLike item, Metal metal, int units)
    {
        meltingRecipes.add(new MeltingRecipe(item, metal, units));
        add(nameOf(item), Ingredient.of(item), metal, units);
    }

    private void add(ItemLike item, float heatCapacity)
    {
        add(Ingredient.of(item), heatCapacity);
    }

    private void add(TagKey<Item> item, float heatCapacity)
    {
        add(Ingredient.of(item), heatCapacity);
    }

    private void add(Ingredient item, float heatCapacity)
    {
        add(nameOf(item), new HeatDefinition(item, heatCapacity, 0f, 0f));
    }

    private void add(Metal metal, Metal.ItemType type)
    {
        if (type.has(metal)) add(metal.getSerializedName() + "/" + type.name().toLowerCase(Locale.ROOT), ingredientOf(metal, type), metal, units(type));
    }

    private void add(Metal metal, Metal.BlockType type)
    {
        if (type.has(metal)) add(metal.getSerializedName() + "/" + type.name().toLowerCase(Locale.ROOT), ingredientOf(metal, type), metal, units(type));
    }

    private void add(Metal metal, String typeName, Ingredient ingredient, int units)
    {
        add(metal.getSerializedName() + "/" + typeName.toLowerCase(Locale.ROOT), ingredient, metal, units);
    }

    private void add(String name, Ingredient ingredient, Metal metal, int units)
    {
        final FluidHeat fluidHeat = FluidHeat.MANAGER.getOrThrow(Helpers.identifier(metal.getSerializedName()));
        add(name, new HeatDefinition(
            ingredient,
            (fluidHeat.specificHeatCapacity() / FLUID_HEAT_CAPACITY) * (units / 100f),
            fluidHeat.meltTemperature() * 0.6f,
            fluidHeat.meltTemperature() * 0.8f));
    }

    record MeltingRecipe(ItemLike item, Metal metal, int units) {}
}
