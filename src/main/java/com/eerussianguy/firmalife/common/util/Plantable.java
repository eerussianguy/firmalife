package com.eerussianguy.firmalife.common.util;

import java.util.List;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.ChatFormatting;
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

import net.dries007.tfc.client.ClientHelpers;
import net.dries007.tfc.common.component.food.FoodCapability;
import net.dries007.tfc.common.items.PlantableInfo;
import net.dries007.tfc.common.recipes.RecipeHelpers;
import net.dries007.tfc.util.collections.IndirectHashCollection;
import net.dries007.tfc.util.data.DataManager;
import net.dries007.tfc.world.Codecs;

import org.jetbrains.annotations.Nullable;

public record Plantable(Ingredient ingredient, PlanterType planter, int tier, int stages, float extraSeedChance, ItemStack seed, ItemStack crop, NutrientList nutrient, List<ResourceLocation> textures, List<ResourceLocation> specials)
{
    public record NutrientList(float nitrogen, float phosphorous, float potassium) {}

    public static final Codec<NutrientList> NUTRIENT_LIST_CODEC = RecordCodecBuilder.create(i -> i.group(
        Codec.FLOAT.fieldOf("nitrogen").forGetter(c -> c.nitrogen),
        Codec.FLOAT.fieldOf("phosphorous").forGetter(c -> c.phosphorous),
        Codec.FLOAT.fieldOf("potassium").forGetter(c -> c.potassium)
    ).apply(i, NutrientList::new));

    public static final StreamCodec<ByteBuf, NutrientList> NUTRIENT_LIST_STREAM_CODEC = StreamCodec.composite(
        ByteBufCodecs.FLOAT, c -> c.nitrogen,
        ByteBufCodecs.FLOAT, c -> c.phosphorous,
        ByteBufCodecs.FLOAT, c -> c.potassium,
        NutrientList::new
    );

    public static final Codec<Plantable> CODEC = RecordCodecBuilder.create(i -> i.group(
        Ingredient.CODEC_NONEMPTY.fieldOf("ingredient").forGetter(c -> c.ingredient),
        PlanterType.CODEC.fieldOf("planter").forGetter(c -> c.planter),
        Codecs.POSITIVE_INT.optionalFieldOf("tier", 0).forGetter(c -> c.tier),
        Codecs.POSITIVE_INT.optionalFieldOf("stages", 0).forGetter(c -> c.stages),
        Codecs.POSITIVE_FLOAT.optionalFieldOf("extra_seed_chance", 0.5f).forGetter(c -> c.extraSeedChance),
        ItemStack.CODEC.fieldOf("seed").forGetter(c -> c.seed),
        ItemStack.CODEC.fieldOf("crop").forGetter(c -> c.crop),
        NUTRIENT_LIST_CODEC.fieldOf("nutrient").forGetter(c -> c.nutrient),
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
        NUTRIENT_LIST_STREAM_CODEC, c -> c.nutrient,
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

    public NutrientList getNutrients()
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

    public void addTooltipInfo(List<Component> tooltip, @Nullable PlantableInfo pi)
    {
        tooltip.add(Component.translatable("firmalife.tooltip.planter_usable", FLHelpers.translateEnum(planter)));

        if (!ClientHelpers.hasShiftDown())
        {
            if (pi == null)
                tooltip.add(Component.translatable("tfc.tooltip.plantable.hold_shift").withStyle(ChatFormatting.GRAY, ChatFormatting.ITALIC));
            return;
        }
        final NutrientList fl = getNutrients();

        float tfcN = 0, tfcP = 0, tfcK = 0;
        boolean showN = true, showP = true, showK = true;
        if (pi != null)
        {
            // If TFC has nutrients, and they're different from our nutrients (they shouldn't be), we need to show the player that somehow
            // Note that Firmalife's nutrients always 'win' in the tooltip at the bottom
            // We show all 3 by default, however if we detect that the tfc and firmalife are the same, we hide (cause TFC shows theirs anyway)
            final PlantableInfo.PlantNutrients tfc = pi.getNutrientsInfo();
            if (tfc != null)
            {
                tfcN = tfc.nitrogen();
                tfcP = tfc.phosphorus();
                tfcK = tfc.potassium();
            }
            showN = !equals(tfcN, fl.nitrogen);
            showP = !equals(tfcP, fl.phosphorous);
            showK = !equals(tfcK, fl.potassium);
        }

        final float n = fl.nitrogen;
        final float p = fl.phosphorous;
        final float k = fl.potassium;
        if (showN || showP || showK)
        {
            final boolean consumesNutrients = n > 0 || p > 0 || k > 0;
            tooltip.add(Component.translatable("firmalife.tooltip.planter.nutrients").withStyle(ChatFormatting.GRAY));
            if (showN)
                tooltip.add(indent(Component.translatable("tfc.tooltip.fertilizer.nitrogen", formatNutrientAmount(n, consumesNutrients)), 1));
            if (showP)
                tooltip.add(indent(Component.translatable("tfc.tooltip.fertilizer.phosphorus", formatNutrientAmount(p, consumesNutrients)), 1));
            if (showK)
                tooltip.add(indent(Component.translatable("tfc.tooltip.fertilizer.potassium", formatNutrientAmount(k, consumesNutrients)), 1));
        }

    }

    private static boolean equals(float float1, float float2)
    {
        return Math.round(float1 * 100) == Math.round(float2 * 100);
    }

    private static Component indent(Component component, int amount)
    {
        return Component.literal(" ".repeat(amount)).append(component);
    }

    private static String formatNutrientAmount(float value, boolean consumesNutrients)
    {
        if (value < 0)
        {
            if (consumesNutrients)
            {
                return String.format("+%.0f", Math.abs(value) * 100);
            }
            // Crops that restore nutrients restore 30% of actual value if they do not consume nutrients
            return String.format("+%.0f", Math.abs(value) * 0.3 * 100);
        }
        return String.format("%.0f", value * 100);
    }
}
