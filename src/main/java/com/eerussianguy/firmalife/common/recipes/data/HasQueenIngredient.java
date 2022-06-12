package com.eerussianguy.firmalife.common.recipes.data;

import com.google.gson.JsonObject;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.common.crafting.IIngredientSerializer;

import com.eerussianguy.firmalife.common.capabilities.bee.BeeCapability;
import com.eerussianguy.firmalife.common.capabilities.bee.IBee;
import net.dries007.tfc.common.recipes.ingredients.DelegateIngredient;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.JsonHelpers;
import org.jetbrains.annotations.Nullable;

public class HasQueenIngredient extends DelegateIngredient
{
    public HasQueenIngredient(@Nullable Ingredient delegate)
    {
        super(delegate);
    }

    @Override
    @Nullable
    protected ItemStack testDefaultItem(ItemStack stack)
    {
        return stack.getCapability(BeeCapability.CAPABILITY)
            .map(h -> {
                h.setHasQueen(true);
                return stack;
            })
            .orElse(null);
    }


    @Override
    public boolean test(@Nullable ItemStack stack)
    {
        if (super.test(stack) && stack != null && !stack.isEmpty())
        {
            return stack.getCapability(BeeCapability.CAPABILITY)
                .map(IBee::hasQueen)
                .orElse(false);
        }
        return false;
    }

    @Override
    public IIngredientSerializer<? extends DelegateIngredient> getSerializer()
    {
        return Serializer.INSTANCE;
    }

    public enum Serializer implements IIngredientSerializer<HasQueenIngredient>
    {
        INSTANCE;

        @Override
        public HasQueenIngredient parse(JsonObject json)
        {
            final Ingredient internal = json.has("ingredient") ? Ingredient.fromJson(JsonHelpers.get(json, "ingredient")) : null;
            return new HasQueenIngredient(internal);
        }

        @Override
        public HasQueenIngredient parse(FriendlyByteBuf buffer)
        {
            return new HasQueenIngredient(Helpers.decodeNullable(buffer, Ingredient::fromNetwork));
        }

        @Override
        public void write(FriendlyByteBuf buffer, HasQueenIngredient ingredient)
        {
            Helpers.encodeNullable(ingredient.delegate, buffer, Ingredient::toNetwork);
        }
    }
}
