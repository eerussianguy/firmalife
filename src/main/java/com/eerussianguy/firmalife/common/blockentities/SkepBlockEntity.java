package com.eerussianguy.firmalife.common.blockentities;

import com.eerussianguy.firmalife.common.FLHelpers;
import com.eerussianguy.firmalife.common.capabilities.FLComponents;
import com.eerussianguy.firmalife.common.capabilities.bee.BeeComponent;
import com.eerussianguy.firmalife.common.misc.FLPOIs;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

public class SkepBlockEntity extends FLBeehiveBlockEntity
{
    public SkepBlockEntity(BlockPos pos, BlockState state)
    {
        super(pos, state, FLBlockEntities.SKEP.get());
    }

    @Override
    public void tryPeriodicUpdate()
    {
        super.tryPeriodicUpdate();
        assert level != null;
        if (beeData.hasQueen() && linkedHive == null && !level.isClientSide)
        {
            final BlockPos hivePos = FLHelpers.getPoint(level, worldPosition, 5, FLPOIs.BEEHIVES, (level, pos) -> {
                return level.getBlockEntity(pos) instanceof FLBeehiveBlockEntity hive && !hive.getBee().hasQueen() && !hive.isSkep();
            });
            if (hivePos != null && level.getBlockEntity(hivePos) instanceof FLBeehiveBlockEntity hive)
            {
                hive.linkSwarmFrom(worldPosition);
            }
        }
    }

    @Override
    protected void applyImplicitComponents(DataComponentInput components)
    {
        final BeeComponent bee = components.getOrDefault(FLComponents.BEE, BeeComponent.DEFAULT);
        if (bee.hasQueen())
        {
            beeData = bee;
            markForSync();
        }
        super.applyImplicitComponents(components);
    }

    @Override
    protected void collectImplicitComponents(DataComponentMap.Builder builder)
    {
        if (beeData.hasQueen())
        {
            builder.set(FLComponents.BEE, beeData);
        }
    }

    @Override
    public boolean isSplitting()
    {
        return false; // skeps always lose the queen.
    }

    @Override
    public boolean isSkep()
    {
        return true;
    }

    @Override
    public boolean isItemValid(int slot, ItemStack stack)
    {
        return false;
    }
}
