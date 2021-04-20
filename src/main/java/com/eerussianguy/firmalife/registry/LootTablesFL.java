package com.eerussianguy.firmalife.registry;

import java.util.Arrays;

import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.LootTableList;
import net.minecraftforge.event.LootTableLoadEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import com.eerussianguy.firmalife.ConfigFL;

import static com.eerussianguy.firmalife.FirmaLife.MOD_ID;

@Mod.EventBusSubscriber(modid = MOD_ID)

public class LootTablesFL
{
    public static ResourceLocation RENNET_DROP;

    public LootTablesFL() {}

    public static void init()
    {
        RENNET_DROP = register("rennet_drop");
    }

    @SubscribeEvent
    public static void onLootTableLoad(LootTableLoadEvent event)
    {
        if (Arrays.stream(ConfigFL.General.BALANCE.rennetLootTable).anyMatch(x -> x.equals(event.getName().getPath())))
        {
            event.getTable().addPool(event.getLootTableManager().getLootTableFromLocation(RENNET_DROP).getPool("rennet_drop"));
        }
    }

    private static ResourceLocation register(String id)
    {
        return LootTableList.register(new ResourceLocation(MOD_ID, id));
    }
}
