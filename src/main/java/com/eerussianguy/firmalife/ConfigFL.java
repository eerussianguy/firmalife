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

        public static final class WorldgenCFG
        {
            @Config.Comment("Rarity of the Cinnamon trees in worldgen. Defined by 1/N chunks. Set to zero to disable")
            @Config.RangeInt(min = 0)
            @Config.LangKey("config." + MOD_ID + ".general.worldgen.cinnamonRarity")
            public int cinnamonRarity = 130;
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
        }
    }
}
