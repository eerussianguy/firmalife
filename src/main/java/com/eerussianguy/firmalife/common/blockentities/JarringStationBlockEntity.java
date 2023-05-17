package com.eerussianguy.firmalife.common.blockentities;

import com.eerussianguy.firmalife.common.FLHelpers;
import com.eerussianguy.firmalife.common.blocks.JarringStationBlock;
import com.eerussianguy.firmalife.common.items.FLItems;
import com.eerussianguy.firmalife.common.items.JarsBlockItem;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.Tag;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.ItemStackHandler;

import net.dries007.tfc.common.blockentities.TickableInventoryBlockEntity;
import net.dries007.tfc.common.capabilities.Capabilities;
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
            vat.getCapability(Capabilities.FLUID).ifPresent(cap -> {
                final FluidStack fluid = cap.getFluidInTank(0);
                int available = fluid.getAmount() / 500;
                if (available > 0 && fluid.hasTag() && fluid.getTag().contains("fruit", Tag.TAG_COMPOUND))
                {
                    final ItemStack stack = ItemStack.of(fluid.getTag().getCompound("fruit"));
                    for (int i = 0; i < SLOTS; i++)
                    {
                        if (station.inventory.getStackInSlot(i).getItem() == FLItems.EMPTY_JAR.get())
                        {
                            Helpers.playSound(level, pos, SoundEvents.BOTTLE_FILL);
                            station.inventory.setStackInSlot(i, Helpers.copyWithSize(stack, 1));
                            cap.drain(500, IFluidHandler.FluidAction.EXECUTE);
                            available--;
                            station.markForSync();
                            vat.markForSync();
                            station.pourTicks = 45;
                        }
                        if (available == 0) break;
                    }
                }
            });
        }
    }

    public static final int SLOTS = 9;

    private int pourTicks = 0;

    public JarringStationBlockEntity(BlockPos pos, BlockState state)
    {
        super(FLBlockEntities.JARRING_STATION.get(), pos, state, defaultInventory(SLOTS), FLHelpers.blockEntityName("jarring_station"));
        sidedInventory
            .on(new PartialItemHandler(inventory).extract(0, 1, 2, 3, 4, 5, 6, 7, 8), Direction.DOWN)
            .on(new PartialItemHandler(inventory).insert(0, 1, 2, 3, 4, 5, 6, 7, 8), Direction.Plane.HORIZONTAL);
    }

    public int getPourTicks()
    {
        return pourTicks;
    }

    @Override
    public boolean isItemValid(int slot, ItemStack stack)
    {
        return stack.getItem() instanceof JarsBlockItem || stack.getItem() == FLItems.EMPTY_JAR.get();
    }

    @Override
    public int getSlotStackLimit(int slot)
    {
        return 1;
    }
}
