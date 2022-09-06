package com.eerussianguy.firmalife.common.blockentities;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import com.eerussianguy.firmalife.common.FLHelpers;
import com.eerussianguy.firmalife.common.recipes.DryingRecipe;
import com.eerussianguy.firmalife.config.FLConfig;
import net.dries007.tfc.common.recipes.inventory.ItemStackInventory;
import net.dries007.tfc.util.calendar.Calendars;
import net.dries007.tfc.util.calendar.ICalendar;

public class DryingMatBlockEntity extends SimpleItemRecipeBlockEntity<DryingRecipe>
{
    public static void serverTick(Level level, BlockPos pos, BlockState state, DryingMatBlockEntity mat)
    {
        // reset when it rains
        if (level.getGameTime() % 40 == 0 && level.isRainingAt(pos.above()))
        {
            mat.startTick = Calendars.SERVER.getTicks();
        }

        if (mat.cachedRecipe != null)
        {
            long remainingTicks = mat.getDuration() - (Calendars.SERVER.getTicks() - mat.startTick);
            if (remainingTicks <= 0)
            {
                mat.finish();
            }
        }
        else
        {
            mat.startTick = Calendars.SERVER.getTicks();
        }
    }

    public DryingMatBlockEntity(BlockPos pos, BlockState state)
    {
        super(FLBlockEntities.DRYING_MAT.get(), pos, state, FLHelpers.blockEntityName("drying_mat"), FLConfig.SERVER.dryingTicks);
    }

    @Override
    public boolean isItemValid(int slot, ItemStack stack)
    {
        assert level != null;
        return DryingRecipe.getRecipe(level, new ItemStackInventory(stack)) != null;
    }

    @Override
    public void updateCache()
    {
        assert level != null;
        cachedRecipe = DryingRecipe.getRecipe(level, new ItemStackInventory(readStack()));
    }
}
