package com.eerussianguy.firmalife.common.util;

import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

import org.apache.commons.lang3.mutable.MutableInt;
import net.minecraft.network.FriendlyByteBuf;

import com.eerussianguy.firmalife.common.FLHelpers;

import net.dries007.tfc.network.PacketHandler;
import net.dries007.tfc.util.data.DataManager;
import net.dries007.tfc.util.data.DataManagers;

public class FLDataManagers
{
    public static void init()
    {
        register(GreenhouseType.MANAGER);
        register(Plantable.MANAGER);

    }

    private static void register(DataManager<?> manager)
    {
        DataManagers.MANAGERS.register(manager.getName(), () -> manager);
    }
}
