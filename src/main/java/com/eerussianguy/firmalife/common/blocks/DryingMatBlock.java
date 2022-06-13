package com.eerussianguy.firmalife.common.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import net.minecraftforge.items.CapabilityItemHandler;

import com.eerussianguy.firmalife.common.FLHelpers;
import com.eerussianguy.firmalife.common.blockentities.SimpleItemRecipeBlockEntity;
import net.dries007.tfc.common.blocks.ExtendedProperties;

public class DryingMatBlock extends BottomSupportedDeviceBlock
{
    public static InteractionResult use(Level level, BlockPos pos, Player player, InteractionHand hand)
    {
        ItemStack held = player.getItemInHand(hand);
        if (level.getBlockEntity(pos) instanceof SimpleItemRecipeBlockEntity<?> mat)
        {
            return mat.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).map(inv -> {
                if (mat.readStack().isEmpty() && !held.isEmpty())
                {
                    InteractionResult res = FLHelpers.insertOne(level, held, 0, inv, player);
                    if (res.consumesAction())
                    {
                        mat.start();
                    }
                    return res;
                }
                else if (!mat.readStack().isEmpty() && held.isEmpty() && player.isShiftKeyDown())
                {
                    return FLHelpers.takeOne(level, 0, inv, player);
                }
                return InteractionResult.PASS;
            }).orElse(InteractionResult.PASS);
        }
        return InteractionResult.PASS;
    }

    private static final VoxelShape SHAPE = box(0, 0, 0, 16, 1, 16);

    public DryingMatBlock(ExtendedProperties properties)
    {
        super(properties, InventoryRemoveBehavior.DROP);
    }

    @Override
    @SuppressWarnings("deprecation")
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context)
    {
        return SHAPE;
    }

    @Override
    @SuppressWarnings("deprecation")
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult result)
    {
        return DryingMatBlock.use(level, pos, player, hand);
    }
}
