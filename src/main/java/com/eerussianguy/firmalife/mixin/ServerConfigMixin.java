package com.eerussianguy.firmalife.mixin;

import java.util.function.Supplier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.dries007.tfc.config.ConfigBuilder;
import net.dries007.tfc.config.ServerConfig;

@Mixin(ServerConfig.class)
public abstract class ServerConfigMixin
{
    @Shadow(remap = false)
    @Mutable
    @Final
    public Supplier<Boolean> enablePumpkinCarving;

    @Inject(method = "<init>", at = @At("TAIL"), remap = false)
    private void inject$init(ConfigBuilder builder, CallbackInfo ci)
    {
        enablePumpkinCarving = () -> false;
    }
}
