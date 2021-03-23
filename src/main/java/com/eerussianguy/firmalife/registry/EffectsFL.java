package com.eerussianguy.firmalife.registry;

import net.minecraft.potion.Potion;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;

import com.eerussianguy.firmalife.player.PotionSwarm;
import net.dries007.tfc.util.Helpers;

import static com.eerussianguy.firmalife.FirmaLife.MOD_ID;

@Mod.EventBusSubscriber(modid = MOD_ID)
@GameRegistry.ObjectHolder(MOD_ID)
public class EffectsFL
{
    public static final Potion SWARM = Helpers.getNull();

    @SubscribeEvent
    public static void registerPotionEffects(RegistryEvent.Register<Potion> event)
    {
        event.getRegistry().registerAll(
            new PotionSwarm().setRegistryName(MOD_ID, "swarm")
        );
    }
}
