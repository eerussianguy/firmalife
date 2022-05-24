package com.eerussianguy.firmalife.common;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.event.OnDatapackSyncEvent;
import net.minecraftforge.event.world.BiomeLoadingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.network.PacketDistributor;

import com.eerussianguy.firmalife.common.blockentities.FLBlockEntities;
import com.eerussianguy.firmalife.common.blocks.FLBlocks;
import com.eerussianguy.firmalife.common.network.FLPackets;
import com.eerussianguy.firmalife.common.util.GreenhouseType;
import net.dries007.tfc.util.events.StartFireEvent;

public class FLForgeEvents
{
    public static void init()
    {
        final IEventBus bus = MinecraftForge.EVENT_BUS;

        bus.addListener(FLForgeEvents::onBiomeLoad);
        bus.addListener(FLForgeEvents::onFireStart);
        bus.addListener(FLForgeEvents::addReloadListeners);
        bus.addListener(FLForgeEvents::onDataPackSync);
    }

    public static void onBiomeLoad(BiomeLoadingEvent event)
    {

    }

    public static void addReloadListeners(AddReloadListenerEvent event)
    {
        event.addListener(GreenhouseType.MANAGER);
    }

    public static void onDataPackSync(OnDatapackSyncEvent event)
    {
        final ServerPlayer player = event.getPlayer();
        final PacketDistributor.PacketTarget target = player == null ? PacketDistributor.ALL.noArg() : PacketDistributor.PLAYER.with(() -> player);

        FLPackets.send(target, GreenhouseType.MANAGER.createSyncPacket());
    }

    public static void onFireStart(StartFireEvent event)
    {
        Level level = event.getLevel();
        BlockPos pos = event.getPos();
        BlockState state = event.getState();
        Block block = state.getBlock();

        if (block == FLBlocks.OVEN_BOTTOM.get() || block == FLBlocks.CURED_OVEN_BOTTOM.get())
        {
            level.getBlockEntity(pos, FLBlockEntities.OVEN_BOTTOM.get()).ifPresent(oven -> oven.light(state));
            event.setCanceled(true);
        }
    }
}
