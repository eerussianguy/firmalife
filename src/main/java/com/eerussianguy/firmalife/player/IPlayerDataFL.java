package com.eerussianguy.firmalife.player;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
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

    /**
     * @param pos The block position of the last hammering
     */
    void setNutPosition(BlockPos pos);

    /**
     * @return Distance in blocks of how far the last hammering was.
     */
    int getNutDistance(BlockPos pos);
}
