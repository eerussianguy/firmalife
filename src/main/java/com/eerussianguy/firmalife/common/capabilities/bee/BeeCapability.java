package com.eerussianguy.firmalife.common.capabilities.bee;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;

import com.eerussianguy.firmalife.common.FLHelpers;

public class BeeCapability
{
    public static final Capability<IBee> CAPABILITY = CapabilityManager.get(new CapabilityToken<>() {});
    public static final ResourceLocation KEY = FLHelpers.identifier("bee");
}
