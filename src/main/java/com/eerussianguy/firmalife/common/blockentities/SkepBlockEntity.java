package com.eerussianguy.firmalife.common.blockentities;

import com.eerussianguy.firmalife.common.FLHelpers;
import com.eerussianguy.firmalife.common.blocks.SkepBlock;
import com.eerussianguy.firmalife.common.capabilities.FLComponents;
import com.eerussianguy.firmalife.common.capabilities.bee.BeeComponent;
import com.eerussianguy.firmalife.common.misc.FLPOIs;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

/**
 * This thing is kind of a hack, it basically ignores the inventory
 * But it's also kind of smart because we're using inheritance to our advantage here to change functionality
 * Any way, if you're working on this class, be smart!
 */
public class SkepBlockEntity extends FLBeehiveBlockEntity
{
    public SkepBlockEntity(BlockPos pos, BlockState state)
    {
        super(pos, state, FLBlockEntities.SKEP.get());
    }

    @Override
    public void addHoney(int honey)
    {
        assert level != null;
        if (getBlockState().getBlock() instanceof SkepBlock)
        {
            level.setBlockAndUpdate(worldPosition, getBlockState().setValue(SkepBlock.HONEY, true));
        }
    }

    @Override
    public int getHoney()
    {
        return getBlockState().getBlock() instanceof SkepBlock && getBlockState().getValue(SkepBlock.HONEY) ? 1 : 0;
    }

    @Override
    public void takeHoney(int honey)
    {
        assert level != null;
        level.setBlockAndUpdate(worldPosition, getBlockState().setValue(SkepBlock.HONEY, false));
    }

    @Override
    public void trySwarm()
    {
        assert level != null;
        if (canSwarm() && !level.isClientSide)
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
    public boolean canSwarm()
    {
        return linkedHive == null && beeData.hasQueen() && isWarmEnough() && getHoney() == 1 && !getBee().hasGeneticDisease();
    }

    /**
     * Skeps and only skeps can be picked up by players.
     */
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
