package com.eerussianguy.firmalife.common.blocks.plant;

import java.util.Arrays;
import java.util.function.Consumer;
import java.util.function.Supplier;
import com.eerussianguy.firmalife.common.blockentities.GrapePlantBlockEntity;
import com.eerussianguy.firmalife.common.blocks.FLBlocks;
import com.eerussianguy.firmalife.common.util.FLClimateRanges;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.blocks.ExtendedProperties;
import net.dries007.tfc.common.blocks.TFCBlockStateProperties;
import net.dries007.tfc.common.blocks.plant.fruit.Lifecycle;
import net.dries007.tfc.common.blocks.soil.FarmlandBlock;
import net.dries007.tfc.common.blocks.soil.HoeOverlayBlock;
import net.dries007.tfc.config.TFCConfig;
import net.dries007.tfc.util.Helpers;

public class GrapeGroundPlantOnStringBlock extends GrapeStringBlock implements IGrape, HoeOverlayBlock
{
    public static final IntegerProperty STAGE = TFCBlockStateProperties.STAGE_2;
    public static final EnumProperty<Lifecycle> LIFECYCLE = TFCBlockStateProperties.LIFECYCLE;

    public static final VoxelShape SHAPE_X_PLANT = Shapes.or(SHAPE_X, box(6, 0, 6, 10, 16, 10));
    public static final VoxelShape SHAPE_Z_PLANT = Shapes.or(SHAPE_Z, box(6, 0, 6, 10, 16, 10));

    private final Supplier<? extends Block> topBlock;

    public GrapeGroundPlantOnStringBlock(ExtendedProperties properties, Supplier<? extends Block> topBlock)
    {
        super(properties);
        this.topBlock = topBlock;
        registerDefaultState(getStateDefinition().any().setValue(STAGE, 0).setValue(LIFECYCLE, Lifecycle.HEALTHY));
    }

    @Override
    public ItemInteractionResult useItemOn(ItemStack held, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult result)
    {
        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    @Override
    public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource rand)
    {
        if (level.getBlockEntity(pos) instanceof GrapePlantBlockEntity grape)
        {
            grape.tryUpdate();
        }
    }

    @Override
    public BlockState updateShape(BlockState state, Direction facing, BlockState facingState, LevelAccessor level, BlockPos pos, BlockPos facingPos)
    {
        final BlockState superState = super.updateShape(state, facing, facingState, level, pos, facingPos);
        if (superState.isAir())
            return superState;
        if (facing == Direction.DOWN && !Helpers.isBlock(facingState, TFCTags.Blocks.BUSH_PLANTABLE_ON))
        {
            return Helpers.copyProperty(FLBlocks.GRAPE_STRING.get().defaultBlockState(), state, AXIS);
        }
        return state;
    }

    @Override
    public void advance(Level level, BlockPos pos, BlockState state)
    {
        if (state.getValue(STAGE) == 2)
        {
            final BlockPos abovePos = pos.above();
            final BlockState above = level.getBlockState(abovePos);
            if (above.getBlock() instanceof GrapeStringBlock && !(above.getBlock() instanceof GrapeStringWithPlantBlock) && above.getValue(AXIS) == state.getValue(AXIS))
            {
                level.setBlockAndUpdate(abovePos, Helpers.copyProperty(topBlock.get().defaultBlockState(), above, AXIS));
            }
        }
        else
        {
            level.setBlockAndUpdate(pos, state.cycle(STAGE));
        }
    }

    @Override
    public void addHoeOverlayInfo(Level level, BlockPos pos, BlockState state, Consumer<Component> tooltip, boolean debug)
    {
        tooltip.accept(FarmlandBlock.getInstantTemperatureTooltip(level, pos, FLClimateRanges.GRAPES.get(), false));
        if (level.getBlockEntity(pos) instanceof GrapePlantBlockEntity grape && TFCConfig.CLIENT.enableDebug.get())
        {
            tooltip.accept(Component.literal("[Debug] Growth: " + grape.getGrowth()));
            tooltip.accept(Component.literal("[Debug] Brain Pos: " + grape.getBrainPos().toShortString()));
            tooltip.accept(Component.literal("[Debug] Soil data: " + Arrays.toString(grape.debugViewOfSoilData())));
            tooltip.accept(Component.literal("[Debug] Bees: " + grape.debugHasBees()));
        }
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context)
    {
        return state.getValue(AXIS) == Direction.Axis.X ? SHAPE_X_PLANT : SHAPE_Z_PLANT;
    }

    @Override
    @SuppressWarnings("deprecation")
    public VoxelShape getBlockSupportShape(BlockState state, BlockGetter level, BlockPos pos)
    {
        return Shapes.empty();
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        super.createBlockStateDefinition(builder.add(STAGE, LIFECYCLE));
    }
}
