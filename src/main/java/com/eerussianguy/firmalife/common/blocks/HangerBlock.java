package com.eerussianguy.firmalife.common.blocks;

import com.eerussianguy.firmalife.common.blockentities.FLBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.common.blocks.ExtendedProperties;
import net.dries007.tfc.common.blocks.devices.DeviceBlock;

public class HangerBlock extends DeviceBlock
{
    public static boolean canSurvive(LevelReader level, BlockPos pos)
    {
        final BlockPos above = pos.above();
        return level.getBlockState(above).isFaceSturdy(level, above, Direction.DOWN);
    }

    public static final VoxelShape BASE_SHAPE = Shapes.or(box(1, 12, 1, 15, 16, 15));

    public HangerBlock(ExtendedProperties properties)
    {
        super(properties, InventoryRemoveBehavior.DROP);
    }

    @Override
    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext)
    {
        return BASE_SHAPE;
    }

    @Override
    public ItemInteractionResult useItemOn(ItemStack held, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult result)
    {
        return level.getBlockEntity(pos, FLBlockEntities.HANGER.get()).map(shelf -> shelf.use(held)).orElse(ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION);
    }

    @Override
    @Nullable
    public BlockState getStateForPlacement(BlockPlaceContext context)
    {
        return canSurvive(context.getLevel(), context.getClickedPos()) ? super.getStateForPlacement(context) : null;
    }

    @Override
    public BlockState updateShape(BlockState state, Direction facing, BlockState facingState, LevelAccessor level, BlockPos currentPos, BlockPos facingPos)
    {
        return facing == Direction.UP && !facingState.isFaceSturdy(level, facingPos, Direction.DOWN) ? Blocks.AIR.defaultBlockState() : super.updateShape(state, facing, facingState, level, currentPos, facingPos);
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos)
    {
        return canSurvive(level, pos);
    }
}
