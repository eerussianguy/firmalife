package com.eerussianguy.firmalife.common.container;

import net.dries007.tfc.common.container.ItemStackContainerProvider;
import net.dries007.tfc.util.Helpers;

public class FLContainerProviders
{
    public static final ItemStackContainerProvider PUMPKIN_KNAPPING = new ItemStackContainerProvider(FLContainerTypes::createPumpkin, Helpers.translatable("firmalife.screen.pumpkin_knapping"));
}
