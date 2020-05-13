package com.eerussianguy.firmalife;

import com.eerussianguy.firmalife.proxy.CommonProxy;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.apache.logging.log4j.Logger;

@Mod(modid = FirmaLife.MOD_ID, name = FirmaLife.MODNAME, version = FirmaLife.MODVERSION)
public class FirmaLife {

    public static final String MOD_ID = "firmalife";
    public static final String MODNAME = "FirmaLife";
    public static final String MODVERSION= "0.0.1";

    @SidedProxy(clientSide = "com.eerussianguy.firmalife.proxy.ClientProxy", serverSide = "com.eerussianguy.firmalife.proxy.ServerProxy")
    public static CommonProxy proxy;

    @Mod.Instance
    public static FirmaLife instance;

    public static Logger logger;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        logger = event.getModLog();
        proxy.preInit(event);
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent e) {
        proxy.init(e);
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent e) {
        proxy.postInit(e);
    }
}