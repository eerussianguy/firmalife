package com.eerussianguy.firmalife.common.blockentities;

import com.eerussianguy.firmalife.common.FLHelpers;
import com.eerussianguy.firmalife.common.blocks.ICure;
import com.eerussianguy.firmalife.common.blocks.OvenHopperBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.items.ItemStackHandler;

import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.blockentities.TickableInventoryBlockEntity;
import net.dries007.tfc.common.capabilities.Capabilities;
import net.dries007.tfc.common.capabilities.PartialItemHandler;
import net.dries007.tfc.util.Helpers;

public class OvenHopperBlockEntity extends TickableInventoryBlockEntity<ItemStackHandler>
{
    public static void serverTick(Level level, BlockPos pos, BlockState state, OvenHopperBlockEntity hopper)
    {
        hopper.checkForLastTickSync();
        if ((level.getGameTime() + 10) % 20 == 0) // insert
        {
            Helpers.gatherAndConsumeItems(level, new AABB(0, 1, 0, 1, 1.5125, 1).move(pos), hopper.inventory, 0, SLOTS - 1);
        }
        if (level.getGameTime() % 40 == 0) // extract
        {
            final Direction dir = state.getValue(OvenHopperBlock.FACING);
            final BlockPos offsetPos = pos.relative(dir);
            if (level.getBlockEntity(offsetPos) instanceof OvenBottomBlockEntity oven)
            {
                final int idx = hopper.getQueuedSlot();
                if (idx != -1)
                {
                    final ItemStack stack = hopper.inventory.getStackInSlot(idx);
                    oven.getCapability(Capabilities.ITEM).ifPresent(inv -> {
                        final ItemStack newStack = stack.copy();
                        newStack.setCount(1);
                        if (inv.insertItem(OvenBottomBlockEntity.SLOT_FUEL_MAX, newStack, true).isEmpty())
                        {
                            inv.insertItem(OvenBottomBlockEntity.SLOT_FUEL_MAX, newStack, false);
                            stack.shrink(1);
                            oven.markForSync();
                            hopper.markForSync();
                            Helpers.playSound(level, pos, SoundEvents.WOOD_PLACE);
                        }
                    });
                }
            }
        }
    }
    public static final int SLOTS = 4;

    public OvenHopperBlockEntity(BlockPos pos, BlockState state)
    {
        super(FLBlockEntities.OVEN_HOPPER.get(), pos, state, defaultInventory(SLOTS), FLHelpers.blockEntityName("oven_hopper"));

        sidedInventory
            .on(new PartialItemHandler(inventory).insert(0, 1, 2, 3), Direction.UP)
            .on(new PartialItemHandler(inventory).extract(0, 1, 2, 3), state.hasProperty(OvenHopperBlock.FACING) ? state.getValue(OvenHopperBlock.FACING) : Direction.NORTH);
    }

    public int getQueuedSlot()
    {
        for (int i = 0; i < SLOTS; i++)
        {
            if (!inventory.getStackInSlot(i).isEmpty())
            {
                return i;
            }
        }
        return -1;
    }

    @Override
    public boolean isItemValid(int slot, ItemStack stack)
    {
        return Helpers.isItem(stack, TFCTags.Items.FIREPIT_FUEL);
    }

    @Override
    public int getSlotStackLimit(int slot)
    {
        return 4;
    }
}
