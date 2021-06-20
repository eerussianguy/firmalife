package com.eerussianguy.firmalife.te;

import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import com.eerussianguy.firmalife.blocks.BlockOven;
import com.eerussianguy.firmalife.blocks.BlockOvenChimney;
import com.eerussianguy.firmalife.blocks.BlockOvenWall;
import com.eerussianguy.firmalife.recipe.OvenRecipe;
import com.eerussianguy.firmalife.registry.BlocksFL;
import mcp.MethodsReturnNonnullByDefault;
import net.dries007.tfc.api.capability.food.CapabilityFood;
import net.dries007.tfc.objects.items.ItemsTFC;
import net.dries007.tfc.objects.te.TEInventory;
import net.dries007.tfc.util.calendar.CalendarTFC;
import net.dries007.tfc.util.calendar.ICalendar;
import net.dries007.tfc.util.fuel.FuelManager;

import static com.eerussianguy.firmalife.init.StatePropertiesFL.CURED;
import static net.dries007.tfc.objects.blocks.property.ILightableBlock.LIT;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class TEOven extends TEInventory implements ITickable
{
    public static final int SLOT_FUEL_1 = 0;
    public static final int SLOT_FUEL_2 = 1;
    public static final int SLOT_MAIN = 2;

    private long startTick;
    private int tickGoal;
    private boolean isBurning;
    private boolean isWarmed;
    private long offTick;

    public TEOven()
    {
        super(3);
        startTick = 0;
        tickGoal = 0;
        isBurning = false;
        isWarmed = false;
        offTick = 0;
    }

    @Override
    public void update()
    {
        if (!world.isRemote)
        {
            if (isBurning)
            {
                if ((int) (CalendarTFC.PLAYER_TIME.getTicks() - startTick) > tickGoal)
                {
                    if (isCuringRecipe())
                    {
                        cureSelfWallsAndChimney();
                        cook();
                        return;
                    }
                    if (BlockOven.isValidHorizontal(world, pos, true) && BlockOven.hasChimney(world, pos, true))
                    {
                        cook();
                    }
                    else
                    {
                        turnOff();
                        clear();
                        world.setBlockToAir(pos);
                        world.setBlockState(pos, Blocks.CLAY.getDefaultState());
                        return;
                    }
                }
                if (inventory.getStackInSlot(SLOT_MAIN).isEmpty())
                    turnOff();
            }
            else
            {
                turnOff();
            }
        }
    }

    @Override
    public int getSlotLimit(int slot)
    {
        return 1;
    }

    @Override
    public boolean isItemValid(int slot, ItemStack stack)
    {
        return (slot < SLOT_MAIN) == FuelManager.isItemFuel(stack);
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt)
    {
        startTick = nbt.getLong("startTick");
        tickGoal = nbt.getInteger("tickGoal");
        isBurning = nbt.getBoolean("isBurning");
        isWarmed = nbt.getBoolean("isWarmed");
        offTick = nbt.getLong("offTick");
        super.readFromNBT(nbt);
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound nbt)
    {
        nbt.setLong("startTick", startTick);
        nbt.setInteger("tickGoal", tickGoal);
        nbt.setBoolean("isBurning", isBurning);
        nbt.setBoolean("isWarmed", isWarmed);
        nbt.setLong("offTick", offTick);
        return super.writeToNBT(nbt);
    }

    @Override
    public boolean canInteractWith(EntityPlayer player)
    {
        return !isBurning && world.getTileEntity(pos) == this;
    }

    public void light()
    {
        if (world != null)
        {
            Block block = world.getBlockState(pos).getBlock();
            if (block instanceof BlockOven)
                world.scheduleBlockUpdate(pos, BlocksFL.OVEN, 1, 1);
        }
        if (recipeExists() && hasFuel())
        {
            isBurning = true;
            startTick = CalendarTFC.PLAYER_TIME.getTicks();
            setDuration();
        }
        else
        {
            turnOff();
        }
        markDirty();
    }

    private boolean recipeExists()
    {
        ItemStack input = inventory.getStackInSlot(SLOT_MAIN);
        OvenRecipe recipe = null;
        if (!input.isEmpty() && !world.isRemote)
        {
            recipe = OvenRecipe.get(input);
        }
        return recipe != null;
    }

    public void setWarmed()
    {
        isWarmed = true;
    }

    private boolean hasFuel()
    {
        return isWarmed || (FuelManager.isItemFuel(inventory.getStackInSlot(SLOT_FUEL_1)) && FuelManager.isItemFuel(inventory.getStackInSlot(SLOT_FUEL_2)));
    }

    private void setDuration()
    {
        ItemStack input = inventory.getStackInSlot(SLOT_MAIN);
        int recipeTime = 0;
        if (!input.isEmpty() && !world.isRemote)
        {
            OvenRecipe recipe = OvenRecipe.get(input);
            if (recipe != null)
            {
                recipeTime = OvenRecipe.getDuration(recipe);
            }
        }
        tickGoal = recipeTime;
    }

    private void cook()
    {
        ItemStack input = inventory.getStackInSlot(SLOT_MAIN);
        if (!input.isEmpty())
        {
            OvenRecipe recipe = OvenRecipe.get(input);
            if (recipe != null && !world.isRemote)
            {
                inventory.setStackInSlot(SLOT_MAIN, CapabilityFood.updateFoodFromPrevious(input, recipe.getOutputItem(input)));
                inventory.setStackInSlot(SLOT_FUEL_1, ItemStack.EMPTY);
                inventory.setStackInSlot(SLOT_FUEL_2, ItemStack.EMPTY);
                setAndUpdateSlots(SLOT_MAIN);
                setAndUpdateSlots(SLOT_FUEL_1);
                setAndUpdateSlots(SLOT_FUEL_2);
            }
            turnOff();
        }
    }

    private void clear()
    {
        inventory.setStackInSlot(SLOT_MAIN, ItemStack.EMPTY);
        inventory.setStackInSlot(SLOT_FUEL_1, ItemStack.EMPTY);
        inventory.setStackInSlot(SLOT_FUEL_2, ItemStack.EMPTY);
        setAndUpdateSlots(SLOT_MAIN);
        setAndUpdateSlots(SLOT_FUEL_1);
        setAndUpdateSlots(SLOT_FUEL_2);
    }


    public void turnOff()
    {
        world.setBlockState(pos, world.getBlockState(pos).withProperty(LIT, false));
        isBurning = false;
        startTick = 0;
        tickGoal = 0;
        offTick = CalendarTFC.PLAYER_TIME.getTicks();
        isWarmed = false;
        markDirty();
    }

    public boolean willDamage()
    {
        return (CalendarTFC.PLAYER_TIME.getTicks() - offTick) > (2 * ICalendar.TICKS_IN_HOUR);
    }

    public void onBreakBlock(World world, BlockPos pos, IBlockState state)
    {
        for (int i = 0; i < 3; i++)
        {
            InventoryHelper.spawnItemStack(world, pos.getX(), pos.getY(), pos.getZ(), inventory.getStackInSlot(i));
        }
    }

    public boolean isCuringRecipe()
    {
        if (recipeExists())
        {
            ItemStack input = inventory.getStackInSlot(SLOT_MAIN);
            return input.isItemEqual(new ItemStack(ItemsTFC.STRAW));
        }
        return false;
    }

    private boolean isCuredBlock(IBlockState state)
    {
        if ((state.getBlock() instanceof BlockOven || state.getBlock() instanceof BlockOvenChimney) || state.getBlock() instanceof BlockOvenWall)
        {
            return state.getValue(CURED);
        }
        return false;
    }

    private void cureSelfWallsAndChimney()
    {
        IBlockState state = world.getBlockState(pos);
        if (!isCuredBlock(state))
            world.setBlockState(pos, state.withProperty(CURED, true));
        for (EnumFacing side : EnumFacing.HORIZONTALS)
        {
            BlockPos changePos = pos.offset(side);
            IBlockState changeState = world.getBlockState(changePos);
            if (changeState.getBlock() instanceof BlockOvenWall && !isCuredBlock(changeState))
            {
                world.setBlockState(changePos, changeState.withProperty(CURED, true));
            }
        }
        BlockPos chimPos = pos.up();
        while (world.getBlockState(chimPos).getBlock() instanceof BlockOvenChimney)
        {
            world.setBlockState(chimPos, world.getBlockState(chimPos).withProperty(CURED, true));
            chimPos = chimPos.up();
        }
    }

    public long getTicksRemaining() {
        return tickGoal - (CalendarTFC.PLAYER_TIME.getTicks() - startTick);
    }
}
