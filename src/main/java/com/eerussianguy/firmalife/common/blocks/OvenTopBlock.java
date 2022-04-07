package com.eerussianguy.firmalife.common.blocks;

import java.util.Random;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;

import com.eerussianguy.firmalife.common.FLTags;
import com.eerussianguy.firmalife.common.blockentities.FLBlockEntities;
import com.eerussianguy.firmalife.common.blockentities.OvenBottomBlockEntity;
import com.eerussianguy.firmalife.common.blockentities.OvenTopBlockEntity;
import net.dries007.tfc.common.TFCDamageSources;
import net.dries007.tfc.common.blocks.ExtendedProperties;
import net.dries007.tfc.util.Helpers;

public class OvenTopBlock extends AbstractOvenBlock
{
    public OvenTopBlock(ExtendedProperties properties)
    {
        super(properties);
    }

    @Override
    @SuppressWarnings("deprecation")
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult result)
    {
        final ItemStack item = player.getItemInHand(hand);
        return level.getBlockEntity(pos, FLBlockEntities.OVEN_TOP.get()).map(oven -> oven.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).map(inv -> {
            final boolean peel = Helpers.isItem(item, FLTags.Items.USABLE_ON_OVEN);
            if (peel || (item.isEmpty() && player.isShiftKeyDown()))
            {
                BlockState downState = level.getBlockState(pos.below());
                if (!peel && Helpers.isBlock(downState, FLBlocks.OVEN_BOTTOM.get()) && downState.getValue(OvenBottomBlock.LIT))
                {
                    player.hurt(TFCDamageSources.GRILL, 0.5f);
                }
                for (int i = OvenTopBlockEntity.SLOT_INPUT_START; i <= OvenTopBlockEntity.SLOT_INPUT_END; i++)
                {
                    ItemStack stack = inv.extractItem(i, 1, false);
                    if (stack.isEmpty()) continue;
                    if (!level.isClientSide) ItemHandlerHelper.giveItemToPlayer(player, stack);
                    return InteractionResult.sidedSuccess(level.isClientSide);
                }
            }
            else if (!item.isEmpty())
            {
                for (int i = OvenTopBlockEntity.SLOT_INPUT_START; i <= OvenTopBlockEntity.SLOT_INPUT_END; i++)
                {
                    if (inv.getStackInSlot(i).isEmpty())
                    {
                        ItemStack stack = inv.insertItem(i, item.split(1), false);
                        if (!stack.isEmpty()) continue;
                        if (!level.isClientSide) ItemHandlerHelper.giveItemToPlayer(player, stack);
                        return InteractionResult.sidedSuccess(level.isClientSide);
                    }
                }
            }
            return InteractionResult.PASS;
        }).orElse(InteractionResult.PASS)).orElse(InteractionResult.PASS);
    }

    @Override
    @SuppressWarnings("deprecation")
    public BlockState updateShape(BlockState state, Direction facing, BlockState facingState, LevelAccessor level, BlockPos currentPos, BlockPos facingPos)
    {
        extinguish(level, currentPos, state);
        return super.updateShape(state, facing, facingState, level, currentPos, facingPos);
    }

    @Override
    @SuppressWarnings("deprecation")
    public void randomTick(BlockState state, ServerLevel level, BlockPos pos, Random rand)
    {
        extinguish(level, pos, state);
    }

    private void extinguish(LevelAccessor level, BlockPos pos, BlockState state)
    {
        if (!insulated(level, pos, state))
        {
            level.getBlockEntity(pos, FLBlockEntities.OVEN_TOP.get()).ifPresent(OvenTopBlockEntity::extinguish);
        }
    }
}
