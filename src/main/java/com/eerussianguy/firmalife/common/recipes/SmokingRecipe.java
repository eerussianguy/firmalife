package com.eerussianguy.firmalife.common.recipes;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;

import net.dries007.tfc.common.recipes.ItemRecipe;
import net.dries007.tfc.common.recipes.RecipeHelpers;
import net.dries007.tfc.common.recipes.outputs.ItemStackProvider;
import net.dries007.tfc.util.collections.IndirectHashCollection;
import org.jetbrains.annotations.Nullable;

public class SmokingRecipe extends ItemRecipe
{
    public static final MapCodec<SmokingRecipe> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
    Ingredient.CODEC.fieldOf("ingredient").forGetter(c -> c.ingredient),
    ItemStackProvider.CODEC.fieldOf("result").forGetter(c -> c.result)
).apply(i, SmokingRecipe::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, SmokingRecipe> STREAM_CODEC = StreamCodec.composite(
        Ingredient.CONTENTS_STREAM_CODEC, c -> c.ingredient,
        ItemStackProvider.STREAM_CODEC, c -> c.result,
        SmokingRecipe::new
    );

    public static final IndirectHashCollection<Item, SmokingRecipe> CACHE = IndirectHashCollection.createForRecipe(SmokingRecipe::getValidItems, FLRecipeTypes.SMOKING);

    public SmokingRecipe(Ingredient ingredient, ItemStackProvider result)
    {
        super(ingredient, result);
    }

    @Nullable
    public static SmokingRecipe getRecipe(ItemStack input)
    {
        return RecipeHelpers.getRecipe(CACHE, input, input.getItem());
    }

    @Override
    public RecipeSerializer<?> getSerializer()
    {
        return FLRecipeSerializers.SMOKING.get();
    }

    @Override
    public RecipeType<?> getType()
    {
        return FLRecipeTypes.SMOKING.get();
    }
}
