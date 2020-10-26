package com.eerussianguy.firmalife.registry;

import net.minecraft.item.ItemStack;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.registries.IForgeRegistry;

import com.eerussianguy.firmalife.init.DryingRecipe;
import com.eerussianguy.firmalife.init.OvenRecipe;
import com.eerussianguy.firmalife.init.PlanterRegistry;
import net.dries007.tfc.objects.inventory.ingredient.IIngredient;
import net.dries007.tfc.objects.items.ItemSeedsTFC;
import net.dries007.tfc.objects.items.ItemsTFC;
import net.dries007.tfc.objects.items.food.ItemFoodTFC;
import net.dries007.tfc.util.agriculture.Crop;
import net.dries007.tfc.util.agriculture.Food;
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
        r.registerAll(
            // the input being straw makes this a curing recipe
            new OvenRecipe(IIngredient.of(new ItemStack(ItemsTFC.STRAW)), new ItemStack(ItemsTFC.WOOD_ASH), 8 * hour).setRegistryName("cure"),

            new OvenRecipe(IIngredient.of(new ItemStack(ModRegistry.DRIED_COCOA_BEANS)), new ItemStack(ModRegistry.ROASTED_COCOA_BEANS), 4 * hour).setRegistryName("dried_cocoa_beans"),

            new OvenRecipe(IIngredient.of(ItemFoodTFC.get(Food.BARLEY_DOUGH)), new ItemStack(ItemFoodTFC.get(Food.BARLEY_BREAD)), 4 * hour).setRegistryName("barley_dough"),
            new OvenRecipe(IIngredient.of(ItemFoodTFC.get(Food.CORNMEAL_DOUGH)), new ItemStack(ItemFoodTFC.get(Food.CORNBREAD)), 4 * hour).setRegistryName("corn_dough"),
            new OvenRecipe(IIngredient.of(ItemFoodTFC.get(Food.OAT_DOUGH)), new ItemStack(ItemFoodTFC.get(Food.OAT_BREAD)), 4 * hour).setRegistryName("oat_dough"),
            new OvenRecipe(IIngredient.of(ItemFoodTFC.get(Food.RICE_DOUGH)), new ItemStack(ItemFoodTFC.get(Food.RICE_BREAD)), 4 * hour).setRegistryName("rice_dough"),
            new OvenRecipe(IIngredient.of(ItemFoodTFC.get(Food.RYE_DOUGH)), new ItemStack(ItemFoodTFC.get(Food.RYE_BREAD)), 4 * hour).setRegistryName("rye_dough"),
            new OvenRecipe(IIngredient.of(ItemFoodTFC.get(Food.WHEAT_DOUGH)), new ItemStack(ItemFoodTFC.get(Food.WHEAT_BREAD)), 4 * hour).setRegistryName("wheat_dough")
        );

    }

    @SubscribeEvent
    public static void onRegisterDryingRecipeEvent(RegistryEvent.Register<DryingRecipe> event)
    {
        IForgeRegistry<DryingRecipe> r = event.getRegistry();
        int day = ICalendar.TICKS_IN_DAY;
        r.registerAll(
            new DryingRecipe(IIngredient.of(new ItemStack(ModRegistry.COCOA_BEANS)), new ItemStack(ModRegistry.DRIED_COCOA_BEANS), day * 5).setRegistryName("cocoa_beans"),
            new DryingRecipe(IIngredient.of(new ItemStack(ModRegistry.CINNAMON_BARK)), new ItemStack(ModRegistry.CINNAMON), day).setRegistryName("cinnamon_bark")
        );
    }

    @SubscribeEvent
    public static void onRegisterPlanterQuadEvent(RegistryEvent.Register<PlanterRegistry> event)
    {
        IForgeRegistry<PlanterRegistry> r = event.getRegistry();
        r.registerAll(
            new PlanterRegistry(IIngredient.of(ItemSeedsTFC.get(Crop.BEET)), new ItemStack(ItemFoodTFC.get(Food.BEET)), 6).setRegistryName("beet"),
            new PlanterRegistry(IIngredient.of(ItemSeedsTFC.get(Crop.CABBAGE)), new ItemStack(ItemFoodTFC.get(Food.CABBAGE)), 5).setRegistryName("cabbage"),
            new PlanterRegistry(IIngredient.of(ItemSeedsTFC.get(Crop.CARROT)), new ItemStack(ItemFoodTFC.get(Food.CARROT)), 4).setRegistryName("carrot"),
            new PlanterRegistry(IIngredient.of(ItemSeedsTFC.get(Crop.GARLIC)), new ItemStack(ItemFoodTFC.get(Food.GARLIC)), 4).setRegistryName("garlic"),
            new PlanterRegistry(IIngredient.of(ItemSeedsTFC.get(Crop.ONION)), new ItemStack(ItemFoodTFC.get(Food.ONION)), 6).setRegistryName("onion"),
            new PlanterRegistry(IIngredient.of(ItemSeedsTFC.get(Crop.POTATO)), new ItemStack(ItemFoodTFC.get(Food.POTATO)), 6).setRegistryName("potato"),
            new PlanterRegistry(IIngredient.of(ItemSeedsTFC.get(Crop.SOYBEAN)), new ItemStack(ItemFoodTFC.get(Food.SOYBEAN)), 6).setRegistryName("soybean")
            );
    }

}
