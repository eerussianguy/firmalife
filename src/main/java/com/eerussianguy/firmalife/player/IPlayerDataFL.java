package com.eerussianguy.firmalife.player;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.INBTSerializable;

public interface IPlayerDataFL extends INBTSerializable<NBTTagCompound>
{
    /**
     * Sets the time the player last hit a nut tree to current time
     */
    void setNuttedTime();

    /**
     * Retrieves the time the player last hit a nut tree
     *
     * @return
     */
    long getNuttedTime();
}
