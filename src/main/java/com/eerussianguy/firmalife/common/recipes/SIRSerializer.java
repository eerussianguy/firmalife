package com.eerussianguy.firmalife.common.recipes;

import com.google.gson.JsonObject;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.crafting.Ingredient;

import net.dries007.tfc.common.recipes.RecipeSerializerImpl;
import net.dries007.tfc.common.recipes.SimpleItemRecipe;
import net.dries007.tfc.common.recipes.outputs.ItemStackProvider;
import net.dries007.tfc.util.JsonHelpers;
import org.jetbrains.annotations.Nullable;

// todo REMOVE ME!!! I AM EVIL!
public class SIRSerializer<R extends SimpleItemRecipe> extends RecipeSerializerImpl<R>
{
    private final Factory<R> factory;

    public SIRSerializer(Factory<R> factory)
    {
        this.factory = factory;
    }

    public R fromJson(ResourceLocation recipeId, JsonObject json)
    {
        Ingredient ingredient = Ingredient.fromJson(JsonHelpers.get(json, "ingredient"));
        ItemStackProvider result = ItemStackProvider.fromJson(GsonHelper.getAsJsonObject(json, "result"));
        return this.factory.create(recipeId, ingredient, result);
    }

    @Nullable
    public R fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer)
    {
        Ingredient ingredient = Ingredient.fromNetwork(buffer);
        ItemStackProvider result = ItemStackProvider.fromNetwork(buffer);
        return this.factory.create(recipeId, ingredient, result);
    }

    public void toNetwork(FriendlyByteBuf buffer, R recipe)
    {
        recipe.getIngredient().toNetwork(buffer);
        buffer.writeItem(recipe.getResultItem());
    }

    public interface Factory<R extends SimpleItemRecipe>
    {
        R create(ResourceLocation var1, Ingredient var2, ItemStackProvider var3);
    }
}
