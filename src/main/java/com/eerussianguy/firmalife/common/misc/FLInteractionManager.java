package com.eerussianguy.firmalife.common.misc;

import com.eerussianguy.firmalife.common.blocks.oven.OvenBottomBlock;
import com.eerussianguy.firmalife.common.items.FLItems;
import com.eerussianguy.firmalife.common.util.FLAdvancements;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.crafting.Ingredient;

import com.eerussianguy.firmalife.common.blocks.FLBlocks;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import net.dries007.tfc.common.items.TFCItems;
import net.dries007.tfc.util.BlockItemPlacement;
import net.dries007.tfc.util.InteractionManager;

public class FLInteractionManager
{
    public static void init()
    {
        InteractionManager.registerBlock(new BlockItemPlacement(TFCItems.WOOL_YARN, FLBlocks.WOOL_STRING) {
            @Override
            public InteractionResult postPlacement(BlockPlaceContext context)
            {
                final Level level = context.getLevel();
                final BlockPos pos = context.getClickedPos();
                final BlockState state = level.getBlockState(pos);
                state.getBlock().setPlacedBy(level, pos, state, context.getPlayer(), context.getItemInHand());
                return super.postPlacement(context);
            }
        });

        InteractionManager.registerBlock(new BlockItemPlacement(FLItems.PINEAPPLE_YARN, FLBlocks.PINEAPPLE_YARN) {
            @Override
            public InteractionResult postPlacement(BlockPlaceContext context)
            {
                final Level level = context.getLevel();
                final BlockPos pos = context.getClickedPos();
                final BlockState state = level.getBlockState(pos);
                state.getBlock().setPlacedBy(level, pos, state, context.getPlayer(), context.getItemInHand());
                return super.postPlacement(context);
            }
        });

        InteractionManager.register(Ingredient.of(TFCItems.WROUGHT_IRON_GRILL), InteractionManager.Target.BLOCKS, (stack, context) -> {
            final Level level = context.getLevel();
            final BlockPos pos = context.getClickedPos();
            final BlockPos abovePos = pos.above();

            if (context.getClickedFace() == Direction.UP && level.getBlockState(pos).getBlock() instanceof OvenBottomBlock && level.getBlockState(abovePos).isAir())
            {
                level.setBlockAndUpdate(abovePos, FLBlocks.STOVETOP_GRILL.get().defaultBlockState());
                if (context.getPlayer() == null || !context.getPlayer().isCreative()) stack.shrink(1);
                if (context.getPlayer() instanceof ServerPlayer server)
                {
                    FLAdvancements.STOVETOP_GRILL.trigger(server);
                }
                return InteractionResult.SUCCESS;
            }

            return InteractionResult.PASS;
        });

        InteractionManager.register(Ingredient.of(TFCItems.POT), InteractionManager.Target.BLOCKS, (stack, context) -> {
            final Level level = context.getLevel();
            final BlockPos pos = context.getClickedPos();
            final BlockPos abovePos = pos.above();

            if (context.getClickedFace() == Direction.UP && level.getBlockState(pos).getBlock() instanceof OvenBottomBlock && level.getBlockState(abovePos).isAir())
            {
                level.setBlockAndUpdate(abovePos, FLBlocks.STOVETOP_POT.get().defaultBlockState());
                if (context.getPlayer() == null || !context.getPlayer().isCreative()) stack.shrink(1);
                if (context.getPlayer() instanceof ServerPlayer server)
                {
                    FLAdvancements.STOVETOP_POT.trigger(server);
                }
                return InteractionResult.SUCCESS;
            }

            return InteractionResult.PASS;
        });
    }
}
