package com.eerussianguy.firmalife.common.blocks;

import java.util.Random;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
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
import net.minecraftforge.items.ItemHandlerHelper;

import com.eerussianguy.firmalife.client.FLClientHelpers;
import com.eerussianguy.firmalife.common.FLTags;
import com.eerussianguy.firmalife.common.blockentities.FLBlockEntities;
import com.eerussianguy.firmalife.common.blockentities.OvenBottomBlockEntity;
import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.blocks.ExtendedProperties;
import net.dries007.tfc.common.blocks.devices.IBellowsConsumer;
import net.dries007.tfc.util.Helpers;

public class OvenBottomBlock extends AbstractOvenBlock implements IBellowsConsumer
{
    public static final BooleanProperty LIT = BlockStateProperties.LIT;
    public static final IntegerProperty LOGS = FLStateProperties.LOGS;

    public OvenBottomBlock(ExtendedProperties properties)
    {
        super(properties);
        registerDefaultState(getStateDefinition().any().setValue(CURED, false).setValue(LOGS, 0).setValue(FACING, Direction.NORTH).setValue(LIT, false));
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
                    ItemStack stack = inv.insertItem(OvenBottomBlockEntity.SLOT_FUEL_MAX, item.split(1), false);
                    if (stack.isEmpty()) return InteractionResult.PASS;
                    if (!level.isClientSide)
                    {
                        ItemHandlerHelper.giveItemToPlayer(player, stack);
                    }
                    return InteractionResult.sidedSuccess(level.isClientSide);
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
    public boolean canAcceptAir(BlockState blockState, Level level, BlockPos blockPos, Direction direction)
    {
        return direction == blockState.getValue(FACING).getOpposite();
    }

    @Override
    public void intakeAir(BlockState blockState, Level level, BlockPos blockPos, Direction direction, int amount)
    {
        level.getBlockEntity(blockPos, FLBlockEntities.OVEN_BOTTOM.get()).ifPresent(oven -> oven.onAirIntake(amount));
    }
}
