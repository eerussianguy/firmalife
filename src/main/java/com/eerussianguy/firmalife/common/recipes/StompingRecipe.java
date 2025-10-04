package com.eerussianguy.firmalife.common.recipes;

import com.mojang.serialization.Codec;
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

import net.dries007.tfc.common.recipes.ItemRecipe;
import net.dries007.tfc.common.recipes.RecipeHelpers;
import net.dries007.tfc.common.recipes.outputs.ItemStackProvider;
import net.dries007.tfc.util.collections.IndirectHashCollection;

public class StompingRecipe extends ItemRecipe
{
    public static final MapCodec<StompingRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
        Ingredient.CODEC.fieldOf("ingredient").forGetter(c -> c.ingredient),
        ItemStackProvider.CODEC.fieldOf("result").forGetter(c -> c.result),
        ResourceLocation.CODEC.fieldOf("input_texture").forGetter(c -> c.inputTexture),
        ResourceLocation.CODEC.fieldOf("output_texture").forGetter(c -> c.outputTexture),
        BuiltInRegistries.SOUND_EVENT.byNameCodec().fieldOf("sound").forGetter(c -> c.sound)
    ).apply(instance, StompingRecipe::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, StompingRecipe> STREAM_CODEC = StreamCodec.composite(
        Ingredient.CONTENTS_STREAM_CODEC, c -> c.ingredient,
        ItemStackProvider.STREAM_CODEC, c -> c.result,
        ResourceLocation.STREAM_CODEC, c -> c.inputTexture,
        ResourceLocation.STREAM_CODEC, c -> c.outputTexture,
        ByteBufCodecs.registry(Registries.SOUND_EVENT), c -> c.sound,
        StompingRecipe::new
    );

    public static final IndirectHashCollection<Item, StompingRecipe> CACHE = IndirectHashCollection.createForRecipe(StompingRecipe::getValidItems, FLRecipeTypes.STOMPING);

    protected final ResourceLocation inputTexture;
    protected final ResourceLocation outputTexture;
    protected final SoundEvent sound;

    public StompingRecipe(Ingredient ingredient, ItemStackProvider result, ResourceLocation inputTexture, ResourceLocation outputTexture, SoundEvent sound)
    {
        super(ingredient, result);
        this.inputTexture = inputTexture;
        this.outputTexture = outputTexture;
        this.sound = sound;
    }

    @Nullable
    public static StompingRecipe getRecipe(ItemStack input)
    {
        return RecipeHelpers.getRecipe(CACHE, input, input.getItem());
    }

    public ResourceLocation getInputTexture()
    {
        return inputTexture;
    }

    public ResourceLocation getOutputTexture()
    {
        return outputTexture;
    }

    public SoundEvent getSound()
    {
        return sound;
    }

    @Override
    public RecipeSerializer<?> getSerializer()
    {
        return FLRecipeSerializers.STOMPING.get();
    }

    @Override
    public RecipeType<?> getType()
    {
        return FLRecipeTypes.STOMPING.get();
    }

}
