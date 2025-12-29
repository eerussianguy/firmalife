package com.eerussianguy.firmalife.common.blocks;

import java.util.function.Supplier;

import com.eerussianguy.firmalife.common.blockentities.ClimateType;
import com.eerussianguy.firmalife.common.blockentities.FLBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import net.minecraftforge.items.ItemHandlerHelper;

import com.eerussianguy.firmalife.common.FLHelpers;
import com.eerussianguy.firmalife.common.blockentities.ClimateReceiver;
import com.eerussianguy.firmalife.common.util.FoodAge;
import com.eerussianguy.firmalife.config.FLConfig;
import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.blocks.ExtendedProperties;
import net.dries007.tfc.common.blocks.TFCBlockStateProperties;
import net.dries007.tfc.common.blocks.TFCBlocks;
import net.dries007.tfc.common.blocks.devices.BarrelBlock;
import net.dries007.tfc.common.blocks.devices.BarrelRackBlock;
import net.dries007.tfc.common.blocks.devices.BottomSupportedDeviceBlock;
import net.dries007.tfc.common.capabilities.food.FoodCapability;
import net.dries007.tfc.common.items.TFCItems;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.calendar.ICalendar;
import org.jetbrains.annotations.Nullable;

public class CheeseWheelBlock extends BottomSupportedDeviceBlock implements ClimateReceiver
{
    public static final IntegerProperty COUNT = TFCBlockStateProperties.COUNT_1_4;
    public static final EnumProperty<FoodAge> AGE = FLStateProperties.AGE;
    public static final BooleanProperty AGING = FLStateProperties.AGING;
    public static final BooleanProperty RACK = TFCBlockStateProperties.RACK;

    private static final VoxelShape SHAPE_1 = box(8, 0, 8, 14, 7, 14);
    private static final VoxelShape SHAPE_1_RACK = Shapes.or(SHAPE_1, BarrelBlock.RACK_SHAPE);
    private static final VoxelShape SHAPE_2 = box(8, 0, 2, 14, 7, 14);
    private static final VoxelShape SHAPE_2_RACK = Shapes.or(SHAPE_2, BarrelBlock.RACK_SHAPE);
    private static final VoxelShape SHAPE_3 = Shapes.or(box(2, 0, 2, 8, 7, 8), box(8, 0, 2, 14, 7, 14));
    private static final VoxelShape SHAPE_3_RACK = Shapes.or(SHAPE_3, BarrelBlock.RACK_SHAPE);
    private static final VoxelShape SHAPE_4 = box(2, 0, 2, 14, 7, 14);
    private static final VoxelShape SHAPE_4_RACK = Shapes.or(SHAPE_4, BarrelBlock.RACK_SHAPE);

    private static final VoxelShape[] SHAPES = new VoxelShape[] {SHAPE_1, SHAPE_2, SHAPE_3, SHAPE_4};
    private static final VoxelShape[] RACK_SHAPES = new VoxelShape[] {SHAPE_1_RACK, SHAPE_2_RACK, SHAPE_3_RACK, SHAPE_4_RACK};

    private final Supplier<? extends Item> slice;

    public CheeseWheelBlock(ExtendedProperties properties, Supplier<? extends Item> slice)
    {
        super(properties, InventoryRemoveBehavior.DROP, SHAPE_4);
        this.slice = slice;

        registerDefaultState(getStateDefinition().any().setValue(COUNT, 4).setValue(AGE, FoodAge.FRESH).setValue(AGING, false).setValue(RACK, false));
    }

