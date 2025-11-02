/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package com.eerussianguy.firmalife;

import java.util.Locale;
import java.util.Map;
import com.eerussianguy.firmalife.common.FLHelpers;
import com.eerussianguy.firmalife.common.blocks.FLBlocks;
import com.eerussianguy.firmalife.common.blocks.FLFluids;
import com.eerussianguy.firmalife.common.blocks.Herb;
import com.eerussianguy.firmalife.common.items.FLFood;
import com.eerussianguy.firmalife.common.items.FLItems;
import com.eerussianguy.firmalife.common.items.Spice;
import com.eerussianguy.firmalife.common.util.ExtraFluid;
import com.eerussianguy.firmalife.common.util.FLFruit;
import com.eerussianguy.firmalife.common.util.FLMetal;
import com.google.common.collect.ImmutableMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.common.crafting.CompoundIngredient;
import net.neoforged.neoforge.common.crafting.SizedIngredient;
import net.neoforged.neoforge.fluids.BaseFlowingFluid;
import net.neoforged.neoforge.fluids.crafting.SizedFluidIngredient;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.common.blocks.TFCBlocks;
import net.dries007.tfc.common.blocks.rock.Ore;
import net.dries007.tfc.common.blocks.wood.Wood;
import net.dries007.tfc.common.component.food.FoodTrait;
import net.dries007.tfc.common.fluids.FluidHolder;
import net.dries007.tfc.common.fluids.SimpleFluid;
import net.dries007.tfc.common.fluids.TFCFluids;
import net.dries007.tfc.common.items.Food;
import net.dries007.tfc.common.items.Powder;
import net.dries007.tfc.common.items.TFCItems;
import net.dries007.tfc.common.recipes.ingredients.AndIngredient;
import net.dries007.tfc.common.recipes.ingredients.HasTraitIngredient;
import net.dries007.tfc.common.recipes.ingredients.LacksTraitIngredient;
import net.dries007.tfc.common.recipes.ingredients.NotRottenIngredient;
import net.dries007.tfc.common.recipes.outputs.CopyFoodModifier;
import net.dries007.tfc.common.recipes.outputs.ItemStackProvider;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.Metal;
import net.dries007.tfc.util.calendar.ICalendar;
import net.dries007.tfc.util.data.FluidHeat;

public interface Accessors
{
    default Ingredient ingredientOf(FLMetal metal, FLMetal.ItemType type)
    {
        return Ingredient.of(FLItems.METAL_ITEMS.get(metal).get(type));
    }

    default Ingredient ingredientOf(FLMetal metal, Metal.BlockType type)
    {
        return Ingredient.of(FLBlocks.METALS.get(metal).get(type));
    }

    default Ingredient ingredientOf(Metal metal, Metal.ItemType type)
    {
        return type.isCommonTagPart()
            ? Ingredient.of(commonTagOf(metal, type))
            : Ingredient.of(TFCItems.METAL_ITEMS.get(metal).get(type).get());
    }

    default Ingredient ingredientOf(Metal metal, Metal.BlockType type)
    {
        return type == Metal.BlockType.BLOCK
            ? Ingredient.of(storageBlockTagOf(Registries.ITEM, metal))
            : Ingredient.of(TFCBlocks.METALS.get(metal).get(type).get());
    }

    default SizedIngredient sized(TagKey<Item> item)
    {
        return SizedIngredient.of(item, 1);
    }

    default SizedIngredient sized(ItemLike item)
    {
        return SizedIngredient.of(item, 1);
    }

    default SizedIngredient sized(ItemLike item, int count)
    {
        return SizedIngredient.of(item, count);
    }

    default SizedIngredient sized(Ingredient ingredient, int count)
    {
        return new SizedIngredient(ingredient, count);
    }

    default SizedIngredient sized(Ingredient ingredient)
    {
        return new SizedIngredient(ingredient, 1);
    }

    default SizedFluidIngredient sized(Fluid fluid)
    {
        return SizedFluidIngredient.of(fluid, 1);
    }

    default SizedFluidIngredient sized(Fluid fluid, int amount)
    {
        return SizedFluidIngredient.of(fluid, amount);
    }

    default Ingredient ingredientOf(Ingredient... values)
    {
        return CompoundIngredient.of(values);
    }

    default <T> TagKey<T> logsTagOf(ResourceKey<Registry<T>> registry, Wood wood)
    {
        return TagKey.create(registry, Helpers.identifier(wood.getSerializedName() + "_logs"));
    }

    default TagKey<Item> commonTagOf(Metal metal, Metal.ItemType type)
    {
        assert type.isCommonTagPart() : "Non-typical use of tag for " + metal.getSerializedName() + " / " + type.name();
        assert type.has(metal) : "Non-typical use of " + metal.getSerializedName() + " / " + type.name();
        return commonTagOf(Registries.ITEM, type.name() + "s/" + metal.name());
    }

