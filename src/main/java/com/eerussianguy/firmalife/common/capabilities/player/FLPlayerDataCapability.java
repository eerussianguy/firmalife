package com.eerussianguy.firmalife.common.capabilities.player;

import com.eerussianguy.firmalife.common.FLHelpers;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;

public class FLPlayerDataCapability
{
    public static final Capability<FLPlayerData> CAPABILITY = CapabilityManager.get(new CapabilityToken<>() {});
    public static final ResourceLocation KEY = FLHelpers.identifier("player_glow");
}
