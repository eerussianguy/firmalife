package com.eerussianguy.firmalife.common.recipes;

import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

import com.google.gson.JsonObject;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

import net.dries007.tfc.common.capabilities.heat.HeatCapability;
import net.dries007.tfc.common.recipes.*;
import net.dries007.tfc.common.recipes.inventory.ItemStackInventory;
import net.dries007.tfc.common.recipes.outputs.ItemStackProvider;
import net.dries007.tfc.util.JsonHelpers;
import net.dries007.tfc.util.collections.IndirectHashCollection;
import org.jetbrains.annotations.Nullable;

public class OvenRecipe implements ISimpleRecipe<ItemStackInventory>
{
    public static final IndirectHashCollection<Item, OvenRecipe> CACHE = IndirectHashCollection.createForRecipe(OvenRecipe::getValidItems, FLRecipeTypes.OVEN);

    @Nullable
    public static OvenRecipe getRecipe(ItemStack stack)
    {
        return getRecipe(new ItemStackInventory(stack));
    }

    @Nullable
    public static OvenRecipe getRecipe(ItemStackInventory wrapper)
    {
        for (OvenRecipe recipe : CACHE.getAll(wrapper.getStack().getItem()))
        {
            if (recipe.matches(wrapper, null))
            {
                return recipe;
            }
        }
        return null;
    }

    private final ResourceLocation id;
    private final Ingredient ingredient;
    private final ItemStackProvider outputItem;
    private final float temperature;
    private final int duration;

    public OvenRecipe(ResourceLocation id, Ingredient ingredient, ItemStackProvider outputItem, float temperature, int duration)
    {
        this.id = id;
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
    public ItemStack getResultItem()
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
    public ItemStack assemble(ItemStackInventory inventory)
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
