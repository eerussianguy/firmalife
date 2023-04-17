package com.eerussianguy.firmalife.common.blocks;

import java.util.function.Supplier;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.util.Helpers;

public class OvenChimneyBlock extends Block implements ICure
{
    private static final VoxelShape SHAPE = Shapes.join(
        Shapes.block(),
        Block.box(4, 0, 4, 12, 16, 12),
        BooleanOp.ONLY_FIRST
    );

    private static final VoxelShape ALT_SHAPE = Shapes.or(
        box(4, 0, 0, 12, 16, 4),
        box(4, 0, 12, 12, 16, 16),
        box(0, 0, 4, 4, 16, 12),
        box(12, 0, 4, 16, 16, 12)
    );

    public static final BooleanProperty ALT = FLStateProperties.ALT;

    @Nullable
    private final Supplier<? extends Block> curedBlock;

    public OvenChimneyBlock(Properties properties, @Nullable Supplier<? extends Block> curedBlock)
    {
        super(properties);
        this.curedBlock = curedBlock;
        registerDefaultState(getStateDefinition().any().setValue(ALT, false));
    }

    @Override
    @SuppressWarnings("deprecation")
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit)
    {
        if (Helpers.isItem(player.getItemInHand(hand), TFCTags.Items.HAMMERS))
        {
            level.setBlockAndUpdate(pos, state.setValue(ALT, !state.getValue(ALT)));
            return InteractionResult.sidedSuccess(level.isClientSide);
        }
        return InteractionResult.PASS;
    }

    @Override
    @SuppressWarnings("deprecation")
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context)
    {
        return state.getValue(ALT) ? ALT_SHAPE : SHAPE;
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
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        super.createBlockStateDefinition(builder.add(ALT));
    }

    @Override
    @Nullable
    public Block getCured()
    {
        return curedBlock == null ? null : curedBlock.get();
    }
}
