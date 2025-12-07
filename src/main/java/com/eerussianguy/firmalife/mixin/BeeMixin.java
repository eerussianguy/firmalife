package com.eerussianguy.firmalife.mixin;

import com.eerussianguy.firmalife.common.entities.FLBee;
import net.dries007.tfc.common.blocks.devices.BarrelRackBlock;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.NeutralMob;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.Bee;
import net.minecraft.world.entity.animal.FlyingAnimal;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Bee.class)
public abstract class BeeMixin extends Animal implements NeutralMob, FlyingAnimal
{
    protected BeeMixin(EntityType<? extends Animal> pEntityType, Level pLevel)
    {
        super(pEntityType, pLevel);
    }

    @Shadow
    abstract void setHasNectar(boolean pHasNectar);

    @Inject(method = "tick", at = @At("TAIL"))
    private void injectLosePollenSometimes(CallbackInfo ci)
    {
        if (((Bee) (Object) this) instanceof FLBee)
        {
            if (((Bee) (Object) this).tickCount % (600) == 0)
            {
                setHasNectar(false);
            }
        }
    }

}
