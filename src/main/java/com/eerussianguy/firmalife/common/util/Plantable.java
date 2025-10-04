package com.eerussianguy.firmalife.common.util;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

import com.eerussianguy.firmalife.common.FLHelpers;
import com.eerussianguy.firmalife.common.blocks.greenhouse.PlanterType;
import net.dries007.tfc.common.blockentities.FarmlandBlockEntity;
import net.dries007.tfc.common.component.food.FoodCapability;
import net.dries007.tfc.common.recipes.RecipeHelpers;
import net.dries007.tfc.network.StreamCodecs;
import net.dries007.tfc.util.collections.IndirectHashCollection;
import net.dries007.tfc.util.data.DataManager;
import net.dries007.tfc.world.Codecs;

import org.jetbrains.annotations.Nullable;

public record Plantable(Ingredient ingredient, PlanterType planter, int tier, int stages, float extraSeedChance, ItemStack seed, ItemStack crop, FarmlandBlockEntity.NutrientType nutrient, List<ResourceLocation> textures, List<ResourceLocation> specials)
{
    private static final Map<String, FarmlandBlockEntity.NutrientType> NUTRIENT_MAP = Map.of("phosphorous", FarmlandBlockEntity.NutrientType.PHOSPHOROUS, "nitrogen", FarmlandBlockEntity.NutrientType.NITROGEN, "potassium", FarmlandBlockEntity.NutrientType.POTASSIUM);

    public static final Codec<FarmlandBlockEntity.NutrientType> NUTRIENT_CODEC = Codec.STRING.flatXmap(s -> {
            final var nut = NUTRIENT_MAP.get(s);
            if (nut != null)
            {
                return DataResult.success(FarmlandBlockEntity.NutrientType.valueOf(s.toUpperCase(Locale.ROOT)));
            }
            return DataResult.<FarmlandBlockEntity.NutrientType>error(() -> "Not a nutrient: " + s);
        }, e -> DataResult.success(e.name().toLowerCase(Locale.ROOT)));
    public static final StreamCodec<ByteBuf, FarmlandBlockEntity.NutrientType> NUTRIENT_STREAM_CODEC = StreamCodecs.forEnum(FarmlandBlockEntity.NutrientType::values);

    public static final Codec<Plantable> CODEC = RecordCodecBuilder.create(i -> i.group(
        Ingredient.CODEC_NONEMPTY.fieldOf("ingredient").forGetter(c -> c.ingredient),
        PlanterType.CODEC.fieldOf("planter").forGetter(c -> c.planter),
        Codecs.POSITIVE_INT.optionalFieldOf("tier", 0).forGetter(c -> c.tier),
        Codecs.POSITIVE_INT.optionalFieldOf("stages", 0).forGetter(c -> c.stages),
        Codecs.POSITIVE_FLOAT.optionalFieldOf("extra_seed_chance", 0.5f).forGetter(c -> c.extraSeedChance),
        ItemStack.CODEC.fieldOf("seed").forGetter(c -> c.seed),
        ItemStack.CODEC.fieldOf("crop").forGetter(c -> c.crop),
        NUTRIENT_CODEC.fieldOf("nutrient").forGetter(c -> c.nutrient),
        ResourceLocation.CODEC.listOf().fieldOf("textures").forGetter(c -> c.textures),
        ResourceLocation.CODEC.listOf().fieldOf("specials").forGetter(c -> c.specials)
    ).apply(i, Plantable::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, Plantable> STREAM_CODEC = FLHelpers.composite(
        Ingredient.CONTENTS_STREAM_CODEC, c -> c.ingredient,
        PlanterType.STREAM_CODEC, c -> c.planter,
        ByteBufCodecs.VAR_INT, c -> c.tier,
        ByteBufCodecs.VAR_INT, c -> c.stages,
        ByteBufCodecs.FLOAT, c -> c.extraSeedChance,
        ItemStack.STREAM_CODEC, c -> c.seed,
        ItemStack.STREAM_CODEC, c -> c.crop,
        NUTRIENT_STREAM_CODEC, c -> c.nutrient,
        ResourceLocation.STREAM_CODEC.apply(ByteBufCodecs.list()), c -> c.textures,
        ResourceLocation.STREAM_CODEC.apply(ByteBufCodecs.list()), c -> c.specials,
        Plantable::new
    );

    public static final DataManager<Plantable> MANAGER = new DataManager<>(FLHelpers.identifier("plantable"), CODEC, STREAM_CODEC);
    public static final IndirectHashCollection<Item, Plantable> CACHE = IndirectHashCollection.create(c -> RecipeHelpers.itemKeys(c.ingredient), MANAGER::getValues);


    @Nullable
    public static Plantable get(ItemStack stack)
    {
        if (stack.isEmpty()) return null;
        for (Plantable def : CACHE.getAll(stack.getItem()))
        {
            if (def.ingredient.test(stack))
            {
                return def;
            }
        }
        return null;
    }


    public ItemStack getSeed()
    {
        return seed.copy();
    }

    public ItemStack getCrop()
    {
        return FoodCapability.setCreatedNow(crop.copy());
    }

    public FarmlandBlockEntity.NutrientType getPrimaryNutrient()
    {
        return nutrient;
    }

    public ResourceLocation getSpecialTexture(int id)
    {
        return specials.get(id);
    }

    public ResourceLocation getTexture(int id)
    {
        return textures.get(id);
    }

    public ResourceLocation getTexture(float growth)
    {
        if (textures.size() == 2)
        {
            return growth >= 1f ? textures.get(1) : textures.get(0);
        }
        return textures.get(Mth.clamp((int) (growth * stages), 0, textures.size() - 1));
    }

    public void addTooltipInfo(List<Component> tooltip)
    {
        tooltip.add(Component.translatable("firmalife.tooltip.planter_usable", FLHelpers.translateEnum(planter)));
    }
}
