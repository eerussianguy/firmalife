package com.eerussianguy.firmalife.player;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;

import net.dries007.tfc.api.capability.player.CapabilityPlayerData;
import net.dries007.tfc.util.calendar.CalendarTFC;

public class PlayerDataFL implements ICapabilitySerializable<NBTTagCompound>, IPlayerDataFL
{
    private long nutted;

    public PlayerDataFL()
    {
        this.nutted = 0;
    }


    @Override
    public void setNuttedTime()
    {
        nutted = CalendarTFC.CALENDAR_TIME.getTicks();
    }

    @Override
    public long getNuttedTime()
    {
        return nutted;
    }

    @Override
    public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing)
    {
        return capability == CapabilityPlayerData.CAPABILITY;
    }

    @Nullable
    @Override
    @SuppressWarnings("unchecked")
    public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing)
    {
        return capability == CapPlayerDataFL.CAPABILITY ? (T) this : null;
    }

    @Override
    public NBTTagCompound serializeNBT()
    {
        NBTTagCompound nbt = new NBTTagCompound();
        nbt.setLong("nutted", nutted);
        return nbt;
    }

    @Override
    public void deserializeNBT(NBTTagCompound nbt)
    {
        if (nbt != null)
            nutted = nbt.getLong("nutted");
    }
}
