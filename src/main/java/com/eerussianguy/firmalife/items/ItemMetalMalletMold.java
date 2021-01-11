package com.eerussianguy.firmalife.items;

import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.FluidTankPropertiesWrapper;
import net.minecraftforge.fluids.capability.IFluidTankProperties;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import net.dries007.tfc.api.capability.IMoldHandler;
import net.dries007.tfc.api.capability.heat.CapabilityItemHeat;
import net.dries007.tfc.api.capability.heat.Heat;
import net.dries007.tfc.api.capability.heat.ItemHeatHandler;
import net.dries007.tfc.api.types.Metal;
import net.dries007.tfc.objects.fluids.FluidsTFC;
import net.dries007.tfc.objects.items.ceramics.ItemPottery;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.calendar.CalendarTFC;

public class ItemMetalMalletMold extends ItemPottery
{
    private final String toolName;

    public ItemMetalMalletMold(String toolName)
    {
        this.toolName = toolName;
    }

    public String getToolName()
    {
        return toolName;
    }

    @Nullable
    @Override
    public ICapabilityProvider initCapabilities(@Nonnull ItemStack stack, @Nullable NBTTagCompound nbt)
    {
        return new FilledMoldCapability(nbt);
    }

    @Override
    public int getItemStackLimit(ItemStack stack)
    {
        IMoldHandler moldHandler = (IMoldHandler) stack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, null);
        if (moldHandler != null && moldHandler.getMetal() != null)
        {
            return 1;
        }
        return super.getItemStackLimit(stack);
    }

    // Extends ItemHeatHandler for ease of use


    public class FilledMoldCapability extends ItemHeatHandler implements ICapabilityProvider, IMoldHandler
    {
        private final FluidTank tank;
        private IFluidTankProperties[] fluidTankProperties;

        public FilledMoldCapability(@Nullable NBTTagCompound nbt)
        {
            tank = new FluidTank(100);

            if (nbt != null)
                deserializeNBT(nbt);
        }

        @Nullable
        @Override
        public Metal getMetal()
        {
            return tank.getFluid() != null ? FluidsTFC.getMetalFromFluid(tank.getFluid().getFluid()) : null;
        }

        @Override
        public int getAmount()
        {
            return tank.getFluidAmount();
        }

        @Override
        public IFluidTankProperties[] getTankProperties()
        {
            if (fluidTankProperties == null)
            {
                fluidTankProperties = new IFluidTankProperties[] {new FluidTankPropertiesWrapper(tank)};
            }
            return fluidTankProperties;
        }

        public int fill(FluidStack resource, boolean doFill)
        {
            if (resource != null)
            {
                Metal metal = FluidsTFC.getMetalFromFluid(resource.getFluid());
                if (metal != null && Metal.ItemType.PROPICK_HEAD.hasMold(metal))
                {
                    int fillAmount = this.tank.fill(resource, doFill);
                    if (fillAmount == this.tank.getFluidAmount())
                    {
                        this.updateFluidData();
                    }

                    return fillAmount;
                }
            }

            return 0;
        }

        @Nullable
        @Override
        public FluidStack drain(FluidStack resource, boolean doDrain)
        {
            return getTemperature() >= meltTemp ? tank.drain(resource, doDrain) : null;
        }

        @Nullable
        @Override
        public FluidStack drain(int maxDrain, boolean doDrain)
        {
            if (getTemperature() > meltTemp)
            {
                FluidStack stack = tank.drain(maxDrain, doDrain);
                if (tank.getFluidAmount() == 0)
                {
                    updateFluidData();
                }
                return stack;
            }
            return null;
        }

        @SideOnly(Side.CLIENT)
        @Override
        public void addHeatInfo(@Nonnull ItemStack stack, @Nonnull List<String> text)
        {
            Metal metal = getMetal();
            if (metal != null)
            {
                String desc = TextFormatting.DARK_GREEN + I18n.format(Helpers.getTypeName(metal)) + ": " + I18n.format("tfc.tooltip.units", getAmount());
                if (this.isMolten())
                {
                    desc += I18n.format("tfc.tooltip.liquid");
                }
                text.add(desc);
            }
            IMoldHandler.super.addHeatInfo(stack, text);
        }

        @Override
        public float getHeatCapacity()
        {
            return heatCapacity;
        }

        @Override
        public float getMeltTemp()
        {
            return meltTemp;
        }

        @Override
        public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing)
        {
            return capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY
                || capability == CapabilityItemHeat.ITEM_HEAT_CAPABILITY;
        }

        @Nullable
        @Override
        @SuppressWarnings("unchecked")
        public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing)
        {
            return hasCapability(capability, facing) ? (T) this : null;
        }

        @Override
        @Nonnull
        public NBTTagCompound serializeNBT()
        {
            NBTTagCompound nbt = new NBTTagCompound();
            float temp = getTemperature();
            nbt.setFloat("heat", temp);
            if (temp <= 0)
            {
                nbt.setLong("ticks", -1);
            }
            else
            {
                nbt.setLong("ticks", CalendarTFC.PLAYER_TIME.getTicks());
            }
            return tank.writeToNBT(nbt);
        }

        @Override
        public void deserializeNBT(NBTTagCompound nbt)
        {
            if (nbt != null)
            {
                temperature = nbt.getFloat("heat");
                lastUpdateTick = nbt.getLong("ticks");
                tank.readFromNBT(nbt);
            }
            updateFluidData();
        }

        private void updateFluidData()
        {
            updateFluidData(tank.getFluid());
        }

        @SuppressWarnings("ConstantConditions")
        private void updateFluidData(FluidStack fluid)
        {
            meltTemp = Heat.maxVisibleTemperature();
            heatCapacity = 1;
            if (fluid != null)
            {
                Metal metal = FluidsTFC.getMetalFromFluid(fluid.getFluid());
                if (metal != null)
                {
                    meltTemp = metal.getMeltTemp();
                    heatCapacity = metal.getSpecificHeat();
                }
            }
        }
    }
}
