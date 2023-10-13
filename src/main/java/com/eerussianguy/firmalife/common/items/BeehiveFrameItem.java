package com.eerussianguy.firmalife.common.items;

import java.util.List;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.items.ItemHandlerHelper;

import com.eerussianguy.firmalife.common.capabilities.bee.BeeCapability;
import com.eerussianguy.firmalife.common.capabilities.bee.BeeHandler;
import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.util.Helpers;
import org.jetbrains.annotations.Nullable;

public class BeehiveFrameItem extends Item
{
    public BeehiveFrameItem(Properties properties)
    {
        super(properties);
    }

    @Override
    public boolean overrideOtherStackedOnMe(ItemStack stack, ItemStack other, Slot slot, ClickAction action, Player player, SlotAccess carried)
    {
        if (action == ClickAction.SECONDARY && Helpers.isItem(other, TFCTags.Items.KNIVES))
        {
            return stack.getCapability(BeeCapability.CAPABILITY).map(bee -> {
                if (bee.hasQueen())
                {
                    slot.set(new ItemStack(this));
                    ItemHandlerHelper.giveItemToPlayer(player, new ItemStack(FLItems.BEESWAX.get()));
                    other.hurtAndBreak(1, player, p -> {});
                    return true;
                }
                return false;
            }).orElse(false);
        }
        return false;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag advanced)
    {
        stack.getCapability(BeeCapability.CAPABILITY).ifPresent(bee -> {
            if (bee.hasQueen())
            {
                tooltip.add(Component.translatable("firmalife.bee.may_scrape"));
            }
        });
    }

    @Nullable
    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundTag nbt)
    {
        return new BeeHandler(stack);
    }

}