    default <T> TagKey<T> storageBlockTagOf(ResourceKey<Registry<T>> key, Metal metal)
    {
        assert metal.defaultParts() : "Non-typical use of a non-default metal " + metal.getSerializedName();
        return commonTagOf(key, "storage_blocks/" + metal.getSerializedName());
    }

    default TagKey<Block> oreBlockTagOf(Ore ore, @Nullable Ore.Grade grade)
    {
        return commonTagOf(Registries.BLOCK, "ores/" + (ore.isGraded() ? ore.metal().name() : ore.name()) + (grade == null ? "" : "/" + grade.name()));
    }

    default <T> TagKey<T> commonTagOf(ResourceKey<Registry<T>> key, String name)
    {
        return TagKey.create(key, ResourceLocation.fromNamespaceAndPath("c", name.toLowerCase(Locale.ROOT)));
    }

    default Item dyeOf(DyeColor color)
    {
        return itemOf(ResourceLocation.withDefaultNamespace(color.getSerializedName() + "_dye"));
    }

    default Item dyedOf(DyeColor color, String suffix)
    {
        return itemOf(ResourceLocation.withDefaultNamespace(color.getSerializedName() + "_" + suffix));
    }

    default Item itemOf(ResourceLocation name)
    {
        assert BuiltInRegistries.ITEM.containsKey(name) : "No item '" + name + "'";
        return BuiltInRegistries.ITEM.get(name);
    }

    default ItemLike itemOf(Herb herb)
    {
        return FLBlocks.HERBS.get(herb);
    }

    default ItemLike itemOf(Powder powder)
    {
        return TFCItems.POWDERS.get(powder);
    }

    default ItemLike itemOf(Food food)
    {
        return TFCItems.FOOD.get(food);
    }

    default ItemLike itemOf(FLFood food)
    {
        return FLItems.FOODS.get(food);
    }

    default ItemLike itemOf(Spice spice)
    {
        return FLItems.SPICES.get(spice);
    }

    default ItemLike itemOf(FLFruit fruit)
    {
        return FLItems.FRUITS.get(fruit);
    }

    default ItemLike itemOf(Wood wood, Wood.BlockType type)
    {
        return TFCBlocks.WOODS.get(wood).get(type);
    }

    default ItemLike itemOf(Metal metal, Metal.ItemType type)
    {
        return TFCItems.METAL_ITEMS.get(metal).get(type);
    }

    default ItemLike itemOf(FLMetal metal, FLMetal.ItemType type)
    {
        return FLItems.METAL_ITEMS.get(metal).get(type);
    }

    default Fluid fluidOf(DyeColor color)
    {
        return TFCFluids.COLORED_FLUIDS.get(color).getSource();
    }

    default Fluid fluidOf(SimpleFluid fluid)
    {
        return TFCFluids.SIMPLE_FLUIDS.get(fluid).getSource();
    }

    default Fluid fluidOf(ExtraFluid fluid)
    {
        return FLFluids.EXTRA_FLUIDS.get(fluid).getSource();
    }

    default Fluid fluidOf(FluidHolder<BaseFlowingFluid> fluid)
    {
        return fluid.getSource();
    }

    default Fluid fluidOf(FLMetal metal)
    {
        return FLFluids.METALS.get(metal).getSource();
    }

    default Fluid fluidOf(Metal metal)
    {
        return TFCFluids.METALS.get(metal).getSource();
    }

    default String nameOf(Ingredient ingredient)
    {
        if (ingredient.getCustomIngredient() instanceof CompoundIngredient ing) return nameOf(ing.children().get(0));
        final Ingredient.Value value = ingredient.getValues()[0];
        if (value instanceof Ingredient.TagValue(TagKey<Item> tag)) return tag.location().getPath();
        if (value instanceof Ingredient.ItemValue(ItemStack item)) return nameOf(item.getItem());
        throw new AssertionError("Unknown ingredient value");
    }

    default String nameOf(Fluid fluid)
    {
        assert fluid != Fluids.EMPTY : "Should never get name of Items.AIR";
        return BuiltInRegistries.FLUID.getKey(fluid).getPath();
    }

    default String nameOf(ItemLike item)
    {
        assert item.asItem() != Items.AIR : "Should never get name of Items.AIR";
        assert item.asItem() != Items.BARRIER : "Should never get name of Items.BARRIER";
        return BuiltInRegistries.ITEM.getKey(item.asItem()).getPath();
    }

    default Ingredient notRotten(TagKey<Item> food)
    {
        return AndIngredient.of(Ingredient.of(food), NotRottenIngredient.INSTANCE);
    }

    default Ingredient notRotten(Ingredient food)
    {
        return AndIngredient.of(food, NotRottenIngredient.INSTANCE);
    }

    default Ingredient notRotten(ItemLike food)
    {
        return AndIngredient.of(Ingredient.of(food), NotRottenIngredient.INSTANCE);
    }

