package com.eerussianguy.firmalife.common.blocks;

import java.util.function.Supplier;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import com.eerussianguy.firmalife.common.FLHelpers;
import net.dries007.tfc.common.blocks.ExtendedProperties;
import net.dries007.tfc.common.blocks.devices.JackOLanternBlock;
import org.jetbrains.annotations.Nullable;

public class FLJackOLanternBlock extends JackOLanternBlock
{
    public FLJackOLanternBlock(ExtendedProperties properties, Supplier<? extends Block> dead)
    {
        super(properties, dead);
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack)
    {
        FLHelpers.resetCounter(level, pos);
        super.setPlacedBy(level, pos, state, placer, stack);
    }
}
