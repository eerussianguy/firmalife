package com.eerussianguy.firmalife.common.util;

import net.dries007.tfc.util.data.DataManager;
import net.dries007.tfc.util.data.DataManagers;

public class FLDataManagers
{
    public static void init()
    {
        register(GreenhouseType.MANAGER);
        register(Plantable.MANAGER);
    }

    private static void register(DataManager<?> manager)
    {
        DataManagers.MANAGERS.register(manager.getName(), () -> manager);
    }
}
