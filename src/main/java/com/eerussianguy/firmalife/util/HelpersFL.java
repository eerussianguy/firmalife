package com.eerussianguy.firmalife.util;

import java.util.Set;

import net.minecraft.item.ItemStack;

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
}
