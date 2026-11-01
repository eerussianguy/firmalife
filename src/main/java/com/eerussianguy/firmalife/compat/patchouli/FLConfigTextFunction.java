package com.eerussianguy.firmalife.compat.patchouli;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;

import com.eerussianguy.firmalife.common.FLHelpers;
import com.eerussianguy.firmalife.common.blocks.plant.FLFruitBlocks;
import com.eerussianguy.firmalife.common.items.FLFoodTraits;
import com.eerussianguy.firmalife.common.util.FLClimateRanges;
import com.eerussianguy.firmalife.common.util.Plantable;
import com.eerussianguy.firmalife.config.FLConfig;
import net.dries007.tfc.common.component.heat.Heat;
import net.dries007.tfc.compat.patchouli.ConfigTextFunction;
import net.dries007.tfc.config.ClimateTemperatureDisplayStyle;
import net.dries007.tfc.config.ItemTemperatureDisplayStyle;
import net.dries007.tfc.config.TFCConfig;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.calendar.Calendars;
import net.dries007.tfc.util.calendar.ICalendar;
import net.dries007.tfc.util.climate.ClimateRange;
import net.dries007.tfc.util.data.DataManager;
import net.dries007.tfc.util.tooltip.Tooltips;

/**
 * Registers the {@code $(flcfg:<name>)} field guide text function, which substitutes a value derived from the
 * Firmalife server config, or from a {@link ClimateRange}, into book text. This is the Firmalife equivalent of TFC's
 * {@code $(cfg:<name>)}, and is registered through TFC so that it also works in places like table cells.
 * <p>
 * {@link #VALUES} is the set of names the book is allowed to reference. Any name used in {@code generate_book.py}
 * must appear here, except for {@code plantable.<id>.<field>}, which is resolved from synced data, see {@link #plantable}.
 */
public final class FLConfigTextFunction
{
    public static final String NAME = "flcfg";
    private static final String PLANTABLE_PREFIX = "plantable.";

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
        ConfigTextFunction.register(NAME, FLConfigTextFunction::resolve);
    }

    @Nullable
    private static Component resolve(String param)
    {
        final Supplier<Component> value = VALUES.get(param);
        if (value != null)
        {
            return value.get();
        }
        return param.startsWith(PLANTABLE_PREFIX) ? plantable(param.substring(PLANTABLE_PREFIX.length())) : null;
    }

    @Nullable
    private static Component plantable(String param)
    {
        final int dot = param.lastIndexOf('.');
        if (dot == -1)
        {
            return null;
        }
        final Plantable plant = Plantable.MANAGER.get(FLHelpers.identifier(param.substring(0, dot)));
        if (plant == null)
        {
            return null;
        }
        final Plantable.NutrientList nutrients = plant.nutrient();
        return switch (param.substring(dot + 1))
        {
            case "n" -> nutrient(nutrients.nitrogen(), nutrients);
            case "p" -> nutrient(nutrients.phosphorous(), nutrients);
            case "k" -> nutrient(nutrients.potassium(), nutrients);
            default -> null;
        };
    }

    private static Component nutrient(float value, Plantable.NutrientList nutrients)
    {
        return Component.literal(Plantable.formatNutrientAmount(value, nutrients.consumesAny()));
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

    private static Component days(double calendarDays)
    {
        return Component.translatable("tfc.tooltip.time_delta_days", number(calendarDays));
    }

    private static Component heat(int degrees)
    {
        final Heat heat = Heat.getHeat(degrees);
        return heat == null ? Component.empty() : Helpers.translateEnum(heat).withStyle(heat.getColor());
    }

    private static Component temperature(int degrees)
    {
        final ItemTemperatureDisplayStyle style = TFCConfig.CLIENT.heatTooltipStyle.get();
        final Component formatted = (style == ItemTemperatureDisplayStyle.COLOR ? ItemTemperatureDisplayStyle.CELSIUS : style).format(degrees);
        return formatted == null ? count(degrees) : formatted;
    }

    private static Component climateTemperature(float value)
    {
        final Component formatted = TFCConfig.CLIENT.climateTooltipStyle.get().formatRange(value);
        return formatted != null ? formatted : Objects.requireNonNull(ClimateTemperatureDisplayStyle.CELSIUS.formatRange(value));
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

    private static Component percent(double fraction)
    {
        return Component.literal(number(100 * fraction) + "%");
    }

    private static String number(double value)
    {
        return value == Math.rint(value)
            ? String.valueOf((long) value)
            : String.format(Locale.ROOT, "%.2f", value).replaceAll("0+$", "").replaceAll("\\.$", "");
    }

    private FLConfigTextFunction() {}
}
