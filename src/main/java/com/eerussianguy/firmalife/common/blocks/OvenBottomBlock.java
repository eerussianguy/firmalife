package com.eerussianguy.firmalife.common.blocks;

import java.util.function.Supplier;
import com.eerussianguy.firmalife.common.FLHelpers;
import com.eerussianguy.firmalife.common.blockentities.FLBlockEntities;
import com.eerussianguy.firmalife.common.blockentities.OvenBottomBlockEntity;
import com.eerussianguy.firmalife.common.items.FLItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
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
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.items.ItemHandlerHelper;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.blocks.ExtendedProperties;
import net.dries007.tfc.common.blocks.devices.IBellowsConsumer;
import net.dries007.tfc.util.Helpers;

public class OvenBottomBlock extends AbstractOvenBlock implements IBellowsConsumer
{
    public static final BooleanProperty LIT = BlockStateProperties.LIT;
    public static final IntegerProperty LOGS = FLStateProperties.LOGS;

    public static final VoxelShape[] SHAPES = Helpers.computeHorizontalShapes(d -> Shapes.join(
        Shapes.block(),
        Helpers.rotateShape(d, 4, 0, 0, 12, 11, 15),
        BooleanOp.ONLY_FIRST
    ));

    @Nullable
    private final Supplier<? extends Block> insulated;

    public OvenBottomBlock(ExtendedProperties properties, @Nullable Supplier<? extends Block> curedBlock)
    {
        this(properties, curedBlock, null);
    }

    public OvenBottomBlock(ExtendedProperties properties, @Nullable Supplier<? extends Block> curedBlock, @Nullable Supplier<? extends Block> insulated)
    {
        super(properties, curedBlock);
        this.insulated = insulated;
        registerDefaultState(getStateDefinition().any().setValue(LOGS, 0).setValue(FACING, Direction.NORTH).setValue(LIT, false).setValue(HAS_CHIMNEY, false));
    }

    @Override
    public ItemInteractionResult useItemOn(ItemStack held, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult result)
    {
        final ItemStack item = player.getItemInHand(hand);
        if (!item.isEmpty() && Helpers.isItem(item, TFCTags.Items.FIREPIT_FUEL))
        {
            final var res1 = FLHelpers.consumeItemInventory(level, pos, FLBlockEntities.OVEN_BOTTOM, (oven, inv) -> {
                if (inv.getStackInSlot(OvenBottomBlockEntity.SLOT_FUEL_MAX).isEmpty())
                {
                    final var res = FLHelpers.insertOne(level, item, OvenBottomBlockEntity.SLOT_FUEL_MAX, inv, player);
                    return res == ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION ? ItemInteractionResult.SUCCESS : res;
                }
                return ItemInteractionResult.sidedSuccess(level.isClientSide);
            });
            return res1 == ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION ? ItemInteractionResult.SUCCESS : res1;
        }
        else if (item.isEmpty() && !state.getValue(LIT))
        {
            return FLHelpers.consumeItemInventory(level, pos, FLBlockEntities.OVEN_BOTTOM, (oven, inv) -> {
                for (int i = OvenBottomBlockEntity.SLOT_FUEL_MIN; i <= OvenBottomBlockEntity.SLOT_FUEL_MAX; i++)
                {
                    ItemHandlerHelper.giveItemToPlayer(player, inv.extractItem(i, 64, false));
                }
                return ItemInteractionResult.sidedSuccess(level.isClientSide);
            });
        }
        else if (Helpers.isItem(item, FLItems.OVEN_INSULATION.get()) && insulated != null)
        {
            item.shrink(1);
            level.setBlockAndUpdate(pos, Helpers.copyProperties(insulated.get().defaultBlockState(), state));
            Helpers.playSound(level, pos, SoundEvents.METAL_PLACE);
            return ItemInteractionResult.sidedSuccess(level.isClientSide);
        }
        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context)
    {
        return SHAPES[state.getValue(FACING).get2DDataValue()];
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random)
    {
        if (state.getValue(LIT))
        {
            if (random.nextInt(10) == 0)
            {
                level.playLocalSound(pos, SoundEvents.CAMPFIRE_CRACKLE, SoundSource.BLOCKS, 0.5F + random.nextFloat(), random.nextFloat() * 0.7F + 0.6F, false);
            }
            super.animateTick(state, level, pos, random);
        }
    }

    @Override
    public void cure(Level level, BlockState state, BlockPos pos)
    {
        if (getCured() != null)
        {
            OvenBottomBlockEntity.cure(level, state, getCured().defaultBlockState(), pos);
        }
    }

    @Override
    @SuppressWarnings("deprecation")
    public BlockState updateShape(BlockState state, Direction facing, BlockState facingState, LevelAccessor level, BlockPos currentPos, BlockPos facingPos)
    {
        return state.getValue(LIT) && !isInsulated(level, currentPos, state) ? state.setValue(LIT, false) : state;
    }

    @Override
    @SuppressWarnings("deprecation")
    public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource rand)
    {
        if (state.getValue(LIT) && !isInsulated(level, pos, state))
        {
            level.setBlockAndUpdate(pos, state.setValue(LIT, false));
        }
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        super.createBlockStateDefinition(builder.add(LIT, LOGS));
    }

    @Override
    public void intakeAir(Level level, BlockPos blockPos, BlockState state, int amount)
    {
        level.getBlockEntity(blockPos, FLBlockEntities.OVEN_BOTTOM.get()).ifPresent(oven -> oven.onAirIntake(amount));
    }
}
