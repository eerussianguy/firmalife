package com.eerussianguy.firmalife.common.recipes;

import java.util.function.Supplier;

import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import net.dries007.tfc.common.recipes.SimpleItemRecipe;

import static com.eerussianguy.firmalife.FirmaLife.MOD_ID;

public class FLRecipeSerializers
{
    public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS = DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, MOD_ID);

    public static final RegistryObject<SimpleItemRecipe.Serializer<DryingRecipe>> DRYING = register("drying", () -> new SimpleItemRecipe.Serializer<>(DryingRecipe::new));
    public static final RegistryObject<SimpleItemRecipe.Serializer<SmokingRecipe>> SMOKING = register("smoking", () -> new SimpleItemRecipe.Serializer<>(SmokingRecipe::new));
    public static final RegistryObject<MixingBowlRecipe.Serializer> MIXING_BOWL = register("mixing_bowl", MixingBowlRecipe.Serializer::new);
    public static final RegistryObject<OvenRecipe.Serializer> OVEN = register("oven", OvenRecipe.Serializer::new);
    public static final RegistryObject<StinkySoupRecipe.Serializer> STINKY_SOUP = register("stinky_soup", StinkySoupRecipe.Serializer::new);
    public static final RegistryObject<VatRecipe.Serializer> VAT = register("vat", VatRecipe.Serializer::new);

    private static <S extends RecipeSerializer<?>> RegistryObject<S> register(String name, Supplier<S> factory)
    {
        return RECIPE_SERIALIZERS.register(name, factory);
    }
}
