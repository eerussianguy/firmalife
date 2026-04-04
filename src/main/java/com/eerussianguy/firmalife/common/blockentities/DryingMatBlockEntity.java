package com.eerussianguy.firmalife.common.blockentities;

import java.util.function.Supplier;
import com.eerussianguy.firmalife.FirmaLife;
import com.eerussianguy.firmalife.common.FLHelpers;
import com.eerussianguy.firmalife.common.recipes.DryingRecipe;
import com.eerussianguy.firmalife.config.FLConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

import net.dries007.tfc.util.EnvironmentHelpers;
import net.dries007.tfc.util.Helpers;

public class DryingMatBlockEntity extends SimpleItemRecipeBlockEntity<DryingRecipe>
{
    public static DryingMatBlockEntity dryingMat(BlockPos pos, BlockState state)
    {
        return new DryingMatBlockEntity(FLBlockEntities.DRYING_MAT.get(), pos, state, FLConfig.SERVER.dryingTicks);
    }

    public static DryingMatBlockEntity solarDrier(BlockPos pos, BlockState state)
    {
        return new DryingMatBlockEntity(FLBlockEntities.SOLAR_DRIER.get(), pos, state, FLConfig.SERVER.solarDryingTicks);
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, DryingMatBlockEntity mat)
    {
        if (mat.needsRecipeUpdate)
        {
            mat.updateCache();
        }
        // reset when it rains
        if (level.getGameTime() % 60 == 0 && level.isRainingAt(pos))
        {
            mat.resetCounter();
        }
        if (level.getGameTime() % 100L == 0L)
        {
            final AABB bounds = new AABB(pos.getX() - 0.2, pos.getY() - 0.2, pos.getZ() - 0.2, pos.getX() + 1.2, pos.getY() + 0.3, pos.getZ() + 1.2);
            Helpers.gatherAndConsumeItems(level, bounds, mat.inventory, 0, 0);
        }

        if (mat.cachedRecipe != null && level.getGameTime() % 20 == 0)
        {
            if (!mat.cachedRecipe.matches(mat.inventory.getStackInSlot(0)))
            {
                mat.cachedRecipe = null;
                mat.resetCounter();
            }
            else if (mat.getTicksLeft() <= 0)
            {
                mat.finish();
            }
        }
    }

    public DryingMatBlockEntity(BlockEntityType<DryingMatBlockEntity> type, BlockPos pos, BlockState state, Supplier<Integer> dryTicks)
    {
        super(type, pos, state, FirmaLife.MOD_ID, dryTicks);
    }

    @Override
    public void setAndUpdateSlots(int slot)
    {
        super.setAndUpdateSlots(slot);
        markForSync();
        if (slot == 0)
            resetCounter();
    }

    public void ejectItem(Direction d)
    {
        assert level != null;
        ItemStack item = inventory.getStackInSlot(0);
        if (!item.isEmpty())
        {
            item = inventory.extractItem(0, 64, false);
            level.addFreshEntity(new ItemEntity(level, worldPosition.getX() + 0.5, worldPosition.getY() + 0.5, worldPosition.getZ() + 0.5, item, d.getStepX() * 0.3, 0.2, d.getStepZ() * 0.3));
            markForSync();
        }
    }

    @Override
    public boolean isItemValid(int slot, ItemStack stack)
    {
        assert level != null;
        return DryingRecipe.getRecipe(stack) != null;
    }

    @Override
    public void updateCache()
    {
        assert level != null;
        cachedRecipe = DryingRecipe.getRecipe(readStack());
        needsRecipeUpdate = false;
    }
}
