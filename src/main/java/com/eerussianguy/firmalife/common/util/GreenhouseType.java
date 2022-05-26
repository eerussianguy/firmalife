package com.eerussianguy.firmalife.common.util;

import com.google.gson.JsonObject;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import com.eerussianguy.firmalife.common.network.DMSPacket;
import net.dries007.tfc.common.recipes.ingredients.BlockIngredient;
import net.dries007.tfc.common.recipes.ingredients.BlockIngredients;
import net.dries007.tfc.util.DataManager;
import net.dries007.tfc.util.JsonHelpers;
import net.dries007.tfc.util.collections.IndirectHashCollection;
import org.jetbrains.annotations.Nullable;

public class GreenhouseType
{
    public static final DataManager<GreenhouseType> MANAGER = new DataManager<>("greenhouse", "greenhouse", GreenhouseType::new, GreenhouseType::new, GreenhouseType::encode, Packet::new);
    public static final IndirectHashCollection<Block, GreenhouseType> CACHE = IndirectHashCollection.create(s -> s.ingredient.getValidBlocks(), MANAGER::getValues);

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

    private GreenhouseType(ResourceLocation id, JsonObject json)
    {
        this.id = id;
        this.ingredient = BlockIngredients.fromJson(JsonHelpers.get(json, "ingredient"));
        this.tier = JsonHelpers.getAsInt(json, "tier");
    }

    private GreenhouseType(ResourceLocation id, FriendlyByteBuf buffer)
    {
        this.id = id;
        this.ingredient = BlockIngredients.fromNetwork(buffer);
        this.tier = buffer.readVarInt();
    }

    public void encode(FriendlyByteBuf buffer)
    {
        ingredient.toNetwork(buffer);
        buffer.writeVarInt(tier);
    }

    public static class Packet extends DMSPacket<GreenhouseType> { }
}
