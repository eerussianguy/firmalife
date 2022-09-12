package com.eerussianguy.firmalife.common.items;

import java.util.Random;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import com.eerussianguy.firmalife.common.FLHelpers;
import com.eerussianguy.firmalife.common.blockentities.ClimateReceiver;

public class WateringCanItem extends Item
{
    public WateringCanItem(Properties properties)
    {
        super(properties);
    }

    @Override
    @SuppressWarnings("deprecation")
    public void onUseTick(Level level, LivingEntity entity, ItemStack stack, int countLeft)
    {
        if (entity instanceof Player player)
        {
            final BlockHitResult hit = getPlayerPOVHitResult(level, player, ClipContext.Fluid.NONE);
            if (level.isClientSide && hit.getType() != HitResult.Type.MISS)
            {
                final Vec3 vec = hit.getLocation();
                final Random rand = entity.getRandom();
                level.addParticle(ParticleTypes.SPLASH, vec.x, vec.y, vec.z, rand.nextFloat() - 0.5f, rand.nextFloat(), rand.nextFloat() - 0.5f);
            }
        }
    }

    @Override
    public void releaseUsing(ItemStack stack, Level level, LivingEntity entity, int left)
    {
        for (BlockPos pos : FLHelpers.allPositionsCentered(entity.blockPosition(), 3, 3))
        {
            if (level.getBlockEntity(pos) instanceof ClimateReceiver receiver)
            {
                receiver.addWater(0.25f);
            }
        }
        stack.hurtAndBreak(1, entity, p -> p.broadcastBreakEvent(entity.getUsedItemHand()));
    }

    @Override
    public InteractionResult useOn(UseOnContext context)
    {
        ItemStack item = context.getItemInHand();
        if (context.getLevel().getFluidState(context.getClickedPos().relative(context.getClickedFace())).getType().isSame(Fluids.WATER))
        {
            if (item.isDamaged())
            {
                item.setDamageValue(0);
                return InteractionResult.SUCCESS;
            }
            return InteractionResult.PASS;
        }
        else if (context.getPlayer() != null && item.getDamageValue() < item.getMaxDamage() - 1)
        {
            context.getPlayer().startUsingItem(context.getHand());
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    public int getUseDuration(ItemStack stack)
    {
        return 20;
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack)
    {
        return UseAnim.BOW;
    }
}
