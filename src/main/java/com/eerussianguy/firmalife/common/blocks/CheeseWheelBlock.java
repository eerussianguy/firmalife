package com.eerussianguy.firmalife.common.blocks;

import java.util.Random;
import java.util.function.Supplier;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
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
import net.dries007.tfc.common.blockentities.TFCBlockEntities;
import net.dries007.tfc.common.blocks.ExtendedProperties;
import net.dries007.tfc.common.blocks.TFCBlockStateProperties;
import net.dries007.tfc.common.capabilities.food.FoodCapability;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.calendar.ICalendar;
import org.jetbrains.annotations.Nullable;

public class CheeseWheelBlock extends BottomSupportedDeviceBlock implements ClimateReceiver
{
    public static final IntegerProperty COUNT = TFCBlockStateProperties.COUNT_1_4;
    public static final EnumProperty<FoodAge> AGE = FLStateProperties.AGE;
    public static final BooleanProperty AGING = FLStateProperties.AGING;

    private static final VoxelShape SHAPE_1 = box(8, 0, 8, 15, 8, 15);
    private static final VoxelShape SHAPE_2 = box(8, 0, 1, 15, 8, 15);
    private static final VoxelShape SHAPE_3 = Shapes.or(box(1, 0, 1, 8, 8, 8), box(8, 0, 1, 15, 8, 15));
    private static final VoxelShape SHAPE_4 = box(1, 0, 1, 15, 8, 15);

    private static final VoxelShape[] SHAPES = new VoxelShape[] {SHAPE_1, SHAPE_2, SHAPE_3, SHAPE_4};

    public static VoxelShape getShape(int slices)
    {
        return SHAPES[slices - 1];
    }

    private final Supplier<? extends Item> slice;

    public CheeseWheelBlock(ExtendedProperties properties, Supplier<? extends Item> slice)
    {
        super(properties, InventoryRemoveBehavior.DROP, SHAPE_4);
        this.slice = slice;

        registerDefaultState(getStateDefinition().any().setValue(COUNT, 4).setValue(AGE, FoodAge.FRESH).setValue(AGING, false));
    }

    @Override
    @SuppressWarnings("deprecation")
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit)
    {
        ItemStack held = player.getItemInHand(hand);
        if (Helpers.isItem(held, TFCTags.Items.KNIVES) && !player.isShiftKeyDown())
        {
            final int count = state.getValue(COUNT);
            ItemStack drop = new ItemStack(slice.get());
            FoodCapability.applyTrait(drop, state.getValue(AGE).getTrait());
            ItemHandlerHelper.giveItemToPlayer(player, drop);
            FLHelpers.resetCounter(level, pos);
            if (count - 1 == 0)
            {
                level.destroyBlock(pos, false);
            }
            else
            {
                level.setBlockAndUpdate(pos, state.setValue(COUNT, count - 1));
            }
        }
        return InteractionResult.PASS;
    }

    @Override
    public boolean isRandomlyTicking(BlockState state)
    {
        return state.getValue(COUNT) == 4;
    }

    @Override
    @SuppressWarnings("deprecation")
    public void randomTick(BlockState state, ServerLevel level, BlockPos pos, Random rand)
    {
        level.getBlockEntity(pos, TFCBlockEntities.TICK_COUNTER.get()).ifPresent(counter -> {
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
        return getShape(state.getValue(COUNT));
    }

    @Override
    public void addWater(float amount) { }

    @Override
    public void setValid(Level level, BlockPos pos, boolean valid, int tier, boolean cellar)
    {
        BlockState state = level.getBlockState(pos);
        if (state.getBlock() instanceof CheeseWheelBlock)
        {
            final boolean isAgingNow = state.getValue(AGING);
            final boolean shouldAge = valid && cellar;
            if (isAgingNow != shouldAge)
            {
                level.setBlockAndUpdate(pos, state.setValue(AGING, shouldAge));
            }
        }
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        super.createBlockStateDefinition(builder.add(COUNT, AGE, AGING));
    }
}
