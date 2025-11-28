package com.eerussianguy.firmalife.providers;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import com.eerussianguy.firmalife.FirmaLife;
import com.eerussianguy.firmalife.common.FLTags;
import com.eerussianguy.firmalife.common.items.FLItems;
import com.eerussianguy.firmalife.common.misc.AddItemModifier;
import com.eerussianguy.firmalife.common.misc.BlockIngredientLootCondition;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemEntityPropertyCondition;
import net.neoforged.neoforge.common.data.GlobalLootModifierProvider;

import net.dries007.tfc.common.recipes.ingredients.BlockIngredient;

public class BuiltinLootModifiers extends GlobalLootModifierProvider
{
    public BuiltinLootModifiers(PackOutput output, CompletableFuture<HolderLookup.Provider> lookup)
    {
        super(output, lookup, FirmaLife.MOD_ID);
    }

    @Override
    protected void start()
    {
        dropTagged("fruit_leaf", FLTags.Blocks.DROPS_FRUIT_LEAF, FLItems.FRUIT_LEAF.get().getDefaultInstance(), 0.5f);
        dropTagged("ice_shavings", FLTags.Blocks.DROPS_ICE_SHAVINGS, FLItems.ICE_SHAVINGS.get().getDefaultInstance(), 1f);
        dropEntity("rennet", FLTags.Entities.DROPS_RENNET, new ItemStack(FLItems.RENNET, 4), 1f);
        dropEntity("more_rennet", FLTags.Entities.DROPS_MORE_RENNET, new ItemStack(FLItems.RENNET, 6), 1f);
    }

    private void dropTagged(String name, TagKey<Block> tag, ItemStack drop, float chance)
    {
        add(
            name,
            new AddItemModifier(new LootItemCondition[]{
                new BlockIngredientLootCondition(BlockIngredient.of(tag))
            }, drop, chance)
        );
    }

    private void dropEntity(String name, TagKey<EntityType<?>> tag, ItemStack drop, float chance)
    {
        add(
            name,
            new AddItemModifier(new LootItemCondition[]{
                new LootItemEntityPropertyCondition(Optional.of(new EntityPredicate.Builder().of(tag).build()), LootContext.EntityTarget.THIS)
            }, drop, chance)
        );
    }

}
