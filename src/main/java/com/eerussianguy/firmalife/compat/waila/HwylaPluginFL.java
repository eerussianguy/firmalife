package com.eerussianguy.firmalife.compat.waila;

import java.util.Arrays;
import java.util.List;

import mcp.mobius.waila.api.IWailaPlugin;
import mcp.mobius.waila.api.IWailaRegistrar;
import mcp.mobius.waila.api.WailaPlugin;
import net.dries007.tfc.compat.waila.interfaces.HwylaBlockInterface;

@WailaPlugin
public class HwylaPluginFL implements IWailaPlugin
{
    public static final List<IWailaPlugin> WAILA_PLUGINS = Arrays.asList(
        new HwylaBlockInterface(new CheesewheelProvider()),
        new HwylaBlockInterface(new OvenProvider()),
        new HwylaBlockInterface(new LeafMatProvider()),
        new HwylaBlockInterface(new PlanterProvider()),
        new HwylaBlockInterface(new HangingPlanterProvider())
    );

    public void register(IWailaRegistrar registrar)
    {
        for (IWailaPlugin plugin : WAILA_PLUGINS)
        {
            plugin.register(registrar);
        }
    }
}
