package com.eerussianguy.firmalife.compat.patchouli;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextColor;
import org.jetbrains.annotations.Nullable;
import vazkii.patchouli.api.PatchouliAPI;

import com.eerussianguy.firmalife.common.blocks.plant.FLFruitBlocks;
import com.eerussianguy.firmalife.common.items.FLFoodTraits;
import com.eerussianguy.firmalife.common.util.FLClimateRanges;
import com.eerussianguy.firmalife.config.FLConfig;
import net.dries007.tfc.common.component.heat.Heat;
import net.dries007.tfc.config.TFCConfig;
import net.dries007.tfc.config.TemperatureDisplayStyle;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.calendar.Calendars;
import net.dries007.tfc.util.calendar.ICalendar;
import net.dries007.tfc.util.climate.ClimateRange;
import net.dries007.tfc.util.data.DataManager;
import net.dries007.tfc.util.tooltip.Tooltips;

/**
 * Registers the {@code $(flcfg:<name>)} field guide text function, which substitutes a value derived from the
 * Firmalife server config, or from a {@link ClimateRange}, into book text. This is the Firmalife equivalent of TFC's
 * {@code $(cfg:<name>)}, which cannot be extended by other mods.
 * <p>
 * {@link #VALUES} is the set of names the book is allowed to reference. Any name used in {@code generate_book.py}
 * must appear here.
 */
public final class FLConfigTextFunction
{
    public static final String NAME = "flcfg";

    private static final Map<String, Supplier<Component>> VALUES = buildValues();

    private static Map<String, Supplier<Component>> buildValues()
    {
        final Map<String, Supplier<Component>> map = new HashMap<>();

        map.put("cheeseAgedDays", () -> days(FLConfig.SERVER.cheeseAgedDays.get()));
        map.put("cheeseVintageDays", () -> days(FLConfig.SERVER.cheeseVintageDays.get()));
        map.put("dryingTicks", () -> duration(FLConfig.SERVER.dryingTicks.get()));
        map.put("solarDryingTicks", () -> duration(FLConfig.SERVER.solarDryingTicks.get()));
        map.put("solarDrierSpeedup", () -> multiplier((double) FLConfig.SERVER.dryingTicks.get() / FLConfig.SERVER.solarDryingTicks.get()));
        map.put("smokingTicks", () -> duration(FLConfig.SERVER.smokingTicks.get()));
        map.put("smokingFirepitRange", () -> count(FLConfig.SERVER.smokingFirepitRange.get()));
        map.put("compostTumblerTicks", () -> duration(FLConfig.SERVER.compostTumblerTicks.get()));
        map.put("ovenCureTicks", () -> duration(FLConfig.SERVER.ovenCureTicks.get()));
        map.put("ovenCureTemperature", () -> temperature(FLConfig.SERVER.ovenCureTemperature.get()));
        map.put("ovenCureHeat", () -> heat(FLConfig.SERVER.ovenCureTemperature.get()));
        map.put("ovenAshChance", () -> percent(FLConfig.SERVER.ovenAshChance.get()));
        map.put("beeUseHoneyDays", () -> days(FLConfig.SERVER.beeUseHoneyDays.get()));
        map.put("hollowShellCapacity", () -> Tooltips.fluidUnits(FLConfig.SERVER.hollowShellCapacity.get()));
        map.put("wineGlassCapacity", () -> Tooltips.fluidUnits(FLConfig.SERVER.wineGlassCapacity.get()));
        map.put("cellarLevel2Temperature", () -> climateTemperature(FLConfig.SERVER.cellarLevel2Temperature.get().floatValue()));
        map.put("cellarLevel3Temperature", () -> climateTemperature(FLConfig.SERVER.cellarLevel3Temperature.get().floatValue()));
        map.put("greenhouseRadius", () -> count(FLConfig.SERVER.greenhouseRadius.get()));
        map.put("greenhouseSize", () -> count(2 * FLConfig.SERVER.greenhouseRadius.get() + 1));
        map.put("cellarRadius", () -> count(FLConfig.SERVER.cellarRadius.get()));
        map.put("cellarSize", () -> count(2 * FLConfig.SERVER.cellarRadius.get() + 1));
        map.put("greenhouseGrowthModifier", () -> multiplier(FLConfig.SERVER.greenhouseGrowthModifier.get()));
        map.put("greenhouseWaterDays", () -> days(FLConfig.SERVER.greenhouseWaterDays.get()));
        map.put("greenhouseNutrientDays", () -> days(FLConfig.SERVER.greenhouseNutrientDays.get()));

        for (FLFoodTraits.Default trait : FLFoodTraits.Default.values())
        {
            map.put("trait." + trait.getName(), () -> percent(FLConfig.SERVER.foodTraits.get(trait).get()));
        }

        for (FLFruitBlocks.Tree tree : FLFruitBlocks.Tree.values())
        {
            putClimate(map, tree.name(), FLClimateRanges.FRUIT_TREES.get(tree));
        }
        for (FLFruitBlocks.StationaryBush bush : FLFruitBlocks.StationaryBush.values())
        {
            putClimate(map, bush.name(), FLClimateRanges.STATIONARY_BUSHES.get(bush));
        }
        putClimate(map, "grapes", FLClimateRanges.GRAPES);

        return Map.copyOf(map);
    }

