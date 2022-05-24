package com.eerussianguy.firmalife.common.network;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkEvent;

import net.dries007.tfc.network.DataManagerSyncPacket;
import net.dries007.tfc.util.DataManager;

public abstract class DMSPacket<T> extends DataManagerSyncPacket<T>
{
    private Map<ResourceLocation, T> elements;

    public DMSPacket()
    {
        elements = Collections.emptyMap();
    }

    void encode(DataManager<T> manager, FriendlyByteBuf buffer)
    {
        buffer.writeVarInt(elements.size());
        for (Map.Entry<ResourceLocation, T> entry : elements.entrySet())
        {
            buffer.writeResourceLocation(entry.getKey());
            manager.encode(buffer, entry.getValue());
        }
    }

    void decode(DataManager<T> manager, FriendlyByteBuf buffer)
    {
        this.elements = new HashMap<>();
        final int size = buffer.readVarInt();
        for (int i = 0; i < size; i++)
        {
            final ResourceLocation id = buffer.readResourceLocation();
            final T element = manager.decode(id, buffer);
            elements.put(id, element);
        }
    }

    void handle(NetworkEvent.Context context, DataManager<T> manager)
    {
        manager.onSync(context, elements);
    }
}
