package com.eerussianguy.firmalife.config;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import com.eerussianguy.firmalife.common.items.FLFoodTraits;
import net.minecraftforge.common.ForgeConfigSpec.*;

import static com.eerussianguy.firmalife.FirmaLife.MOD_ID;

public class FLServerConfig
{
    public final IntValue cheeseAgedDays;
    public final IntValue cheeseVintageDays;
    public final IntValue dryingTicks;
    public final IntValue solarDryingTicks;
    public final IntValue smokingTicks;
    public final IntValue smokingFirepitRange;
    public final IntValue ironComposterTicks;
    public final IntValue ovenCureTicks;
    public final IntValue ovenCureTemperature;
    public final DoubleValue ovenAshChance;
    public final BooleanValue ovenRequirePeel;
    public final BooleanValue enableSeedBalls;
    public final BooleanValue enableBeeSwarm;
    public final IntValue hollowShellCapacity;
    public final DoubleValue cellarLevel2Temperature;
    public final DoubleValue cellarLevel3Temperature;
    public final DoubleValue greenhouseGrowthDays;
    public final DoubleValue greenhouseWaterDays;
    public final DoubleValue greenhouseNutrientDays;
    public final Map<FLFoodTraits.Default, DoubleValue> foodTraits;


    FLServerConfig(Builder innerBuilder)
    {
        Function<String, Builder> builder = name -> innerBuilder.translation(MOD_ID + ".config.server." + name);

        innerBuilder.push("general");

        cheeseAgedDays = builder.apply("cheeseAgedDays").comment("Days in a cellar to make cheese Aged.").defineInRange("cheeseAgedDays", 28, 1, Integer.MAX_VALUE);
        cheeseVintageDays = builder.apply("cheeseVintageDays").comment("Days in a cellar to make cheese Vintage.").defineInRange("cheeseVintageDays", 112, 1, Integer.MAX_VALUE);
        dryingTicks = builder.apply("dryingTicks").comment("Ticks to dry something on a drying mat (24000 ticks = 1 day)").defineInRange("dryingTicks", 12000, 1, Integer.MAX_VALUE);
        solarDryingTicks = builder.apply("solarDryingTicks").comment("Ticks to dry something on a solar drier (24000 ticks = 1 day)").defineInRange("solarDryingTicks", 1000, 1, Integer.MAX_VALUE);
        smokingTicks = builder.apply("smokingTicks").comment("Ticks to smoke something on a string (24000 ticks = 1 day)").defineInRange("smokingTicks", 8000, 1, Integer.MAX_VALUE);
        smokingFirepitRange = builder.apply("smokingFirepitRange").comment("Number of blocks below the firepit that wool string will search for valid smoking firepits.").defineInRange("smokingFirepitRange", 6, 1, Integer.MAX_VALUE);
        ironComposterTicks = builder.apply("ironComposterTicks").comment("Ticks for an iron composter to finish (24000 ticks = 1 day)").defineInRange("ironComposterTicks", 96000, 1, Integer.MAX_VALUE);
        ovenCureTicks = builder.apply("ovenCureTicks").comment("Ticks for an oven to cure (24000 ticks = 1 day)").defineInRange("ovenCureTicks", 2000, 1, Integer.MAX_VALUE);
        ovenCureTemperature = builder.apply("ovenCureTemperature").comment("Minimum temperature for an oven to start the curing process (24000 ticks = 1 day)").defineInRange("ovenCureTemperature", 600, 1, Integer.MAX_VALUE);
        ovenRequirePeel = builder.apply("ovenRequirePeel").comment("If true, ovens will hurt the player if they touch it without a peel in hand.").define("ovenRequirePeel", true);
        ovenAshChance = builder.apply("ovenAshChance").comment("The chance for fuel burning in an oven to drop an ash block into an ashtray.").defineInRange("ovenAshChance", 0.5, 0, 1);
        enableSeedBalls = builder.apply("enableSeedBalls").comment("If true, players can throw seed balls.").define("enableSeedBalls", true);
        enableBeeSwarm = builder.apply("enableBeeSwarm").comment("If true, bees can swarm and hurt the player if provoked.").define("enableBeeSwarm", true);
        hollowShellCapacity = builder.apply("hollowShellCapacity").comment("The capacity in mB of the hollow shell. Default 100").defineInRange("hollowShellCapacity", 100, 1, Integer.MAX_VALUE);
        cellarLevel2Temperature = builder.apply("cellarLevel2Temperature").comment("The average temperature below which stronger decay modifiers apply to cellar blocks.").defineInRange("cellarLevel2Temperature", 0d, Double.MIN_VALUE, Double.MAX_VALUE);
        cellarLevel3Temperature = builder.apply("cellarLevel3Temperature").comment("The average temperature below which even stronger decay modifiers apply to cellar blocks.").defineInRange("cellarLevel3Temperature", -12d, Double.MIN_VALUE, Double.MAX_VALUE);
        greenhouseGrowthDays = builder.apply("greenhouseGrowthDays").comment("The average amount of days for a crop in a greenhouse to grow. For normal crops, this is 24 days.").defineInRange("greenhouseGrowthDays", 20d, Double.MIN_VALUE, Double.MAX_VALUE);
        greenhouseWaterDays = builder.apply("greenhouseWaterDays").comment("The average amount of days for a crop in a greenhouse to consume all its water.").defineInRange("greenhouseWaterDays", 12d, 0, Double.MAX_VALUE);
        greenhouseNutrientDays = builder.apply("greenhouseNutrientDays").comment("The average amount of days for a crop to consume all of a nutrient. You should probably not configure this value unless you know what it does in the code. For regular crops this value is 12.").defineInRange("greenhouseNutrientDays", 8d, 0, Double.MAX_VALUE);

        innerBuilder.pop().push("foodTraits");

        foodTraits = new HashMap<>();
        Arrays.stream(FLFoodTraits.Default.values()).forEach(trait ->
            foodTraits.put(trait, builder.apply("trait" + trait.getCapitalizedName() + "Modifier").comment("The modifier for the '" + trait.getCapitalizedName() + "' food trait. Values less than 1 extend food lifetime, values greater than one decrease it. A value of zero stops decay.")
                .defineInRange("trait" + trait.getCapitalizedName() + "Modifier", trait.getMod(), 0, Double.MAX_VALUE))
        );

        innerBuilder.pop();
    }
}
