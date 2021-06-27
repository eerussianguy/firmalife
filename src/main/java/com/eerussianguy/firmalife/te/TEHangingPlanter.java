package com.eerussianguy.firmalife.te;

import javax.annotation.Nonnull;

import net.minecraft.nbt.NBTTagCompound;

import com.eerussianguy.firmalife.util.GreenhouseHelpers;
import net.dries007.tfc.objects.te.TETickCounter;

public class TEHangingPlanter extends TETickCounter implements GreenhouseHelpers.IGreenhouseReceiver
{
    private boolean isClimateValid;

    public TEHangingPlanter()
    {
        super();
        isClimateValid = false;
    }

    @Override
    public void setValidity(boolean approvalStatus, int tier)
    {
        isClimateValid = approvalStatus;
        markForSync();
    }

    public boolean isClimateValid()
    {
        return isClimateValid;
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt)
    {
        isClimateValid = nbt.getBoolean("isClimateValid");
        super.readFromNBT(nbt);
    }

    @Nonnull
    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound nbt)
    {
        nbt.setBoolean("isClimateValid", isClimateValid);
        return super.writeToNBT(nbt);
    }
}
