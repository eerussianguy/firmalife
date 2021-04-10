package com.eerussianguy.firmalife.compat.dynamictrees;

import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

import com.eerussianguy.firmalife.compat.ModuleCore;

public class DTModule extends ModuleCore
{
    public DTModule()
    {
        super("dynamictreestfc");
    }

    @Override
    public boolean isLoaded() {
        return Loader.isModLoaded(this.getDep());
    }

    @Override
    public void preInit(FMLPreInitializationEvent event)
    {
        addRegistry(new DTRegistry());
    }

    @Override
    public void init(FMLInitializationEvent event)
    {
    }

    @Override
    public void postInit(FMLPostInitializationEvent event)
    {

    }
}
