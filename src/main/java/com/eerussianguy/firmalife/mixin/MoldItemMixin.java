package com.eerussianguy.firmalife.mixin;

import com.eerussianguy.firmalife.common.items.FLItems;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.items.ItemHandlerHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.dries007.tfc.common.items.MoldItem;

@Mixin(MoldItem.class)
public abstract class MoldItemMixin
{
//    @Inject(method = "use", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;shrink(I)V"))
//    private void inject$use(Level level, Player player, InteractionHand hand, CallbackInfoReturnable<InteractionResultHolder<ItemStack>> cir)
//    {
//        ItemHandlerHelper.giveItemToPlayer(player, FLItems.POTTERY_SHERD.get().getDefaultInstance());
//    }
//
//    @Inject(method = "overrideOtherStackedOnMe", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;shrink(I)V"))
//    private void inject$overrideOtherStackedOnMe(ItemStack stack, ItemStack carried, Slot slot, ClickAction action, Player player, SlotAccess carriedSlot, CallbackInfoReturnable<Boolean> cir)
//    {
//        ItemHandlerHelper.giveItemToPlayer(player, FLItems.POTTERY_SHERD.get().getDefaultInstance());
//    }

}
