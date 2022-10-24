package com.eerussianguy.firmalife.common.blockentities;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;

import com.eerussianguy.firmalife.config.FLConfig;
import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.blockentities.ComposterBlockEntity;
import net.dries007.tfc.common.blocks.devices.TFCComposterBlock;
import net.dries007.tfc.config.TFCConfig;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.climate.Climate;

public class IronComposterBlockEntity extends ComposterBlockEntity
{
    public IronComposterBlockEntity(BlockPos pos, BlockState state)
    {
        super(FLBlockEntities.IRON_COMPOSTER.get(), pos, state);
    }

    @Override
    public long getReadyTicks()
    {
        assert level != null;
        final BlockPos.MutableBlockPos cursor = new BlockPos.MutableBlockPos();
        cursor.set(getBlockPos());
        final float rainfall = Climate.getRainfall(level, cursor);
        long readyTicks = FLConfig.SERVER.ironComposterTicks.get();
        if (TFCConfig.SERVER.composterRainfallCheck.get())
        {
            if (rainfall < 150f) // inverted trapezoid wave
            {
                readyTicks *= (long) ((150f - rainfall) / 50f + 1f);
            }
            else if (rainfall > 350f)
            {
                readyTicks *= (long) ((rainfall - 350f) / 50f + 1f);
            }
        }
        cursor.move(0, 1, 0);
        if (Helpers.isBlock(level.getBlockState(cursor), TFCTags.Blocks.SNOW))
        {
            readyTicks *= 0.9f;
        }
        for (Direction direction : Direction.Plane.HORIZONTAL)
        {
            cursor.setWithOffset(getBlockPos(), direction);
            if (level.getBlockState(cursor).getBlock() instanceof TFCComposterBlock)
            {
                readyTicks *= 1.05f;
            }
        }
        return readyTicks;
    }
}
