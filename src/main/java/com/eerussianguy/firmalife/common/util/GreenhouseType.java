package com.eerussianguy.firmalife.common.util;

import com.google.gson.JsonObject;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import com.eerussianguy.firmalife.common.FLHelpers;
import net.dries007.tfc.common.recipes.ingredients.BlockIngredient;
import net.dries007.tfc.network.DataManagerSyncPacket;
import net.dries007.tfc.util.DataManager;
import net.dries007.tfc.util.JsonHelpers;
import net.dries007.tfc.util.collections.IndirectHashCollection;
import org.jetbrains.annotations.Nullable;

public class GreenhouseType
{
    public static final DataManager<GreenhouseType> MANAGER = new DataManager<>(FLHelpers.identifier("greenhouse"), "greenhouse", GreenhouseType::new, GreenhouseType::new, GreenhouseType::encode, Packet::new);
    public static final IndirectHashCollection<Block, GreenhouseType> CACHE = IndirectHashCollection.create(s -> s.ingredient.blocks(), MANAGER::getValues);

    @Nullable
    public static GreenhouseType get(BlockState state)
    {
        for (GreenhouseType def : CACHE.getAll(state.getBlock()))
        {
            if (def.ingredient.test(state))
            {
                return def;
            }
        }
        return null;
    }

    @Nullable
    public static GreenhouseType get(ResourceLocation id)
    {
        for (GreenhouseType def : MANAGER.getValues())
        {
            if (def.id == id)
            {
                return def;
            }
        }
        return null;
    }

    public final ResourceLocation id;
    public final BlockIngredient ingredient;
    public final int tier;
    private final String translationKey;

    private GreenhouseType(ResourceLocation id, JsonObject json)
    {
        this.id = id;
        this.ingredient = BlockIngredient.fromJson(JsonHelpers.get(json, "ingredient"));
        this.tier = JsonHelpers.getAsInt(json, "tier");
        this.translationKey = "greenhouse." + id.getNamespace() + "." + id.getPath();
    }

    private GreenhouseType(ResourceLocation id, FriendlyByteBuf buffer)
    {
        this.id = id;
        this.ingredient = BlockIngredient.fromNetwork(buffer);
        this.tier = buffer.readVarInt();
        this.translationKey = "greenhouse." + id.getNamespace() + "." + id.getPath();
    }

    public void encode(FriendlyByteBuf buffer)
    {
        ingredient.toNetwork(buffer);
        buffer.writeVarInt(tier);
    }

    public Component getTitle()
    {
        return Component.translatable(translationKey);
    }

    public static class Packet extends DataManagerSyncPacket<GreenhouseType> { }
}
