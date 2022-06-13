package com.eerussianguy.firmalife.common.blocks;

import java.util.function.Supplier;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import net.dries007.tfc.common.blockentities.FirepitBlockEntity;
import net.dries007.tfc.common.blocks.ExtendedProperties;
import net.dries007.tfc.common.blocks.devices.DeviceBlock;
import org.jetbrains.annotations.Nullable;

public class StringBlock extends DeviceBlock
{
    @Nullable
    public static FirepitBlockEntity findFirepit(Level level, BlockPos pos)
    {
        BlockPos.MutableBlockPos mutable = new BlockPos.MutableBlockPos();
        mutable.set(pos);
        for (int i = 0; i < 4; i++)
        {
            mutable.move(0, -1, 0);
            if (!level.getBlockState(pos).isAir())
            {
                return level.getBlockEntity(pos) instanceof FirepitBlockEntity firepit ? firepit : null;
            }
        }
        return null;
    }

    public static final EnumProperty<Direction.Axis> AXIS = BlockStateProperties.HORIZONTAL_AXIS;

    private static final VoxelShape SHAPE_X = box(0, 8, 7, 1, 10, 9);
    private static final VoxelShape SHAPE_Z = box(7, 8, 0, 9, 10, 1);

    private final Supplier<? extends Item> item;

    public StringBlock(ExtendedProperties properties, Supplier<? extends Item> item)
    {
        super(properties, InventoryRemoveBehavior.DROP);
        this.item = item;
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context)
    {
        Direction.Axis axis = context.getClickedFace().getAxis();
        if (axis.isHorizontal())
        {
            return defaultBlockState().setValue(AXIS, axis);
        }
        else
        {
            Player player = context.getPlayer();
            if (player != null)
            {
                axis = player.getDirection().getAxis();
                if (axis.isHorizontal())
                {
                    return defaultBlockState().setValue(AXIS, axis);
                }
            }
        }
        return null;
    }

    @Override
    @SuppressWarnings("deprecation")
    public VoxelShape getShape(BlockState state, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext)
    {
        return state.getValue(AXIS) == Direction.Axis.X ? SHAPE_X : SHAPE_Z;
    }

    @Override
    public ItemStack getCloneItemStack(BlockState state, HitResult target, BlockGetter level, BlockPos pos, Player player)
    {
        return getItem();
    }

    @Override
    @SuppressWarnings("deprecation")
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult result)
    {
        return DryingMatBlock.use(level, pos, player, hand);
    }

    @Override
    @SuppressWarnings("deprecation")
    public BlockState rotate(BlockState state, Rotation rot)
    {
        return switch (rot)
            {
                case COUNTERCLOCKWISE_90, CLOCKWISE_90 -> switch (state.getValue(AXIS))
                    {
                        case Z -> state.setValue(AXIS, Direction.Axis.X);
                        case X -> state.setValue(AXIS, Direction.Axis.Z);
                        default -> state;
                    };
                default -> state;
            };
    }

    public ItemStack getItem()
    {
        return new ItemStack(item.get());
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        super.createBlockStateDefinition(builder.add(AXIS));
    }
}
