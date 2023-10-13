package com.eerussianguy.firmalife.common.items;

import java.util.Map;
import java.util.function.Supplier;
import com.eerussianguy.firmalife.common.blocks.FLBlocks;
import com.eerussianguy.firmalife.common.blocks.OvenType;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.registries.RegistryObject;

import net.dries007.tfc.util.Helpers;

public class FinishItem extends Item
{
    private final OvenType type;

    public FinishItem(Properties properties, OvenType type)
    {
        super(properties);
        this.type = type;
    }

    @Override
    public InteractionResult useOn(UseOnContext context)
    {
        final Level level = context.getLevel();
        final BlockPos pos = context.getClickedPos();
        final BlockState state = level.getBlockState(pos);
        final Player player = context.getPlayer();
        if (player != null)
        {
            final ItemStack stack = player.getItemInHand(context.getHand());
            if (Helpers.isBlock(state, grab(FLBlocks.CURED_OVEN_BOTTOM)))
            {
                level.setBlockAndUpdate(pos, copy(FLBlocks.CURED_OVEN_BOTTOM.get(type), state));
            }
            else if (Helpers.isBlock(state, grab(FLBlocks.CURED_OVEN_TOP)))
            {
                level.setBlockAndUpdate(pos, copy(FLBlocks.CURED_OVEN_TOP.get(type), state));
            }
            else if (Helpers.isBlock(state, grab(FLBlocks.CURED_OVEN_HOPPER)))
            {
                level.setBlockAndUpdate(pos, copy(FLBlocks.CURED_OVEN_HOPPER.get(type), state));
            }
            else if (Helpers.isBlock(state, grab(FLBlocks.CURED_OVEN_CHIMNEY)))
            {
                level.setBlockAndUpdate(pos, copy(FLBlocks.CURED_OVEN_CHIMNEY.get(type), state));
            }
            else if (Helpers.isBlock(state, grab(FLBlocks.INSULATED_OVEN_BOTTOM)))
            {
                level.setBlockAndUpdate(pos, copy(FLBlocks.INSULATED_OVEN_BOTTOM.get(type), state));
            }
            else if (Helpers.isBlock(state, grab(FLBlocks.OVEN_COUNTERTOP)))
            {
                level.setBlockAndUpdate(pos, copy(FLBlocks.OVEN_COUNTERTOP.get(type), state));
            }
            else if (Helpers.isBlock(state, Blocks.BRICKS) && (type == OvenType.RUSTIC || type == OvenType.TILE))
            {
                if (type == OvenType.RUSTIC)
                {
                    level.setBlockAndUpdate(pos, FLBlocks.RUSTIC_BRICKS.get().defaultBlockState());
                }
                else
                {
                    level.setBlockAndUpdate(pos, FLBlocks.TILES.get().defaultBlockState());
                }
            }
            else
            {
                return InteractionResult.PASS;
            }
            stack.shrink(1);
            Helpers.playSound(level, pos, SoundEvents.STONE_PLACE);
            return InteractionResult.sidedSuccess(level.isClientSide);
        }

        return InteractionResult.PASS;
    }

    private Block grab(Map<OvenType, RegistryObject<Block>> map)
    {
        return map.get(OvenType.BRICK).get();
    }

    private BlockState copy(Supplier<? extends Block> block, BlockState originalState)
    {
        return Helpers.copyProperties(block.get().defaultBlockState(), originalState);
    }
}
