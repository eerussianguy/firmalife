package com.eerussianguy.firmalife.common.blockentities;

import java.util.Set;
import com.eerussianguy.firmalife.common.FLHelpers;
import com.eerussianguy.firmalife.common.FLTags;
import com.eerussianguy.firmalife.common.blocks.bee.SkepBlock;
import com.eerussianguy.firmalife.common.capabilities.FLComponents;
import com.eerussianguy.firmalife.common.capabilities.bee.BeeComponent;
import com.eerussianguy.firmalife.common.misc.FLPOIs;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

import net.dries007.tfc.util.Helpers;

public class SkepBlockEntity extends FLBeehiveBlockEntity
{
    public SkepBlockEntity(BlockPos pos, BlockState state)
    {
        super(pos, state, FLBlockEntities.SKEP.get(), 1);
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
    public void trySwarm(boolean occluded)
    {
        assert level != null;
        if (canSwarm(occluded) && !level.isClientSide)
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
    public void updateTick(float temperature, boolean occluded, Set<Flower> flowers)
    {
        super.updateTick(temperature, occluded, flowers);
        assert level != null;
        if (hasBait(flowers.size()) && level.random.nextInt(8) == 0)
        {
            beeData = BeeComponent.getWildBee(level, worldPosition);
            inventory.setStackInSlot(0, ItemStack.EMPTY);
        }
    }

    @Override
    public boolean canSwarm(boolean occluded)
    {
        return linkedHive == null && beeData.hasQueen() && isWarmEnough() && !getBee().hasGeneticDisease();
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
        return slot == 0 && !beeData.hasQueen() && Helpers.isItem(stack, FLTags.Items.BEE_BAIT);
    }

    public boolean hasBait(int flowers)
    {
        return Helpers.isItem(inventory.getStackInSlot(0), FLTags.Items.BEE_BAIT) && isWarmEnough() && !beeData.hasQueen() && flowers > 30;
    }
}
