package com.eerussianguy.firmalife.common.container;

import net.minecraft.network.chat.Component;

import net.dries007.tfc.common.container.ItemStackContainerProvider;
import net.dries007.tfc.util.Helpers;

public class FLContainerProviders
{
    public static final ItemStackContainerProvider PUMPKIN_KNAPPING = new ItemStackContainerProvider(FLContainerTypes::createPumpkin, Component.translatable("firmalife.screen.pumpkin_knapping"));
}
