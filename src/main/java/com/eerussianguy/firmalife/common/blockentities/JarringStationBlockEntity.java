package com.eerussianguy.firmalife.common.blockentities;

import com.eerussianguy.firmalife.FirmaLife;
import com.eerussianguy.firmalife.common.blocks.JarringStationBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.ItemStackHandler;

import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.blockentities.TickableInventoryBlockEntity;
import net.dries007.tfc.common.capabilities.PartialItemHandler;
import net.dries007.tfc.util.Helpers;

public class JarringStationBlockEntity extends TickableInventoryBlockEntity<ItemStackHandler>
{
    public static void tick(Level level, BlockPos pos, BlockState state, JarringStationBlockEntity station)
    {
        station.checkForLastTickSync();
        if (station.pourTicks > 0) station.pourTicks--;

        if (level.getGameTime() % 60 == 0 && state.hasProperty(JarringStationBlock.FACING) && level.getBlockEntity(pos.relative(state.getValue(JarringStationBlock.FACING))) instanceof VatBlockEntity vat)
        {
            if (vat.hasOutput())
            {
                final ItemStack jars = vat.getOutput();
                for (int i = 0; i < SLOTS; i++)
                {
                    if (Helpers.isItem(station.inventory.getStackInSlot(i).getItem(), TFCTags.Items.EMPTY_JAR_WITH_LID))
                    {
                        Helpers.playSound(level, pos, SoundEvents.BOTTLE_FILL);
                        station.inventory.setStackInSlot(i, jars.split(1));
                        station.markForSync();
                        vat.markForSync();
                        station.pourTicks = 45;
                    }
                    if (jars.isEmpty())
                        break;
                }
            }
        }
    }

    public static final int SLOTS = 9;

    private int pourTicks = 0;

    public JarringStationBlockEntity(BlockPos pos, BlockState state)
    {
        super(FLBlockEntities.JARRING_STATION.get(), pos, state, defaultInventory(SLOTS), FirmaLife.MOD_ID);
        sidedInventory
            .on(new PartialItemHandler(inventory).extractAll(), Direction.DOWN)
            .on(new PartialItemHandler(inventory).insertAll(), Direction.Plane.HORIZONTAL);
    }

    @Override
    public void setAndUpdateSlots(int slot)
    {
        super.setAndUpdateSlots(slot);
        if (level != null)
        {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_CLIENTS);
        }
    }

    @Override
    public boolean isItemValid(int slot, ItemStack stack)
    {
        return Helpers.isItem(stack, TFCTags.Items.EMPTY_JAR_WITH_LID);
    }

    @Override
    public int getSlotStackLimit(int slot)
    {
        return 1;
    }
}
