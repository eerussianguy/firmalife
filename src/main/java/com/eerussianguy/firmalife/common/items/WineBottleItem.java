package com.eerussianguy.firmalife.common.items;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;

public class WineBottleItem extends Item
{
    private final ResourceLocation modelLocation;

    public WineBottleItem(Properties properties, ResourceLocation modelLocation)
    {
        super(properties);
        this.modelLocation = modelLocation;
    }

    public ResourceLocation getModelLocation()
    {
        return modelLocation;
    }
}
