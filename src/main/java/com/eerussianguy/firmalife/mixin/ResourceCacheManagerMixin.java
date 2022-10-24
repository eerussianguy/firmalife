package com.eerussianguy.firmalife.mixin;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.resource.ResourceCacheManager;

import com.eerussianguy.firmalife.FirmaLife;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * For some reason bad namespaces are being initialized. This is the only way to fix that.
 */
@Pseudo
@Mixin(ResourceCacheManager.class)
public class ResourceCacheManagerMixin
{
    @Inject(method = "index", at = @At("HEAD"), cancellable = true, remap = false)
    private void inject$initForNamespace(String namespace, CallbackInfo ci)
    {
        if (!ResourceLocation.isValidNamespace(namespace))
        {
            ci.cancel();
            FirmaLife.LOGGER.info("Namespace REJECTED: " + namespace);
        }
    }

}