    @Override
    @SuppressWarnings("deprecation")
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit)
    {
        final ItemStack held = player.getItemInHand(hand);
        if (held.isEmpty() && player.isShiftKeyDown())
        {
            if (state.getValue(RACK) && level.getBlockState(pos.above()).isAir() && hit.getLocation().y - pos.getY() > 0.875f)
            {
                Helpers.playPlaceSound(level, pos, TFCBlocks.BARREL_RACK.get().defaultBlockState());
                level.setBlockAndUpdate(pos, state.setValue(RACK, false));
                ItemHandlerHelper.giveItemToPlayer(player, new ItemStack(TFCBlocks.BARREL_RACK.get()));
                return InteractionResult.sidedSuccess(level.isClientSide);
            }
        }
        else if (Helpers.isItem(held, TFCBlocks.BARREL_RACK.get().asItem()) && !state.getValue(RACK))
        {
            if (!player.isCreative())
                held.shrink(1);
            level.setBlockAndUpdate(pos, state.setValue(RACK, true));
            Helpers.playPlaceSound(level, pos, TFCBlocks.BARREL_RACK.get().defaultBlockState());
            return InteractionResult.sidedSuccess(level.isClientSide);
        }
        if (Helpers.isItem(held, TFCTags.Items.KNIVES))
        {
            final int count = state.getValue(COUNT);
            ItemStack drop = new ItemStack(slice.get());
            FoodCapability.applyTrait(drop, state.getValue(AGE).getTrait());
            drop.getCapability(FoodCapability.CAPABILITY).ifPresent(cap -> cap.setCreationDate(FoodCapability.getRoundedCreationDate()));
            ItemHandlerHelper.giveItemToPlayer(player, drop);
            FLHelpers.resetCounter(level, pos);
            if (count - 1 == 0)
            {
                if (!state.getValue(RACK))
                {
                    level.destroyBlock(pos, false);
                }
                else
                {
                    level.setBlockAndUpdate(pos, TFCBlocks.BARREL_RACK.get().defaultBlockState());
                    Helpers.playPlaceSound(level, pos, TFCBlocks.BARREL_RACK.get().defaultBlockState());
                }
            }
            else
            {
                Helpers.playSound(level, pos, getSoundType(state, level, pos, player).getBreakSound());
                level.setBlockAndUpdate(pos, state.setValue(COUNT, count - 1));
            }
            return InteractionResult.sidedSuccess(level.isClientSide);
        }
        return InteractionResult.PASS;
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context)
    {
        final Level level = context.getLevel();
        final BlockPos pos = context.getClickedPos();
        final BlockState state = super.getStateForPlacement(context);
        if (state != null)
        {
            // case of replacing a barrel rack block
            if (Helpers.isBlock(level.getBlockState(pos), TFCBlocks.BARREL_RACK.get()))
            {
                return state.setValue(RACK, true);
            }
        }
        return state;
    }

    @Override
    public boolean isRandomlyTicking(BlockState state)
    {
        return state.getValue(COUNT) == 4;
    }

    @Override
    @SuppressWarnings("deprecation")
    public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource rand)
    {
        level.getBlockEntity(pos, FLBlockEntities.TICK_COUNTER.get()).ifPresent(counter -> {
            if (state.getValue(AGING))
            {
                long days = counter.getTicksSinceUpdate() / ICalendar.TICKS_IN_DAY;
                if (state.getValue(AGE) == FoodAge.FRESH && state.getValue(COUNT) == 4 && days > FLConfig.SERVER.cheeseAgedDays.get())
                {
                    level.setBlockAndUpdate(pos, state.setValue(AGE, FoodAge.AGED));
                    counter.resetCounter();
                }
                else if (state.getValue(AGE) == FoodAge.AGED && state.getValue(COUNT) == 4 && days > FLConfig.SERVER.cheeseVintageDays.get())
                {
                    level.setBlockAndUpdate(pos, state.setValue(AGE, FoodAge.VINTAGE));
                }
            }
            else
            {
                counter.resetCounter();
            }
        });
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack)
    {
        super.setPlacedBy(level, pos, state, placer, stack);
        FLHelpers.resetCounter(level, pos);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext)
    {
        final int slices = state.getValue(COUNT) - 1;
        return state.getValue(RACK) ? RACK_SHAPES[slices] : SHAPES[slices];
    }

    @Override
    public void setValid(Level level, BlockPos pos, boolean valid, int tier, ClimateType climate)
    {
        BlockState state = level.getBlockState(pos);
        if (state.getBlock() instanceof CheeseWheelBlock)
        {
            final boolean isAgingNow = state.getValue(AGING);
            final boolean shouldAge = valid && climate == ClimateType.CELLAR;
            if (isAgingNow != shouldAge)
            {
                level.setBlockAndUpdate(pos, state.setValue(AGING, shouldAge));
            }
        }
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving)
    {
        if (!(Helpers.isBlock(state, newState.getBlock())) && state.getValue(RACK) && !(newState.getBlock() instanceof BarrelRackBlock))
        {
            Helpers.spawnItem(level, pos, new ItemStack(TFCBlocks.BARREL_RACK.get()));
        }
        super.onRemove(state, level, pos, newState, isMoving);
    }

    @Override
    public boolean onDestroyedByPlayer(BlockState state, Level level, BlockPos pos, Player player, boolean willHarvest, FluidState fluid)
    {
        if (state.getValue(RACK))
        {
            // Replace with a barrel rack, and drop + destroy the barrel
            playerWillDestroy(level, pos, state, player);
            return level.setBlock(pos, TFCBlocks.BARREL_RACK.get().defaultBlockState(), level.isClientSide ? Block.UPDATE_ALL_IMMEDIATE : Block.UPDATE_ALL);
        }
        else
        {
            return super.onDestroyedByPlayer(state, level, pos, player, willHarvest, fluid);
        }
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        super.createBlockStateDefinition(builder.add(COUNT, AGE, AGING, RACK));
    }
}
