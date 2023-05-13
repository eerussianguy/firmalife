package com.eerussianguy.firmalife.common.util;

import java.util.Locale;
import com.eerussianguy.firmalife.common.FLHelpers;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.crafting.Ingredient;

public enum FLArmorMaterials implements ArmorMaterial
{
    BEEKEEPER(5, 1, 2, 3, 1, 0f, 0f, 0f, 0f, 3f);

    private final ResourceLocation serializedName;
    private final int feetDamage;
    private final int legDamage;
    private final int chestDamage;
    private final int headDamage;
    private final int feetReduction;
    private final int legReduction;
    private final int chestReduction;
    private final int headReduction;
    private final float toughness;
    private final float knockbackResistance;
    private final float crushingModifier;
    private final float piercingModifier;
    private final float slashingModifier;

    FLArmorMaterials(int armor, int feetReduction, int legReduction, int chestReduction, int headReduction, float toughness, float knockbackResistance, float crushingModifier, float piercingModifier, float slashingModifier)
    {
        this.serializedName = FLHelpers.identifier(this.name().toLowerCase(Locale.ROOT));
        this.feetDamage = 13 * armor;
        this.legDamage = 15 * armor;
        this.chestDamage = 16 * armor;
        this.headDamage = 11 * armor;
        this.feetReduction = feetReduction;
        this.legReduction = legReduction;
        this.chestReduction = chestReduction;
        this.headReduction = headReduction;
        this.toughness = toughness;
        this.knockbackResistance = knockbackResistance;
        this.crushingModifier = crushingModifier * 0.25F;
        this.piercingModifier = piercingModifier * 0.25F;
        this.slashingModifier = slashingModifier * 0.25F;
    }

    public float crushing()
    {
        return this.crushingModifier;
    }

    public float piercing()
    {
        return this.piercingModifier;
    }

    public float slashing()
    {
        return this.slashingModifier;
    }

    @Override
    public int getDurabilityForSlot(EquipmentSlot slot)
    {
        return switch (slot)
            {
                case FEET -> this.feetDamage;
                case LEGS -> this.legDamage;
                case CHEST -> this.chestDamage;
                case HEAD -> this.headDamage;
                default -> 0;
            };
    }

    @Override
    public int getDefenseForSlot(EquipmentSlot slot)
    {
        return switch (slot)
            {
                case FEET -> this.feetReduction;
                case LEGS -> this.legReduction;
                case CHEST -> this.chestReduction;
                case HEAD -> this.headReduction;
                default -> 0;
            };
    }

    @Override
    public int getEnchantmentValue()
    {
        return 0;
    }

    @Override
    public SoundEvent getEquipSound()
    {
        return SoundEvents.ARMOR_EQUIP_LEATHER;
    }

    public ResourceLocation getId()
    {
        return this.serializedName;
    }

    @Override
    public float getToughness()
    {
        return this.toughness;
    }

    @Override
    public float getKnockbackResistance()
    {
        return this.knockbackResistance;
    }

    @Override
    public Ingredient getRepairIngredient()
    {
        return Ingredient.EMPTY;
    }

    @Override
    public String getName()
    {
        return serializedName.toString();
    }
}
