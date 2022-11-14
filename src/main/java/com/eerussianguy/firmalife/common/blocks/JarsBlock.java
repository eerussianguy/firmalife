package com.eerussianguy.firmalife.common.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.VoxelShape;

import net.minecraftforge.items.ItemHandlerHelper;

import net.dries007.tfc.common.blocks.ExtendedProperties;
import net.dries007.tfc.common.blocks.TFCBlockStateProperties;
import net.dries007.tfc.common.blocks.devices.BottomSupportedDeviceBlock;
import net.dries007.tfc.common.blocks.devices.DeviceBlock;
import net.dries007.tfc.util.Helpers;
import org.jetbrains.annotations.Nullable;

public class JarsBlock extends BottomSupportedDeviceBlock
{
    public static final IntegerProperty COUNT = TFCBlockStateProperties.COUNT_1_4;

    public static final VoxelShape SHAPE = box(0, 0, 0, 16, 4, 16);

    public JarsBlock(ExtendedProperties properties)
    {
        super(properties, DeviceBlock.InventoryRemoveBehavior.NOOP, SHAPE);
        registerDefaultState(getStateDefinition().any().setValue(COUNT, 1));
    }

    @Override
    @SuppressWarnings("deprecation")
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit)
    {
        ItemStack held = player.getItemInHand(hand);
        if (Helpers.isItem(held, this.asItem()) && !player.isShiftKeyDown())
        {
            int count = state.getValue(COUNT);
            if (count < 4)
            {
                level.setBlockAndUpdate(pos, state.setValue(COUNT, count + 1));
                if (!player.isCreative()) held.shrink(1);
                return InteractionResult.sidedSuccess(level.isClientSide);
            }
        }
        else if (held.isEmpty() && player.isShiftKeyDown())
        {
            int count = state.getValue(COUNT);
            ItemStack drop = new ItemStack(this);
            ItemHandlerHelper.giveItemToPlayer(player, drop);
            if (count > 1)
            {
                level.setBlockAndUpdate(pos, state.setValue(COUNT, count - 1));
            }
            else
            {
                level.destroyBlock(pos, false);
            }
            return InteractionResult.sidedSuccess(level.isClientSide);
        }
        return InteractionResult.PASS;
    }

    @Override
    @Nullable
    public BlockState getStateForPlacement(BlockPlaceContext context)
    {
        BlockPos pos = context.getClickedPos();
        Level level = context.getLevel();
        return level.getBlockState(pos).getBlock() == this || !canSurvive(level, pos) ? null : super.getStateForPlacement(context);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        super.createBlockStateDefinition(builder.add(COUNT));
    }
}
