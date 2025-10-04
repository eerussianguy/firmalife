package com.eerussianguy.firmalife.common.blocks.plant;

import com.eerussianguy.firmalife.common.blocks.FLBlocks;
import com.eerussianguy.firmalife.common.blocks.FLStateProperties;
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
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.common.blocks.ExtendedBlock;
import net.dries007.tfc.common.blocks.ExtendedProperties;
import net.dries007.tfc.common.blocks.devices.BottomSupportedDeviceBlock;
import net.dries007.tfc.common.items.TFCItems;
import net.dries007.tfc.util.Helpers;

public class GrapeTrellisPostBlock extends ExtendedBlock
{
    public static final EnumProperty<Direction.Axis> AXIS = BlockStateProperties.HORIZONTAL_AXIS;
    public static final BooleanProperty STRING_PLUS = FLStateProperties.STRING_PLUS;
    public static final BooleanProperty STRING_MINUS = FLStateProperties.STRING_MINUS;
    public static final VoxelShape SHAPE = box(6, 0, 6, 10, 16, 10);

    public GrapeTrellisPostBlock(ExtendedProperties properties)
    {
        super(properties);
        registerDefaultState(getStateDefinition().any().setValue(AXIS, Direction.Axis.X).setValue(STRING_PLUS, false).setValue(STRING_MINUS, false));
    }

    @Override
    public ItemInteractionResult useItemOn(ItemStack held, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult result)
    {
        if (result.getDirection().getAxis().isVertical())
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        if (Helpers.isItem(held, TFCItems.JUTE_FIBER.get()))
        {
            final BlockState string = FLBlocks.GRAPE_STRING.get().defaultBlockState().setValue(AXIS, result.getDirection().getAxis());
            final BlockPos potentialPos = pos.relative(result.getDirection());
            if (string.canSurvive(level, potentialPos) && level.getBlockState(potentialPos).canBeReplaced())
            {
                if (!player.isCreative())
                    held.shrink(1);
                Helpers.playPlaceSound(player, level, pos, string);
                level.setBlockAndUpdate(potentialPos, string);
                return ItemInteractionResult.SUCCESS;
            }
        }
        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context)
    {
        final Level level = context.getLevel();
        final BlockPos pos = context.getClickedPos();
        final BlockState below = level.getBlockState(pos.below());
        if (below.getBlock() instanceof GrapeTrellisPostBlock)
        {
            return Helpers.copyProperty(defaultBlockState(), below, AXIS);
        }
        if (!below.isFaceSturdy(level, pos.below(), Direction.UP))
        {
            return null;
        }
        return defaultBlockState().setValue(AXIS, context.getHorizontalDirection().getAxis());
    }

    @Override
    @SuppressWarnings("deprecation")
    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos)
    {
        return BottomSupportedDeviceBlock.canSurvive(level, pos) || level.getBlockState(pos.below()).getBlock() instanceof GrapeTrellisPostBlock;
    }

    @Override
    @SuppressWarnings("deprecation")
    public BlockState updateShape(BlockState state, Direction facing, BlockState facingState, LevelAccessor level, BlockPos pos, BlockPos facingPos)
    {
        if (facing.getAxis() != state.getValue(AXIS) && facing.getAxis() != Direction.Axis.Y)
        {
            final boolean plus = facing.getAxisDirection() == Direction.AxisDirection.POSITIVE;
            return state.setValue(plus ? STRING_PLUS : STRING_MINUS, facingState.getBlock() instanceof GrapeStringBlock);
        }
        if (facing == Direction.DOWN && !(facingState.getBlock() instanceof GrapeTrellisPostBlock || facingState.isFaceSturdy(level, facingPos, Direction.UP)))
        {
            return Blocks.AIR.defaultBlockState();
        }
        return state;
    }

    @Override
    @SuppressWarnings("deprecation")
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context)
    {
        return SHAPE;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        super.createBlockStateDefinition(builder.add(AXIS, STRING_MINUS, STRING_PLUS));
    }
}
