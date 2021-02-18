package com.eerussianguy.firmalife.te;

import javax.annotation.Nonnull;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ITickable;
import net.minecraftforge.items.ItemHandlerHelper;

import com.eerussianguy.firmalife.init.StatePropertiesFL;
import com.eerussianguy.firmalife.recipe.PlanterRecipe;
import com.eerussianguy.firmalife.util.GreenhouseHelpers;
import com.eerussianguy.firmalife.util.IGreenhouseReceiver;
import com.eerussianguy.firmalife.util.IWaterable;
import net.dries007.tfc.ConfigTFC;
import net.dries007.tfc.Constants;
import net.dries007.tfc.objects.te.TEInventory;
import net.dries007.tfc.util.calendar.CalendarTFC;
import net.dries007.tfc.util.calendar.ICalendar;
import net.dries007.tfc.util.calendar.ICalendarTickable;

/**
 * Evil combination of TEInventory and TECropBase because I can't code
 */
public class TEQuadPlanter extends TEInventory implements ITickable, ICalendarTickable, IWaterable, IGreenhouseReceiver
{
    protected int[] stages;
    private long lastUpdateTick;
    private long lastTickCalChecked;
    private int waterUses;
    private boolean isClimateValid;

    public TEQuadPlanter()
    {
        super(4);
        stages = new int[] {0, 0, 0, 0};
        lastUpdateTick = 0;
        lastTickCalChecked = CalendarTFC.PLAYER_TIME.getTicks();
        waterUses = 0;
        isClimateValid = false;
    }

    @Override
    public void update()
    {
        ICalendarTickable.super.checkForCalendarUpdate();
        if (waterUses < 0)
        {
            waterUses = 0;
            world.setBlockState(pos, world.getBlockState(pos).withProperty(StatePropertiesFL.WET, false));
        }
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt)
    {
        stages = nbt.getIntArray("stages");
        lastUpdateTick = nbt.getLong("tick");
        lastTickCalChecked = nbt.getLong("lastTickCalChecked");
        waterUses = nbt.getInteger("waterUses");
        isClimateValid = nbt.getBoolean("isClimateValid");
        super.readFromNBT(nbt);
    }

    @Nonnull
    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound nbt)
    {
        nbt.setIntArray("stages", stages);
        nbt.setLong("tick", lastUpdateTick);
        nbt.setLong("lastTickCalChecked", lastTickCalChecked);
        nbt.setInteger("waterUses", waterUses);
        nbt.setBoolean("isClimateValid", isClimateValid);
        return super.writeToNBT(nbt);
    }

    @Override
    public int getSlotLimit(int slot)
    {
        return 1;
    }

    public void onInsert(int slot)
    {
        stages[slot] = 0;
        markForSync();
        markForBlockUpdate();
    }

    public void grow(int slot)
    {
        PlanterRecipe recipe = getRecipe(slot);
        if (recipe != null && getStage(slot) < PlanterRecipe.getMaxStage(recipe))
        {
            stages[slot] = Math.min(PlanterRecipe.getMaxStage(recipe), getStage(slot) + 1);
            waterUses--;
        }
        markForSync();
    }

    public long getTicksSinceUpdate()
    {
        return CalendarTFC.PLAYER_TIME.getTicks() - lastUpdateTick;
    }

    public void resetCounter()
    {
        lastUpdateTick = CalendarTFC.PLAYER_TIME.getTicks();
        markForSync();
    }

    public void reduceCounter(long amount)
    {
        lastUpdateTick += amount;
        markForSync();
    }

    @Override
    public void onCalendarUpdate(long l)
    {
        long growthTicks = (long) (ICalendar.TICKS_IN_DAY * ConfigTFC.General.FOOD.cropGrowthTimeModifier);
        while (getTicksSinceUpdate() > growthTicks)
        {
            reduceCounter(growthTicks);
            int slot = Constants.RNG.nextInt(4);
            if (canGrow(slot))
            {
                grow(slot);
            }
        }
    }

    @Override
    public long getLastUpdateTick()
    {
        return lastTickCalChecked;
    }

    @Override
    public void setLastUpdateTick(long l)
    {
        lastTickCalChecked = serializeNBT().getLong("lastTickCalChecked");
        markDirty();
    }

    @Override
    public void setValidity(boolean approvalStatus)
    {
        isClimateValid = approvalStatus;
    }

    private boolean canGrow(int slot)
    {
        PlanterRecipe recipe = getRecipe(slot);
        return isClimateValid && recipe != null && getStage(slot) < PlanterRecipe.getMaxStage(recipe) &&
            world.getBlockState(pos).getValue(StatePropertiesFL.WET) && GreenhouseHelpers.isSkylightValid(world, pos);
    }

    public PlanterRecipe getRecipe(int slot)
    {
        ItemStack input = inventory.getStackInSlot(slot);
        PlanterRecipe recipe = null;
        if (!input.isEmpty())
        {
            recipe = PlanterRecipe.get(input);
        }
        return recipe;
    }

    public boolean tryHarvest(EntityPlayer player, int slot)
    {
        PlanterRecipe recipe = getRecipe(slot);
        if (recipe != null && PlanterRecipe.getMaxStage(recipe) == getStage(slot))
        {
            ItemStack returnStack = recipe.getOutputItem(inventory.getStackInSlot(slot));
            ItemHandlerHelper.giveItemToPlayer(player, returnStack);
            ItemHandlerHelper.giveItemToPlayer(player, inventory.getStackInSlot(slot), 1 + Constants.RNG.nextInt());
            inventory.setStackInSlot(slot, ItemStack.EMPTY);
            stages[slot] = 0;
            resetCounter();
            markForSync();
            return true;
        }
        return false;
    }

    public int getStage(int slot)
    {
        return stages[slot];
    }

    @Override
    public void addWater(int amount)
    {
        waterUses += amount;
    }
}
