package com.eerussianguy.firmalife.common.blockentities;

import java.util.function.Supplier;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.common.blockentities.InventoryBlockEntity;
import net.dries007.tfc.common.recipes.ItemRecipe;
import net.dries007.tfc.util.calendar.Calendars;

public abstract class SimpleItemRecipeBlockEntity<T extends ItemRecipe> extends InventoryBlockEntity<ItemStackHandler>
{
    private final Supplier<Integer> duration;
    protected long startTick;
    @Nullable protected T cachedRecipe;
    protected boolean needsRecipeUpdate = false;

    public SimpleItemRecipeBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state, String modId, Supplier<Integer> duration)
    {
        super(type, pos, state, defaultInventory(1), modId);
        this.duration = duration;
    }

    @Override
    public void loadAdditional(CompoundTag nbt, HolderLookup.Provider access)
    {
        startTick = nbt.getLong("startTick");
        super.loadAdditional(nbt, access);
        needsRecipeUpdate = true;
    }

    @Override
    public void saveAdditional(CompoundTag nbt, HolderLookup.Provider access)
    {
        nbt.putLong("startTick", startTick);
        super.saveAdditional(nbt, access);
    }

    @Override
    public void setAndUpdateSlots(int slot)
    {
        super.setAndUpdateSlots(slot);
        needsRecipeUpdate = true;
    }

    @Override
    public int getSlotStackLimit(int slot)
    {
        return 1;
    }

    abstract void updateCache();

    @Nullable
    public T getCachedRecipe()
    {
        if (cachedRecipe == null && level != null && level.isClientSide)
        {
            updateCache();
        }
        return cachedRecipe;
    }

    public long getTicksLeft()
    {
        assert level != null;
        return getDuration() - (Calendars.get(level).getTicks() - startTick);
    }

    public void start()
    {
        assert level != null;
        updateCache();
        if (cachedRecipe != null)
        {
            startTick = Calendars.get(level).getTicks();
            if (!level.canSeeSky(worldPosition.above()))
            {
                startTick += getDuration(); // takes twice as long indoors
            }
            markForSync();
        }
    }

    public void finish()
    {
        assert level != null;
        final T recipe = this.cachedRecipe;
        if (recipe != null)
        {
            final int ct = readStack().getCount();
            final ItemStack out = recipe.assemble(readStack());
            out.setCount(out.getCount() * ct);
            inventory.setStackInSlot(0, out);
            updateCache();
            markForSync();
        }
    }

    public void resetCounter()
    {
        startTick = Calendars.SERVER.getTicks();
        markForSync();
    }

    public ItemStack viewStack()
    {
        return inventory.getStackInSlot(0);
    }

    public ItemStack readStack()
    {
        return inventory.getStackInSlot(0).copy();
    }

    public int getDuration()
    {
        return duration.get();
    }
}
