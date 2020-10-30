package com.eerussianguy.firmalife.player;

import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;

import net.dries007.tfc.api.capability.DumbStorage;

import static com.eerussianguy.firmalife.FirmaLife.MOD_ID;

@ParametersAreNonnullByDefault
public final class CapPlayerDataFL
{
    public static ResourceLocation NAMESPACE = new ResourceLocation(MOD_ID, "firmalife_data");
    @CapabilityInject(IPlayerDataFL.class)
    public static Capability<IPlayerDataFL> CAPABILITY;

    public static void preInit()
    {
        CapabilityManager.INSTANCE.register(IPlayerDataFL.class, new DumbStorage<>(), () -> null);
    }

}
