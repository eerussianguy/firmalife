package com.eerussianguy.firmalife.mixin;

import org.spongepowered.asm.mixin.Mixin;

import net.dries007.tfc.common.items.JugItem;

@Mixin(JugItem.class)
public abstract class JugItemMixin
{
//    @Inject(method = "finishUsingItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;shrink(I)V"))
//    private void inject$finishUsingItem(ItemStack stack, Level level, LivingEntity entity, CallbackInfoReturnable<ItemStack> cir)
//    {
//        if (entity instanceof Player player)
//        {
//            ItemHandlerHelper.giveItemToPlayer(player, FLItems.POTTERY_SHERD.get().getDefaultInstance());
//        }
//    }

}
