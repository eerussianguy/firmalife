package com.eerussianguy.firmalife.common.blockentities;

import java.util.function.Supplier;
import com.eerussianguy.firmalife.common.FLHelpers;
import com.eerussianguy.firmalife.common.recipes.DryingRecipe;
import com.eerussianguy.firmalife.config.FLConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import net.dries007.tfc.common.capabilities.PartialItemHandler;
import net.dries007.tfc.common.recipes.inventory.ItemStackInventory;
import net.dries007.tfc.util.EnvironmentHelpers;

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
        if (level.getGameTime() % 60 == 0 && EnvironmentHelpers.isRainingOrSnowing(level, pos) && level.canSeeSky(pos))
        {
            mat.resetCounter();
        }

        if (mat.cachedRecipe != null)
        {
            if (!mat.cachedRecipe.matches(new ItemStackInventory(mat.inventory.getStackInSlot(0)), level))
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
        super(type, pos, state, FLHelpers.blockEntityName("drying_mat"), dryTicks);

        sidedInventory
            .on(new PartialItemHandler(inventory).extract(0), Direction.DOWN)
            .on(new PartialItemHandler(inventory).insert(0), Direction.Plane.HORIZONTAL);
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
        needsRecipeUpdate = false;
    }
}
