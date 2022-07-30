package com.eerussianguy.firmalife.common.blocks;

import java.util.Random;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.state.BlockState;

import net.minecraftforge.registries.ForgeRegistries;

import com.eerussianguy.firmalife.common.FLTags;
import net.dries007.tfc.common.blocks.ExtendedProperties;
import net.dries007.tfc.common.blocks.plant.PlantBlock;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.registry.RegistryPlant;

public abstract class MutatingPlantBlock extends PlantBlock
{
    public static MutatingPlantBlock create(final RegistryPlant plant, ExtendedProperties properties)
    {
        return new MutatingPlantBlock(properties)
        {
            public RegistryPlant getPlant()
            {
                return plant;
            }
        };
    }

    public MutatingPlantBlock(ExtendedProperties properties)
    {
        super(properties);
    }

    @Override
    public void randomTick(BlockState state, ServerLevel level, final BlockPos pos, Random rand)
    {
        super.randomTick(state, level, pos, rand);
        if (state.getValue(AGE) == 3)
        {
            if (rand.nextInt(10) == 0)
            {
                Helpers.getRandomElement(ForgeRegistries.BLOCKS, FLTags.Blocks.BUTTERFLY_GRASS_MUTANTS, rand).ifPresent(block ->
                    level.setBlockAndUpdate(pos, block.defaultBlockState())
                );
            }
            else
            {
                level.setBlockAndUpdate(pos, state.setValue(AGE, 1));
                BlockPos offsetPos = pos.relative(Direction.Plane.HORIZONTAL.getRandomDirection(rand));
                BlockState stateAt = level.getBlockState(offsetPos);
                if (stateAt.getMaterial().isReplaceable() && stateAt.getBlock() != this && state.canSurvive(level, offsetPos))
                {
                    level.setBlockAndUpdate(offsetPos, state.setValue(AGE, 1));
                }
            }
        }
    }

}
