package com.eerussianguy.firmalife.common.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.VoxelShape;

import com.eerussianguy.firmalife.common.FLHelpers;
import com.eerussianguy.firmalife.common.blockentities.SimpleItemRecipeBlockEntity;
import net.dries007.tfc.common.blocks.ExtendedProperties;
import net.dries007.tfc.common.blocks.devices.BottomSupportedDeviceBlock;
import net.dries007.tfc.common.capabilities.Capabilities;

public class DryingMatBlock extends BottomSupportedDeviceBlock
{
    public static InteractionResult use(Level level, BlockPos pos, Player player, InteractionHand hand)
    {
        ItemStack held = player.getItemInHand(hand);
        if (level.getBlockEntity(pos) instanceof SimpleItemRecipeBlockEntity<?> mat)
        {
            InteractionResult result = mat.getCapability(Capabilities.ITEM).map(inv -> {
                if (inv.getStackInSlot(0).isEmpty() && !held.isEmpty())
                {
                    InteractionResult res = FLHelpers.insertOne(level, held, 0, inv, player);
                    if (res.consumesAction())
                    {
                        mat.markForSync();
                        mat.start();
                    }
                    return res;
                }
                else if (!inv.getStackInSlot(0).isEmpty() && held.isEmpty() && player.isShiftKeyDown())
                {
                    mat.markForSync();
                    return FLHelpers.takeOne(level, 0, inv, player);
                }
                return InteractionResult.PASS;
            }).orElse(InteractionResult.PASS);
            mat.markForSync();
            return result;
        }
        return InteractionResult.PASS;
    }

    private static final VoxelShape SHAPE = box(0, 0, 0, 16, 1, 16);

    public DryingMatBlock(ExtendedProperties properties)
    {
        super(properties, InventoryRemoveBehavior.DROP, SHAPE);
    }

    @Override
    @SuppressWarnings("deprecation")
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult result)
    {
        return DryingMatBlock.use(level, pos, player, hand);
    }
}
