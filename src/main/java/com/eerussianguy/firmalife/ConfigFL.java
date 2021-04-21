package com.eerussianguy.firmalife;

import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import com.eerussianguy.firmalife.util.HelpersFL;
import net.dries007.tfc.TerraFirmaCraft;

import static com.eerussianguy.firmalife.FirmaLife.MOD_ID;

@Mod.EventBusSubscriber(modid = MOD_ID)
public class ConfigFL
{
    @SubscribeEvent
    public static void onConfigChangedEvent(ConfigChangedEvent.OnConfigChangedEvent event)
    {
        if (event.getModID().equals(MOD_ID))
        {
            if (ConfigFL.General.COMPAT.logging)
                FirmaLife.logger.warn("Config changed.");
            ConfigManager.sync(TerraFirmaCraft.MOD_ID, Config.Type.INSTANCE);
        }
        if (event.getModID().equals(TerraFirmaCraft.MOD_ID))
        {
            HelpersFL.insertWhitelist();
        }
    }

    @Config(modid = MOD_ID, category = "general", name = "Firmalife -- General")
    @Config.LangKey("config." + MOD_ID + ".general")
    public static final class General
    {
        @Config.Comment("Worldgen Settings")
        @Config.LangKey("config." + MOD_ID + ".general.worldgen")
        public static final WorldgenCFG WORLDGEN = new WorldgenCFG();
        @Config.Comment("Compatibility Settings")
        @Config.LangKey("config." + MOD_ID + ".general.compat")
        public static final CompatCFG COMPAT = new CompatCFG();
        @Config.Comment("Balance Settings")
        @Config.LangKey("config." + MOD_ID + ".general.balance")
        public static final BalanceCFG BALANCE = new BalanceCFG();

        public static final class WorldgenCFG
        {
            @Config.Comment("Rarity of the Cinnamon trees in worldgen. Defined by 1/N chunks. Set to zero to disable")
            @Config.RangeInt(min = 0)
            @Config.LangKey("config." + MOD_ID + ".general.worldgen.cinnamonRarity")
            public int cinnamonRarity = 80;

            @Config.Comment("Rarity of the Wild Bees in worldgen. Defined by 1/N chunks. Set to zero to disable")
            @Config.RangeInt(min = 0)
            @Config.LangKey("config." + MOD_ID + ".general.worldgen.beeRarity")
            public int beeRarity = 40;
        }

        public static final class CompatCFG
        {
            @Config.Comment("Enable adding Firmalife fluids to TFC's wooden bucket whitelist?")
            @Config.LangKey("config." + MOD_ID + "general.compat.woodenWhitelist")
            public boolean addToWoodenBucket = true;

            @Config.Comment("Enable adding Firmalife fluids to TFC's barrel whitelist?")
            @Config.LangKey("config." + MOD_ID + "general.compat.barrelWhitelist")
            public boolean addToBarrel = true;

            @Config.Comment("Enable TFC cow variants giving custom milk?")
            @Config.LangKey("config." + MOD_ID + "general.compat.customMilk")
            public boolean customMilk = true;

            @Config.Comment("Remove some TFC crafting recipes??")
            @Config.LangKey("config." + MOD_ID + "general.compat.removeTFC")
            public boolean removeTFC = true;

            @Config.Comment("Enable logging of some actions Firmalife takes (such as recipe removals)")
            @Config.LangKey("config." + MOD_ID + "general.compat.logging")
            public boolean logging = true;

            @Config.Comment("List of fluids allowed to be picked up by a cheesecloth")
            @Config.LangKey("config." + MOD_ID + "general.compat.cheeseclothWhitelist")
            public String[] cheeseclothWhitelist = new String[] {"milk_curdled", "curdled_goat_milk", "curdled_yak_milk"};
        }

        public static final class BalanceCFG
        {
            @Config.Comment("Require peel to take stuff out of the oven?")
            @Config.LangKey("config." + MOD_ID + "general.balance.peelNeeded")
            public boolean peelNeeded = true;

            @Config.Comment("Distance required between two nut hammerings")
            @Config.LangKey("config." + MOD_ID + "general.balance.nutDistance")
            public int nutDistance = 30;

            @Config.Comment("Ticks required between two nut hammerings")
            @Config.LangKey("config." + MOD_ID + "general.balance.nutTime")
            public int nutTime = 2000;

            @Config.Comment("List of animals that drop rennet")
            @Config.LangKey("config." + MOD_ID + "general.balance.rennetLootTable")
            public String[] rennetLootTable = new String[] {"animals/sheep", "animals/cow", "animals/deer", "animals/goat", "animals/zebu", "animals/gazelle", "animals/wildebeest", "animals/muskox", "animals/yak"};

            @Config.Comment("Ticks required for a cheese to become aged")
            @Config.LangKey("config." + MOD_ID + "general.balance.cheeseTimeToAged")
            public int cheeseTicksToAged = 672000; //default 672000 (28 days)

            @Config.Comment("Ticks required for a cheese to become vintage")
            @Config.LangKey("config." + MOD_ID + "general.balance.cheeseTimeToVintage")
            public int cheeseTicksToVintage = 2688000; //default 2688000 (112 days)
        }
    }
}
