package com.eerussianguy.firmalife.te;

import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.nbt.NBTTagCompound;

import mcp.MethodsReturnNonnullByDefault;
import net.dries007.tfc.objects.te.TEBase;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class TEClimateStation extends TEBase
{
    public int forward;
    public int arcs;
    public int height;
    public boolean isSeeded;

    public TEClimateStation()
    {
        forward = 0;
        arcs = 0;
        height = 0;
        isSeeded = false;
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound nbt)
    {
        nbt.setInteger("forward", forward);
        nbt.setInteger("arcs", arcs);
        nbt.setInteger("height", height);
        nbt.setBoolean("seeded", isSeeded);
        return super.writeToNBT(nbt);
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt)
    {
        forward = nbt.getInteger("forward");
        arcs = nbt.getInteger("arcs");
        height = nbt.getInteger("height");
        isSeeded = nbt.getBoolean("seeded");
        super.readFromNBT(nbt);
    }

    public void setPositions(int forward, int arcs, int height)
    {
        this.forward = forward;
        this.arcs = arcs;
        this.height = height;
        isSeeded = true;
    }
}
