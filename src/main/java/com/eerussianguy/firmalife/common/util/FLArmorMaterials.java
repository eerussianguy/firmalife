package com.eerussianguy.firmalife.common.util;

import java.util.EnumMap;
import java.util.List;
import com.eerussianguy.firmalife.FirmaLife;
import com.eerussianguy.firmalife.common.FLHelpers;
import net.minecraft.Util;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.registries.DeferredRegister;

import net.dries007.tfc.common.TFCArmorMaterials;
import net.dries007.tfc.util.Helpers;

public final class FLArmorMaterials
{
    public static final DeferredRegister<ArmorMaterial> ARMOR_MATERIAL = DeferredRegister.create(Registries.ARMOR_MATERIAL, FirmaLife.MOD_ID);

    public static final TFCArmorMaterials.Id BEEKEEPER = register("beekeeper", SoundEvents.ARMOR_EQUIP_LEATHER, 160, 200, 215, 150, 1, 2, 3, 1, 15, 0f, 0f);

    private static TFCArmorMaterials.Id register(
        String name,
        Holder<SoundEvent> equipSound,
        int feetDamage, int legDamage, int chestDamage, int headDamage,
        int feetReduction, int legReduction, int chestReduction, int headReduction,
        int enchantability, float toughness, float knockbackResistance
    ) {
        return new TFCArmorMaterials.Id(ARMOR_MATERIAL.register(name, () -> new ArmorMaterial(
            Util.make(new EnumMap<>(ArmorItem.Type.class), map -> {
                map.put(ArmorItem.Type.BOOTS, feetReduction);
                map.put(ArmorItem.Type.LEGGINGS, legReduction);
                map.put(ArmorItem.Type.CHESTPLATE, chestReduction);
                map.put(ArmorItem.Type.HELMET, headReduction);
                map.put(ArmorItem.Type.BODY, chestReduction);
            }),
            enchantability,
            equipSound,
            () -> Ingredient.EMPTY,
            List.of(new ArmorMaterial.Layer(FLHelpers.identifier(name))),
            toughness,
            knockbackResistance
        )), feetDamage, legDamage, chestDamage, headDamage);
    }

}
