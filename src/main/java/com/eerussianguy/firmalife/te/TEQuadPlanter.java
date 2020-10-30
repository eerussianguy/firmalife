package com.eerussianguy.firmalife.te;

import javax.annotation.Nonnull;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ITickable;
import net.minecraftforge.items.ItemHandlerHelper;

import com.eerussianguy.firmalife.init.StatePropertiesFL;
import com.eerussianguy.firmalife.recipe.PlanterRecipe;
import net.dries007.tfc.Constants;
import net.dries007.tfc.objects.te.TEInventory;
import net.dries007.tfc.util.calendar.CalendarTFC;
import net.dries007.tfc.util.calendar.ICalendar;

public class TEQuadPlanter extends TEInventory implements ITickable
{
    protected int[] stages;
    private long startTick;
    private static final int DAYS_TO_GROW = ICalendar.TICKS_IN_DAY;

    public TEQuadPlanter()
    {
        super(4);
        stages = new int[] {0, 0, 0, 0};
        startTick = 0;
    }

    @Override
    public void update()
    {
        for (int i = 0; i < 4; i++)
        {
            if (checkIsGrowing(i))
            {
                float tickDiff = CalendarTFC.PLAYER_TIME.getTicks() - startTick;
                if (tickDiff > DAYS_TO_GROW)
                {
                    int growAmount = (int) Math.floor(tickDiff / DAYS_TO_GROW);
                    if (growAmount >= 1)
                        grow(growAmount, i);
                }
            }
        }
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt)
    {
        stages = nbt.getIntArray("stages");
        super.readFromNBT(nbt);
    }

    @Nonnull
    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound nbt)
    {
        nbt.setIntArray("stages", stages);
        return super.writeToNBT(nbt);
    }

    public void onInsert(int slot)
    {
        stages[slot] = 0;
        startTick = CalendarTFC.PLAYER_TIME.getTicks();
        markForSync();
    }

    public void grow(int growAmount, int slot)
    {
        PlanterRecipe recipe = getRecipe(slot);
        if (recipe != null)
        {
            stages[slot] = Math.min(PlanterRecipe.getMaxStage(recipe), stages[slot] + growAmount);
            startTick = CalendarTFC.PLAYER_TIME.getTicks();
        }
        markForSync();
    }

    private boolean checkIsGrowing(int slot)
    {
        PlanterRecipe recipe = getRecipe(slot);
        return recipe != null && world.getBlockState(pos).getValue(StatePropertiesFL.WET) && stages[slot] < PlanterRecipe.getMaxStage(recipe); //and in a greenhouse
    }


    public PlanterRecipe getRecipe(int slot)
    {
        ItemStack input = inventory.getStackInSlot(slot);
        PlanterRecipe recipe = null;
        if (!input.isEmpty() && !world.isRemote)
        {
            recipe = PlanterRecipe.get(input);
        }
        return recipe;
    }

    public void tryHarvest(EntityPlayer player, int slot)
    {
        PlanterRecipe recipe = getRecipe(slot);
        if (recipe != null && PlanterRecipe.getMaxStage(recipe) == stages[slot])
        {
            ItemStack returnStack = recipe.getOutputItem(inventory.getStackInSlot(slot));
            ItemHandlerHelper.giveItemToPlayer(player, returnStack);
            ItemHandlerHelper.giveItemToPlayer(player, inventory.getStackInSlot(slot), 1 + Constants.RNG.nextInt());
            inventory.setStackInSlot(slot, ItemStack.EMPTY);
            stages[slot] = 0;
            startTick = CalendarTFC.PLAYER_TIME.getTicks();
        }
        markForSync();
    }

    public int getStage(int slot)
    {
        return stages[slot];
    }

}
