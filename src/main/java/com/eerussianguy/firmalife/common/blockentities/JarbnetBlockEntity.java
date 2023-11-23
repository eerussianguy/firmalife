package com.eerussianguy.firmalife.common.blockentities;

import com.eerussianguy.firmalife.common.FLHelpers;
import com.eerussianguy.firmalife.common.blocks.JarbnetBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.items.ItemStackHandler;

import net.dries007.tfc.common.blockentities.InventoryBlockEntity;
import net.dries007.tfc.common.capabilities.PartialItemHandler;
import net.dries007.tfc.common.items.CandleBlockItem;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.calendar.Calendars;

public class JarbnetBlockEntity extends InventoryBlockEntity<ItemStackHandler>
{
    public static final int SLOTS = 6;

    private long lastUpdateTick = Long.MIN_VALUE;

    public JarbnetBlockEntity(BlockPos pos, BlockState state)
    {
        super(FLBlockEntities.JARBNET.get(), pos, state, defaultInventory(SLOTS), FLHelpers.blockEntityName("jarbnet"));
        sidedInventory
            .on(new PartialItemHandler(inventory).insert(0, 1, 2, 3, 4, 5), Direction.Plane.HORIZONTAL)
            .on(new PartialItemHandler(inventory).extract(0, 1, 2, 3, 4, 5), Direction.DOWN);
    }

    @Override
    public boolean isItemValid(int slot, ItemStack stack)
    {
        return JarbnetBlock.isItemAllowed(stack);
    }

    @Override
    public void setAndUpdateSlots(int slot)
    {
        super.setAndUpdateSlots(slot);
        updateState();
    }

    @Override
    public void onLoadAdditional()
    {
        updateState();
    }

    private void updateState()
    {
        final boolean isLit = getBlockState().hasProperty(JarbnetBlock.LIT) && getBlockState().getValue(JarbnetBlock.LIT);
        if (isLit && level != null)
        {
            for (ItemStack stack : Helpers.iterate(inventory))
            {
                if (stack.getItem() instanceof CandleBlockItem)
                {
                    return;
                }
            }
            level.setBlockAndUpdate(worldPosition, getBlockState().setValue(JarbnetBlock.LIT, false));
        }
        if (level != null)
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_CLIENTS);
    }

    @Override
    public int getSlotStackLimit(int slot)
    {
        return 1;
    }

    public long getTicksSinceUpdate()
    {
        assert this.level != null;

        return Calendars.get(this.level).getTicks() - this.lastUpdateTick;
    }

    public void setLastUpdateTick(long tick)
    {
        this.lastUpdateTick = tick;
        this.setChanged();
    }

    public long getLastUpdateTick()
    {
        return this.lastUpdateTick;
    }

    public void resetCounter()
    {
        this.lastUpdateTick = Calendars.SERVER.getTicks();
        this.setChanged();
    }

    @Override
    public void loadAdditional(CompoundTag nbt)
    {
        this.lastUpdateTick = nbt.getLong("tick");
        super.loadAdditional(nbt);
    }

    @Override
    public void saveAdditional(CompoundTag nbt)
    {
        nbt.putLong("tick", this.lastUpdateTick);
        super.saveAdditional(nbt);
    }
}
