package com.eerussianguy.firmalife.common.blockentities;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

import net.minecraftforge.items.CapabilityItemHandler;

import com.eerussianguy.firmalife.common.FLHelpers;
import com.eerussianguy.firmalife.common.blocks.greenhouse.TrellisPlanterBlock;
import org.jetbrains.annotations.Nullable;

public class TrellisPlanterBlockEntity extends LargePlanterBlockEntity
{
    public static final Component NAME = FLHelpers.blockEntityName("large_planter");

    public TrellisPlanterBlockEntity(BlockPos pos, BlockState state)
    {
        super(FLBlockEntities.TRELLIS_PLANTER.get(), pos, state, defaultInventory(LARGE_PLANTER_SLOTS), NAME);
    }

    @Override
    @Nullable
    protected Direction airFindOffset()
    {
        return null;
    }

    @Override
    public void afterGrowthTickStep(boolean wasGrowing)
    {
        assert level != null;
        if (wasGrowing && level.random.nextInt(10) == 0 && getGrowth(0) >= 1f)
        {
            ItemStack inside = inventory.getStackInSlot(0);
            if (!inside.isEmpty())
            {
                BlockPos above = worldPosition.above();
                if (level.getBlockState(above).getBlock() instanceof TrellisPlanterBlock && level.getBlockEntity(above) instanceof TrellisPlanterBlockEntity planter)
                {
                    planter.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent(inv -> {
                        if (inv.getStackInSlot(0).isEmpty() && inv.isItemValid(0, inside))
                        {
                            ItemStack leftover = inv.insertItem(0, inside.copy(), false);
                            if (!leftover.isEmpty())
                            {
                                planter.setGrowth(0, 0);
                                planter.updateCache();
                            }
                        }
                    });
                }
            }

        }
    }
}

