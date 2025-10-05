package com.eerussianguy.firmalife.common.capabilities.wine;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

import com.eerussianguy.firmalife.common.FLHelpers;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

import net.dries007.tfc.common.component.food.FoodTrait;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.calendar.Calendars;
import net.dries007.tfc.util.climate.KoppenClimateClassification;

public record WineComponent(
    long creationDate,
    long openDate,
    Optional<String> labelText,
    WineType wineType,
    KoppenClimateClassification climate,
    List<Holder<FoodTrait>> traits
)
{
    public static final Codec<WineComponent> CODEC = RecordCodecBuilder.create(i -> i.group(
        Codec.LONG.fieldOf("creation_date").forGetter(c -> c.creationDate),
        Codec.LONG.fieldOf("open_date").forGetter(c -> c.openDate),
        Codec.STRING.optionalFieldOf("label_text").forGetter(c -> c.labelText),
        WineType.CODEC.fieldOf("wine").forGetter(c -> c.wineType),
        FLHelpers.KOPPEN_CODEC.fieldOf("climate").forGetter(c -> c.climate),
        FoodTrait.CODEC.listOf().fieldOf("traits").forGetter(c -> c.traits)
    ).apply(i, WineComponent::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, WineComponent> STREAM_CODEC = StreamCodec.composite(
        ByteBufCodecs.VAR_LONG, c -> c.creationDate,
        ByteBufCodecs.VAR_LONG, c -> c.openDate,
        ByteBufCodecs.optional(ByteBufCodecs.STRING_UTF8), c -> c.labelText,
        WineType.STREAM_CODEC, c -> c.wineType,
        FLHelpers.KOPPEN_STREAM_CODEC, c -> c.climate,
        FoodTrait.STREAM_CODEC.apply(ByteBufCodecs.list()), c -> c.traits,
        WineComponent::new
    );

    public static final WineComponent DEFAULT = new WineComponent(-1, -1, Optional.empty(), WineType.RED, KoppenClimateClassification.AF, Collections.emptyList());

    public static WineComponent opened(WineComponent w, long date)
    {
        return new WineComponent(w.creationDate, date, w.labelText, w.wineType, w.climate, List.copyOf(w.traits));
    }

    public boolean isSealed()
    {
        return openDate == -1;
    }

    public void acceptTooltipInfo(Consumer<Component> tooltip)
    {
        // todo: fluid container
//        final FluidStack contained = getFluidHandler().getFluidInTank(0);
//        if (contained.isEmpty())
//        {
//            tooltip.accept(Component.translatable("firmalife.wine.empty"));
//            labelText.ifPresent(t -> tooltip.accept(Component.literal(t)));
//            return;
//        }
        if (isSealed())
        {
            labelText.ifPresent(t -> tooltip.accept(Component.literal(t)));
            tooltip.accept(Component.translatable("firmalife.wine.age_time", Calendars.CLIENT.getTimeDelta(Calendars.CLIENT.getTicks() - creationDate)));
        }
        else
        {
            labelText.ifPresent(t -> tooltip.accept(Component.literal(t)));
            tooltip.accept(Component.translatable("firmalife.wine.age_time_opened", Calendars.CLIENT.getTimeDelta(openDate - creationDate)));
//            tooltip.accept(Tooltips.fluidUnits(contained.getAmount()).withStyle(ChatFormatting.ITALIC).withStyle(ChatFormatting.GRAY));
        }
        tooltip.accept(Helpers.translateEnum(climate).withStyle(ChatFormatting.DARK_GREEN).withStyle(ChatFormatting.ITALIC));

        traits.forEach(trait -> trait.value().addTooltipInfo(tooltip));
    }
}
