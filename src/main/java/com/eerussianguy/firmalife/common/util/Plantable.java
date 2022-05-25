package com.eerussianguy.firmalife.common.util;

import com.google.gson.JsonObject;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

import com.eerussianguy.firmalife.common.network.DMSPacket;
import net.dries007.tfc.util.DataManager;
import net.dries007.tfc.util.ItemDefinition;
import net.dries007.tfc.util.JsonHelpers;
import net.dries007.tfc.util.collections.IndirectHashCollection;
import org.jetbrains.annotations.Nullable;

public class Plantable extends ItemDefinition
{
    public static final DataManager<Plantable> MANAGER = new DataManager<>("plantable", "plantable", Plantable::new, Plantable::new, Plantable::encode, Plantable.Packet::new);
    public static final IndirectHashCollection<Item, Plantable> CACHE = IndirectHashCollection.create(Plantable::getValidItems, MANAGER::getValues);

    @Nullable
    public static Plantable get(ItemStack stack)
    {
        for (Plantable def : CACHE.getAll(stack.getItem()))
        {
            if (def.matches(stack))
            {
                return def;
            }
        }
        return null;
    }

    private final boolean large;
    private final int stages;

    private Plantable(ResourceLocation id, JsonObject json)
    {
        super(id, Ingredient.fromJson(JsonHelpers.get(json, "ingredient")));

        large = JsonHelpers.getAsBoolean(json, "large", false);
        stages = JsonHelpers.getAsInt(json, "stages");
    }

    private Plantable(ResourceLocation id, FriendlyByteBuf buffer)
    {
        super(id, Ingredient.fromNetwork(buffer));
        large = buffer.readBoolean();
        stages = buffer.readVarInt();
    }

    public void encode(FriendlyByteBuf buffer)
    {
        ingredient.toNetwork(buffer);
        buffer.writeBoolean(large);
        buffer.writeVarInt(stages);
    }

    public boolean isLarge()
    {
        return large;
    }

    public int getStages()
    {
        return stages;
    }

    public static class Packet extends DMSPacket<Plantable> {}
}
