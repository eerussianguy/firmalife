package com.eerussianguy.firmalife.common.recipes;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

import net.minecraftforge.fluids.FluidStack;

import com.eerussianguy.firmalife.common.blockentities.MixingBowlBlockEntity;
import net.dries007.tfc.common.capabilities.food.FoodCapability;
import net.dries007.tfc.common.recipes.ISimpleRecipe;
import net.dries007.tfc.common.recipes.RecipeSerializerImpl;
import net.dries007.tfc.common.recipes.ingredients.FluidStackIngredient;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.JsonHelpers;
import org.jetbrains.annotations.Nullable;

public class MixingBowlRecipe implements ISimpleRecipe<MixingBowlBlockEntity.MixingBowlInventory>
{
    private final ResourceLocation id;
    private final List<Ingredient> itemIngredients;
    private final FluidStackIngredient fluidIngredient;
    private final ItemStack resultItem;
    private final FluidStack resultFluid;

    protected MixingBowlRecipe(ResourceLocation id, List<Ingredient> itemIngredients, FluidStackIngredient fluidIngredient, ItemStack resultItem, FluidStack resultFluid)
    {
        this.id = id;
        this.itemIngredients = itemIngredients;
        this.fluidIngredient = fluidIngredient;
        this.resultItem = resultItem;
        this.resultFluid = resultFluid;
        FoodCapability.setStackNonDecaying(this.resultItem);
    }

    @Override
    public boolean matches(MixingBowlBlockEntity.MixingBowlInventory inventory, Level level)
    {
        if (!fluidIngredient.test(inventory.getFluidInTank(0)))
        {
            return false;
        }
        final List<ItemStack> stacks = new ArrayList<>();
        for (int i = 0; i < MixingBowlBlockEntity.SLOTS; i++)
        {
            ItemStack stack = inventory.getStackInSlot(i);
            if (!stack.isEmpty())
            {
                stacks.add(stack);
            }
        }
        // we allow no ingredients, in this case we are only testing the fluid. but we need an empty inventory. just an efficiency thing.
        return itemIngredients.isEmpty() ? stacks.isEmpty() : Helpers.perfectMatchExists(stacks, itemIngredients);
    }

    public FluidStackIngredient getFluidIngredient()
    {
        return fluidIngredient;
    }

    public FluidStack getDisplayFluid()
    {
        return resultFluid;
    }

    public FluidStack getResultFluid()
    {
        return resultFluid.copy();
    }

    public List<Ingredient> getItemIngredients()
    {
        return itemIngredients;
    }

    @Override
    public ItemStack getResultItem()
    {
        return resultItem;
    }

    @Override
    public ResourceLocation getId()
    {
        return id;
    }

    @Override
    public RecipeSerializer<?> getSerializer()
    {
        return FLRecipeSerializers.MIXING_BOWL.get();
    }

    @Override
    public RecipeType<?> getType()
    {
        return FLRecipeTypes.MIXING_BOWL.get();
    }

    public static class Serializer extends RecipeSerializerImpl<MixingBowlRecipe>
    {
        @Override
        public MixingBowlRecipe fromJson(ResourceLocation id, JsonObject json)
        {
            final List<Ingredient> ingredients = new ArrayList<>();
            if (json.has("ingredients"))
            {
                final JsonArray array = GsonHelper.getAsJsonArray(json, "ingredients");
                for (JsonElement element : array)
                {
                    ingredients.add(Ingredient.fromJson(element));
                }
            }
            final FluidStackIngredient fluidIngredient = json.has("fluid_ingredient") ? FluidStackIngredient.fromJson(GsonHelper.getAsJsonObject(json, "fluid_ingredient")) : FluidStackIngredient.EMPTY;
            final ItemStack result = json.has("output_item") ? JsonHelpers.getItemStack(json, "output_item") : ItemStack.EMPTY;
            final FluidStack fluidResult = json.has("output_fluid") ? JsonHelpers.getFluidStack(json, "output_fluid") : FluidStack.EMPTY;
            return new MixingBowlRecipe(id, ingredients, fluidIngredient, result, fluidResult);
        }

        @Nullable
        @Override
        public MixingBowlRecipe fromNetwork(ResourceLocation id, FriendlyByteBuf buffer)
        {
            final int count = buffer.readVarInt();
            final List<Ingredient> ingredients = new ArrayList<>(count);
            for (int i = 0; i < count; i++)
            {
                ingredients.add(Ingredient.fromNetwork(buffer));
            }
            final FluidStackIngredient fluidIngredient = FluidStackIngredient.fromNetwork(buffer);
            final ItemStack result = buffer.readItem();
            final FluidStack fluidOut = buffer.readFluidStack();
            return new MixingBowlRecipe(id, ingredients, fluidIngredient, result, fluidOut);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buffer, MixingBowlRecipe recipe)
        {
            buffer.writeVarInt(recipe.itemIngredients.size());
            for (Ingredient ingredient : recipe.itemIngredients)
            {
                ingredient.toNetwork(buffer);
            }
            recipe.fluidIngredient.toNetwork(buffer);
            buffer.writeItem(recipe.resultItem);
            buffer.writeFluidStack(recipe.resultFluid);
        }
    }
}
