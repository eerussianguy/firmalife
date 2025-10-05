package com.eerussianguy.firmalife.common.recipes;

import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;

import net.dries007.tfc.common.component.heat.HeatCapability;
import net.dries007.tfc.common.component.heat.IHeat;
import net.dries007.tfc.common.recipes.*;
import net.dries007.tfc.common.recipes.outputs.ItemStackProvider;
import net.dries007.tfc.util.collections.IndirectHashCollection;
import net.dries007.tfc.world.Codecs;

import org.jetbrains.annotations.Nullable;

public class OvenRecipe implements INoopInputRecipe, IRecipePredicate<ItemStack>
{
    public static final MapCodec<OvenRecipe> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
        Ingredient.CODEC.fieldOf("ingredient").forGetter(c -> c.ingredient),
        ItemStackProvider.CODEC.fieldOf("result").forGetter(c -> c.outputItem),
        Codec.FLOAT.fieldOf("temperature").forGetter(c -> c.temperature),
        Codecs.POSITIVE_INT.fieldOf("duration").forGetter(c -> c.duration)
    ).apply(i, OvenRecipe::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, OvenRecipe> STREAM_CODEC = StreamCodec.composite(
        Ingredient.CONTENTS_STREAM_CODEC, c -> c.ingredient,
        ItemStackProvider.STREAM_CODEC, c -> c.outputItem,
        ByteBufCodecs.FLOAT, c -> c.temperature,
        ByteBufCodecs.INT, c -> c.duration,
        OvenRecipe::new
    );

    public static final IndirectHashCollection<Item, OvenRecipe> CACHE = IndirectHashCollection.createForRecipe(OvenRecipe::getValidItems, FLRecipeTypes.OVEN);


    @Nullable
    public static OvenRecipe getRecipe(ItemStack input)
    {
        return RecipeHelpers.getRecipe(CACHE, input, input.getItem());
    }

    private final Ingredient ingredient;
    private final ItemStackProvider outputItem;
    private final float temperature;
    private final int duration;

    public OvenRecipe(Ingredient ingredient, ItemStackProvider outputItem, float temperature, int duration)
    {
        this.ingredient = ingredient;
        this.outputItem = outputItem;
        this.temperature = temperature;
        this.duration = duration;
    }

    public ItemStackProvider getResult()
    {
        return outputItem;
    }

    public int getDuration()
    {
        return duration;
    }

    @Override
    public RecipeSerializer<?> getSerializer()
    {
        return FLRecipeSerializers.OVEN.get();
    }

    @Override
    public RecipeType<?> getType()
    {
        return FLRecipeTypes.OVEN.get();
    }

    public ItemStack assembleItem(ItemStack input)
    {
        final ItemStack outputStack = outputItem.getSingleStack(input);
        // We always upgrade the heat regardless
        final @Nullable IHeat inputHeat = HeatCapability.get(input);
        if (inputHeat != null)
        {
            HeatCapability.setTemperature(outputStack, inputHeat.getTemperature());
        }
        return outputStack;
    }

    public float getTemperature()
    {
        return temperature;
    }

    public boolean isValidTemperature(float temperatureIn)
    {
        return temperatureIn >= temperature;
    }

    public Collection<Item> getValidItems()
    {
        return Arrays.stream(this.getIngredient().getItems()).map(ItemStack::getItem).collect(Collectors.toSet());
    }

    public Ingredient getIngredient()
    {
        return ingredient;
    }

    @Override
    public boolean matches(ItemStack itemStack)
    {
        return getIngredient().test(itemStack);
    }
}
