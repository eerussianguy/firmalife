package com.eerussianguy.firmalife.common.blocks.bee;

import com.eerussianguy.firmalife.common.FLHelpers;
import com.eerussianguy.firmalife.common.blockentities.CentrifugeBlockEntity;
import com.eerussianguy.firmalife.common.blocks.FourWayDeviceBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import net.dries007.tfc.client.TFCSounds;
import net.dries007.tfc.common.blocks.ExtendedProperties;
import net.dries007.tfc.util.Helpers;

public class CentrifugeBlock extends FourWayDeviceBlock
{
    public static final VoxelShape SHAPE = Shapes.or(
        box(0, 0, 0, 16, 15, 16),
        box(5, 15, 5, 11, 16, 11)
    );

    public CentrifugeBlock(ExtendedProperties properties)
    {
        super(properties, InventoryRemoveBehavior.DROP);
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult)
    {
        if (level.getBlockEntity(pos) instanceof CentrifugeBlockEntity cent && !cent.isWorking())
        {
            if (stack.isEmpty())
            {
                if (!player.isShiftKeyDown() && !cent.isConnectedToNetwork() && cent.startWorking())
                {
                    Helpers.playSound(level, pos, TFCSounds.QUERN_DRAG.get());
                    return ItemInteractionResult.sidedSuccess(level.isClientSide);
                }
                return FLHelpers.takeOneAny(level, 0, CentrifugeBlockEntity.SLOTS - 1, cent.getInventory(), player);
            }
            else if (cent.isItemValid(0, stack))
            {
                return FLHelpers.insertOneAny(level, stack, 0, CentrifugeBlockEntity.SLOTS - 1, cent.getInventory(), player);
            }
        }

        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context)
    {
        return SHAPE;
    }

    @Override
    protected boolean hasAnalogOutputSignal(BlockState state)
    {
        return true;
    }

    @Override
    protected int getAnalogOutputSignal(BlockState state, Level level, BlockPos pos)
    {
        return FLHelpers.getRedstoneSignalFromContainer(level, pos);
    }
}
