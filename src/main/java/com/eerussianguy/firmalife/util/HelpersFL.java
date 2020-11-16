package com.eerussianguy.firmalife.util;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import net.minecraft.item.ItemStack;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;

import com.eerussianguy.firmalife.ConfigFL;
import com.eerussianguy.firmalife.FirmaLife;
import net.dries007.tfc.ConfigTFC;
import net.dries007.tfc.TerraFirmaCraft;

public class HelpersFL
{
    public HelpersFL()
    {

    }

    public static boolean doesStackMatchTool(ItemStack stack, String toolClass)
    {
        Set<String> toolClasses = stack.getItem().getToolClasses(stack);
        return toolClasses.contains(toolClass);
    }

    public static void insertWhitelist()
    {
        ConfigManager.sync(TerraFirmaCraft.MOD_ID, Config.Type.INSTANCE);
        String[] additions = {"yeast_starter", "coconut_milk", "yak_milk", "zebu_milk", "goat_milk", "curdled_yak_milk", "curdled_zebu_milk", "curdled_goat_milk"};
        if (ConfigFL.General.COMPAT.addToWoodenBucket)
        {
            Set<String> woodenBucketSet = new HashSet<>(Arrays.asList(ConfigTFC.General.MISC.woodenBucketWhitelist));
            for (String a : additions)
            {
                if (woodenBucketSet.add(a))
                {
                    FirmaLife.logger.info("Added {} to TFC's wooden bucket fluid whitelist", a);
                }
            }
            ConfigTFC.General.MISC.woodenBucketWhitelist = woodenBucketSet.toArray(new String[] {});
        }
        if (ConfigFL.General.COMPAT.addToBarrel)
        {
            Set<String> barrelSet = new HashSet<>(Arrays.asList(ConfigTFC.Devices.BARREL.fluidWhitelist));
            for (String a : additions)
            {
                if (barrelSet.add(a))
                {
                    FirmaLife.logger.info("Added {} to TFC's barrel fluid whitelist", a);
                }
            }
            ConfigTFC.Devices.BARREL.fluidWhitelist = barrelSet.toArray(new String[] {});
        }
    }

}
