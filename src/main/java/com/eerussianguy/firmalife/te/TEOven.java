package com.eerussianguy.firmalife.te;

import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
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
import mcp.MethodsReturnNonnullByDefault;
import net.dries007.tfc.api.capability.food.CapabilityFood;
import net.dries007.tfc.objects.items.ItemsTFC;
import net.dries007.tfc.objects.te.TEInventory;
import net.dries007.tfc.util.calendar.CalendarTFC;
import net.dries007.tfc.util.fuel.FuelManager;

import static com.eerussianguy.firmalife.init.StatePropertiesFL.CURED;
import static net.dries007.tfc.objects.blocks.property.ILightableBlock.LIT;
import static net.minecraft.block.BlockHorizontal.FACING;

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

    public TEOven()
    {
        super(3);
        startTick = 0;
        tickGoal = 0;
        isBurning = false;
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
                    if (isValidHorizontal(true) && hasChimney())
                    {
                        cook();
                    }
                    else
                    {
                        turnOff();
                        clear();
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
        if (slot == SLOT_FUEL_1 || slot == SLOT_FUEL_2)
        {
            return FuelManager.isItemFuel(stack);
        }
        return true;
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt)
    {
        startTick = nbt.getLong("startTick");
        tickGoal = nbt.getInteger("tickGoal");
        isBurning = nbt.getBoolean("isBurning");
        super.readFromNBT(nbt);
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound nbt)
    {
        nbt.setLong("startTick", startTick);
        nbt.setInteger("tickGoal", tickGoal);
        nbt.setBoolean("isBurning", isBurning);
        return super.writeToNBT(nbt);
    }

    @Override
    public boolean canInteractWith(EntityPlayer player)
    {
        return !isBurning && world.getTileEntity(pos) == this;
    }

    public void light()
    {
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

    private boolean hasFuel()
    {
        return !inventory.getStackInSlot(SLOT_FUEL_1).isEmpty() && !inventory.getStackInSlot(SLOT_FUEL_2).isEmpty();
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
        markDirty();
    }

    public void onBreakBlock(World world, BlockPos pos, IBlockState state)
    {
        for (int i = 0; i < 3; i++)
        {
            InventoryHelper.spawnItemStack(world, pos.getX(), pos.getY(), pos.getZ(), inventory.getStackInSlot(i));
        }
    }

    private boolean isCuringRecipe()
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

    // see OvenBlock
    public boolean isValidHorizontal(boolean needsCure)
    {
        BlockPos ovenPos = pos;
        IBlockState ovenState = world.getBlockState(ovenPos);
        EnumFacing facing = ovenState.getValue(FACING);
        EnumFacing left = facing.rotateYCCW();
        EnumFacing right = facing.rotateY();
        IBlockState leftState = world.getBlockState(ovenPos.offset(left));
        IBlockState rightState = world.getBlockState(ovenPos.offset(right));
        IBlockState[] checkStates = {leftState, rightState};

        for (IBlockState state : checkStates)
        {
            if (needsCure && !isCuredBlock(state))
            {
                return false;
            }
            Block b = state.getBlock();
            if (!(b instanceof BlockOven || b instanceof BlockOvenWall))
            {
                return false; // return false if it's not an oven or oven wall
            }
            if (b instanceof BlockOven)
            {
                if (state.getValue(FACING) != ovenState.getValue(FACING))
                {
                    return false; // if it's an oven, it should face the same way
                }
            }
            if (b instanceof BlockOvenWall)
            {
                if (state == leftState)
                {
                    if (leftState.getValue(FACING) != facing.getOpposite())
                    {
                        return false; // if it's a wall, it should be rotated to touch the oven properly
                    }
                }
                if (state == rightState)
                {
                    if (rightState.getValue(FACING) != facing)
                    {
                        return false; // see above
                    }
                }
            }
        }
        return true;
    }

    // see BlockOven, sorta
    public boolean hasChimney()
    {
        BlockPos ovenPos = pos;
        IBlockState ovenState = world.getBlockState(pos);
        EnumFacing facing = ovenState.getValue(FACING);
        EnumFacing left = facing.rotateYCCW();
        EnumFacing right = facing.rotateY();

        BlockPos[] checkPositions = {ovenPos.up(), ovenPos.offset(left).up(), ovenPos.offset(left, 2).up(), ovenPos.offset(right).up(), ovenPos.offset(right, 2).up()};
        boolean noChimneys = true;
        for (BlockPos pos : checkPositions)
        {
            if (world.getBlockState(pos).getBlock() instanceof BlockOvenChimney)
            {
                noChimneys = false;
                for (int i = 0; i < 3; i++)
                {
                    BlockPos chimPos = pos.offset(EnumFacing.UP, i);
                    IBlockState chimState = world.getBlockState(chimPos);
                    if (chimState.getBlock() instanceof BlockOvenChimney)
                    {
                        if (!chimState.getValue(CURED))
                            return false;
                    }
                    else
                    {
                        return false;
                    }
                }
            }
        }
        return !noChimneys;
    }
}
