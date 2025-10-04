package com.eerussianguy.firmalife.common.blocks;

import com.eerussianguy.firmalife.common.FLHelpers;
import com.eerussianguy.firmalife.common.FLTags;
import com.eerussianguy.firmalife.common.blockentities.FLBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.items.ItemHandlerHelper;

import net.dries007.tfc.common.blocks.ExtendedProperties;
import net.dries007.tfc.common.blocks.devices.DeviceBlock;
import net.dries007.tfc.util.Helpers;

public class BarrelPressBlock extends DeviceBlock
{
    public BarrelPressBlock(ExtendedProperties properties)
    {
        super(properties, InventoryRemoveBehavior.DROP);
    }

    @Override
    @SuppressWarnings("deprecation")
    public ItemInteractionResult useItemOn(ItemStack held, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult result)
    {
        return FLHelpers.consumeItemInventory(level, pos, FLBlockEntities.BARREL_PRESS, (press, inv) -> {
            if (press.hasOutput() && Helpers.isItem(held, FLTags.Items.EMPTY_WINE_BOTTLES))
            {
                final ItemStack newStack = press.tryFillWine(level, pos, held);
                if (!newStack.isEmpty())
                {
                    ItemHandlerHelper.giveItemToPlayer(player, newStack);
                    return ItemInteractionResult.sidedSuccess(level.isClientSide);
                }
            }
            if (held.isEmpty() && player.isShiftKeyDown() && !press.hasOutput())
            {
                return press.push();
            }
            else if (player instanceof ServerPlayer server)
            {
                server.openMenu(state.getMenuProvider(level, pos));
            }
            return ItemInteractionResult.SUCCESS;
        });
    }

    @Override
    @SuppressWarnings("deprecation")
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context)
    {
        return StompingBarrelBlock.SHAPE;
    }

}
