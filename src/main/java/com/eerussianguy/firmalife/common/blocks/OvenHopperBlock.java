package com.eerussianguy.firmalife.common.blocks;

import java.util.function.Supplier;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.common.blocks.ExtendedProperties;
import net.dries007.tfc.util.Helpers;

public class OvenHopperBlock extends FourWayDeviceBlock implements ICure
{
    @Nullable
    private final Supplier<? extends Block> cured;

    public OvenHopperBlock(ExtendedProperties properties, @Nullable Supplier<? extends Block> cured)
    {
        super(properties, InventoryRemoveBehavior.DROP);
        this.cured = cured;
    }

    @Override
    public void cure(Level level, BlockState state, BlockPos pos)
    {
        if (getCured() != null)
        {
            BlockState cured = getCured().defaultBlockState();
            cured = Helpers.setProperty(cured, FACING, state.getValue(FACING));
            level.setBlockAndUpdate(pos, cured);
        }
    }

    @Override
    public @Nullable Block getCured()
    {
        return cured == null ? null : cured.get();
    }
}
