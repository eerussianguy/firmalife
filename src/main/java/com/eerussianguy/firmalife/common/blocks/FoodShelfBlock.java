package com.eerussianguy.firmalife.common.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import com.eerussianguy.firmalife.common.blockentities.FLBlockEntities;
import net.dries007.tfc.common.blocks.ExtendedProperties;

public class FoodShelfBlock extends FourWayDeviceBlock
{
    public static final VoxelShape SOUTH_SHAPE = box(0, 0, 0, 16, 16, 9);
    public static final VoxelShape EAST_SHAPE = box(0, 0, 0, 9, 16, 16);
    public static final VoxelShape WEST_SHAPE = box(7, 0, 0, 16, 16, 16);
    public static final VoxelShape NORTH_SHAPE = box(0, 0, 7, 16, 16, 16);

    public FoodShelfBlock(ExtendedProperties properties)
    {
        super(properties, InventoryRemoveBehavior.DROP);
    }

    @Override
    @SuppressWarnings("deprecation")
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext pContext)
    {
        return switch (state.getValue(FACING))
            {
                case NORTH -> NORTH_SHAPE;
                case SOUTH -> SOUTH_SHAPE;
                case WEST -> WEST_SHAPE;
                default -> EAST_SHAPE;
            };
    }

    @Override
    @SuppressWarnings("deprecation")
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult result)
    {
        return level.getBlockEntity(pos, FLBlockEntities.FOOD_SHELF.get()).map(shelf -> shelf.use(player, hand)).orElse(InteractionResult.PASS);
    }
}