    default Ingredient hasTrait(ItemLike food, Holder<FoodTrait> trait)
    {
        return hasTrait(Ingredient.of(food), trait);
    }

    default Ingredient hasTrait(Ingredient food, Holder<FoodTrait> trait)
    {
        return AndIngredient.of(food, HasTraitIngredient.of(trait));
    }

    default Ingredient lacksTrait(ItemLike food, Holder<FoodTrait> trait)
    {
        return lacksTrait(Ingredient.of(food), trait);
    }

    default Ingredient lacksTrait(Ingredient food, Holder<FoodTrait> trait)
    {
        return AndIngredient.of(food, LacksTraitIngredient.of(trait));
    }

    default ItemStackProvider copyFood(ItemStack output)
    {
        return ItemStackProvider.of(output, CopyFoodModifier.INSTANCE);
    }

    default ItemStackProvider copyFood(ItemLike output)
    {
        return ItemStackProvider.of(new ItemStack(output), CopyFoodModifier.INSTANCE);
    }

    default int units(FLMetal.ItemType type)
    {
        return switch (type)
        {
            case ROD -> 50;
            default -> 100;
            case DOUBLE_INGOT, SHEET -> 200;
            case DOUBLE_SHEET -> 400;
        };
    }

    default int units(Metal.ItemType type)
    {
        return switch (type)
        {
            case ROD -> 50;
            default -> 100;
            case DOUBLE_INGOT, SHEET, FISH_HOOK, FISHING_ROD, SWORD, SWORD_BLADE, MACE, MACE_HEAD, SHEARS, UNFINISHED_BOOTS -> 200;
            case DOUBLE_SHEET, TUYERE, UNFINISHED_HELMET, UNFINISHED_CHESTPLATE, UNFINISHED_GREAVES, SHIELD, BOOTS -> 400;
            case HELMET, GREAVES -> 600;
            case CHESTPLATE -> 800;
            case HORSE_ARMOR -> 1200;
        };
    }

    default int units(Metal.BlockType type)
    {
        return switch (type)
        {
            case ANVIL -> 1400;
            case BLOCK, EXPOSED_BLOCK, WEATHERED_BLOCK, OXIDIZED_BLOCK, LAMP, GRATE, EXPOSED_GRATE, WEATHERED_GRATE, OXIDIZED_GRATE -> 100;
            case BLOCK_SLAB, EXPOSED_BLOCK_SLAB, WEATHERED_BLOCK_SLAB, OXIDIZED_BLOCK_SLAB -> 50;
            case BLOCK_STAIRS, EXPOSED_BLOCK_STAIRS, WEATHERED_BLOCK_STAIRS, OXIDIZED_BLOCK_STAIRS -> 75;
            case BARS -> 25;
            case CHAIN -> 6;
            case TRAPDOOR -> 200;
        };
    }

    default float temperatureOf(Metal metal)
    {
        return FluidHeat.MANAGER.getOrThrow(Helpers.identifier(metal.getSerializedName())).meltTemperature();
    }

    default float temperatureOf(FLMetal metal)
    {
        return FluidHeat.MANAGER.getOrThrow(FLHelpers.identifier(metal.getSerializedName())).meltTemperature();
    }

    default int hours(int hours)
    {
        return hours * ICalendar.CALENDAR_TICKS_IN_HOUR;
    }

    /**
     * Given a {@code Map<T1, Map<T2, V1>>}, and a key {@code T2}, constructs a map of all the mappings of {@code T1} to maps which contain
     * an entry for the given key {@code T2}
     *
     * @return An immutable map, with iteration order given by iteration order of the input map
     */
    default <T1, T2, V> Map<T1, V> pivot(Map<T1, Map<T2, V>> map, T2 key)
    {
        // This method must maintain a consistent, deterministic ordering, so we can't collect into a typical
        // hash map - we must use an order-preserving map here - immutable map is the easiest way to do that
        final ImmutableMap.Builder<T1, V> builder = new ImmutableMap.Builder<>();
        for (Map.Entry<T1, Map<T2, V>> entry : map.entrySet())
            if (entry.getValue().containsKey(key))
                builder.put(entry.getKey(), entry.getValue().get(key));
        return builder.build();
    }

    default BlockGetter empty()
    {
        return new BlockGetter()
        {
            @Nullable
            @Override
            public BlockEntity getBlockEntity(BlockPos pos)
            {
                return null;
            }

            @Override
            public BlockState getBlockState(BlockPos pos)
            {
                return Blocks.AIR.defaultBlockState();
            }

            @Override
            public FluidState getFluidState(BlockPos pos)
            {
                return Fluids.EMPTY.defaultFluidState();
            }

            @Override
            public int getHeight()
            {
                return 0;
            }

            @Override
            public int getMinBuildHeight()
            {
                return 0;
            }
        };
    }
}
