package com.eerussianguy.firmalife.common.capabilities.player;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class FLPlayerData implements ICapabilitySerializable<CompoundTag>
{
    private final Player player;
    private final LazyOptional<FLPlayerData> cap;

    public FLPlayerData(Player player)
    {
        this.player = player;
        this.cap = LazyOptional.of(() -> this);
    }

    @NotNull
    @Override
    public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side)
    {
        if (cap == FLPlayerDataCapability.CAPABILITY)
        {
            return this.cap.cast();
        }
        return LazyOptional.empty();
    }

    @Override
    public CompoundTag serializeNBT()
    {
        final CompoundTag nbt = new CompoundTag();
        //nbt.putLong("lastSeen", timePlayed);
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt)
    {
        //timePlayed = nbt.getLong("lastSeen");
    }
}
