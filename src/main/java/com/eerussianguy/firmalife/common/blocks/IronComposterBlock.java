package com.eerussianguy.firmalife.common.blocks;

import java.util.Random;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

import com.eerussianguy.firmalife.common.FLHelpers;
import com.eerussianguy.firmalife.common.blockentities.FLBlockEntities;
import net.dries007.tfc.common.blockentities.ComposterBlockEntity;
import net.dries007.tfc.common.blocks.ExtendedProperties;
import net.dries007.tfc.common.blocks.devices.TFCComposterBlock;
import net.dries007.tfc.common.capabilities.Capabilities;
import net.dries007.tfc.common.items.TFCItems;
import net.dries007.tfc.util.Helpers;

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
    public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random)
    {
        if (level.getBlockEntity(pos) instanceof ComposterBlockEntity composter)
        {
            composter.randomTick();
            if (composter.isReady())
            {
                final BlockEntity below = level.getBlockEntity(pos.below());
                if (below != null)
                {
                    below.getCapability(Capabilities.ITEM, Direction.UP).ifPresent(cap -> {
                        if (Helpers.insertAllSlots(cap, new ItemStack(TFCItems.COMPOST.get())).isEmpty())
                        {
                            composter.reset();
                        }
                    });
                }
            }
        }
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack)
    {
        FLHelpers.resetCounter(level, pos);
        super.setPlacedBy(level, pos, state, placer, stack);
    }

}
