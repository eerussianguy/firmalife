package com.eerussianguy.firmalife.common.blockentities;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import com.eerussianguy.firmalife.common.blocks.AbstractOvenBlock;
import com.eerussianguy.firmalife.common.blocks.FLStateProperties;
import com.eerussianguy.firmalife.config.FLConfig;
import net.dries007.tfc.util.Helpers;

public interface OvenLike
{
    static void regularBlockUpdate(Level level, BlockPos pos, BlockState state, OvenLike oven, boolean cured, int updateInterval)
    {
        if (state.hasProperty(FLStateProperties.HAS_CHIMNEY))
        {
            final boolean chimney = state.getValue(FLStateProperties.HAS_CHIMNEY);
            final BlockPos chimneyPos = AbstractOvenBlock.locateChimney(level, pos, state);
            final boolean chimneyNow = chimneyPos != null;
            if (chimneyNow != chimney)
            {
                level.setBlockAndUpdate(pos, state.setValue(FLStateProperties.HAS_CHIMNEY, chimneyNow));
            }
            if (!chimneyNow && level.random.nextInt(4) == 0 && level instanceof ServerLevel server && oven.getTemperature() > 0f)
            {
                Helpers.fireSpreaderTick(server, pos, level.random, 2);
            }
        }
        if (oven.getCureTicks() <= FLConfig.SERVER.ovenCureTicks.get()) oven.addCureTicks(updateInterval);
        if (oven.getTemperature() > (float) FLConfig.SERVER.ovenCureTemperature.get() && oven.getCureTicks() > FLConfig.SERVER.ovenCureTicks.get())
        {
            AbstractOvenBlock.cureAllAround(level, pos, !cured);
        }
    }

    float getTemperature();

    int getCureTicks();

    void addCureTicks(int ticks);

}
