package com.eerussianguy.firmalife.common.blocks;

import com.eerussianguy.firmalife.common.FLHelpers;
import com.eerussianguy.firmalife.common.blockentities.FLBlockEntities;
import com.eerussianguy.firmalife.common.blockentities.JarbnetBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
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
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.blocks.ExtendedProperties;
import net.dries007.tfc.common.items.CandleBlockItem;
import net.dries007.tfc.common.items.JugItem;
import net.dries007.tfc.config.TFCConfig;
import net.dries007.tfc.util.Helpers;

public class JarbnetBlock extends FourWayDeviceBlock
{
    public static boolean isItemAllowed(ItemStack stack)
    {
        final Item item = stack.getItem();
        return Helpers.isItem(item, TFCTags.Items.JARS) || item instanceof JugItem || item instanceof CandleBlockItem;
    }

    public static final BooleanProperty OPEN = BlockStateProperties.OPEN;
    public static final BooleanProperty LIT = BlockStateProperties.LIT;

    public static final VoxelShape NORTH_SHAPE = box(0, 0, 4, 16, 16, 16);
    public static final VoxelShape WEST_SHAPE = Helpers.rotateShape(Direction.WEST, 0, 0, 4, 16, 16, 16);
    public static final VoxelShape EAST_SHAPE = Helpers.rotateShape(Direction.EAST, 0, 0, 4, 16, 16, 16);
    public static final VoxelShape SOUTH_SHAPE = Helpers.rotateShape(Direction.SOUTH, 0, 0, 4, 16, 16, 16);

    private static void addParticlesAndSound(Level level, double x, double y, double z, RandomSource rand)
    {
        final float value = rand.nextFloat();
        if (value < 0.3F)
        {
            level.addParticle(ParticleTypes.SMOKE, x, y, z, 0.0D, 0.0D, 0.0D);
            if (value < 0.17F)
            {
                level.playLocalSound(x + 0.5D, y + 0.5D, z + 0.5D, SoundEvents.CANDLE_AMBIENT, SoundSource.BLOCKS, 1.0F + rand.nextFloat(), rand.nextFloat() * 0.7F + 0.3F, false);
            }
        }
        level.addParticle(ParticleTypes.SMALL_FLAME, x, y, z, 0.0D, 0.0D, 0.0D);
    }

    public JarbnetBlock(ExtendedProperties properties)
    {
        super(properties, InventoryRemoveBehavior.DROP);
        registerDefaultState(getStateDefinition().any().setValue(OPEN, true).setValue(FACING, Direction.NORTH).setValue(LIT, false));
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random)
    {
        if (state.getValue(LIT))
        {
            final Direction dir = state.getValue(FACING);
            final int dx = Mth.abs(dir.getStepX());
            final int dz = Mth.abs(dir.getStepZ());
            final double x = pos.getX() + (0.5 * random.nextFloat()) + (0.5 * dx);
            final double y = pos.getY() + (random.nextFloat() * 0.15f + (random.nextBoolean() ? 0.33f : 0.66f));
            final double z = pos.getZ() + (0.5 * random.nextFloat()) + (0.5 * dz);
            addParticlesAndSound(level, x, y, z, random);
        }
    }

    @Override
    @SuppressWarnings("deprecation")
    public BlockState updateShape(BlockState state, Direction facing, BlockState facingState, LevelAccessor level, BlockPos pos, BlockPos facingPos)
    {
        if (state.getValue(LIT) && !state.getValue(OPEN))
        {
            return state.setValue(LIT, false);
        }
        return state;
    }

    @Override
    @SuppressWarnings("deprecation")
    public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource rand)
    {
        if (level.getBlockEntity(pos) instanceof JarbnetBlockEntity jarbnet && state.getValue(LIT))
        {
            final int ct = TFCConfig.SERVER.candleTicks.get();
            if (jarbnet.getTicksSinceUpdate() > ct && ct != -1)
            {
                level.setBlockAndUpdate(pos, state.setValue(LIT, false));
            }
        }
    }

    @Override
    @SuppressWarnings("deprecation")
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult result)
    {
        final ItemStack held = player.getItemInHand(hand);
        if (held.isEmpty())
        {
            if (player.isShiftKeyDown())
            {
                final boolean open = state.getValue(OPEN);
                BlockState newState = state.setValue(OPEN, !open);
                if (open)
                {
                    if (state.getValue(LIT))
                    {
                        Helpers.playSound(level, pos, SoundEvents.FIRE_EXTINGUISH);
                        newState = newState.setValue(LIT, false);
                    }
                    Helpers.playSound(level, pos, SoundEvents.WOODEN_TRAPDOOR_CLOSE);
                }
                else
                {
                    Helpers.playSound(level, pos, SoundEvents.WOODEN_TRAPDOOR_OPEN);
                }
                level.setBlockAndUpdate(pos, newState);
                return InteractionResult.sidedSuccess(level.isClientSide);
            }
            else
            {
                return FLHelpers.consumeInventory(level, pos, FLBlockEntities.JARBNET, (jar, inv) -> FLHelpers.takeOneAny(level, 0, JarbnetBlockEntity.SLOTS - 1, inv, player));
            }
        }
        else if (isItemAllowed(held))
        {
            return FLHelpers.consumeInventory(level, pos, FLBlockEntities.JARBNET, (jar, inv) -> FLHelpers.insertOneAny(level, held, 0, JarbnetBlockEntity.SLOTS - 1, inv, player));
        }
        return InteractionResult.PASS;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        super.createBlockStateDefinition(builder.add(OPEN, LIT));
    }

    @Override
    @SuppressWarnings("deprecation")
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext pContext)
    {
        return switch (state.getValue(FACING))
            {
                case NORTH -> NORTH_SHAPE;
                case SOUTH -> SOUTH_SHAPE;
                case WEST -> WEST_SHAPE;
                default -> EAST_SHAPE;
            };
    }
}
