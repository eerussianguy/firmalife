package com.eerussianguy.firmalife.common.blocks.plant;

import java.util.List;
import java.util.function.Supplier;
import com.eerussianguy.firmalife.common.blockentities.GrapePlantBlockEntity;
import com.eerussianguy.firmalife.common.blocks.FLBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.items.ItemHandlerHelper;
import net.neoforged.neoforge.registries.DeferredHolder;

import net.dries007.tfc.common.blocks.ExtendedProperties;
import net.dries007.tfc.common.blocks.TFCBlockStateProperties;
import net.dries007.tfc.common.blocks.plant.fruit.Lifecycle;
import net.dries007.tfc.common.component.food.FoodCapability;
import net.dries007.tfc.common.component.food.FoodTrait;
import net.dries007.tfc.util.Helpers;

public class GrapeStringWithPlantBlock extends GrapeStringBlock implements IGrape
{
    public static final EnumProperty<Lifecycle> LIFECYCLE = TFCBlockStateProperties.LIFECYCLE;
    public static final VoxelShape SHAPE = box(0, 4, 0, 16, 16, 16);

    private final Supplier<? extends Block> postPlantBlock;
    private final Supplier<? extends Block> fluffBlock;
    private final Supplier<? extends Item> grapeItem;

    public GrapeStringWithPlantBlock(ExtendedProperties properties, Supplier<? extends Block> postPlantBlock, Supplier<? extends Block> fluffBlock, Supplier<? extends Item> grape)
    {
        super(properties);
        this.postPlantBlock = postPlantBlock;
        this.fluffBlock = fluffBlock;
        this.grapeItem = grape;
        registerDefaultState(getStateDefinition().any().setValue(LIFECYCLE, Lifecycle.HEALTHY));
    }

    @Override
    public BlockState updateShape(BlockState state, Direction facing, BlockState facingState, LevelAccessor level, BlockPos pos, BlockPos facingPos)
    {
        final BlockState superState = super.updateShape(state, facing, facingState, level, pos, facingPos);
        if (superState.isAir())
            return superState;
        if (facing == Direction.DOWN && !(facingState.getBlock() instanceof GrapeGroundPlantOnStringBlock))
        {
            return Helpers.copyProperty(FLBlocks.GRAPE_STRING.get().defaultBlockState(), state, AXIS);
        }
        return state;
    }

    @Override
    public InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult result)
    {
        if (state.getValue(LIFECYCLE) == Lifecycle.FRUITING)
        {
            level.playSound(player, pos, SoundEvents.SWEET_BERRY_BUSH_PICK_BERRIES, SoundSource.PLAYERS, 1.0F, level.getRandom().nextFloat() + 0.7F + 0.3F);
            final ItemStack stack = grapeItem.get().getDefaultInstance();
            if (level.getBlockEntity(pos.below()) instanceof GrapePlantBlockEntity grape)
            {
                final List<DeferredHolder<FoodTrait, FoodTrait>> traits = grape.scanAndReport();
                for (DeferredHolder<FoodTrait, FoodTrait> trait : traits)
                {
                    FoodCapability.applyTrait(stack, trait);
                }
            }
            FoodCapability.setCreationDate(stack, FoodCapability.getRoundedCreationDate());
            ItemHandlerHelper.giveItemToPlayer(player, stack);
            level.setBlockAndUpdate(pos, state.setValue(LIFECYCLE, Lifecycle.HEALTHY));
            return InteractionResult.sidedSuccess(level.isClientSide);
        }
        return InteractionResult.PASS;
    }

    @Override
    public void advance(Level level, BlockPos pos, BlockState state)
    {
        final Direction dir = state.getValue(AXIS) == Direction.Axis.X ? Direction.EAST : Direction.SOUTH;
        convertToPlant(level, pos.relative(dir));
        convertToPlant(level, pos.relative(dir.getOpposite()));

        final BlockPos above = pos.above();
        if (level.getBlockState(above).isAir())
        {
            level.setBlockAndUpdate(above, fluffBlock.get().defaultBlockState());
        }
    }

    @Override
    public void advanceLifecycle(Level level, BlockPos pos, BlockState state, Lifecycle lifecycle)
    {
        updateLifecycle(level, pos, state, lifecycle);
        final Direction dir = state.getValue(AXIS) == Direction.Axis.X ? Direction.EAST : Direction.SOUTH;
        updateLifecycle(level, pos.relative(dir), level.getBlockState(pos.relative(dir)), lifecycle);
        updateLifecycle(level, pos.relative(dir.getOpposite()), level.getBlockState(pos.relative(dir.getOpposite())), lifecycle);
    }

    private void convertToPlant(Level level, BlockPos pos)
    {
        final BlockState state = level.getBlockState(pos);
        if (state.getBlock() instanceof GrapeTrellisPostBlock && !(state.getBlock() instanceof GrapeTrellisPostWithPlantBlock))
        {
            level.setBlockAndUpdate(pos, Helpers.copyProperties(postPlantBlock.get().defaultBlockState(), state));
        }
        final BlockPos above = pos.above();
        if (level.getBlockState(above).isAir())
        {
            level.setBlockAndUpdate(above, fluffBlock.get().defaultBlockState());
        }
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context)
    {
        return state.getValue(AXIS) == Direction.Axis.X ? SHAPE_X : SHAPE_Z;
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context)
    {
        return SHAPE;
    }

    @Override
    public VoxelShape getBlockSupportShape(BlockState state, BlockGetter level, BlockPos pos)
    {
        return Shapes.empty();
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        super.createBlockStateDefinition(builder.add(LIFECYCLE));
    }
}
