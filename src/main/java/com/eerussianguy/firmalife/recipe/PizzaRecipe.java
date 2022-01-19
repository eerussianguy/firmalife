package com.eerussianguy.firmalife.recipe;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nonnull;

import com.google.gson.JsonObject;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.crafting.IRecipeFactory;
import net.minecraftforge.common.crafting.JsonContext;

import com.eerussianguy.firmalife.items.ItemPizza;
import net.dries007.tfc.api.capability.food.CapabilityFood;
import net.dries007.tfc.api.capability.food.FoodData;
import net.dries007.tfc.api.capability.food.IFood;
import net.dries007.tfc.objects.recipes.RecipeUtils;
import net.dries007.tfc.objects.recipes.ShapedDamageRecipe;
import net.dries007.tfc.util.calendar.CalendarTFC;

public class PizzaRecipe extends SandwichBasedRecipe
{
    public PizzaRecipe(ResourceLocation group, CraftingHelper.ShapedPrimer input, @Nonnull ItemStack result, int damage)
    {
        super(group, input, result, damage);
    }

    @Nonnull
    @Override
    public ItemStack getCraftingResult(@Nonnull InventoryCrafting inv)
    {
        ItemStack output = super.getCraftingResult(inv);
        IFood food = output.getCapability(CapabilityFood.CAPABILITY, null);
        if (food instanceof ItemPizza.PizzaHandler)
        {
            ItemPizza.PizzaHandler trailMix = (ItemPizza.PizzaHandler) food;
            List<FoodData> ingredients = new ArrayList<>();
            getIngredients(inv, ingredients);
            if (ingredients.size() < 1) return ItemStack.EMPTY;

            trailMix.initCreationFoods(ingredients);
            trailMix.setCreationDate(CalendarTFC.PLAYER_TIME.getTicks()); // Meals get decay reset as they have on average, high decay modifiers. Also it's too much of a pain to re-calculate a remaining decay fraction average
        }
        return output;
    }

    @SuppressWarnings("unused")
    public static class Factory implements IRecipeFactory
    {
        @Override
        public IRecipe parse(final JsonContext context, final JsonObject json)
        {
            String group = JsonUtils.getString(json, "group", "");

            CraftingHelper.ShapedPrimer primer = RecipeUtils.parsePhaped(context, json);

            ItemStack result = CraftingHelper.getItemStack(JsonUtils.getJsonObject(json, "result"), context);
            final int damage;
            if (JsonUtils.hasField(json, "damage"))
                damage = JsonUtils.getInt(json, "damage");
            else damage = 1;
            return new PizzaRecipe(group.isEmpty() ? null : new ResourceLocation(group), primer, result, damage);
        }
    }
}
