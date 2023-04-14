package com.eerussianguy.firmalife.common.blocks;

import com.eerussianguy.firmalife.common.FLHelpers;
import com.eerussianguy.firmalife.common.blockentities.FLBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
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
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.items.ItemHandlerHelper;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.blocks.ExtendedProperties;
import net.dries007.tfc.util.Helpers;

public class IceFishingStationBlock extends FourWayDeviceBlock
{
    public static final VoxelShape[] SHAPES = Helpers.computeHorizontalShapes(dir -> Shapes.or(
        Helpers.rotateShape(dir, 4, 0, 0, 12, 1, 12),
        Helpers.rotateShape(dir, 5, 0, 8, 11, 2, 10),
        Helpers.rotateShape(dir, 5, 0, 5, 11, 2, 7),
        Helpers.rotateShape(dir, 5, 0, 7, 7, 2, 8),
        Helpers.rotateShape(dir, 9, 0, 7, 11, 2, 8)
    ));

    public static boolean canSurvive(LevelReader level, BlockPos pos)
    {
        return Helpers.isBlock(level.getBlockState(pos.below()), BlockTags.ICE);
    }

    public static final BooleanProperty CAST = FLStateProperties.CAST;

    public IceFishingStationBlock(ExtendedProperties properties)
    {
        super(properties, InventoryRemoveBehavior.DROP);
        registerDefaultState(getStateDefinition().any().setValue(CAST, false));
    }

    @Override
    @SuppressWarnings("deprecation")
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult result)
    {
        return FLHelpers.consumeInventory(level, pos, FLBlockEntities.ICE_FISHING_STATION, (station, inv) -> {
            final ItemStack inside = station.getRod();
            final ItemStack held = player.getItemInHand(hand);
            if (inside.isEmpty() && inv.isItemValid(0, held))
            {
                player.setItemInHand(hand, inv.insertItem(0, held, false));
                return InteractionResult.sidedSuccess(level.isClientSide);
            }
            else if (!inside.isEmpty() && held.isEmpty() && player.isShiftKeyDown())
            {
                station.withdraw();
                ItemHandlerHelper.giveItemToPlayer(player, inv.extractItem(0, 1, false));
                return InteractionResult.sidedSuccess(level.isClientSide);
            }
            else if (!state.getValue(CAST) && !inside.isEmpty())
            {
                if (!station.hasBait() && Helpers.isItem(held, TFCTags.Items.SMALL_FISHING_BAIT) && Helpers.isItem(inside, TFCTags.Items.HOLDS_SMALL_FISHING_BAIT))
                {
                    inside.getOrCreateTag().put("bait", held.split(1).save(new CompoundTag()));
                    station.markForSync();
                    player.displayClientMessage(Helpers.translatable("firmalife.fishing.bait_added"), true);
                    return InteractionResult.sidedSuccess(level.isClientSide);
                }
                else if (station.hasBait())
                {
                    if (!Helpers.isFluid(level.getBlockState(station.getBaitPos(state)).getFluidState(), FluidTags.WATER))
                    {
                        player.displayClientMessage(Helpers.translatable("firmalife.fishing.no_water"), true);
                        return InteractionResult.FAIL;
                    }
                    level.setBlockAndUpdate(pos, state.setValue(CAST, true));
                    return InteractionResult.sidedSuccess(level.isClientSide);
                }
                else
                {
                    player.displayClientMessage(Helpers.translatable("firmalife.fishing.no_bait"), true);
                }
            }
            else if (state.getValue(CAST))
            {
                if (station.hasHooked())
                {
                    station.pullHooked();
                    return InteractionResult.sidedSuccess(level.isClientSide);
                }
                else if (player.isShiftKeyDown())
                {
                    station.withdraw();
                    return InteractionResult.sidedSuccess(level.isClientSide);
                }
            }
            return InteractionResult.PASS;
        });
    }

    @Override
    @SuppressWarnings("deprecation")
    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos)
    {
        return canSurvive(level, pos);
    }

    @Override
    @Nullable
    public BlockState getStateForPlacement(BlockPlaceContext context)
    {
        return canSurvive(context.getLevel(), context.getClickedPos()) ? super.getStateForPlacement(context) : null;
    }

    @Override
    @SuppressWarnings("deprecation")
    public BlockState updateShape(BlockState state, Direction facing, BlockState facingState, LevelAccessor level, BlockPos currentPos, BlockPos facingPos)
    {
        return facing == Direction.DOWN && !Helpers.isBlock(facingState, BlockTags.ICE) ? Blocks.AIR.defaultBlockState() : super.updateShape(state, facing, facingState, level, currentPos, facingPos);
    }

    @Override
    @SuppressWarnings("deprecation")
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext pContext)
    {
        return SHAPES[state.getValue(FACING).get2DDataValue()];
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        super.createBlockStateDefinition(builder.add(CAST));
    }
}
