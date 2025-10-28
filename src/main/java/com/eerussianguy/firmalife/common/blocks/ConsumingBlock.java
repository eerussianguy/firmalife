package com.eerussianguy.firmalife.common.blocks;

import com.eerussianguy.firmalife.common.FLHelpers;
import com.eerussianguy.firmalife.common.blockentities.FLBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.items.ItemHandlerHelper;

import net.dries007.tfc.common.blocks.ExtendedProperties;
import net.dries007.tfc.common.blocks.devices.BottomSupportedDeviceBlock;
import net.dries007.tfc.common.component.food.FoodCapability;
import net.dries007.tfc.common.component.food.IFood;
import net.dries007.tfc.common.player.IPlayerInfo;
import net.dries007.tfc.common.player.PlayerInfo;
import net.dries007.tfc.util.Helpers;

public class ConsumingBlock extends BottomSupportedDeviceBlock
{
    public static ConsumingBlock plate(ExtendedProperties properties)
    {
        return new ConsumingBlock(properties, PLATE_SHAPE);
    }

    public static final VoxelShape PLATE_SHAPE = box(2, 0, 2, 14, 1, 14);

    public ConsumingBlock(ExtendedProperties properties, VoxelShape shape)
    {
        super(properties, InventoryRemoveBehavior.DROP, shape);
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit)
    {
        return FLHelpers.consumeItemInventory(level, pos, FLBlockEntities.PLATE, (plate, inv) -> {
            final ItemStack held = player.getItemInHand(hand);
            if (held.isEmpty())
            {
                final ItemStack s1 = inv.getStackInSlot(0);
                final IFood cap = FoodCapability.get(s1);
                if (!s1.isEmpty() && cap != null && player.isShiftKeyDown() && player.getFoodData() instanceof PlayerInfo data && data.needsFood())
                {
                    final ItemStack newItem = s1.getItem().finishUsingItem(s1, level, player);
                    s1.shrink(1);
                    ItemHandlerHelper.giveItemToPlayer(player, newItem);
                    Helpers.playSound(level, pos, SoundEvents.GENERIC_EAT);
                    return ItemInteractionResult.sidedSuccess(level.isClientSide);
                }
                return FLHelpers.takeOne(level, 0, inv, player);
            }
            else
            {
                plate.setRotation(player);
                return FLHelpers.insertOne(level, held, 0, inv, player);
            }
        });
    }
}
