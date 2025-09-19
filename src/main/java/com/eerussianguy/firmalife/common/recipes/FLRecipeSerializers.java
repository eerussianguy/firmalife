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

    public static final Id<SimpleItemRecipe.Serializer<DryingRecipe>> DRYING = register("drying", () -> new SimpleItemRecipe.Serializer<>(DryingRecipe::new));
    public static final Id<SimpleItemRecipe.Serializer<SmokingRecipe>> SMOKING = register("smoking", () -> new SimpleItemRecipe.Serializer<>(SmokingRecipe::new));
    public static final Id<StompingRecipe.StompingSerializer> STOMPING = register("stomping", StompingRecipe.StompingSerializer::new);
    public static final Id<MixingBowlRecipe.Serializer> MIXING_BOWL = register("mixing_bowl", MixingBowlRecipe.Serializer::new);
    public static final Id<OvenRecipe.Serializer> OVEN = register("oven", OvenRecipe.Serializer::new);
    public static final Id<StinkySoupRecipe.Serializer> STINKY_SOUP = register("stinky_soup", StinkySoupRecipe.Serializer::new);
    public static final Id<BowlPotRecipe.Serializer> BOWL_POT = register("bowl_pot", BowlPotRecipe.Serializer::new);
    public static final Id<VatRecipe.Serializer> VAT = register("vat", VatRecipe.Serializer::new);
    public static final Id<StompingRecipe.StompingSerializer> PRESS = register("press", PressRecipe.StompingSerializer::new);

    private static <R extends Recipe<?>> Id<R> register(String name, MapCodec<R> codec, StreamCodec<RegistryFriendlyByteBuf, R> stream)
    {
        return register(name, new RecipeSerializerImpl<>(codec, stream));
    }

    private static <R extends Recipe<?>> Id<R> register(String name, RecipeSerializer<R> serializer)
    {
        return new Id<>(RECIPE_SERIALIZER.register(name, () -> serializer));
    }
}
