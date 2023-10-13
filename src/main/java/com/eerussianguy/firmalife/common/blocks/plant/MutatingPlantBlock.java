package com.eerussianguy.firmalife.common.blocks.plant;

import java.util.Random;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;

import net.minecraftforge.registries.ForgeRegistries;

import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.blocks.ExtendedProperties;
import net.dries007.tfc.common.blocks.TFCBlockStateProperties;
import net.dries007.tfc.common.blocks.plant.PlantBlock;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.registry.RegistryPlant;

public abstract class MutatingPlantBlock extends PlantBlock
{
    public static MutatingPlantBlock create(final RegistryPlant plant, ExtendedProperties properties, TagKey<Block> mutant)
    {
        return new MutatingPlantBlock(properties, mutant)
        {
            public RegistryPlant getPlant()
            {
                return plant;
            }
        };
    }

    public static final BooleanProperty MATURE = TFCBlockStateProperties.MATURE;

    private final TagKey<Block> mutant;

    public MutatingPlantBlock(ExtendedProperties properties, TagKey<Block> mutant)
    {
        super(properties);
        this.mutant = mutant;
        registerDefaultState(getStateDefinition().any().setValue(MATURE, true));
    }

    @Override
    protected boolean mayPlaceOn(BlockState state, BlockGetter level, BlockPos pos)
    {
        return Helpers.isBlock(state.getBlock(), TFCTags.Blocks.GRASS_PLANTABLE_ON);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        super.createBlockStateDefinition(builder.add(MATURE));
    }

    @Override
    public void randomTick(BlockState state, ServerLevel level, final BlockPos pos, RandomSource rand)
    {
        super.randomTick(state, level, pos, rand);
        if (state.getValue(AGE) == 3)
        {
            if (!state.getValue(MATURE) && rand.nextInt(5) == 0)
            {
                level.setBlockAndUpdate(pos, state.setValue(AGE, 1));
                BlockPos offsetPos = pos.relative(Direction.Plane.HORIZONTAL.getRandomDirection(rand));
                BlockState stateAt = level.getBlockState(offsetPos);
                if (stateAt.canBeReplaced() && stateAt.getBlock() != this && state.canSurvive(level, offsetPos))
                {
                    level.setBlockAndUpdate(offsetPos, state.setValue(AGE, 1).setValue(MATURE, true));
                }

            }
            else if (rand.nextInt(8) == 0)
            {
                Helpers.getRandomElement(ForgeRegistries.BLOCKS, mutant, rand).ifPresent(block ->
                    level.setBlockAndUpdate(pos, block.defaultBlockState())
                );
            }
        }
    }

}
