package com.eerussianguy.firmalife.config;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;
import com.eerussianguy.firmalife.common.items.FLFoodTraits;

import net.dries007.tfc.config.BaseConfig;
import net.dries007.tfc.config.ConfigBuilder;

public class FLServerConfig extends BaseConfig
{
    public final Supplier<Integer> cheeseAgedDays;
    public final Supplier<Integer> cheeseVintageDays;
    public final Supplier<Integer> dryingTicks;
    public final Supplier<Integer> solarDryingTicks;
    public final Supplier<Integer> smokingTicks;
    public final Supplier<Integer> smokingFirepitRange;
    public final Supplier<Integer> compostTumblerTicks;
    public final Supplier<Integer> ovenCureTicks;
    public final Supplier<Integer> ovenCureTemperature;
    public final Supplier<Double> ovenAshChance;
    public final Supplier<Boolean> ovenRequirePeel;
    public final Supplier<Boolean> enableSeedBalls;
    public final Supplier<Boolean> enableBeeSwarm;
    public final Supplier<Integer> hollowShellCapacity;
    public final Supplier<Integer> wineGlassCapacity;
    public final Supplier<Double> cellarLevel2Temperature;
    public final Supplier<Double> cellarLevel3Temperature;
    public final Supplier<Double> greenhouseGrowthDays;
    public final Supplier<Double> greenhouseWaterDays;
    public final Supplier<Double> greenhouseNutrientDays;
    public final Map<FLFoodTraits.Default, Supplier<Double>> foodTraits;
    public final Supplier<Integer> greenhouseRadius;
    public final Supplier<Integer> cellarRadius;
    public final Supplier<Boolean> mechanicalPowerCheatMode;
    public final Supplier<Boolean> usePipesForSprinklers;


    FLServerConfig(ConfigBuilder builder)
    {

        builder.push("general");

        cheeseAgedDays = builder.comment("Days in a cellar to make cheese Aged.").define("cheeseAgedDays", 28, 1, Integer.MAX_VALUE);
        cheeseVintageDays = builder.comment("Days in a cellar to make cheese Vintage.").define("cheeseVintageDays", 112, 1, Integer.MAX_VALUE);
        dryingTicks = builder.comment("Ticks to dry something on a drying mat (24000 ticks = 1 day)").define("dryingTicks", 12000, 1, Integer.MAX_VALUE);
        solarDryingTicks = builder.comment("Ticks to dry something on a solar drier (24000 ticks = 1 day)").define("solarDryingTicks", 1000, 1, Integer.MAX_VALUE);
        smokingTicks = builder.comment("Ticks to smoke something on a string (24000 ticks = 1 day)").define("smokingTicks", 8000, 1, Integer.MAX_VALUE);
        smokingFirepitRange = builder.comment("Number of blocks below the firepit that wool string will search for valid smoking firepits.").define("smokingFirepitRange", 6, 1, Integer.MAX_VALUE);
        compostTumblerTicks = builder.comment("Ticks for a composter tumbler to finish (24000 ticks = 1 day)").define("compostTumblerTicks", 96000, 1, Integer.MAX_VALUE);
        ovenCureTicks = builder.comment("Ticks for an oven to cure (24000 ticks = 1 day)").define("ovenCureTicks", 2000, 1, Integer.MAX_VALUE);
        ovenCureTemperature = builder.comment("Minimum temperature for an oven to start the curing process (24000 ticks = 1 day)").define("ovenCureTemperature", 600, 1, Integer.MAX_VALUE);
        ovenRequirePeel = builder.comment("If true, ovens will hurt the player if they touch it without a peel in hand.").define("ovenRequirePeel", true);
        ovenAshChance = builder.comment("The chance for fuel burning in an oven to drop an ash block into an ashtray.").define("ovenAshChance", 0.5, 0, 1);
        enableSeedBalls = builder.comment("If true, players can throw seed balls.").define("enableSeedBalls", true);
        enableBeeSwarm = builder.comment("If true, bees can swarm and hurt the player if provoked.").define("enableBeeSwarm", true);
        hollowShellCapacity = builder.comment("The capacity in mB of the hollow shell. Default 100").define("hollowShellCapacity", 100, 1, Integer.MAX_VALUE);
        wineGlassCapacity = builder.comment("The capacity in mB of the wine glass. Default 250").define("hollowShellCapacity", 250, 1, Integer.MAX_VALUE);
        cellarLevel2Temperature = builder.comment("The average temperature below which stronger decay modifiers apply to cellar blocks.").define("cellarLevel2Temperature", 0d, -Double.MAX_VALUE, Double.MAX_VALUE);
        cellarLevel3Temperature = builder.comment("The average temperature below which even stronger decay modifiers apply to cellar blocks.").define("cellarLevel3Temperature", -12d, -Double.MAX_VALUE, Double.MAX_VALUE);
        greenhouseGrowthDays = builder.comment("The average amount of days for a crop in a greenhouse to grow. For normal crops, this is 24 days.").define("greenhouseGrowthDays", 20d, Double.MIN_VALUE, Double.MAX_VALUE);
        greenhouseWaterDays = builder.comment("The average amount of days for a crop in a greenhouse to consume all its water.").define("greenhouseWaterDays", 12d, 0, Double.MAX_VALUE);
        greenhouseNutrientDays = builder.comment("The average amount of days for a crop to consume all of a nutrient. You should probably not configure this value unless you know what it does in the code. For regular crops this value is 12.").define("greenhouseNutrientDays", 8d, 0, Double.MAX_VALUE);
        greenhouseRadius = builder.comment("The max bounded distance from the climate station a greenhouse wall can be. Higher numbers = more lag.").define("greenhouseRadius", 15, 1, 128);
        cellarRadius = builder.comment("The max bounded distance from the climate station a cellar wall can be. Higher numbers = more lag.").define("cellarRadius", 15, 1, 128);
        mechanicalPowerCheatMode = builder.comment("If true, the tumbler and the pumping station work magically with a redstone signal and no power required.").define("mechanicalPowerCheatMode", false);
        usePipesForSprinklers = builder.comment("If false, sprinklers will not accept Firmalife pipes and will instead require something that exposes a fluid capability, eg. a barrel.").define("usePipesForSprinklers", true);

        builder.pop().push("foodTraits");

        foodTraits = new HashMap<>();
        Arrays.stream(FLFoodTraits.Default.values()).forEach(trait ->
            foodTraits.put(trait, builder.comment("The modifier for the '" + trait.getCapitalizedName() + "' food trait. Values less than 1 extend food lifetime, values greater than one decrease it. A value of zero stops decay.")
                .define("trait" + trait.getCapitalizedName() + "Modifier", trait.getMod(), 0, Double.MAX_VALUE))
        );

        builder.pop();
    }
}
