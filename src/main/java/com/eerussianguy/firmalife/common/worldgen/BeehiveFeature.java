package com.eerussianguy.firmalife.common.worldgen;

import com.eerussianguy.firmalife.common.blocks.FLBlocks;
import com.eerussianguy.firmalife.common.blocks.WildBeehiveBlock;
import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

import net.dries007.tfc.util.Helpers;

public class BeehiveFeature extends Feature<NoneFeatureConfiguration>
{
    public BeehiveFeature(Codec<NoneFeatureConfiguration> codec)
    {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context)
    {
        final WorldGenLevel level = context.level();
        final BlockPos pos = context.origin();
        final RandomSource rand = context.random();

        BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos();
        final int radius = 5;
        for (int i = 0; i < 5; i++)
        {
            mutablePos.setWithOffset(pos, Helpers.triangle(rand, radius), Helpers.triangle(rand, 4) + 10, Helpers.triangle(rand, radius));
            mutablePos.move(Direction.UP);
            BlockState aboveState = level.getBlockState(mutablePos);
            mutablePos.move(Direction.DOWN);

            // need logs or leaves above
            if (WildBeehiveBlock.canHangOn(aboveState) && level.isEmptyBlock(mutablePos))
            {
                mutablePos.move(Direction.DOWN); // need air below
                if (level.isEmptyBlock(mutablePos))
                {
                    mutablePos.move(Direction.UP);
                    setBlock(level, mutablePos, FLBlocks.WILD_BEEHIVE.get().defaultBlockState()
                        .setValue(WildBeehiveBlock.FACING, Direction.Plane.HORIZONTAL.getRandomDirection(rand))
                        .setValue(WildBeehiveBlock.HONEY, rand.nextBoolean())
                    );
                    return true;
                }

            }
        }
        return false;
    }
}
