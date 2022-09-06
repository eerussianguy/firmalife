package com.eerussianguy.firmalife.common.items;

import java.util.List;

import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import com.eerussianguy.firmalife.common.entities.SeedBall;
import com.eerussianguy.firmalife.config.FLConfig;
import net.dries007.tfc.util.Helpers;
import org.jetbrains.annotations.Nullable;

public class SeedBallItem extends Item
{
    public SeedBallItem(Properties properties)
    {
        super(properties);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> text, TooltipFlag flags)
    {
        text.add(Helpers.translatable("firmalife.tooltip.seed_ball"));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand)
    {
        final ItemStack held = player.getItemInHand(hand);
        if (!FLConfig.SERVER.enableSeedBalls.get())
        {
            player.displayClientMessage(Helpers.translatable("firmalife.tooltip.seed_ball_disabled"), true);
            return InteractionResultHolder.pass(held);
        }

        Helpers.playSound(level, player.blockPosition(), SoundEvents.SNOWBALL_THROW);
        player.getCooldowns().addCooldown(this, 20);
        if (!level.isClientSide)
        {
            Helpers.playSound(level, player.blockPosition(), SoundEvents.SNOWBALL_THROW);
            final SeedBall ball = new SeedBall(player, level);
            ball.setItem(held);
            ball.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, 1.5F, 1.0F);
            level.addFreshEntity(ball);
        }

        player.awardStat(Stats.ITEM_USED.get(this));
        if (!player.getAbilities().instabuild) held.shrink(1);

        return InteractionResultHolder.sidedSuccess(held, level.isClientSide());
    }
}
