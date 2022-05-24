package com.eerussianguy.firmalife.common.network;

import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

import org.apache.commons.lang3.mutable.MutableInt;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

import com.eerussianguy.firmalife.common.FLHelpers;
import com.eerussianguy.firmalife.common.util.GreenhouseType;
import net.dries007.tfc.util.DataManager;

public class FLPackets
{
    private static final String VERSION = Integer.toString(1);
    private static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(FLHelpers.identifier("network"), () -> VERSION, VERSION::equals, VERSION::equals);
    private static final MutableInt ID = new MutableInt(0);

    public static void send(PacketDistributor.PacketTarget target, Object message)
    {
        CHANNEL.send(target, message);
    }


    public static void init()
    {
        // S -> C

        registerDataManager(GreenhouseType.Packet.class, GreenhouseType.MANAGER);

        // C -> S
    }

    @SuppressWarnings("unchecked")
    private static <T extends DMSPacket<E>, E> void registerDataManager(Class<T> cls, DataManager<E> manager)
    {
        CHANNEL.registerMessage(ID.getAndIncrement(), cls,
            (packet, buffer) -> packet.encode(manager, buffer),
            buffer -> {
                final T packet = (T) manager.createEmptyPacket();
                packet.decode(manager, buffer);
                return packet;
            },
            (packet, context) -> {
                context.get().setPacketHandled(true);
                context.get().enqueueWork(() -> packet.handle(context.get(), manager));
            });
    }

    private static <T> void register(Class<T> cls, BiConsumer<T, FriendlyByteBuf> encoder, Function<FriendlyByteBuf, T> decoder, BiConsumer<T, NetworkEvent.Context> handler)
    {
        CHANNEL.registerMessage(ID.getAndIncrement(), cls, encoder, decoder, (packet, context) -> {
            context.get().setPacketHandled(true);
            handler.accept(packet, context.get());
        });
    }

    private static <T> void register(Class<T> cls, Supplier<T> factory, BiConsumer<T, NetworkEvent.Context> handler)
    {
        CHANNEL.registerMessage(ID.getAndIncrement(), cls, (packet, buffer) -> {}, buffer -> factory.get(), (packet, context) -> {
            context.get().setPacketHandled(true);
            handler.accept(packet, context.get());
        });
    }
}
