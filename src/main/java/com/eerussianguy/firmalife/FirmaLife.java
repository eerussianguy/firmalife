package com.eerussianguy.firmalife;

import org.apache.logging.log4j.Logger;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

import com.eerussianguy.firmalife.compat.ModuleManager;
import com.eerussianguy.firmalife.gui.FLGuiHandler;
import com.eerussianguy.firmalife.init.VeinAdder;
import com.eerussianguy.firmalife.network.PacketDrawBoundingBox;
import com.eerussianguy.firmalife.network.PacketSpawnVanillaParticle;
import com.eerussianguy.firmalife.player.CapPlayerDataFL;
import com.eerussianguy.firmalife.proxy.CommonProxy;
import com.eerussianguy.firmalife.registry.ItemsFL;
import com.eerussianguy.firmalife.registry.LootTablesFL;
import com.eerussianguy.firmalife.util.HelpersFL;
import com.eerussianguy.firmalife.util.OreDictsFL;
import net.dries007.tfc.api.capability.heat.CapabilityItemHeat;
import net.dries007.tfc.api.capability.heat.ItemHeatHandler;
import net.dries007.tfc.objects.inventory.ingredient.IIngredient;

@Mod(modid = FirmaLife.MOD_ID, name = FirmaLife.MODNAME, version = FirmaLife.MODVERSION, dependencies = "required-after:tfc@[1.7.17.175,);after:dynamictreestfc")
public class FirmaLife
{
    public static final String MOD_ID = "firmalife";
    public static final String MODNAME = "FirmaLife";
    public static final String MODVERSION = "0.5.1";

    @Mod.Instance
    private static FirmaLife INSTANCE = null;

    @SidedProxy(clientSide = "com.eerussianguy.firmalife.proxy.ClientProxy", serverSide = "com.eerussianguy.firmalife.proxy.ServerProxy")
    public static CommonProxy proxy;

    public static Logger logger;

    public static FirmaLife getInstance()
    {
        return INSTANCE;
    }

    public static SimpleNetworkWrapper getNetwork()
    {
        return INSTANCE.network;
    }

    private SimpleNetworkWrapper network;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        logger = event.getModLog();
        proxy.preInit(event);

        NetworkRegistry.INSTANCE.registerGuiHandler(this, new FLGuiHandler());
        network = NetworkRegistry.INSTANCE.newSimpleChannel(MOD_ID);
        int id = 0;
        // received client side
        network.registerMessage(new PacketSpawnVanillaParticle.Handler(), PacketSpawnVanillaParticle.class, ++id, Side.CLIENT);
        network.registerMessage(new PacketDrawBoundingBox.Handler(), PacketDrawBoundingBox.class, ++id, Side.CLIENT);

        VeinAdder.ADDER.addVeins(event.getModConfigurationDirectory());

        CapPlayerDataFL.preInit();
        HelpersFL.insertWhitelist();

        ModuleManager.initModules();
        ModuleManager.getModules().forEach(mod -> mod.preInit(event));
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event)
    {
        proxy.init(event);
        LootTablesFL.init();
        ModuleManager.getModules().forEach(mod -> mod.init(event));

        CapabilityItemHeat.CUSTOM_ITEMS.put(IIngredient.of(ItemsFL.HONEYCOMB), () -> new ItemHeatHandler(null, 1, 600));
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event)
    {
        proxy.postInit(event);
        OreDictsFL.addStaticOres();
        ModuleManager.getModules().forEach(mod -> mod.postInit(event));
    }
}