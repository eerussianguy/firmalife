package com.eerussianguy.firmalife.common.blocks.plant;

import java.util.Random;
import java.util.function.Supplier;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import com.eerussianguy.firmalife.common.FLTags;
import com.eerussianguy.firmalife.common.misc.SwarmEffect;
import net.dries007.tfc.common.blocks.ExtendedProperties;
import net.dries007.tfc.common.blocks.plant.fruit.FruitTreeLeavesBlock;
import net.dries007.tfc.common.blocks.plant.fruit.Lifecycle;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.climate.ClimateRange;

public class FLFruitTreeLeavesBlock extends FruitTreeLeavesBlock
{
    public FLFruitTreeLeavesBlock(ExtendedProperties properties, Supplier<? extends Item> productItem, Lifecycle[] stages, Supplier<ClimateRange> climateRange)
    {
        super(properties, productItem, stages, climateRange);
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, Random rand)
    {
        if (state.getValue(LIFECYCLE) == Lifecycle.FLOWERING && Helpers.isBlock(this, FLTags.Blocks.BUZZING_LEAVES) && rand.nextInt(100) == 0)
        {
            SwarmEffect.particles(level, pos, rand);
        }
    }

}
