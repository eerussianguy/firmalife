package com.eerussianguy.firmalife;

import org.apache.logging.log4j.Logger;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;

import com.eerussianguy.firmalife.gui.FLGuiHandler;
import com.eerussianguy.firmalife.init.VeinAdder;
import com.eerussianguy.firmalife.player.CapPlayerDataFL;
import com.eerussianguy.firmalife.proxy.CommonProxy;
import com.eerussianguy.firmalife.util.HelpersFL;
import com.eerussianguy.firmalife.util.OreDictsFL;

@Mod(modid = FirmaLife.MOD_ID, name = FirmaLife.MODNAME, version = FirmaLife.MODVERSION)
public class FirmaLife
{
    public static final String MOD_ID = "firmalife";
    public static final String MODNAME = "FirmaLife";
    public static final String MODVERSION = "0.2.2";

    @Mod.Instance
    private static FirmaLife INSTANCE = null;

    private static final String ORE_FROM = "assets/firmalife/config/firmalife_ores.json";

    @SidedProxy(clientSide = "com.eerussianguy.firmalife.proxy.ClientProxy", serverSide = "com.eerussianguy.firmalife.proxy.ServerProxy")
    public static CommonProxy proxy;

    public static Logger logger;

    public static FirmaLife getInstance()
    {
        return INSTANCE;
    }

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        logger = event.getModLog();
        proxy.preInit(event);

        NetworkRegistry.INSTANCE.registerGuiHandler(this, new FLGuiHandler());

        VeinAdder.ADDER.addVeins(event.getModConfigurationDirectory());

        CapPlayerDataFL.preInit();
        HelpersFL.insertWhitelist();
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent e)
    {
        proxy.init(e);
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent e)
    {
        proxy.postInit(e);
        OreDictsFL.addStaticOres();
    }
}