package com.eerussianguy.firmalife.compat;

import javax.annotation.Nullable;

import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public abstract class ModuleCore
{
    private final String dep;
    private CompatibleRecipeRegistry registry;

    public ModuleCore(String dep)
    {
        this.dep = dep;
    }

    public ModuleCore(String dep, CompatibleRecipeRegistry registry)
    {
        this(dep);
        this.registry = registry;
    }

    public void addRegistry(CompatibleRecipeRegistry registry)
    {
        this.registry = registry;
    }

    @Nullable
    public CompatibleRecipeRegistry getRegistry()
    {
        return registry;
    }

    public String getDep()
    {
        return this.dep;
    }

    public abstract boolean isLoaded();

    public abstract void preInit(FMLPreInitializationEvent event);

    public abstract void init(FMLInitializationEvent event);

    public abstract void postInit(FMLPostInitializationEvent event);
}
