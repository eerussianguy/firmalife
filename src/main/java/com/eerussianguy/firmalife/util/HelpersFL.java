package com.eerussianguy.firmalife.util;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import net.minecraft.item.ItemStack;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;

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
        String[] additions = {"yeast_starter","coconut_milk"};
        Set<String> stringSet = new HashSet<>(Arrays.asList(ConfigTFC.General.MISC.woodenBucketWhitelist));
        for (String a : additions)
        {
            if (stringSet.add(a))
            {
                FirmaLife.logger.info("Added {} to TFC's config", a);
            }
        }
        ConfigTFC.General.MISC.woodenBucketWhitelist = stringSet.toArray(new String[]{});
    }

}
