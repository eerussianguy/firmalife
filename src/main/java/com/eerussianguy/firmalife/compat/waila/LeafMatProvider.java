package com.eerussianguy.firmalife.compat.waila;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.annotation.Nonnull;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraftforge.items.CapabilityItemHandler;

import com.eerussianguy.firmalife.recipe.DryingRecipe;
import com.eerussianguy.firmalife.te.TELeafMat;
import net.dries007.tfc.ConfigTFC;
import net.dries007.tfc.compat.waila.interfaces.IWailaBlock;
import net.dries007.tfc.util.calendar.ICalendar;

public class LeafMatProvider implements IWailaBlock
{
    @Nonnull
    @Override
    public List<String> getTooltip(World world, @Nonnull BlockPos pos, @Nonnull NBTTagCompound nbt)
    {
        List<String> currentTooltip = new ArrayList<>();
        TileEntity te = world.getTileEntity(pos);
        if (te instanceof TELeafMat)
        {
            TELeafMat leafMat = (TELeafMat) te;
            ItemStack mainSlot = leafMat.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null).getStackInSlot(0);
            DryingRecipe recipe = DryingRecipe.get(mainSlot);
            if (!mainSlot.isEmpty() && recipe != null)
            {
                long remainingTicks = leafMat.getTicksRemaining();
                switch (ConfigTFC.Client.TOOLTIP.timeTooltipMode)
                {
                    case NONE:
                        break;
                    case TICKS:
                        currentTooltip.add(new TextComponentTranslation("waila.tfc.devices.ticks_remaining", remainingTicks).getFormattedText());
                    case MINECRAFT_HOURS:
                        long remainingHours = Math.round(remainingTicks / (float) ICalendar.TICKS_IN_HOUR);
                        currentTooltip.add(new TextComponentTranslation("waila.tfc.devices.hours_remaining", remainingHours).getFormattedText());
                        break;
                    case REAL_MINUTES:
                        long remainingMinutes = Math.round(remainingTicks / 1200.0f);
                        currentTooltip.add(new TextComponentTranslation("waila.tfc.devices.minutes_remaining", remainingMinutes).getFormattedText());
                        break;
                }
                currentTooltip.add(new TextComponentTranslation(recipe.getOutputItem(mainSlot).getTranslationKey() + ".name").getFormattedText());
            }
        }
        return currentTooltip;
    }

    @Nonnull
    @Override
    public List<Class<?>> getLookupClass()
    {
        return Collections.singletonList(TELeafMat.class);
    }
}
