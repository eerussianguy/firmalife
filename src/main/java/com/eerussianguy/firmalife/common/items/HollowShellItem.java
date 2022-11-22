package com.eerussianguy.firmalife.common.items;

import java.util.function.Supplier;

import com.eerussianguy.firmalife.common.blocks.FLBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.TagKey;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.fluids.capability.IFluidHandler;

import com.eerussianguy.firmalife.common.misc.FLSounds;

import net.dries007.tfc.common.fluids.FluidHelpers;
import net.dries007.tfc.common.items.FluidContainerItem;
import net.dries007.tfc.util.Helpers;

public class HollowShellItem extends FluidContainerItem
{
    public HollowShellItem(Properties properties, Supplier<Integer> capacity, TagKey<Fluid> whitelist, boolean canPlaceSourceBlocks, boolean canPlaceLiquidsInWorld)
    {
        super(properties, capacity, whitelist, canPlaceSourceBlocks, canPlaceLiquidsInWorld);
    }

    @Override
    protected InteractionResultHolder<ItemStack> afterFillFailed(IFluidHandler handler, Level level, Player player, ItemStack stack, InteractionHand hand)
    {
        if (player.isShiftKeyDown())
        {
            final BlockHitResult hit = Helpers.rayTracePlayer(level, player, ClipContext.Fluid.NONE);
            if (hit.getType() != HitResult.Type.MISS && hit.getDirection() == Direction.UP)
            {
                final BlockPos pos = hit.getBlockPos().above();
                BlockState state = FLBlocks.HOLLOW_SHELL.get().defaultBlockState();
                if (state.canSurvive(level, pos))
                {
                    Fluid fluid = level.getFluidState(pos).getType();
                    state = FluidHelpers.fillWithFluid(state, fluid);
                    if (state != null)
                    {
                        Helpers.playSound(level, pos, state.getSoundType().getPlaceSound());
                        level.setBlockAndUpdate(pos, state);
                        stack.shrink(1);
                        return InteractionResultHolder.consume(stack);
                    }
                }
            }
        }
        level.playSound(player, player.blockPosition(), FLSounds.HOLLOW_SHELL_BLOW.get(), SoundSource.PLAYERS, 1.0F, 0.8F + (float)(player.getLookAngle().y / 2.0));
        return InteractionResultHolder.success(stack);
    }
}
