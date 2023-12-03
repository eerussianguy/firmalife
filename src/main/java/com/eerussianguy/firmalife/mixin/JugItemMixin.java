package com.eerussianguy.firmalife.mixin;

import com.eerussianguy.firmalife.common.items.FLItems;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.items.ItemHandlerHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.dries007.tfc.common.items.JugItem;

@Mixin(JugItem.class)
public abstract class JugItemMixin
{
    @Inject(method = "finishUsingItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;shrink(I)V"))
    private void inject$finishUsingItem(ItemStack stack, Level level, LivingEntity entity, CallbackInfoReturnable<ItemStack> cir)
    {
        if (entity instanceof Player player)
        {
            ItemHandlerHelper.giveItemToPlayer(player, FLItems.POTTERY_SHERD.get().getDefaultInstance());
        }
    }

}
