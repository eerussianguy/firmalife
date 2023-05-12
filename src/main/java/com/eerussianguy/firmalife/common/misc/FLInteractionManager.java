package com.eerussianguy.firmalife.common.misc;

import com.eerussianguy.firmalife.common.blocks.OvenBottomBlock;
import com.eerussianguy.firmalife.common.util.FLAdvancements;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.crafting.Ingredient;

import com.eerussianguy.firmalife.common.FLTags;
import com.eerussianguy.firmalife.common.blocks.FLBlocks;
import com.eerussianguy.firmalife.common.container.FLContainerProviders;
import net.minecraft.world.level.Level;

import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.capabilities.food.FoodCapability;
import net.dries007.tfc.common.items.TFCItems;
import net.dries007.tfc.util.BlockItemPlacement;
import net.dries007.tfc.util.InteractionManager;

public class FLInteractionManager
{
    public static void init()
    {
        InteractionManager.register(new BlockItemPlacement(TFCItems.WOOL_YARN, FLBlocks.WOOL_STRING));

        InteractionManager.register(Ingredient.of(FLTags.Items.PUMPKIN_KNAPPING), false, true, InteractionManager.createKnappingInteraction((s, p) -> p.getInventory().contains(TFCTags.Items.KNIVES) && s.getCapability(FoodCapability.CAPABILITY).map(f -> !f.isRotten()).orElse(false), FLContainerProviders.PUMPKIN_KNAPPING));

        InteractionManager.register(Ingredient.of(TFCItems.WROUGHT_IRON_GRILL.get()), false, (stack, context) -> {
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

        InteractionManager.register(Ingredient.of(TFCItems.POT.get()), false, (stack, context) -> {
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
