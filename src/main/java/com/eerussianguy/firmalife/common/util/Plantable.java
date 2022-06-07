package com.eerussianguy.firmalife.common.util;

import com.google.gson.JsonObject;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

import com.eerussianguy.firmalife.common.FLHelpers;
import com.eerussianguy.firmalife.common.blocks.greenhouse.PlanterType;
import net.dries007.tfc.common.blockentities.FarmlandBlockEntity;
import net.dries007.tfc.network.DataManagerSyncPacket;
import net.dries007.tfc.util.DataManager;
import net.dries007.tfc.util.ItemDefinition;
import net.dries007.tfc.util.JsonHelpers;
import net.dries007.tfc.util.collections.IndirectHashCollection;
import org.jetbrains.annotations.Nullable;

public class Plantable extends ItemDefinition
{
    public static final DataManager<Plantable> MANAGER = new DataManager<>(FLHelpers.identifier("plantable"), "plantable", Plantable::new, Plantable::new, Plantable::encode, Plantable.Packet::new);
    public static final IndirectHashCollection<Item, Plantable> CACHE = IndirectHashCollection.create(Plantable::getValidItems, MANAGER::getValues);

    @Nullable
    public static Plantable get(ItemStack stack)
    {
        if (stack.isEmpty()) return null;
        for (Plantable def : CACHE.getAll(stack.getItem()))
        {
            if (def.matches(stack))
            {
                return def;
            }
        }
        return null;
    }

    @Nullable
    public static Plantable get(ResourceLocation id)
    {
        for (Plantable def : MANAGER.getValues())
        {
            if (def.id == id)
            {
                return def;
            }
        }
        return null;
    }

    private final PlanterType planter;
    private final int tier;
    private final int stages;
    private final float extraSeedChance;
    private final ItemStack seed;
    private final ItemStack crop;
    private final FarmlandBlockEntity.NutrientType nutrient;
    private final String texture;

    private Plantable(ResourceLocation id, JsonObject json)
    {
        super(id, Ingredient.fromJson(JsonHelpers.get(json, "ingredient")));

        planter = JsonHelpers.getEnum(json, "planter", PlanterType.class, PlanterType.QUAD);
        tier = JsonHelpers.getAsInt(json, "tier", 0);
        stages = JsonHelpers.getAsInt(json, "stages", 0);
        extraSeedChance = JsonHelpers.getAsFloat(json, "extra_seed_chance", 0.5f);
        seed = JsonHelpers.getItemStack(json, "seed");
        crop = JsonHelpers.getItemStack(json, "crop");
        nutrient = JsonHelpers.getEnum(json, "nutrient", FarmlandBlockEntity.NutrientType.class, FarmlandBlockEntity.NutrientType.NITROGEN);
        texture = JsonHelpers.getAsString(json, "texture");
    }

    private Plantable(ResourceLocation id, FriendlyByteBuf buffer)
    {
        super(id, Ingredient.fromNetwork(buffer));
        planter = buffer.readEnum(PlanterType.class);
        tier = buffer.readVarInt();
        stages = buffer.readVarInt();
        extraSeedChance = buffer.readFloat();
        seed = buffer.readItem();
        crop = buffer.readItem();
        nutrient = buffer.readEnum(FarmlandBlockEntity.NutrientType.class);
        texture = buffer.readUtf();
    }

    public void encode(FriendlyByteBuf buffer)
    {
        ingredient.toNetwork(buffer);
        buffer.writeEnum(planter);
        buffer.writeVarInt(tier);
        buffer.writeVarInt(stages);
        buffer.writeFloat(extraSeedChance);
        buffer.writeItem(seed);
        buffer.writeItem(crop);
        buffer.writeEnum(nutrient);
        buffer.writeUtf(texture);
    }

    public PlanterType getPlanterType()
    {
        return planter;
    }

    public int getStages()
    {
        return stages;
    }

    public ItemStack getSeed()
    {
        return seed.copy();
    }

    public ItemStack getCrop()
    {
        return crop.copy();
    }

    public FarmlandBlockEntity.NutrientType getPrimaryNutrient()
    {
        return nutrient;
    }

    public int getTier()
    {
        return tier;
    }

    public String getTextureLocation()
    {
        return texture;
    }

    public float getExtraSeedChance()
    {
        return extraSeedChance;
    }

    public ResourceLocation getTexture(float growth)
    {
        return new ResourceLocation(texture + "_" + (int) (growth * stages));
    }

    public static class Packet extends DataManagerSyncPacket<Plantable> {}
}
