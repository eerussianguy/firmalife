package com.eerussianguy.firmalife.common.blocks;

import java.util.function.Supplier;
import com.eerussianguy.firmalife.common.blockentities.KegBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;

import net.dries007.tfc.common.blocks.ExtendedProperties;
import net.dries007.tfc.common.blocks.TFCBlockStateProperties;
import net.dries007.tfc.common.fluids.FluidHelpers;
import net.dries007.tfc.config.TFCConfig;

public class KegCoreBlock extends TwoByTwoCoreBlock
{
    public static final BooleanProperty SEALED = TFCBlockStateProperties.SEALED;
    public static final BooleanProperty POWERED = BlockStateProperties.POWERED;

    public static void toggleSeal(Level level, BlockPos pos, BlockState state)
    {
        if (level.getBlockEntity(pos) instanceof KegBlockEntity barrel)
        {
            final boolean previousSealed = state.getValue(SEALED);
            level.setBlockAndUpdate(pos, state.setValue(SEALED, !previousSealed));

            if (previousSealed)
            {
                barrel.onUnseal();
            }
            else
            {
                barrel.onSeal();
            }
        }
    }

    public KegCoreBlock(ExtendedProperties properties, Supplier<? extends Block> subBlock)
    {
        super(properties, subBlock);
        registerDefaultState(getStateDefinition().any().setValue(FACING, Direction.NORTH).setValue(SEALED, false).setValue(POWERED, false));
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult)
    {
        if (level.getBlockEntity(pos) instanceof KegBlockEntity barrel)
        {
            if (stack.isEmpty() && player.isShiftKeyDown())
            {
                toggleSeal(level, pos, state);
                level.playSound(null, pos, SoundEvents.WOOD_PLACE, SoundSource.BLOCKS, 1.0f, 0.85f);
                return ItemInteractionResult.sidedSuccess(level.isClientSide);
            }
            else if (FluidHelpers.transferBetweenBlockEntityAndItem(stack, barrel, player, hand))
            {
                return ItemInteractionResult.sidedSuccess(level.isClientSide);
            }
            else if (!level.isClientSide() && player instanceof ServerPlayer serverPlayer)
            {
                serverPlayer.openMenu(barrel, barrel.getBlockPos());
            }
            return ItemInteractionResult.sidedSuccess(level.isClientSide);
        }
        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    @Override
    protected BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos neighborPos)
    {
        return coreBlockSurvives(level, pos, state) ? state : Blocks.AIR.defaultBlockState();
    }

    @Override
    protected void neighborChanged(BlockState state, Level level, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving)
    {
        if (TFCConfig.SERVER.barrelEnableRedstoneSeal.get() && level.getBlockEntity(pos) instanceof KegBlockEntity barrel)
        {
            handleNeighborChanged(state, level, pos, barrel::onSeal, barrel::onUnseal);
        }
    }

    public void handleNeighborChanged(BlockState state, Level level, BlockPos pos, Runnable onSeal, Runnable onUnseal)
    {
        final boolean signal = level.hasNeighborSignal(pos);
        if (signal != state.getValue(POWERED))
        {
            if (signal != state.getValue(SEALED))
            {
                level.setBlockAndUpdate(pos, state.setValue(POWERED, signal).setValue(SEALED, signal));

                if (signal) onSeal.run();
                else onUnseal.run();
            }
            else
            {
                level.setBlockAndUpdate(pos, state.setValue(POWERED, signal));
            }
        }
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        super.createBlockStateDefinition(builder.add(SEALED, POWERED));
    }
}
