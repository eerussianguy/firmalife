package com.eerussianguy.firmalife.common.misc;

import net.minecraft.core.registries.Registries;
import net.minecraft.sounds.SoundEvent;

import com.eerussianguy.firmalife.FirmaLife;
import net.neoforged.neoforge.registries.DeferredRegister;

import net.dries007.tfc.client.TFCSounds.Id;
import net.dries007.tfc.util.Helpers;

public class FLSounds
{
    public static final DeferredRegister<SoundEvent> SOUND = DeferredRegister.create(Registries.SOUND_EVENT, FirmaLife.MOD_ID);

    public static final Id HOLLOW_SHELL_BLOW = register("item.hollow_shell.blow");

    private static Id register(String name)
    {
        return new Id(SOUND.register(name, () -> SoundEvent.createVariableRangeEvent(Helpers.identifier(name))));
    }
}
