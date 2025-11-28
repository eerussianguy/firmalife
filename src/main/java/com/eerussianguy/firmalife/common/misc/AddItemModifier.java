package com.eerussianguy.firmalife.common.misc;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

import net.neoforged.neoforge.common.loot.IGlobalLootModifier;
import net.neoforged.neoforge.common.loot.LootModifier;

import net.dries007.tfc.common.component.food.FoodCapability;

public class AddItemModifier extends LootModifier
{
    public static final MapCodec<AddItemModifier> CODEC = RecordCodecBuilder.mapCodec(instance -> codecStart(instance)
        .and(ItemStack.CODEC.fieldOf("item").forGetter(c -> c.item))
        .and(Codec.FLOAT.optionalFieldOf("chance", 1f).forGetter(c -> c.chance)
    ).apply(instance, AddItemModifier::new));

    private final ItemStack item;
    private final float chance;

    public AddItemModifier(LootItemCondition[] conditions, ItemStack item, float chance)
    {
        super(conditions);
        this.item = FoodCapability.setTransientNonDecaying(item);
        this.chance = chance;
    }

    @Override
    public MapCodec<? extends IGlobalLootModifier> codec()
    {
        return CODEC;
    }

    @Override
    protected ObjectArrayList<ItemStack> doApply(ObjectArrayList<ItemStack> loot, LootContext context)
    {
        if (context.hasParam(LootContextParams.TOOL))
        {
            final Holder.Reference<Enchantment> enchantment = context.getLevel().registryAccess().lookupOrThrow(Registries.ENCHANTMENT).get(Enchantments.SILK_TOUCH).orElseThrow();
            if (EnchantmentHelper.getEnchantmentsForCrafting(context.getParam(LootContextParams.TOOL)).getLevel(enchantment) > 0)
            {
                return loot;
            }
        }
        if (context.getRandom().nextFloat() < chance)
        {
            loot.add(item.copy());
        }
        return loot;
    }
}
