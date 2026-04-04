package com.eerussianguy.firmalife.common.blocks;

import com.eerussianguy.firmalife.common.FLHelpers;
import com.eerussianguy.firmalife.common.blockentities.DryingMatBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.piston.PistonHeadBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import net.dries007.tfc.common.blocks.ExtendedProperties;
import net.dries007.tfc.common.blocks.devices.BottomSupportedDeviceBlock;

public class SolarDrierBlock extends BottomSupportedDeviceBlock
{
    private static final VoxelShape SHAPE = Shapes.or(
        box(0, 0, 0, 16, 1, 16),
        box(0, 1, 0, 2, 3, 2),
        box(14, 1, 14, 16, 3, 16),
        box(14, 1, 0, 16, 3, 2),
        box(0, 1, 14, 2, 3, 16),
        box(0, 3, 0, 16, 4, 16)
    );

    public SolarDrierBlock(ExtendedProperties properties)
    {
        super(properties, InventoryRemoveBehavior.DROP, SHAPE);
    }

    @Override
    public BlockState updateShape(BlockState state, Direction facing, BlockState facingState, LevelAccessor level, BlockPos currentPos, BlockPos facingPos)
    {
        if (facingState.getBlock() instanceof PistonHeadBlock && facingState.getValue(PistonHeadBlock.FACING) == facing.getOpposite() && facing.getAxis().isHorizontal())
        {
            if (level.getBlockEntity(currentPos) instanceof DryingMatBlockEntity mat)
            {
                mat.ejectItem(facing.getOpposite());
            }
        }
        return super.updateShape(state, facing, facingState, level, currentPos, facingPos);
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

    @Override
    public ItemInteractionResult useItemOn(ItemStack held, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult result)
    {
        return DryingMatBlock.use(level, pos, player, held);
    }
}
