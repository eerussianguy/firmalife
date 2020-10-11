package com.eerussianguy.firmalife.init;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

import org.apache.commons.io.FileUtils;

import net.dries007.tfc.TerraFirmaCraft;
import net.dries007.tfc.world.classic.worldgen.vein.VeinRegistry;

public enum VeinAdder
{
    ADDER;

    private static final String ORE_FROM = "assets/firmalife/config/firmalife_ores.json";

    public void addVeins(File configDir) // this gets the installation's config directory
    {
        File tfc = new File(configDir, TerraFirmaCraft.MOD_ID); // this is just /config/tfc/
        if (!tfc.exists() && !tfc.mkdir())
        {
            throw new Error("Sorry, but I couldn't find the TFC directory.");
        }
        File firmalife = new File(tfc, "firmalife_ores.json");
        try
        {
            if (firmalife.createNewFile()) // creates an empty json file and then copies it to config
            {
                FileUtils.copyInputStreamToFile(Objects.requireNonNull(VeinRegistry.class.getClassLoader().getResourceAsStream(ORE_FROM)), firmalife);
            }
        }
        catch (IOException e) // java requires that we handle what happens if it goes wrong
        {
            throw new Error("Sorry, but I couldn't copy my ore spawning file into TFC's config directory.");
        }

    }
}
