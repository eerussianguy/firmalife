package com.eerussianguy.firmalife.common.blocks;

import java.util.List;
import java.util.Random;
import java.util.function.Supplier;

import com.eerussianguy.firmalife.config.FLConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import com.eerussianguy.firmalife.common.blockentities.FLBlockEntities;
import com.eerussianguy.firmalife.common.items.FLFoodTraits;
import net.dries007.tfc.common.blockentities.FirepitBlockEntity;
import net.dries007.tfc.common.blocks.ExtendedProperties;
import net.dries007.tfc.common.blocks.devices.DeviceBlock;
import net.dries007.tfc.common.capabilities.food.FoodCapability;
import net.dries007.tfc.common.capabilities.food.FoodTrait;
import net.dries007.tfc.util.Helpers;
import org.jetbrains.annotations.Nullable;

public class StringBlock extends DeviceBlock
{
    @Nullable
    public static FirepitBlockEntity findFirepit(Level level, BlockPos pos)
    {
        BlockPos.MutableBlockPos mutable = new BlockPos.MutableBlockPos();
        mutable.set(pos);
        final int range = FLConfig.SERVER.smokingFirepitRange.get();
        for (int i = 0; i < range; i++)
        {
            mutable.move(0, -1, 0);
            final BlockState stateAt = level.getBlockState(mutable);
            if (!stateAt.isAir() && !(stateAt.getBlock() instanceof StringBlock))
            {
                return level.getBlockEntity(mutable) instanceof FirepitBlockEntity firepit ? firepit : null;
            }
        }
        return null;
    }

    public static final EnumProperty<Direction.Axis> AXIS = BlockStateProperties.HORIZONTAL_AXIS;

    private static final VoxelShape SHAPE_X = box(0, 8, 7, 16, 10, 9);
    private static final VoxelShape SHAPE_Z = box(7, 8, 0, 9, 10, 16);

    private final Supplier<? extends Item> item;

    public StringBlock(ExtendedProperties properties, Supplier<? extends Item> item)
    {
        super(properties, InventoryRemoveBehavior.DROP);
        this.item = item;
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, Random random)
    {
        level.getBlockEntity(pos, FLBlockEntities.STRING.get()).ifPresent(string -> {
            ItemStack stack = string.readStack();
            if (!stack.isEmpty())
            {
                stack.getCapability(FoodCapability.CAPABILITY).ifPresent(food -> {
                    List<FoodTrait> traits = food.getTraits();
                    if (traits.contains(FLFoodTraits.SMOKED) || traits.contains(FLFoodTraits.RANCID_SMOKED))
                    {
                        final double x = pos.getX() + 0.5;
                        final double y = pos.getY() + 0.55;
                        final double z = pos.getZ() + 0.5;
                        for (int i = 0; i < 4; i++)
                        {
                            level.addParticle(ParticleTypes.SMOKE, x, y, z, Helpers.triangle(random) / 10f, random.nextFloat() / 5f, Helpers.triangle(random) / 10f);
                        }
                    }
                });
            }
        });
    }

    @Override
    @SuppressWarnings("deprecation")
    public BlockState updateShape(BlockState state, Direction facing, BlockState facingState, LevelAccessor level, BlockPos pos, BlockPos facingPos)
    {
        return canSurvive(level, pos, state) ? super.updateShape(state, facing, facingState, level, pos, facingPos) : Blocks.AIR.defaultBlockState();
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context)
    {
        BlockState state = null;
        Direction.Axis axis = context.getClickedFace().getAxis();
        if (axis.isHorizontal())
        {
            state = defaultBlockState().setValue(AXIS, axis);
        }
        else
        {
            Player player = context.getPlayer();
            if (player != null)
            {
                axis = player.getDirection().getAxis();
                if (axis.isHorizontal())
                {
                    state = defaultBlockState().setValue(AXIS, axis);
                }
            }
        }
        if (state != null && canSurvive(context.getLevel(), context.getClickedPos(), state))
        {
            return state;
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

    // todo this simply does not work
    private boolean canSurvive(LevelAccessor level, BlockPos pos, BlockState state)
    {
        BlockPos.MutableBlockPos mutable = new BlockPos.MutableBlockPos();
        Direction.Axis axis = state.getValue(AXIS);
        for (Direction d : Direction.Plane.HORIZONTAL)
        {
            if (d.getAxis() == axis)
            {
                mutable.set(pos);
                for (int i = 0; i < 5; i++)
                {
                    mutable.move(d);
                    BlockState stateAt = level.getBlockState(mutable);
                    if (stateAt.getBlock() instanceof StringBlock)
                    {
                        continue;
                    }
                    if (stateAt.isFaceSturdy(level, mutable, d.getOpposite()))
                    {
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
