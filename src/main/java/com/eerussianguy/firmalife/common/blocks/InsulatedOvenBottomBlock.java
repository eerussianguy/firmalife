package com.eerussianguy.firmalife.common.blocks;

import java.util.function.Supplier;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.common.blocks.ExtendedProperties;

public class InsulatedOvenBottomBlock extends OvenBottomBlock
{
    public InsulatedOvenBottomBlock(ExtendedProperties properties, @Nullable Supplier<? extends Block> curedBlock)
    {
        super(properties, curedBlock);
    }

    @Override
    public boolean isInsulated(LevelAccessor level, BlockPos pos, BlockState state)
    {
        return true;
    }
}
