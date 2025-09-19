package com.eerussianguy.firmalife.common.misc;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.loot.IGlobalLootModifier;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import net.dries007.tfc.util.loot.TFCLoot;
import net.dries007.tfc.util.registry.RegistryHolder;

import static com.eerussianguy.firmalife.FirmaLife.MOD_ID;

public class FLLoot
{
    public static final DeferredRegister<MapCodec<? extends IGlobalLootModifier>> MODIFIER_SERIALIZER = DeferredRegister.create(NeoForgeRegistries.Keys.GLOBAL_LOOT_MODIFIER_SERIALIZERS, MOD_ID);
    public static final DeferredRegister<LootItemConditionType> CONDITION = DeferredRegister.create(Registries.LOOT_CONDITION_TYPE, MOD_ID);

    public static final TFCLoot.Id<LootItemConditionType> BLOCK_INGREDIENT = lootCondition("block_ingredient", BlockIngredientLootCondition.CODEC);
    public static final GLMId<AddItemModifier> ADD_ITEM = glmSerializer("add_item", AddItemModifier.CODEC);

    private static <T extends IGlobalLootModifier> GLMId<T> glmSerializer(String id, MapCodec<T> modifier)
    {
        return new GLMId<>(MODIFIER_SERIALIZER.register(id, () -> modifier));
    }

    private static TFCLoot.Id<LootItemConditionType> lootCondition(String id, MapCodec<? extends LootItemCondition> codec)
    {
        return new TFCLoot.Id<>(CONDITION.register(id, () -> new LootItemConditionType(codec)));
    }

    public record GLMId<T extends IGlobalLootModifier>(DeferredHolder<MapCodec<? extends IGlobalLootModifier>, MapCodec<T>> holder)
        implements RegistryHolder<MapCodec<? extends IGlobalLootModifier>, MapCodec<T>> {}

    public static void registerAll(IEventBus bus)
    {
        CONDITION.register(bus);
        MODIFIER_SERIALIZER.register(bus);
    }
}
