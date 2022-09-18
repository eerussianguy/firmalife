package com.eerussianguy.firmalife.config;

import java.util.function.Function;

import net.minecraftforge.common.ForgeConfigSpec.*;

import static com.eerussianguy.firmalife.FirmaLife.MOD_ID;

public class FLServerConfig
{
    public final IntValue cheeseAgedDays;
    public final IntValue cheeseVintageDays;
    public final IntValue dryingTicks;
    public final IntValue solarDryingTicks;
    public final IntValue smokingTicks;
    public final IntValue ironComposterTicks;
    public final IntValue ovenCookTicks;
    public final IntValue ovenCureTicks;
    public final IntValue ovenCureTemperature;
    public final BooleanValue ovenRequirePeel;
    public final BooleanValue enableSeedBalls;
    public final BooleanValue enableBeeSwarm;
    public final IntValue hollowShellCapacity;

    FLServerConfig(Builder innerBuilder)
    {
        Function<String, Builder> builder = name -> innerBuilder.translation(MOD_ID + ".config.server." + name);

        innerBuilder.push("general");

        cheeseAgedDays = builder.apply("cheeseAgedDays").comment("Days in a cellar to make cheese Aged.").defineInRange("cheeseAgedDays", 28, 1, Integer.MAX_VALUE);
        cheeseVintageDays = builder.apply("cheeseVintageDays").comment("Days in a cellar to make cheese Vintage.").defineInRange("cheeseVintageDays", 112, 1, Integer.MAX_VALUE);
        dryingTicks = builder.apply("dryingTicks").comment("Ticks to dry something on a drying mat (24000 ticks = 1 day)").defineInRange("dryingTicks", 12000, 1, Integer.MAX_VALUE);
        solarDryingTicks = builder.apply("solarDryingTicks").comment("Ticks to dry something on a solar drier (24000 ticks = 1 day)").defineInRange("solarDryingTicks", 1000, 1, Integer.MAX_VALUE);
        smokingTicks = builder.apply("smokingTicks").comment("Ticks to smoke something on a string (24000 ticks = 1 day)").defineInRange("smokingTicks", 8000, 1, Integer.MAX_VALUE);
        ironComposterTicks = builder.apply("ironComposterTicks").comment("Ticks for an iron composter to finish (24000 ticks = 1 day)").defineInRange("ironComposterTicks", 72000, 1, Integer.MAX_VALUE);
        ovenCookTicks = builder.apply("ovenCookTicks").comment("Ticks for an oven to cook an item (24000 ticks = 1 day)").defineInRange("ovenCookTicks", 2000, 1, Integer.MAX_VALUE);
        ovenCureTicks = builder.apply("ovenCureTicks").comment("Ticks for an oven to cure (24000 ticks = 1 day)").defineInRange("ovenCureTicks", 2000, 1, Integer.MAX_VALUE);
        ovenCureTemperature = builder.apply("ovenCureTemperature").comment("Minimum temperature for an oven to start the curing process (24000 ticks = 1 day)").defineInRange("ovenCureTemperature", 600, 1, Integer.MAX_VALUE);
        ovenRequirePeel = builder.apply("ovenRequirePeel").comment("If true, ovens will hurt the player if they touch it without a peel in hand.").define("ovenRequirePeel", true);
        enableSeedBalls = builder.apply("enableSeedBalls").comment("If true, players can throw seed balls.").define("enableSeedBalls", true);
        enableBeeSwarm = builder.apply("enableBeeSwarm").comment("If true, bees can swarm and hurt the player if provoked.").define("enableBeeSwarm", true);
        hollowShellCapacity = builder.apply("hollowShellCapacity").comment("The capacity in mB of the hollow shell. Default 100").defineInRange("hollowShellCapacity", 100, 1, Integer.MAX_VALUE);

        innerBuilder.pop();
    }
}
