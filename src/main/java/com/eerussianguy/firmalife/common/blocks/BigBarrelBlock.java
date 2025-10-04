package com.eerussianguy.firmalife.common.blocks;

import com.eerussianguy.firmalife.common.blockentities.BigBarrelBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

import net.dries007.tfc.common.blocks.ExtendedProperties;
import net.dries007.tfc.common.fluids.FluidHelpers;

public class BigBarrelBlock extends TwoByTwoBlock
{
    public BigBarrelBlock(ExtendedProperties properties)
    {
        super(properties);
    }

    @Override
    public ItemInteractionResult useCoreBlock(ItemStack held, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit)
    {
        if (level.getBlockEntity(pos) instanceof BigBarrelBlockEntity barrel)
        {
            final ItemStack stack = player.getItemInHand(hand);
            if (FluidHelpers.transferBetweenBlockEntityAndItem(stack, barrel, player, hand))
            {
                return ItemInteractionResult.SUCCESS;
            }
            else if (!level.isClientSide() && player instanceof ServerPlayer serverPlayer)
            {
                serverPlayer.openMenu(state.getMenuProvider(level, pos));
            }
            return ItemInteractionResult.SUCCESS;
        }
        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }
}
