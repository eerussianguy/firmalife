package com.eerussianguy.firmalife.common.recipes;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
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

public class DryingRecipe extends ItemRecipe
{
    public static final MapCodec<DryingRecipe> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
        Ingredient.CODEC.fieldOf("ingredient").forGetter(c -> c.ingredient),
        ItemStackProvider.CODEC.fieldOf("result").forGetter(c -> c.result)
    ).apply(i, DryingRecipe::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, DryingRecipe> STREAM_CODEC = StreamCodec.composite(
        Ingredient.CONTENTS_STREAM_CODEC, c -> c.ingredient,
        ItemStackProvider.STREAM_CODEC, c -> c.result,
        DryingRecipe::new
    );

    public static final IndirectHashCollection<Item, DryingRecipe> CACHE = IndirectHashCollection.createForRecipe(DryingRecipe::getValidItems, FLRecipeTypes.DRYING);

    public DryingRecipe(Ingredient ingredient, ItemStackProvider result)
    {
        super(ingredient, result);
    }

    @Nullable
    public static DryingRecipe getRecipe(ItemStack input)
    {
        return RecipeHelpers.getRecipe(CACHE, input, input.getItem());
    }

    @Override
    public RecipeSerializer<?> getSerializer()
    {
        return FLRecipeSerializers.DRYING.get();
    }

    @Override
    public RecipeType<?> getType()
    {
        return FLRecipeTypes.DRYING.get();
    }
}
