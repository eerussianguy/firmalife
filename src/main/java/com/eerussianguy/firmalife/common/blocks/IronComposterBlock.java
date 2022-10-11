package com.eerussianguy.firmalife.common.blocks;

import java.util.Random;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

import com.eerussianguy.firmalife.common.FLHelpers;
import com.eerussianguy.firmalife.common.blockentities.FLBlockEntities;
import net.dries007.tfc.common.blockentities.ComposterBlockEntity;
import net.dries007.tfc.common.blocks.ExtendedProperties;
import net.dries007.tfc.common.blocks.devices.TFCComposterBlock;
import org.jetbrains.annotations.Nullable;

public class IronComposterBlock extends TFCComposterBlock
{
    public IronComposterBlock(ExtendedProperties properties)
    {
        super(properties);
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult result)
    {
        return level.getBlockEntity(pos, FLBlockEntities.IRON_COMPOSTER.get()).map(composter -> composter.use(player.getItemInHand(hand), player, level.isClientSide)).orElse(InteractionResult.PASS);
    }

    @Override
    public void randomTick(BlockState state, ServerLevel level, BlockPos pos, Random random)
    {
        level.getBlockEntity(pos, FLBlockEntities.IRON_COMPOSTER.get()).ifPresent(ComposterBlockEntity::randomTick);
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack)
    {
        FLHelpers.resetCounter(level, pos);
        super.setPlacedBy(level, pos, state, placer, stack);
    }

}
