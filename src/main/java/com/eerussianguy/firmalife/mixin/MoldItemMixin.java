package com.eerussianguy.firmalife.mixin;

import org.spongepowered.asm.mixin.Mixin;

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
