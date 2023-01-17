package com.eerussianguy.firmalife.common.blocks.plant;

import java.util.Random;
import java.util.function.Supplier;
import com.eerussianguy.firmalife.common.blockentities.FLTickCounterBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.PipeBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.blocks.ExtendedProperties;
import net.dries007.tfc.common.blocks.TFCBlockStateProperties;
import net.dries007.tfc.common.blocks.plant.fruit.FruitTreeSaplingBlock;
import net.dries007.tfc.common.blocks.plant.fruit.Lifecycle;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.climate.ClimateRange;

public class FLFruitTreeSaplingBlock extends FruitTreeSaplingBlock
{
    public FLFruitTreeSaplingBlock(ExtendedProperties properties, Supplier<? extends Block> block, int treeGrowthDays, Supplier<ClimateRange> climateRange, Lifecycle[] stages)
    {
        super(properties, block, treeGrowthDays, climateRange, stages);
    }

    public FLFruitTreeSaplingBlock(ExtendedProperties properties, Supplier<? extends Block> block, Supplier<Integer> treeGrowthDays, Supplier<ClimateRange> climateRange, Lifecycle[] stages)
    {
        super(properties, block, treeGrowthDays, climateRange, stages);
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack)
    {
        FLTickCounterBlockEntity.reset(level, pos);
        super.setPlacedBy(level, pos, state, placer, stack);
    }

    @Override
    public void createTree(Level level, BlockPos pos, BlockState state, Random random)
    {
        final boolean onBranch = Helpers.isBlock(level.getBlockState(pos.below()), TFCTags.Blocks.FRUIT_TREE_BRANCH);
        int internalSapling = onBranch ? 3 : state.getValue(TFCBlockStateProperties.SAPLINGS);
        if (internalSapling == 1 && random.nextBoolean()) internalSapling += 1;
        level.setBlockAndUpdate(pos, block.get().defaultBlockState().setValue(PipeBlock.DOWN, true).setValue(TFCBlockStateProperties.SAPLINGS, internalSapling).setValue(TFCBlockStateProperties.STAGE_3, onBranch ? 1 : 0));
        FLTickCounterBlockEntity.reset(level, pos);
    }
}
