package com.eerussianguy.firmalife.providers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import com.eerussianguy.firmalife.Accessors;
import com.eerussianguy.firmalife.FirmaLife;
import com.eerussianguy.firmalife.common.FLHelpers;
import com.eerussianguy.firmalife.common.blocks.FLBlocks;
import com.eerussianguy.firmalife.common.items.FLFood;
import com.eerussianguy.firmalife.common.items.FLItems;
import com.eerussianguy.firmalife.common.util.FLMetal;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;

import net.dries007.tfc.TerraFirmaCraft;
import net.dries007.tfc.common.blocks.rock.Ore;
import net.dries007.tfc.common.component.heat.HeatCapability;
import net.dries007.tfc.common.component.heat.HeatDefinition;
import net.dries007.tfc.common.fluids.TFCFluids;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.Metal;
import net.dries007.tfc.util.data.FluidHeat;

public class BuiltinItemHeat extends DataManagerProvider<HeatDefinition> implements Accessors
{
    public static final float FLUID_HEAT_CAPACITY = 0.003f;

    public final List<MeltingRecipe> meltingRecipes = new ArrayList<>();
    private final CompletableFuture<?> before;

    public BuiltinItemHeat(PackOutput output, CompletableFuture<HolderLookup.Provider> lookup, CompletableFuture<?> before)
    {
        super(HeatCapability.MANAGER, output, lookup, TerraFirmaCraft.MOD_ID);
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
        FLHelpers.fakeDataManager(FluidHeat.MANAGER, Map.of(
            Metal.COPPER.getSerializedName(), new FluidHeat(TFCFluids.METALS.get(Metal.COPPER).getSource(), 0.35f, 1080)
        ));
        
        add("copper_pipe", Ingredient.of(FLBlocks.COPPER_PIPE), Metal.COPPER, 12);
        add("oxidized_copper_pipe", Ingredient.of(FLBlocks.OXIDIZED_COPPER_PIPE), Metal.COPPER, 12);
        addAndMelt(FLItems.STAINLESS_STEEL_JAR_LID, FLMetal.STAINLESS_STEEL, 6);
        addAndMelt(FLBlocks.SMALL_CHROMITE, FLMetal.CHROMIUM, 10);
        addAndMelt(FLItems.CHROMIUM_ORES.get(Ore.Grade.POOR), FLMetal.CHROMIUM, 15);
        addAndMelt(FLItems.CHROMIUM_ORES.get(Ore.Grade.NORMAL), FLMetal.CHROMIUM, 25);
        addAndMelt(FLItems.CHROMIUM_ORES.get(Ore.Grade.RICH), FLMetal.CHROMIUM, 35);

        for (var food : List.of(FLFood.BACON, FLFood.MASA, FLFood.WHEAT_SLICE, FLFood.MAIZE_SLICE, FLFood.BARLEY_SLICE, FLFood.OAT_SLICE, FLFood.RICE_SLICE, FLFood.RYE_SLICE, FLFood.RAW_LASAGNA, FLFood.COOKIE_DOUGH, FLFood.CHOCOLATE_CHIP_COOKIE_DOUGH, FLFood.HARDTACK_DOUGH, FLFood.CORN_TORTILLA, FLFood.COCOA_BEANS))
        {
            add(itemOf(food), 1);
        }

        add(FLItems.RAW_PIZZA, 1);
        add(FLItems.RAW_PUMPKIN_PIE, 1);
        add(FLItems.FILLED_PIE, 1);

        FLItems.METAL_ITEMS.forEach((metal, items) -> {
            add(metal, FLMetal.ItemType.INGOT);
            add(metal, FLMetal.ItemType.DOUBLE_INGOT);
            add(metal, FLMetal.ItemType.SHEET);
            add(metal, FLMetal.ItemType.DOUBLE_SHEET);
            add(metal, FLMetal.ItemType.ROD);

            for (int amount : new int[] {50, 100, 200, 400, 600, 800, 1200})
            {
                final ItemLike[] parts = Arrays.stream(Metal.ItemType.values())
                    .filter(type -> !type.isCommonTagPart() && units(type) == amount)
                    .map(items::get)
                    .filter(Objects::nonNull)
                    .toArray(ItemLike[]::new);
                if (parts.length > 0) add(metal, "parts_" + amount, Ingredient.of(parts), amount);
            }
        });
        FLBlocks.METALS.forEach((metal, blocks) -> {
            if (metal.weatheredParts())
            {
                add(metal, "block", ingredientOf(
                    ingredientOf(metal, Metal.BlockType.BLOCK)
                ), units(Metal.BlockType.BLOCK));
                add(metal, "block_slab", Ingredient.of(
                    blocks.get(Metal.BlockType.BLOCK_SLAB)
                ), units(Metal.BlockType.BLOCK_SLAB));
                add(metal, "block_stairs", Ingredient.of(
                    blocks.get(Metal.BlockType.BLOCK_STAIRS)
                ), units(Metal.BlockType.BLOCK_STAIRS));

            }
            else
            {
                add(metal, Metal.BlockType.BLOCK);
                add(metal, Metal.BlockType.BLOCK_SLAB);
                add(metal, Metal.BlockType.BLOCK_STAIRS);
            }

        });
    }

    private void addAndMelt(ItemLike item, FLMetal metal, int units)
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

    private void add(FLMetal metal, FLMetal.ItemType type)
    {
        add(metal.getSerializedName() + "/" + type.name().toLowerCase(Locale.ROOT), ingredientOf(metal, type), metal, units(type));
    }

    private void add(FLMetal metal, Metal.BlockType type)
    {
        add(metal.getSerializedName() + "/" + type.name().toLowerCase(Locale.ROOT), ingredientOf(metal, type), metal, units(type));
    }

    private void add(FLMetal metal, String typeName, Ingredient ingredient, int units)
    {
        add(metal.getSerializedName() + "/" + typeName.toLowerCase(Locale.ROOT), ingredient, metal, units);
    }

    private void add(String name, Ingredient ingredient, Metal metal, int units)
    {
        add(name, Helpers.identifier(metal.getSerializedName()), ingredient, units);
    }

    private void add(String name, Ingredient ingredient, FLMetal metal, int units)
    {
        add(name, FLHelpers.identifier(metal.getSerializedName()), ingredient, units);
    }

    private void add(String name, ResourceLocation metalSerializedName, Ingredient ingredient, int units)
    {
        if (FluidHeat.MANAGER.getValues().isEmpty())
        {
            FirmaLife.LOGGER.error("FluidHeat manager has not been loaded.");
            return;
        }
        final FluidHeat fluidHeat = FluidHeat.MANAGER.getOrThrow(metalSerializedName);
        add(name, new HeatDefinition(
            ingredient,
            (fluidHeat.specificHeatCapacity() / FLUID_HEAT_CAPACITY) * (units / 100f),
            fluidHeat.meltTemperature() * 0.6f,
            fluidHeat.meltTemperature() * 0.8f));
    }

    record MeltingRecipe(ItemLike item, FLMetal metal, int units) {}
}
