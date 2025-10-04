package com.eerussianguy.firmalife.common.recipes;

import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

import com.eerussianguy.firmalife.common.blockentities.OvenTopBlockEntity;
import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

import net.dries007.tfc.common.recipes.*;
import net.dries007.tfc.common.recipes.outputs.ItemStackProvider;
import net.dries007.tfc.util.collections.IndirectHashCollection;
import net.dries007.tfc.world.Codecs;

import org.jetbrains.annotations.Nullable;

public class OvenRecipe implements ISimpleRecipe<OvenTopBlockEntity.OvenInventory>
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
    public static OvenRecipe getRecipe(ItemStack stack)
    {
        return getRecipe(new ItemStackInventory(stack));
    }

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

    @Override
    public boolean matches(ItemStackInventory inventory, @Nullable Level level)
    {
        return getIngredient().test(inventory.getStack());
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
    public ItemStack getResultItem(RegistryAccess access)
    {
        return outputItem.getEmptyStack();
    }

    @Override
    public ResourceLocation getId()
    {
        return id;
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

    @Override
    public ItemStack assemble(HolderLookup.Provider access)
    {
        final ItemStack inputStack = inventory.getStack();
        final ItemStack outputStack = outputItem.getSingleStack(inputStack);
        // We always upgrade the heat regardless
        inputStack.getCapability(HeatCapability.CAPABILITY).ifPresent(oldCap ->
            outputStack.getCapability(HeatCapability.CAPABILITY).ifPresent(newCap ->
                newCap.setTemperature(oldCap.getTemperature())));
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

    public static class Serializer extends RecipeSerializerImpl<OvenRecipe>
    {
        @Override
        public OvenRecipe fromJson(ResourceLocation recipeId, JsonObject json)
        {
            final Ingredient ingredient = Ingredient.fromJson(json.get("ingredient"));
            final ItemStackProvider outputItem = json.has("result_item") ? ItemStackProvider.fromJson(json.getAsJsonObject("result_item")): ItemStackProvider.empty();
            final float temperature = JsonHelpers.getAsFloat(json, "temperature");
            final int time = JsonHelpers.getAsInt(json, "duration");
            return new OvenRecipe(recipeId, ingredient, outputItem, temperature, time);
        }

        @Nullable
        @Override
        public OvenRecipe fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer)
        {
            final Ingredient ingredient = Ingredient.fromNetwork(buffer);
            final ItemStackProvider outputItem = ItemStackProvider.fromNetwork(buffer);
            final float temperature = buffer.readFloat();
            final int duration = buffer.readVarInt();
            return new OvenRecipe(recipeId, ingredient, outputItem, temperature, duration);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buffer, OvenRecipe recipe)
        {
            recipe.getIngredient().toNetwork(buffer);
            recipe.outputItem.toNetwork(buffer);
            buffer.writeFloat(recipe.temperature);
            buffer.writeVarInt(recipe.duration);
        }
    }
}
