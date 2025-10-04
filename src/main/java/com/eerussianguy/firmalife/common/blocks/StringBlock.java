package com.eerussianguy.firmalife.common.blocks;

import java.util.List;
import java.util.function.Supplier;
import com.eerussianguy.firmalife.common.FLHelpers;
import com.eerussianguy.firmalife.common.blockentities.FLBlockEntities;
import com.eerussianguy.firmalife.common.items.FLFoodTraits;
import com.eerussianguy.firmalife.config.FLConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
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
import net.neoforged.neoforge.items.ItemHandlerHelper;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.common.blockentities.FirepitBlockEntity;
import net.dries007.tfc.common.blocks.ExtendedProperties;
import net.dries007.tfc.common.blocks.devices.DeviceBlock;
import net.dries007.tfc.util.Helpers;

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
            if (!stateAt.isAir() && !isStringBlock(stateAt))
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
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random)
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
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext context)
    {
        final Level level = context.getLevel();
        final BlockPos pos = context.getClickedPos();
        for (Direction d : Direction.Plane.HORIZONTAL)
        {
            if (getHorizontalDistance(d, level, pos) > 0) // we found a pole it could connect to
            {
                if (isWallSupport(level, pos.relative(d.getOpposite()), level.getBlockState(pos.relative(d.getOpposite())), d.getOpposite()))
                {
                    return defaultBlockState().setValue(AXIS, d.getAxis());
                }
            }
        }
        return null;
    }

    private boolean isWallSupport(LevelReader level, BlockPos pos, BlockState state, Direction direction)
    {
        return state.isFaceSturdy(level, pos, direction.getOpposite());
    }

    private static boolean isStringBlock(BlockState state)
    {
        return state.getBlock() instanceof StringBlock;
    }

    @Override
    public BlockState updateShape(BlockState state, Direction facing, BlockState facingState, LevelAccessor level, BlockPos currentPos, BlockPos facingPos)
    {
        if (facing.getAxis() == state.getValue(AXIS))
        {
            if (!isStringBlock(facingState) && !facingState.isFaceSturdy(level, facingPos, facing))
            {
                return Blocks.AIR.defaultBlockState();
            }
        }
        return state;
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack)
    {
        final BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos();
        Direction direction = null;
        for (Direction checkDir : Direction.Plane.HORIZONTAL)
        {
            if (checkDir.getAxis() == state.getValue(AXIS))
            {
                mutablePos.set(pos).move(checkDir);
                if (isWallSupport(level, mutablePos, level.getBlockState(mutablePos), checkDir))
                {
                    direction = checkDir.getOpposite();
                    break;
                }
            }

        }
        if (direction == null)
            return;

        final int distance = getHorizontalDistance(direction, level, pos);
        if (distance == 0 || stack.getCount() < distance)
        {
            level.destroyBlock(pos, true);
        }
        else if (distance > 0)
        {
            stack.shrink(distance - 1); // first one will be used by BlockItem
            for (int i = 1; i < distance; i++)
            {
                mutablePos.set(pos).move(direction, i);
                final BlockState stateAt = level.getBlockState(mutablePos);
                if (stateAt.isAir())
                {
                    level.setBlock(mutablePos, state.setValue(AXIS, direction.getAxis()), 2);
                    mutablePos.move(Direction.DOWN);
                    level.scheduleTick(mutablePos, level.getFluidState(mutablePos).getType(), 3);
                }
            }
        }
    }

    private int getHorizontalDistance(Direction direction, LevelReader level, BlockPos pos)
    {
        int distance = -1;
        final BlockPos.MutableBlockPos cursor = new BlockPos.MutableBlockPos();
        for (int i = 0; i < 5; ++i)
        {
            cursor.set(pos).move(direction, i);
            BlockState state = level.getBlockState(cursor);
            if (!isStringBlock(state) && !state.isAir() && !isWallSupport(level, cursor, state, direction))
            {
                return 0;
            }

            cursor.move(direction, 1);
            state = level.getBlockState(cursor);
            if (isWallSupport(level, cursor, state, direction))
            {
                distance = i;
                break;
            }
        }

        return distance == -1 ? 0 : distance + 1;
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext)
    {
        return state.getValue(AXIS) == Direction.Axis.X ? SHAPE_X : SHAPE_Z;
    }

    @Override
    public ItemStack getCloneItemStack(BlockState state, HitResult target, LevelReader level, BlockPos pos, Player player)
    {
        return getItem();
    }

    @Override
    public ItemInteractionResult useItemOn(ItemStack item, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult result)
    {
        return FLHelpers.consumeItemInventory(level, pos, FLBlockEntities.STRING, (string, inv) -> {
            if (item.isEmpty())
            {
                ItemStack stack = inv.extractItem(0, player.isShiftKeyDown() ? Integer.MAX_VALUE : 1, false);
                if (stack.isEmpty()) return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
                ItemHandlerHelper.giveItemToPlayer(player, stack);
                string.resetCounter();
                return ItemInteractionResult.sidedSuccess(level.isClientSide());
            }
            else if (inv.isItemValid(0, item))
            {
                string.resetCounter();
                player.setItemInHand(hand, FLHelpers.mergeInsertStack(inv, 0, item));
                return ItemInteractionResult.sidedSuccess(level.isClientSide());
            }
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        });
    }

    @Override
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
