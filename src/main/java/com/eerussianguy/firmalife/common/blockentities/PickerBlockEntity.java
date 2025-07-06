package com.eerussianguy.firmalife.common.blockentities;

import com.eerussianguy.firmalife.common.blocks.greenhouse.LargePlanterBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import net.dries007.tfc.common.blockentities.TickableBlockEntity;
import net.dries007.tfc.util.Helpers;

public class PickerBlockEntity extends TickableBlockEntity
{
    public static void tick(Level level, BlockPos pos, BlockState state, PickerBlockEntity picker)
    {
        picker.checkForLastTickSync();

        if (picker.justPushed && level.getGameTime() - picker.lastPushed > 10)
        {
            picker.pick();
            picker.justPushed = false;
        }
    }

    private boolean justPushed = false;
    private long lastPushed = 0;

    public PickerBlockEntity(BlockPos pos, BlockState state)
    {
        super(FLBlockEntities.PICKER.get(), pos, state);
    }

    public float getExtensionLength()
    {
        if (level == null)
            return 0f;
        int time = (int) (this.level.getGameTime() - this.lastPushed);
        if (time < 10)
        {
            return (float) time * 0.05F;
        }
        else
        {
            return time < 20 ? (float) (20 - time) * 0.05F : 0f;
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag)
    {
        super.saveAdditional(tag);
        tag.putLong("pushed", this.lastPushed);
        tag.putBoolean("justPushed", this.justPushed);
    }

    @Override
    protected void loadAdditional(CompoundTag tag)
    {
        super.loadAdditional(tag);
        this.lastPushed = tag.getLong("pushed");
        this.justPushed = tag.getBoolean("justPushed");
    }

    public long getLastPushed()
    {
        return lastPushed;
    }

    public void push()
    {
        assert level != null;
        if (justPushed)
            return;
        lastPushed = level.getGameTime();
        justPushed = true;
        markForSync();
    }

    public void pick()
    {
        assert level != null;
        if (level.isClientSide)
            return;
        final BlockPos pickPos = worldPosition.below();
        if (level.getBlockEntity(pickPos) instanceof QuadPlanterBlockEntity planter)
        {
            for (int i = 0; i < planter.slots(); i++)
            {
                LargePlanterBlock.takeSlot(level, planter, i, item -> Helpers.spawnItem(level, worldPosition, item));
            }
        }
    }

}
