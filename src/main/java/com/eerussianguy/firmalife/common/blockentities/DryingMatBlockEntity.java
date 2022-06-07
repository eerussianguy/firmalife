package com.eerussianguy.firmalife.common.blockentities;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.items.ItemStackHandler;

import com.eerussianguy.firmalife.common.FLHelpers;
import com.eerussianguy.firmalife.common.recipes.DryingRecipe;
import net.dries007.tfc.common.blockentities.InventoryBlockEntity;
import net.dries007.tfc.common.recipes.inventory.ItemStackInventory;
import net.dries007.tfc.util.calendar.Calendars;
import net.dries007.tfc.util.calendar.ICalendar;
import org.jetbrains.annotations.Nullable;

public class DryingMatBlockEntity extends InventoryBlockEntity<ItemStackHandler>
{
    public static final int DURATION = ICalendar.TICKS_IN_DAY / 2;

    public static void serverTick(Level level, BlockPos pos, BlockState state, DryingMatBlockEntity mat)
    {
        // reset when it rains
        if (level.getGameTime() % 40 == 0 && level.isRainingAt(pos.above()))
        {
            mat.startTick = Calendars.SERVER.getTicks();
        }

        long remainingTicks = DURATION - (Calendars.SERVER.getTicks() - mat.startTick);
        if (remainingTicks <= 0)
        {
            mat.dry();
        }
    }

    private long startTick;
    @Nullable
    private DryingRecipe cachedRecipe;

    public DryingMatBlockEntity(BlockPos pos, BlockState state)
    {
        super(FLBlockEntities.DRYING_MAT.get(), pos, state, defaultInventory(1), FLHelpers.blockEntityName("drying_mat"));
        startTick = 0;
        cachedRecipe = null;
    }

    @Override
    public void loadAdditional(CompoundTag nbt)
    {
        startTick = nbt.getLong("startTick");
        updateCache();
        super.loadAdditional(nbt);
    }

    @Override
    public void saveAdditional(CompoundTag nbt)
    {
        nbt.putLong("startTick", startTick);
        super.saveAdditional(nbt);
    }

    @Override
    public int getSlotStackLimit(int slot)
    {
        return 1;
    }

    public void updateCache()
    {
        assert level != null;
        cachedRecipe = DryingRecipe.getRecipe(level, new ItemStackInventory(readStack()));
    }

    public void start()
    {
        assert level != null;
        updateCache();
        if (cachedRecipe != null)
        {
            startTick = Calendars.SERVER.getTicks();
            if (!level.canSeeSky(worldPosition.above()))
            {
                startTick += DURATION; // takes twice as long indoors
            }
            markForSync();
        }
    }

    public void dry()
    {
        final DryingRecipe recipe = this.cachedRecipe;
        if (recipe != null)
        {
            final ItemStack out = recipe.assemble(new ItemStackInventory(readStack()));
            inventory.setStackInSlot(0, out);
            updateCache();
            markForSync();
        }
    }

    public ItemStack insert(ItemStack item)
    {
        return inventory.insertItem(0, item, false);
    }

    public ItemStack extract()
    {
        return inventory.extractItem(0, 1, false);
    }

    public ItemStack readStack()
    {
        return inventory.getStackInSlot(0).copy();
    }
}
