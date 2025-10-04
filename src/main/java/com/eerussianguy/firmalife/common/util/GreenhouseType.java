package com.eerussianguy.firmalife.common.util;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import com.eerussianguy.firmalife.common.FLHelpers;
import net.dries007.tfc.common.recipes.ingredients.BlockIngredient;
import net.dries007.tfc.util.collections.IndirectHashCollection;
import net.dries007.tfc.util.data.DataManager;
import net.dries007.tfc.world.Codecs;

import org.jetbrains.annotations.Nullable;

public record GreenhouseType(BlockIngredient ingredient, int tier, Component translationKey)
{
    public static final Codec<GreenhouseType> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        BlockIngredient.CODEC.fieldOf("ingredient").forGetter(c -> c.ingredient),
        Codecs.POSITIVE_INT.fieldOf("tier").forGetter(c -> c.tier),
        ComponentSerialization.CODEC.fieldOf("translation_key").forGetter(c -> c.translationKey)
    ).apply(instance, GreenhouseType::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, GreenhouseType> STREAM_CODEC = StreamCodec.composite(
        BlockIngredient.STREAM_CODEC, GreenhouseType::ingredient,
        ByteBufCodecs.VAR_INT, GreenhouseType::tier,
        ComponentSerialization.STREAM_CODEC, GreenhouseType::translationKey,
        GreenhouseType::new
    );

    public static final DataManager<GreenhouseType> MANAGER = new DataManager<>(FLHelpers.identifier("greenhouse"), CODEC, STREAM_CODEC);
    public static final IndirectHashCollection<Block, GreenhouseType> CACHE = IndirectHashCollection.create(s -> s.ingredient.blocks(), MANAGER::getValues);

    @Nullable
    public static GreenhouseType get(BlockState state)
    {
        for (GreenhouseType def : CACHE.getAll(state.getBlock()))
        {
            if (def.ingredient.test(state))
            {
                return def;
            }
        }
        return null;
    }

    public Component getTitle()
    {
        return translationKey;
    }
}
