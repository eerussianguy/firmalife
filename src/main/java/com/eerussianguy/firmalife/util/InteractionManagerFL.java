package com.eerussianguy.firmalife.util;

import com.eerussianguy.firmalife.gui.FLGuiHandler;
import com.eerussianguy.firmalife.registry.ModRegistry;
import net.dries007.tfc.util.Helpers;
import net.minecraft.item.Item;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import static com.eerussianguy.firmalife.FirmaLife.MOD_ID;

@Mod.EventBusSubscriber(modid = MOD_ID)
public class InteractionManagerFL
{

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onRightClickItem(PlayerInteractEvent.RightClickItem event)
    {
        if(event.getItemStack().getItem() == Item.getItemFromBlock(ModRegistry.PUMPKIN_FRUIT) &&
           Helpers.playerHasItemMatchingOre(event.getEntityPlayer().inventory, "knife") &&
           !event.getWorld().isRemote)
        {
            FLGuiHandler.openGui(event.getWorld(), event.getPos(), event.getEntityPlayer(), FLGuiHandler.Type.KNAPPING_PUMPKIN);
        }
    }
}
