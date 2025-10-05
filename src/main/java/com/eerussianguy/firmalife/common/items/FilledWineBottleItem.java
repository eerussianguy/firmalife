package com.eerussianguy.firmalife.common.items;

import java.util.List;
import com.eerussianguy.firmalife.common.FLHelpers;
import com.eerussianguy.firmalife.common.capabilities.FLComponents;
import com.eerussianguy.firmalife.common.capabilities.wine.WineComponent;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.neoforge.capabilities.Capabilities;

import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.fluids.FluidHelpers;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.calendar.Calendars;

public class FilledWineBottleItem extends WineBottleItem
{
    public FilledWineBottleItem(Properties properties, ResourceLocation modelLocation)
    {
        super(properties, modelLocation);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand)
    {
        final ItemStack stack = player.getItemInHand(hand);
        final BlockHitResult hit = getPlayerPOVHitResult(level, player, ClipContext.Fluid.SOURCE_ONLY);
        if (FluidHelpers.transferBetweenWorldAndItem(stack, level, hit, player, hand, false, false, false))
        {
            return InteractionResultHolder.success(player.getItemInHand(hand));
        }
        return InteractionResultHolder.pass(stack);
    }

    @Override
    public boolean overrideOtherStackedOnMe(ItemStack stack, ItemStack other, Slot slot, ClickAction action, Player player, SlotAccess carried)
    {
        if (action == ClickAction.SECONDARY && Helpers.isItem(other, TFCTags.Items.TOOLS_KNIFE))
        {
            final WineComponent wine = stack.get(FLComponents.WINE);
            if (wine != null)
            {
                if (wine.isSealed())
                {
                    player.playSound(SoundEvents.BAMBOO_BREAK);
                    stack.set(FLComponents.WINE, WineComponent.opened(wine, Calendars.get(player.level()).getTicks()));
                    if (player.level() instanceof ServerLevel server)
                    {
                        other.hurtAndBreak(1, server, player, i -> {});
                    }
                    return true;
                }
                return false;
            }
        }
        return false;
    }

    @Override
    public Component getName(ItemStack stack)
    {
        final WineComponent wine = stack.get(FLComponents.WINE);
        return wine != null && wine.creationDate() > 0 ? Component.translatable("firmalife.wine." + (wine.isSealed() ? "sealed" : "unsealed"), FLHelpers.translateEnum(wine.wineType())) : super.getName(stack);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag debug)
    {
        final WineComponent wine = stack.get(FLComponents.WINE);
        if (wine != null && wine.isSealed())
        {
            tooltip.add(Component.translatable("firmalife.wine.how_to_open").withStyle(ChatFormatting.GRAY));
        }
    }

    @Override
    public ItemStack getCraftingRemainingItem(ItemStack stack)
    {
        return new ItemStack(this);
    }

    @Override
    public boolean hasCraftingRemainingItem(ItemStack stack)
    {
        return stack.getCapability(Capabilities.FLUID_ITEM).map((cap) -> !cap.getFluidInTank(0).isEmpty()).orElse(false);
    }
}
