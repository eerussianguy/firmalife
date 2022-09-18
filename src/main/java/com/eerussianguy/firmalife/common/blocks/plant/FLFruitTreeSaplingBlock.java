package com.eerussianguy.firmalife.common.blocks.plant;

import java.util.Random;
import java.util.function.Supplier;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import com.eerussianguy.firmalife.common.blockentities.FLBlockEntities;
import net.dries007.tfc.common.blockentities.TickCounterBlockEntity;
import net.dries007.tfc.common.blocks.ExtendedProperties;
import net.dries007.tfc.common.blocks.TFCBlocks;
import net.dries007.tfc.common.blocks.plant.Plant;
import net.dries007.tfc.common.blocks.plant.fruit.FruitTreeLeavesBlock;
import net.dries007.tfc.common.blocks.plant.fruit.FruitTreeSaplingBlock;
import net.dries007.tfc.common.blocks.plant.fruit.Lifecycle;
import net.dries007.tfc.util.calendar.Calendars;
import net.dries007.tfc.util.climate.Climate;
import net.dries007.tfc.util.climate.ClimateRange;

public class FLFruitTreeSaplingBlock extends FruitTreeSaplingBlock
{
    private final Lifecycle[] stages;
    private final Supplier<ClimateRange> climateRange;

    public FLFruitTreeSaplingBlock(ExtendedProperties properties, Supplier<? extends Block> block, int treeGrowthDays, Supplier<ClimateRange> climateRange, Lifecycle[] stages)
    {
        super(properties, block, treeGrowthDays, climateRange, stages);
        this.climateRange = climateRange;
        this.stages = stages;
    }

    @Override
    public void randomTick(BlockState state, ServerLevel level, BlockPos pos, Random random)
    {
        if (this.stages[Calendars.SERVER.getCalendarMonthOfYear().ordinal()].active())
        {
            if (level.getBlockEntity(pos) instanceof TickCounterBlockEntity counter)
            {
                if (counter.getTicksSinceUpdate() > 24000L * (long) this.treeGrowthDays)
                {
                    int hydration = FruitTreeLeavesBlock.getHydration(level, pos);
                    float temp = Climate.getAverageTemperature(level, pos);
                    if (!this.climateRange.get().checkBoth(hydration, temp, false))
                    {
                        level.setBlockAndUpdate(pos, TFCBlocks.PLANTS.get(Plant.DEAD_BUSH).get().defaultBlockState());
                    }
                    else
                    {
                        this.createTree(level, pos, state, random);
                    }
                }

            }
        }

    }
}