    private static void putClimate(Map<String, Supplier<Component>> map, String name, DataManager.Reference<ClimateRange> range)
    {
        final String prefix = "climate." + name.toLowerCase(Locale.ROOT) + ".";
        assert !map.containsKey(prefix + "temperature") : "Duplicate climate range name: " + name;
        map.put(prefix + "temperature", () -> range(climateTemperature(range.get().getMinTemperature(false)), climateTemperature(range.get().getMaxTemperature(false))));
        map.put(prefix + "hydration", () -> range(range.get().getMinHydration(false), range.get().getMaxHydration(false)));
    }

    /**
     * When evaluated during datagen this returns the fallback default config values (which is what we want)
     */
    public static Map<String, Supplier<Component>> values()
    {
        return VALUES;
    }

    public static void register()
    {
        PatchouliAPI.get().registerFunction(NAME, (param, style) -> {
            final Component component = unknownOr(resolve(param), param);
            // The book renders text, not components, so any color the value carries has to be moved onto the span
            final TextColor color = component.getStyle().getColor();
            if (color != null)
            {
                style.modifyStyle(s -> s.withColor(color));
            }
            return component.getString();
        });
    }

    @Nullable
    private static Component resolve(String param)
    {
        final Supplier<Component> value = VALUES.get(param);
        return value != null ? value.get() : null;
    }

    private static Component unknownOr(@Nullable Component value, String param)
    {
        return value != null ? value : Component.literal("[UNKNOWN CONFIG: " + param + "]");
    }

    /**
     * Convert player ticks -> a whole number of calendar days, or hours if it is shorter than a day
     */
    private static Component duration(int playerTicks)
    {
        if (playerTicks <= 0)
        {
            return Component.translatable("tfc.field_guide.indefinitely");
        }
        // Round rather than floor, as getTotalCalendarDays() would. The tick rate is a float, so a whole number of
        // days lands just under the boundary, and ten days would otherwise read as nine
        final long calendarTicks = Calendars.CLIENT.getFixedCalendarTicksFromTick(playerTicks);
        return calendarTicks >= ICalendar.CALENDAR_TICKS_IN_DAY
            ? Component.translatable("tfc.tooltip.time_delta_days", Math.round((double) calendarTicks / ICalendar.CALENDAR_TICKS_IN_DAY))
            : Component.translatable("tfc.field_guide.hours", Math.round((double) calendarTicks / ICalendar.CALENDAR_TICKS_IN_HOUR));
    }

    /**
     * For config values which are already a count of calendar days, rather than a duration in ticks
     */
    private static Component days(double calendarDays)
    {
        return Component.translatable("tfc.tooltip.time_delta_days", number(calendarDays));
    }

    private static Component heat(int degrees)
    {
        final Heat heat = Heat.getHeat(degrees);
        return heat == null ? Component.empty() : Helpers.translateEnum(heat).withStyle(heat.getColor());
    }

    /**
     * The heat of an item, which is never negative
     */
    private static Component temperature(int degrees)
    {
        final TemperatureDisplayStyle style = TFCConfig.CLIENT.heatTooltipStyle.get();
        final Component formatted = (style == TemperatureDisplayStyle.COLOR ? TemperatureDisplayStyle.CELSIUS : style).format(degrees);
        return formatted == null ? count(degrees) : formatted;
    }

    /**
     * Climate temperatures, unlike the heat of an item, are frequently negative, so they use {@code formatRange},
     * which does not treat that as absent.
     */
    private static Component climateTemperature(float value)
    {
        final Component formatted = TFCConfig.CLIENT.climateTooltipStyle.get().formatRange(value);
        return formatted != null ? formatted : Objects.requireNonNull(TemperatureDisplayStyle.CELSIUS.formatRange(value));
    }

    private static Component range(Object min, Object max)
    {
        return Component.translatable("tfc.field_guide.range", min, max);
    }

    private static Component count(int amount)
    {
        return Component.literal(String.valueOf(amount));
    }

    private static Component multiplier(double factor)
    {
        return Component.literal(number(factor));
    }

    /**
     * Food trait modifiers and chances are stored as a fraction, but the book talks about them as a percentage
     */
    private static Component percent(double fraction)
    {
        return Component.literal(number(100 * fraction) + "%");
    }

    /**
     * Format without a trailing {@code .0}, as most of these values are configured as whole numbers
     */
    private static String number(double value)
    {
        return value == Math.rint(value)
            ? String.valueOf((long) value)
            : String.format(Locale.ROOT, "%.2f", value).replaceAll("0+$", "").replaceAll("\\.$", "");
    }

    private FLConfigTextFunction() {}
}
