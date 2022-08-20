package com.eerussianguy.firmalife.common.misc;

import java.util.function.Supplier;

import net.minecraft.core.Registry;
import net.minecraft.world.level.storage.loot.Serializer;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;
import net.minecraftforge.common.loot.GlobalLootModifierSerializer;
import net.minecraftforge.common.loot.IGlobalLootModifier;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import static com.eerussianguy.firmalife.FirmaLife.MOD_ID;

public class FLLoot
{
    public static final DeferredRegister<GlobalLootModifierSerializer<?>> MODIFIER_SERIALIZERS = DeferredRegister.create(ForgeRegistries.Keys.LOOT_MODIFIER_SERIALIZERS, MOD_ID);
    public static final DeferredRegister<LootItemConditionType> CONDITIONS = DeferredRegister.create(Registry.LOOT_ITEM_REGISTRY, MOD_ID);

    public static final RegistryObject<LootItemConditionType> BLOCK_INGREDIENT = lootCondition("block_ingredient", new BlockIngredientLootCondition.Serializer());
    public static final RegistryObject<AddItemModifier.Serializer> ADD_ITEM = glmSerializer("add_item", AddItemModifier.Serializer::new);

    private static RegistryObject<LootItemConditionType> lootCondition(String id, Serializer<? extends LootItemCondition> serializer)
    {
        return CONDITIONS.register(id, () -> new LootItemConditionType(serializer));
    }

    private static <T extends GlobalLootModifierSerializer<? extends IGlobalLootModifier>> RegistryObject<T> glmSerializer(String id, Supplier<T> modifier)
    {
        return MODIFIER_SERIALIZERS.register(id, modifier);
    }

    public static void registerAll(IEventBus bus)
    {
        CONDITIONS.register(bus);
        MODIFIER_SERIALIZERS.register(bus);
    }
}
