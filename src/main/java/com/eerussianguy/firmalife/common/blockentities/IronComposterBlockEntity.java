package com.eerussianguy.firmalife.common.blockentities;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

import com.eerussianguy.firmalife.config.FLConfig;
import net.dries007.tfc.common.blockentities.ComposterBlockEntity;
import net.dries007.tfc.common.blockentities.TFCBlockEntities;
import net.dries007.tfc.common.blocks.devices.TFCComposterBlock;
import net.dries007.tfc.config.TFCConfig;
import net.dries007.tfc.util.climate.Climate;

public class IronComposterBlockEntity extends ComposterBlockEntity
{
    public IronComposterBlockEntity(BlockPos pos, BlockState state)
    {
        super(TFCBlockEntities.COMPOSTER.get(), pos, state);
    }

    @Override
    public void randomTick()
    {
        if (getGreen() >= 4 && getBrown() >= 4 & !isRotten())
        {
            assert level != null;
            final float rainfall = Climate.getRainfall(level, getBlockPos());
            long readyTicks = FLConfig.SERVER.ironComposterTicks.get(); // firmalife: divide ready time by 4
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
            if (getTicksSinceUpdate() > readyTicks)
            {
                setState(TFCComposterBlock.CompostType.READY);
                markForSync();
            }
        }
    }

    private void setState(TFCComposterBlock.CompostType type)
    {
        assert level != null;
        level.setBlockAndUpdate(getBlockPos(), level.getBlockState(getBlockPos()).setValue(TFCComposterBlock.TYPE, type));
    }

    private boolean isRotten()
    {
        assert level != null;
        return level.getBlockState(getBlockPos()).getValue(TFCComposterBlock.TYPE) == TFCComposterBlock.CompostType.ROTTEN;
    }

}
