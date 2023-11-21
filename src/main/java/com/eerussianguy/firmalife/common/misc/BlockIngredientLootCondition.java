package com.eerussianguy.firmalife.common.misc;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;

import net.dries007.tfc.common.recipes.ingredients.BlockIngredient;

public record BlockIngredientLootCondition(BlockIngredient ingredient) implements LootItemCondition
{

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

    public static class Serializer implements net.minecraft.world.level.storage.loot.Serializer<BlockIngredientLootCondition>
    {
        @Override
        public void serialize(JsonObject json, BlockIngredientLootCondition condition, JsonSerializationContext context)
        {
            // impossible...
        }

        @Override
        public BlockIngredientLootCondition deserialize(JsonObject json, JsonDeserializationContext context)
        {
            return new BlockIngredientLootCondition(BlockIngredient.fromJson(json.get("ingredient")));
        }
    }
}
