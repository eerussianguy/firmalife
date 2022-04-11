package com.eerussianguy.firmalife.common.blocks;

import java.util.function.Supplier;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import org.jetbrains.annotations.Nullable;

public class OvenChimneyBlock extends Block implements ICure
{
    private static final VoxelShape SHAPE = Shapes.join(
        Shapes.block(),
        Block.box(4, 4, 4, 12, 12, 12),
        BooleanOp.ONLY_FIRST
    );

    @Nullable
    private final Supplier<? extends Block> curedBlock;

    public OvenChimneyBlock(Properties properties, @Nullable Supplier<? extends Block> curedBlock)
    {
        super(properties);
        this.curedBlock = curedBlock;
    }

    @Override
    @SuppressWarnings("deprecation")
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context)
    {
        return SHAPE;
    }

    @Override
    public void cure(Level level, BlockState state, BlockPos pos)
    {
        if (getCured() != null)
        {
            level.setBlockAndUpdate(pos, getCured().defaultBlockState());
        }
        BlockState upState = level.getBlockState(pos.above());
        if (upState.getBlock() instanceof OvenChimneyBlock aboveBlock)
        {
            aboveBlock.cure(level, upState, pos.above());
        }
    }

    @Override
    @Nullable
    public Block getCured()
    {
        return curedBlock == null ? null : curedBlock.get();
    }
}
