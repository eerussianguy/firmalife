package com.eerussianguy.firmalife.common.blockentities;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

import com.eerussianguy.firmalife.config.FLConfig;
import net.dries007.tfc.common.blockentities.ComposterBlockEntity;
import net.dries007.tfc.common.blockentities.TFCBlockEntities;
import net.dries007.tfc.config.TFCConfig;
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
        return readyTicks;
    }
}
