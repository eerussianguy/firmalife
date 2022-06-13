package com.eerussianguy.firmalife.common.misc;

import java.util.List;

import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraftforge.common.loot.GlobalLootModifierSerializer;
import net.minecraftforge.common.loot.LootModifier;

import com.eerussianguy.firmalife.common.FLHelpers;
import net.dries007.tfc.common.capabilities.food.FoodCapability;
import net.dries007.tfc.util.JsonHelpers;
import org.jetbrains.annotations.NotNull;

public class AddItemModifier extends LootModifier
{
    private final ItemStack item;

    protected AddItemModifier(LootItemCondition[] conditions, ItemStack item)
    {
        super(conditions);
        this.item = FoodCapability.setStackNonDecaying(item);
    }

    @NotNull
    @Override
    protected List<ItemStack> doApply(List<ItemStack> loot, LootContext context)
    {
        loot.add(item.copy());
        return loot;
    }

    public static class Serializer extends GlobalLootModifierSerializer<AddItemModifier>
    {
        @Override
        public AddItemModifier read(ResourceLocation location, JsonObject json, LootItemCondition[] conditions)
        {
            return new AddItemModifier(conditions, JsonHelpers.getItemStack(json, "item"));
        }

        @Override
        public JsonObject write(AddItemModifier instance)
        {
            JsonObject json = makeConditions(instance.conditions);
            json.add("item", FLHelpers.codecToJson(ItemStack.CODEC, instance.item));
            return json;
        }
    }
}
