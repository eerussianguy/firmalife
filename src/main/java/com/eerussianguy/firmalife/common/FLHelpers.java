package com.eerussianguy.firmalife.common;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;

import com.eerussianguy.firmalife.Firmalife;
import org.jetbrains.annotations.NotNull;

public class FLHelpers
{
    public static Direction[] NOT_DOWN = new Direction[] {Direction.WEST, Direction.EAST, Direction.NORTH, Direction.SOUTH, Direction.UP};

    public static ResourceLocation identifier(String id)
    {
        return new ResourceLocation(Firmalife.MOD_ID, id);
    }

    public static Component blockEntityName(String name)
    {
        return new TranslatableComponent(Firmalife.MOD_ID + ".block_entity." + name);
    }

    public static InteractionResult insertOne(Level level, ItemStack item, int slot, IItemHandler inv, Player player)
    {
        if (!inv.isItemValid(slot, item)) return InteractionResult.PASS;
        return completeInsertion(level, item, inv, player, slot);
    }

    public static InteractionResult takeOne(Level level, int slot, IItemHandler inv, Player player)
    {
        ItemStack stack = inv.extractItem(slot, 1, false);
        if (stack.isEmpty()) return InteractionResult.PASS;
        if (!level.isClientSide) ItemHandlerHelper.giveItemToPlayer(player, stack);
        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    public static InteractionResult insertOneAny(Level level, ItemStack item, int start, int end, IItemHandler inv, Player player)
    {
        for (int i = start; i <= end; i++)
        {
            if (inv.getStackInSlot(i).isEmpty())
            {
                return completeInsertion(level, item, inv, player, i);
            }
        }
        return InteractionResult.PASS;
    }

    private static InteractionResult completeInsertion(Level level, ItemStack item, IItemHandler inv, Player player, int slot)
    {
        ItemStack stack = inv.insertItem(slot, item.split(1), false);
        if (stack.isEmpty()) return InteractionResult.sidedSuccess(level.isClientSide);
        if (!level.isClientSide)
        {
            ItemHandlerHelper.giveItemToPlayer(player, stack);
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    public static InteractionResult takeOneAny(Level level, int start, int end, IItemHandler inv, Player player)
    {
        for (int i = start; i <= end; i++)
        {
            ItemStack stack = inv.extractItem(i, 1, false);
            if (stack.isEmpty()) continue;
            if (!level.isClientSide) ItemHandlerHelper.giveItemToPlayer(player, stack);
            return InteractionResult.sidedSuccess(level.isClientSide);
        }
        return InteractionResult.PASS;
    }

    public static Iterable<BlockPos> allPositionsCentered(BlockPos center, int radius, int height)
    {
        return BlockPos.betweenClosed(center.offset(-radius, -height, -radius), center.offset(radius, height, radius));
    }
}
