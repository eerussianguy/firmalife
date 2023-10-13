package com.eerussianguy.firmalife.common.misc;

import java.util.Map;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraftforge.common.loot.IGlobalLootModifier;
import net.minecraftforge.common.loot.LootModifier;

import net.dries007.tfc.common.capabilities.food.FoodCapability;
import org.jetbrains.annotations.NotNull;

public class AddItemModifier extends LootModifier
{
    public static final Codec<AddItemModifier> CODEC = RecordCodecBuilder.create(instance -> codecStart(instance)
        .and(ItemStack.CODEC.fieldOf("item").forGetter(c -> c.item))
        .and(Codec.FLOAT.fieldOf("chance").forGetter(c -> c.chance)
    ).apply(instance, AddItemModifier::new));

    private final ItemStack item;
    private final float chance;

    protected AddItemModifier(LootItemCondition[] conditions, ItemStack item, float chance)
    {
        super(conditions);
        this.item = FoodCapability.setStackNonDecaying(item);
        this.chance = chance;
    }

    @Override
    public Codec<? extends IGlobalLootModifier> codec()
    {
        return FLLoot.ADD_ITEM.get();
    }

    @NotNull
    @Override
    protected ObjectArrayList<ItemStack> doApply(ObjectArrayList<ItemStack> loot, LootContext context)
    {
        if (context.hasParam(LootContextParams.TOOL))
        {
            final Map<Enchantment, Integer> enchants = EnchantmentHelper.deserializeEnchantments(context.getParam(LootContextParams.TOOL).getEnchantmentTags());
            if (enchants.containsKey(Enchantments.SILK_TOUCH))
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
