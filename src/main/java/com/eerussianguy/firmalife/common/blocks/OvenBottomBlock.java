package com.eerussianguy.firmalife.common.blocks;

import java.util.Random;
import java.util.function.Supplier;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;

import net.minecraftforge.items.CapabilityItemHandler;

import com.eerussianguy.firmalife.common.FLHelpers;
import com.eerussianguy.firmalife.common.blockentities.FLBlockEntities;
import com.eerussianguy.firmalife.common.blockentities.OvenBottomBlockEntity;
import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.blocks.ExtendedProperties;
import net.dries007.tfc.common.blocks.devices.IBellowsConsumer;
import org.jetbrains.annotations.Nullable;

public class OvenBottomBlock extends AbstractOvenBlock implements IBellowsConsumer
{
    public static final BooleanProperty LIT = BlockStateProperties.LIT;
    public static final IntegerProperty LOGS = FLStateProperties.LOGS;

    public OvenBottomBlock(ExtendedProperties properties, @Nullable Supplier<? extends Block> curedBlock)
    {
        super(properties, curedBlock);
        registerDefaultState(getStateDefinition().any().setValue(LOGS, 0).setValue(FACING, Direction.NORTH).setValue(LIT, false));
    }

    @Override
    @SuppressWarnings("deprecation")
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult result)
    {
        final ItemStack item = player.getItemInHand(hand);
        if (!item.isEmpty() && item.is(TFCTags.Items.FIREPIT_FUEL))
        {
            return level.getBlockEntity(pos, FLBlockEntities.OVEN_BOTTOM.get()).map(oven -> oven.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).map(inv -> {
                if (inv.getStackInSlot(OvenBottomBlockEntity.SLOT_FUEL_MAX).isEmpty())
                {
                    return FLHelpers.insertOne(level, item, OvenBottomBlockEntity.SLOT_FUEL_MAX, inv, player);
                }
                return InteractionResult.FAIL;
            }).orElse(InteractionResult.FAIL)).orElse(InteractionResult.FAIL);
        }
        return InteractionResult.PASS;
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, Random random)
    {
        if (state.getValue(LIT))
        {
            super.animateTick(state, level, pos, random);
        }
    }

    @Override
    public void cure(Level level, BlockState state, BlockPos pos)
    {
        if (getCured() != null)
        {
            OvenBottomBlockEntity.cure(level, state, getCured().defaultBlockState(), pos);
        }
    }

    @Override
    @SuppressWarnings("deprecation")
    public BlockState updateShape(BlockState state, Direction facing, BlockState facingState, LevelAccessor level, BlockPos currentPos, BlockPos facingPos)
    {
        return state.getValue(LIT) && !insulated(level, currentPos, state) ? state.setValue(LIT, false) : state;
    }

    @Override
    @SuppressWarnings("deprecation")
    public void randomTick(BlockState state, ServerLevel level, BlockPos pos, Random rand)
    {
        if (state.getValue(LIT) && !insulated(level, pos, state))
        {
            level.setBlockAndUpdate(pos, state.setValue(LIT, false));
        }
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        super.createBlockStateDefinition(builder.add(LIT, LOGS));
    }

    @Override
    public void intakeAir(Level level, BlockPos blockPos, BlockState state, int amount)
    {
        level.getBlockEntity(blockPos, FLBlockEntities.OVEN_BOTTOM.get()).ifPresent(oven -> oven.onAirIntake(amount));
    }
}
