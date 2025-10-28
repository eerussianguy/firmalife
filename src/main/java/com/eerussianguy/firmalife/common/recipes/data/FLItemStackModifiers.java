package com.eerussianguy.firmalife.common.recipes.data;

import com.eerussianguy.firmalife.FirmaLife;
import com.eerussianguy.firmalife.common.FLHelpers;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import net.dries007.tfc.TerraFirmaCraft;
import net.dries007.tfc.common.component.TFCComponents;
import net.dries007.tfc.common.recipes.outputs.ItemStackModifier;
import net.dries007.tfc.common.recipes.outputs.ItemStackModifierType;
import net.dries007.tfc.common.recipes.outputs.ItemStackModifiers;
import net.dries007.tfc.util.registry.RegistryHolder;

public class FLItemStackModifiers
{
    public static final DeferredRegister<ItemStackModifierType<?>> TYPES = DeferredRegister.create(ItemStackModifiers.KEY, FirmaLife.MOD_ID);

    public static final Id<AddPiePanModifier> ADD_PIE_PAN = register("add_pie_pan", AddPiePanModifier.INSTANCE);
    public static final Id<CopyDynamicFoodModifier> COPY_DYNAMIC_FOOD =register("copy_dynamic_food", CopyDynamicFoodModifier.INSTANCE);
    public static final Id<CopyBowlModifier> COPY_BOWL = register("copy_bowl", CopyBowlModifier.INSTANCE);
    public static final Id<EmptyPanModifier> EMPTY_PAN = register("empty_pan", EmptyPanModifier.INSTANCE);

    // Copied from TFC
    private static <T extends ItemStackModifier> FLItemStackModifiers.Id<T> register(String name, T singleInstance)
    {
        return new FLItemStackModifiers.Id<>(TYPES.register(name, () -> new ItemStackModifierType<>(MapCodec.unit(singleInstance), StreamCodec.unit(singleInstance))));
    }

    record Id<T extends ItemStackModifier>(DeferredHolder<ItemStackModifierType<?>, ItemStackModifierType<T>> holder)
        implements RegistryHolder<ItemStackModifierType<?>, ItemStackModifierType<T>> {}
}
