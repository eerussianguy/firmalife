package com.eerussianguy.firmalife.common;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;

import com.eerussianguy.firmalife.Firmalife;

public class FLHelpers
{
    public static ResourceLocation identifier(String id)
    {
        return new ResourceLocation(Firmalife.MOD_ID, id);
    }

    public static Component blockEntityName(String name)
    {
        return new TranslatableComponent(Firmalife.MOD_ID + ".block_entity." + name);
    }
}
