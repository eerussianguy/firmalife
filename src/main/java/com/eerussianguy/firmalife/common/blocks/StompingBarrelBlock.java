package com.eerussianguy.firmalife.common.blocks;

import com.eerussianguy.firmalife.common.FLHelpers;
import com.eerussianguy.firmalife.common.blockentities.FLBlockEntities;
import com.eerussianguy.firmalife.common.blockentities.StompingBarrelBlockEntity;
import com.eerussianguy.firmalife.common.recipes.StompingRecipe;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.Entity;
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

public class StompingBarrelBlock extends DeviceBlock
{
    public static final VoxelShape SHAPE = box(1, 0, 1, 15, 8, 15);

    public StompingBarrelBlock(ExtendedProperties properties)
    {
        super(properties, InventoryRemoveBehavior.DROP);
    }

    @Override
    public ItemInteractionResult useItemOn(ItemStack held, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult result)
    {
        return FLHelpers.consumeItemInventory(level, pos, FLBlockEntities.STOMPING_BARREL, (barrel, inv) -> {
            final ItemStack current = inv.getStackInSlot(0);
            if (StompingRecipe.getRecipe(held) != null)
            {
                if ((ItemStack.isSameItem(current, held) || current.isEmpty()) && current.getCount() < StompingBarrelBlockEntity.MAX_GRAPES)
                {
                    Helpers.playSound(level, pos, SoundEvents.SLIME_SQUISH_SMALL);
                    final ItemStack leftover = inv.insertItem(0, held.split(StompingBarrelBlockEntity.MAX_GRAPES), false);
                    if (!leftover.isEmpty())
                    {
                        ItemHandlerHelper.giveItemToPlayer(player, leftover);
                    }
                    return ItemInteractionResult.sidedSuccess(level.isClientSide);
                }
            }
            else if (held.isEmpty() && !current.isEmpty())
            {
                ItemHandlerHelper.giveItemToPlayer(player, inv.extractItem(0, 64, false));
                return ItemInteractionResult.sidedSuccess(level.isClientSide);
            }
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        });
    }

    @Override
    public void fallOn(Level level, BlockState state, BlockPos pos, Entity entity, float distance)
    {
        if (level.getBlockEntity(pos) instanceof StompingBarrelBlockEntity barrel)
        {
            barrel.stomp(entity);
        }
    }

    @Override
    @SuppressWarnings("deprecation")
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context)
    {
        return SHAPE;
    }
}
