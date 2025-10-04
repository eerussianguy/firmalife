package com.eerussianguy.firmalife.common.blocks;

import java.util.function.Supplier;
import com.eerussianguy.firmalife.common.FLHelpers;
import com.eerussianguy.firmalife.common.FLTags;
import com.eerussianguy.firmalife.common.blockentities.FLBlockEntities;
import com.eerussianguy.firmalife.common.blockentities.OvenTopBlockEntity;
import com.eerussianguy.firmalife.common.items.FinishItem;
import com.eerussianguy.firmalife.common.misc.FLDamageSources;
import com.eerussianguy.firmalife.config.FLConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.common.blocks.ExtendedProperties;
import net.dries007.tfc.util.Helpers;

public class OvenTopBlock extends AbstractOvenBlock
{
    public static final VoxelShape[] SHAPES = Helpers.computeHorizontalShapes(d -> Shapes.join(
        Shapes.block(),
        Helpers.rotateShape(d, 2, 0, 0, 14, 11, 15),
        BooleanOp.ONLY_FIRST
    ));

    public OvenTopBlock(ExtendedProperties properties, @Nullable Supplier<? extends Block> curedBlock)
    {
        super(properties, curedBlock);
        registerDefaultState(getStateDefinition().any().setValue(FACING, Direction.NORTH).setValue(HAS_CHIMNEY, false));
    }

    @Override
    @SuppressWarnings("deprecation")
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context)
    {
        return SHAPES[state.getValue(FACING).get2DDataValue()];
    }

    @Override
    public ItemInteractionResult useItemOn(ItemStack item, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult result)
    {
        if (item.getItem() instanceof FinishItem)
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        return FLHelpers.consumeItemInventory(level, pos, FLBlockEntities.OVEN_TOP, (oven, inv) -> {
            final boolean peel = Helpers.isItem(item, FLTags.Items.USABLE_ON_OVEN);
            if (peel || (item.isEmpty() && player.isShiftKeyDown()))
            {
                if (!peel && oven.getTemperature() > 100f && FLConfig.SERVER.ovenRequirePeel.get() && !player.isCreative())
                {
                    FLDamageSources.oven(player, 0.5f);
                }
                return FLHelpers.takeOneAny(level, OvenTopBlockEntity.SLOT_INPUT_START, OvenTopBlockEntity.SLOT_INPUT_END, inv, player);
            }
            else if (!item.isEmpty())
            {
                return FLHelpers.insertOneAny(level, item, OvenTopBlockEntity.SLOT_INPUT_START, OvenTopBlockEntity.SLOT_INPUT_END, inv, player);
            }
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        });
    }

    @Override
    public int getLightBlock(BlockState state, BlockGetter level, BlockPos pos)
    {
        return level.getBlockState(pos.below()).getBlock() instanceof OvenBottomBlock ? 0 : super.getLightBlock(state, level, pos);
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random)
    {
        if (level.getBlockEntity(pos, FLBlockEntities.OVEN_TOP.get()).map(oven -> oven.getTemperature() > 0f).orElse(false))
        {
            super.animateTick(state, level, pos, random);
        }
    }

    @Override
    public void cure(Level level, BlockState state, BlockPos pos)
    {
        if (getCured() != null)
        {
            OvenTopBlockEntity.cure(level, state, getCured().defaultBlockState(), pos);
        }
    }
}
