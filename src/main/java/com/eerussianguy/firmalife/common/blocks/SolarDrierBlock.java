package com.eerussianguy.firmalife.common.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import net.dries007.tfc.common.blocks.ExtendedProperties;

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
    @SuppressWarnings("deprecation")
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult result)
    {
        return DryingMatBlock.use(level, pos, player, hand);
    }
}
