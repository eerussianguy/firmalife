package com.eerussianguy.firmalife.common.misc;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;

import net.dries007.tfc.common.recipes.ingredients.BlockIngredient;

public record BlockIngredientLootCondition(BlockIngredient ingredient) implements LootItemCondition
{
    public static final MapCodec<BlockIngredientLootCondition> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
        BlockIngredient.CODEC.fieldOf("ingredient").forGetter(e -> e.ingredient)
    ).apply(instance, BlockIngredientLootCondition::new));

    @Override
    public LootItemConditionType getType()
    {
        return FLLoot.BLOCK_INGREDIENT.get();
    }

    @Override
    public boolean test(LootContext lootContext)
    {
        BlockState state = lootContext.getParamOrNull(LootContextParams.BLOCK_STATE);
        return state != null && ingredient.test(state);
    }

}
