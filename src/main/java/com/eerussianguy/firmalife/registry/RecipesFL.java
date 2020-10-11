package com.eerussianguy.firmalife.registry;

import net.minecraft.item.ItemStack;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.registries.IForgeRegistry;

import com.eerussianguy.firmalife.init.OvenRecipe;
import net.dries007.tfc.objects.inventory.ingredient.IIngredient;
import net.dries007.tfc.util.calendar.ICalendar;

import static com.eerussianguy.firmalife.FirmaLife.MOD_ID;

@Mod.EventBusSubscriber(modid = MOD_ID)
public class RecipesFL
{
    @SubscribeEvent
    public static void onRegisterOvenRecipeEvent(RegistryEvent.Register<OvenRecipe> event)
    {
        IForgeRegistry<OvenRecipe> r = event.getRegistry();
        int hour = ICalendar.TICKS_IN_HOUR;

        r.register(new OvenRecipe(IIngredient.of(new ItemStack(ModRegistry.DRIED_COCOA_BEANS)), new ItemStack(ModRegistry.ROASTED_COCOA_BEANS), hour).setRegistryName("dried_cocoa_beans"));
    }
}
