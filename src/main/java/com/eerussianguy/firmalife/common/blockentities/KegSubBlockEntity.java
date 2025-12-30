package com.eerussianguy.firmalife.common.blockentities;

import com.eerussianguy.firmalife.common.blocks.KegSubBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.templates.EmptyFluidHandler;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.wrapper.EmptyItemHandler;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.common.blockentities.TFCBlockEntity;

public class KegSubBlockEntity extends TFCBlockEntity
{
    public KegSubBlockEntity(BlockPos pos, BlockState state)
    {
        super(FLBlockEntities.KEG_SUB.get(), pos, state);
    }

    public @Nullable IItemHandler getSidedInventory(@Nullable Direction context)
    {
        final BlockPos pos = getZeroPos();
        if (pos == null || level == null)
            return EmptyItemHandler.INSTANCE;
        if (level.getBlockEntity(pos) instanceof KegBlockEntity core)
        {
            return core.getSidedInventory(context);
        }
        return EmptyItemHandler.INSTANCE;
    }

    @Nullable
    public IFluidHandler getSidedFluidInventory(@Nullable Direction dir)
    {
        final BlockPos pos = getZeroPos();
        if (pos == null || level == null)
            return EmptyFluidHandler.INSTANCE;
        if (level.getBlockEntity(pos) instanceof KegBlockEntity core)
        {
            return core.getSidedFluidInventory(dir);
        }
        return null;
    }

    @Nullable
    public BlockPos getZeroPos()
    {
        if (getBlockState().getBlock() instanceof KegSubBlock)
        {
            return KegSubBlock.findZeroPos(worldPosition, getBlockState());
        }
        return null;
    }
}
