package com.eerussianguy.firmalife.common.blocks;

import java.util.Random;
import java.util.function.Supplier;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

import com.eerussianguy.firmalife.common.FLHelpers;
import com.eerussianguy.firmalife.common.FLTags;
import com.eerussianguy.firmalife.common.blockentities.FLBlockEntities;
import com.eerussianguy.firmalife.common.blockentities.OvenTopBlockEntity;
import com.eerussianguy.firmalife.common.misc.FLDamageSources;
import com.eerussianguy.firmalife.config.FLConfig;
import net.dries007.tfc.common.blocks.ExtendedProperties;
import net.dries007.tfc.util.Helpers;
import org.jetbrains.annotations.Nullable;

public class OvenTopBlock extends AbstractOvenBlock
{
    public OvenTopBlock(ExtendedProperties properties, @Nullable Supplier<? extends Block> curedBlock)
    {
        super(properties, curedBlock);
        registerDefaultState(getStateDefinition().any().setValue(FACING, Direction.NORTH).setValue(HAS_CHIMNEY, false));
    }

    @Override
    @SuppressWarnings("deprecation")
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult result)
    {
        final ItemStack item = player.getItemInHand(hand);
        return FLHelpers.consumeInventory(level, pos, FLBlockEntities.OVEN_TOP, (oven, inv) -> {
            final boolean peel = Helpers.isItem(item, FLTags.Items.USABLE_ON_OVEN);
            if (peel || (item.isEmpty() && player.isShiftKeyDown()))
            {
                if (!peel && oven.getTemperature() > 100f && FLConfig.SERVER.ovenRequirePeel.get())
                {
                    player.hurt(FLDamageSources.OVEN, 0.5f);
                }
                return FLHelpers.takeOneAny(level, OvenTopBlockEntity.SLOT_INPUT_START, OvenTopBlockEntity.SLOT_INPUT_END, inv, player);
            }
            else if (!item.isEmpty())
            {
                return FLHelpers.insertOneAny(level, item, OvenTopBlockEntity.SLOT_INPUT_START, OvenTopBlockEntity.SLOT_INPUT_END, inv, player);
            }
            return InteractionResult.PASS;
        });
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, Random random)
    {
        if (level.getBlockEntity(pos, FLBlockEntities.OVEN_TOP.get()).map(oven -> oven.getTemperature() > 0f).orElse(false))
        {
            super.animateTick(state, level, pos, random);
        }
    }

    @Override
    @SuppressWarnings("deprecation")
    public BlockState updateShape(BlockState state, Direction facing, BlockState facingState, LevelAccessor level, BlockPos currentPos, BlockPos facingPos)
    {
        extinguish(level, currentPos, state);
        return super.updateShape(state, facing, facingState, level, currentPos, facingPos);
    }

    @Override
    @SuppressWarnings("deprecation")
    public void randomTick(BlockState state, ServerLevel level, BlockPos pos, Random rand)
    {
        extinguish(level, pos, state);
    }

    @Override
    public void cure(Level level, BlockState state, BlockPos pos)
    {
        if (getCured() != null)
        {
            OvenTopBlockEntity.cure(level, state, getCured().defaultBlockState(), pos);
        }
    }

    private void extinguish(LevelAccessor level, BlockPos pos, BlockState state)
    {
        if (!insulated(level, pos, state))
        {
            level.getBlockEntity(pos, FLBlockEntities.OVEN_TOP.get()).ifPresent(OvenTopBlockEntity::extinguish);
        }
    }
}
