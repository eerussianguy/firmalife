package com.eerussianguy.firmalife.common.items;

import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;

public class WineBottleItem extends Item
{
    private final ModelResourceLocation modelLocation;

    public WineBottleItem(Properties properties, ResourceLocation modelLocation)
    {
        super(properties);
        this.modelLocation = ModelResourceLocation.standalone(modelLocation);
    }

    public ModelResourceLocation getModelLocation()
    {
        return modelLocation;
    }
}
