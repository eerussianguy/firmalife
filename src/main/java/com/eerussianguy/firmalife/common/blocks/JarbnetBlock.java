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
import net.minecraft.world.ItemInteractionResult;
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
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
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

    public static final VoxelShape[] CLOSED_SHAPES = Helpers.computeHorizontalShapes(dir -> Helpers.rotateShape(dir, 0, 0, 4, 16, 16, 16));
    public static final VoxelShape[] OPEN_SHAPES = Helpers.computeHorizontalShapes(dir -> Shapes.or(
        Shapes.join(
            CLOSED_SHAPES[dir.get2DDataValue()],
            Helpers.rotateShape(dir, 1, 1, 4, 15, 15, 15),
            BooleanOp.ONLY_FIRST
        ),
        // Shelf
        Helpers.rotateShape(dir, 1, 7, 5, 15, 8, 15)
    ));
    // Only used to detect when a slot on the shelf is clicked
    // Helpful for preventing taking items out of the back/sides of the block
    public static final VoxelShape[][] INVENTORY_SLOT_SHAPES = new VoxelShape[][] {
        Helpers.computeHorizontalShapes(dir -> Helpers.rotateShape(dir, 1, 8, 5, 6, 15, 15)),
        Helpers.computeHorizontalShapes(dir -> Helpers.rotateShape(dir, 6, 8, 5, 10, 15, 15)),
        Helpers.computeHorizontalShapes(dir -> Helpers.rotateShape(dir, 10, 8, 5, 15, 15, 15)),
        Helpers.computeHorizontalShapes(dir -> Helpers.rotateShape(dir, 1, 1, 5, 6, 7, 15)),
        Helpers.computeHorizontalShapes(dir -> Helpers.rotateShape(dir, 6, 1, 5, 10, 7, 15)),
        Helpers.computeHorizontalShapes(dir -> Helpers.rotateShape(dir, 10, 1, 5, 15, 7, 15)),
    };

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
            final Vec3 center = getShape(state, level, pos, CollisionContext.empty()).bounds().getCenter();
            final double x = pos.getX() + (0.5 * random.nextFloat()) + (0.5 * center.x);
            final double y = pos.getY() + (random.nextFloat() * 0.15f + (random.nextBoolean() ? 0.33f : 0.66f));
            final double z = pos.getZ() + (0.5 * random.nextFloat()) + (0.5 * center.z);
            addParticlesAndSound(level, x, y, z, random);
        }
    }

    @Override
    public BlockState updateShape(BlockState state, Direction facing, BlockState facingState, LevelAccessor level, BlockPos pos, BlockPos facingPos)
    {
        if (state.getValue(LIT) && !state.getValue(OPEN))
        {
            return state.setValue(LIT, false);
        }
        return state;
    }

    @Override
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
    public ItemInteractionResult useItemOn(ItemStack held, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult result)
    {
        //TODO allow swapping item in inventory with item in hand
        final Direction facing = state.getValue(FACING);
        final boolean open = state.getValue(OPEN);
        int slot = getSlotFromPos(facing, result.getLocation().subtract(pos.getX(), pos.getY(), pos.getZ()));
        if (held.isEmpty())
        {
            if (player.isShiftKeyDown())
            {
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
                return ItemInteractionResult.sidedSuccess(level.isClientSide);
            }
            else if (slot >= 0 && open)
            {
                return FLHelpers.consumeItemInventory(level, pos, FLBlockEntities.JARBNET, (jar, inv) -> FLHelpers.takeOne(level, slot, inv, player));
            }
        }
        else if (isItemAllowed(held) && slot >= 0 && open)
        {
            return FLHelpers.consumeItemInventory(level, pos, FLBlockEntities.JARBNET, (jar, inv) -> FLHelpers.insertOne(level, held, slot, inv, player));
        }
        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        super.createBlockStateDefinition(builder.add(OPEN, LIT));
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext pContext)
    {
        int facing = state.getValue(FACING).get2DDataValue();
        if (state.getValue(OPEN))
        {
            return OPEN_SHAPES[facing];
        }
        return CLOSED_SHAPES[facing];
    }

    private int getSlotFromPos(Direction facing, Vec3 pos)
    {
        int index = 0;
        for (VoxelShape[] directionalSlotShape : INVENTORY_SLOT_SHAPES)
        {
            //AABB#contains creates inconsistent behavior i.r.t. clicking on the inner left of the shelf vs the inner right of the shelf
            AABB shape = directionalSlotShape[facing.get2DDataValue()].bounds();
            if (pos.x >= shape.minX && pos.x <= shape.maxX && pos.y >= shape.minY && pos.y <= shape.maxY && pos.z >= shape.minZ && pos.z <= shape.maxZ)
            {
                return index;
            }
            index++;
        }
        return -1;
    }
}
