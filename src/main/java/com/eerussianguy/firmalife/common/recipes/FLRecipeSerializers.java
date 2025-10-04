package com.eerussianguy.firmalife.common.recipes;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.neoforged.neoforge.registries.DeferredRegister;

import net.dries007.tfc.common.recipes.RecipeSerializerImpl;
import net.dries007.tfc.common.recipes.TFCRecipeSerializers.Id;

import static com.eerussianguy.firmalife.FirmaLife.MOD_ID;

public class FLRecipeSerializers
{
    public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZER = DeferredRegister.create(Registries.RECIPE_SERIALIZER, MOD_ID);

    public static final Id<DryingRecipe> DRYING = register("drying", DryingRecipe.CODEC, DryingRecipe.STREAM_CODEC);
    public static final Id<SmokingRecipe> SMOKING = register("smoking", SmokingRecipe.CODEC, SmokingRecipe.STREAM_CODEC);
    public static final Id<StompingRecipe> STOMPING = register("stomping", StompingRecipe.CODEC, StompingRecipe.STREAM_CODEC);
    public static final Id<MixingBowlRecipe> MIXING_BOWL = register("mixing_bowl", MixingBowlRecipe.CODEC, MixingBowlRecipe.STREAM_CODEC);
    public static final Id<OvenRecipe> OVEN = register("oven", OvenRecipe.CODEC, OvenRecipe.STREAM_CODEC);
    public static final Id<StinkySoupRecipe> STINKY_SOUP = register("stinky_soup", StinkySoupRecipe.CODEC, StinkySoupRecipe.STREAM_CODEC);
    public static final Id<BowlPotRecipe> BOWL_POT = register("bowl_pot", BowlPotRecipe.CODEC, BowlPotRecipe.STREAM_CODEC);
    public static final Id<VatRecipe> VAT = register("vat", VatRecipe.CODEC, VatRecipe.STREAM_CODEC);
    public static final Id<PressRecipe> PRESS = register("press", PressRecipe.P_CODEC, PressRecipe.P_STREAM_CODEC);

    private static <R extends Recipe<?>> Id<R> register(String name, MapCodec<R> codec, StreamCodec<RegistryFriendlyByteBuf, R> stream)
    {
        return register(name, new RecipeSerializerImpl<>(codec, stream));
    }

    private static <R extends Recipe<?>> Id<R> register(String name, RecipeSerializer<R> serializer)
    {
        return new Id<>(RECIPE_SERIALIZER.register(name, () -> serializer));
    }
}
