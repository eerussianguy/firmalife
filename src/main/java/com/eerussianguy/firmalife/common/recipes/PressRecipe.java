package com.eerussianguy.firmalife.common.recipes;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.common.recipes.RecipeHelpers;
import net.dries007.tfc.common.recipes.outputs.ItemStackProvider;
import net.dries007.tfc.util.collections.IndirectHashCollection;

public class PressRecipe extends StompingRecipe
{
    public static final MapCodec<PressRecipe> P_CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
        Ingredient.CODEC.fieldOf("ingredient").forGetter(c -> c.ingredient),
        ItemStackProvider.CODEC.fieldOf("result").forGetter(c -> c.result),
        ResourceLocation.CODEC.fieldOf("input_texture").forGetter(c -> c.inputTexture),
        ResourceLocation.CODEC.fieldOf("output_texture").forGetter(c -> c.outputTexture),
        BuiltInRegistries.SOUND_EVENT.byNameCodec().fieldOf("sound").forGetter(c -> c.sound)
    ).apply(instance, PressRecipe::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, PressRecipe> P_STREAM_CODEC = StreamCodec.composite(
        Ingredient.CONTENTS_STREAM_CODEC, c -> c.ingredient,
        ItemStackProvider.STREAM_CODEC, c -> c.result,
        ResourceLocation.STREAM_CODEC, c -> c.inputTexture,
        ResourceLocation.STREAM_CODEC, c -> c.outputTexture,
        ByteBufCodecs.registry(Registries.SOUND_EVENT), c -> c.sound,
        PressRecipe::new
    );

    public static final IndirectHashCollection<Item, PressRecipe> PRESS_CACHE = IndirectHashCollection.createForRecipe(PressRecipe::getValidItems, FLRecipeTypes.PRESS);

    public PressRecipe(Ingredient ingredient, ItemStackProvider result, ResourceLocation inputTexture, ResourceLocation outputTexture, SoundEvent sound)
    {
        super(ingredient, result, inputTexture, outputTexture, sound);
    }

    @Nullable
    public static PressRecipe getPressRecipe(ItemStack input)
    {
        return RecipeHelpers.getRecipe(PRESS_CACHE, input, input.getItem());
    }

    @Override
    public RecipeType<?> getType()
    {
        return FLRecipeTypes.PRESS.get();
    }

    @Override
    public RecipeSerializer<?> getSerializer()
    {
        return FLRecipeSerializers.PRESS.get();
    }
}
