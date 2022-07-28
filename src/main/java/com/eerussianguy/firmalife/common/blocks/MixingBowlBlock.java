package com.eerussianguy.firmalife.common.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.VoxelShape;

import com.eerussianguy.firmalife.common.FLHelpers;
import com.eerussianguy.firmalife.common.blockentities.FLBlockEntities;
import com.eerussianguy.firmalife.common.blockentities.MixingBowlBlockEntity;
import com.eerussianguy.firmalife.common.items.FLItems;
import net.dries007.tfc.common.blocks.ExtendedProperties;
import net.dries007.tfc.common.fluids.FluidHelpers;
import net.dries007.tfc.util.Helpers;

public class MixingBowlBlock extends BottomSupportedDeviceBlock
{
    public static final BooleanProperty SPOON = FLStateProperties.SPOON;
    private static final VoxelShape SHAPE = box(2, 0, 2, 14, 6, 14);

    public MixingBowlBlock(ExtendedProperties properties)
    {
        super(properties, InventoryRemoveBehavior.DROP, SHAPE);
        registerDefaultState(getStateDefinition().any().setValue(SPOON, false));
    }

    @Override
    @SuppressWarnings("deprecation")
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult result)
    {
        ItemStack held = player.getItemInHand(hand);
        return level.getBlockEntity(pos, FLBlockEntities.MIXING_BOWL.get()).map(bowl -> {
            if (!bowl.isMixing())
            {
                bowl.markForSync();
                if (FluidHelpers.transferBetweenBlockEntityAndItem(held, bowl, player, hand))
                {
                    return InteractionResult.sidedSuccess(level.isClientSide);
                }
                else if (held.isEmpty() && player.isShiftKeyDown())
                {
                    return FLHelpers.takeOneAny(level, 0, MixingBowlBlockEntity.SLOTS - 1, bowl, player);
                }
                else if (Helpers.isItem(held, FLItems.SPOON.get()))
                {
                    if (!player.isCreative()) held.shrink(1);
                    level.setBlockAndUpdate(pos, state.setValue(SPOON, true));
                    return InteractionResult.sidedSuccess(level.isClientSide);
                }
                else if (!player.isShiftKeyDown() && !held.isEmpty())
                {
                    return FLHelpers.insertOneAny(level, held, 0, MixingBowlBlockEntity.SLOTS - 1, bowl, player);
                }
                else if (bowl.startMixing(player))
                {
                    Helpers.playSound(level, pos, SoundEvents.SLIME_SQUISH);
                    return InteractionResult.sidedSuccess(level.isClientSide);
                }
            }
            return InteractionResult.PASS;
        }).orElse(InteractionResult.PASS);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        builder.add(SPOON);
    }
}
