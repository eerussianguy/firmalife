package com.eerussianguy.firmalife.te;

import net.minecraft.block.state.IBlockState;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import com.eerussianguy.firmalife.init.DryingRecipe;
import net.dries007.tfc.api.capability.food.CapabilityFood;
import net.dries007.tfc.objects.te.TEInventory;
import net.dries007.tfc.util.calendar.CalendarTFC;

public class TELeafMat extends TEInventory implements ITickable
{
    private long startTick;
    private int tickGoal;

    public TELeafMat()
    {
        super(1);
        startTick = 0;
        tickGoal = 0;
    }

    @Override
    public boolean isItemValid(int slot, ItemStack stack)
    {
        return true;
    }

    @Override
    public void update()
    {
        if (!world.isRemote)
        {
            if (world.isRaining())
            {
                if (world.getTotalWorldTime() % 5 == 0)
                {
                    tickGoal++;
                    return;
                }
            }
            if ((int) (CalendarTFC.PLAYER_TIME.getTicks() - startTick) > tickGoal)
            {
                if (recipeExists())
                {
                    dry();
                }
            }
        }
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt)
    {
        startTick = nbt.getLong("startTick");
        tickGoal = nbt.getInteger("tickGoal");
        super.readFromNBT(nbt);
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound nbt)
    {
        nbt.setLong("startTick", startTick);
        nbt.setInteger("tickGoal", tickGoal);
        return super.writeToNBT(nbt);
    }

    private boolean recipeExists()
    {
        ItemStack input = inventory.getStackInSlot(0);
        DryingRecipe recipe = null;
        if (!input.isEmpty() && !world.isRemote)
        {
            recipe = DryingRecipe.get(input);
        }
        return recipe != null;
    }

    private void setDuration()
    {
        ItemStack input = inventory.getStackInSlot(0);
        int recipeTime = 0;
        if (!input.isEmpty() && !world.isRemote)
        {
            DryingRecipe recipe = DryingRecipe.get(input);
            if (recipe != null)
            {
                recipeTime = DryingRecipe.getDuration(recipe);
            }
        }
        tickGoal = recipeTime;
    }

    private void dry()
    {
        ItemStack input = inventory.getStackInSlot(0);
        if (!input.isEmpty())
        {
            DryingRecipe recipe = DryingRecipe.get(input);
            if (recipe != null && !world.isRemote)
            {
                inventory.setStackInSlot(0, CapabilityFood.updateFoodFromPrevious(input, recipe.getOutputItem(input)));
                clear();
                setAndUpdateSlots(0);
            }
        }
        markDirty();
    }

    public void onBreakBlock(World world, BlockPos pos, IBlockState state)
    {
        InventoryHelper.spawnItemStack(world, pos.getX(), pos.getY(), pos.getZ(), inventory.getStackInSlot(0));
    }

    public void clear()
    {
        startTick = 0;
        tickGoal = 0;
        markDirty();
    }

    public void deleteSlot()
    {
        inventory.setStackInSlot(0, ItemStack.EMPTY);
    }

    public void start()
    {
        if (recipeExists())
        {
            startTick = CalendarTFC.PLAYER_TIME.getTicks();
            setDuration();
        }
        else
        {
            InventoryHelper.spawnItemStack(world, pos.getX(), pos.getY(), pos.getZ(), inventory.getStackInSlot(0));
            deleteSlot();
        }
        markDirty();
    }
}
