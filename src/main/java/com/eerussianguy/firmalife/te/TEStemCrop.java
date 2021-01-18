package com.eerussianguy.firmalife.te;

import javax.annotation.Nonnull;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;

import net.dries007.tfc.Constants;
import net.dries007.tfc.objects.te.TECropBase;

public class TEStemCrop extends TECropBase
{
    private EnumFacing fruitDirection = EnumFacing.Plane.HORIZONTAL.random(Constants.RNG);

    @Override
    @Nonnull
    public NBTTagCompound writeToNBT(NBTTagCompound tag)
    {
        tag.setInteger("fruitDirection", fruitDirection.getIndex());
        return super.writeToNBT(tag);
    }

    @Override
    public void readFromNBT(NBTTagCompound tag)
    {
        fruitDirection = EnumFacing.byIndex(tag.getInteger("fruitDirection"));
        super.readFromNBT(tag);
    }

    public EnumFacing getFruitDirection()
    {
        return fruitDirection;
    }
}
