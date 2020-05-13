
@Mod(modid = FirmaLife.MOD_ID, name = FirmaLife.MODNAME, version = FirmaLife.MODVERSION)
public class FirmaLife {

    public static final String MOD_ID = "firmalife";
    public static final String MODNAME = "FirmaLife";
    public static final String MODVERSION= "0.0.1";

    @SidedProxy(clientSide = "mcjty.firmalife.proxy.ClientProxy", serverSide = "mcjty.firmalife.proxy.ServerProxy")
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