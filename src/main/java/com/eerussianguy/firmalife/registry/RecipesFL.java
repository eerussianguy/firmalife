package com.eerussianguy.firmalife.registry;

import net.minecraft.item.ItemStack;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.registries.IForgeRegistry;

import com.eerussianguy.firmalife.init.Fruit;
import com.eerussianguy.firmalife.recipe.*;
import net.dries007.tfc.api.registries.TFCRegistries;
import net.dries007.tfc.api.types.Tree;
import net.dries007.tfc.objects.blocks.wood.BlockLeavesTFC;
import net.dries007.tfc.objects.blocks.wood.BlockLogTFC;
import net.dries007.tfc.objects.inventory.ingredient.IIngredient;
import net.dries007.tfc.objects.items.ItemSeedsTFC;
import net.dries007.tfc.objects.items.ItemsTFC;
import net.dries007.tfc.objects.items.food.ItemFoodTFC;
import net.dries007.tfc.types.DefaultTrees;
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

            new OvenRecipe(IIngredient.of(new ItemStack(ItemsFL.DRIED_COCOA_BEANS)), new ItemStack(ItemsFL.ROASTED_COCOA_BEANS), 4 * hour).setRegistryName("dried_cocoa_beans"),
            new OvenRecipe(IIngredient.of(new ItemStack(ItemsFL.CHESTNUTS)), new ItemStack(ItemsFL.ROASTED_CHESTNUTS), hour).setRegistryName("chestnuts"),

            new OvenRecipe(IIngredient.of(ItemFoodTFC.get(Food.BARLEY_DOUGH)), new ItemStack(ItemFoodTFC.get(Food.BARLEY_BREAD)), 4 * hour).setRegistryName("barley_dough"),
            new OvenRecipe(IIngredient.of(ItemFoodTFC.get(Food.CORNMEAL_DOUGH)), new ItemStack(ItemFoodTFC.get(Food.CORNBREAD)), 4 * hour).setRegistryName("corn_dough"),
            new OvenRecipe(IIngredient.of(ItemFoodTFC.get(Food.OAT_DOUGH)), new ItemStack(ItemFoodTFC.get(Food.OAT_BREAD)), 4 * hour).setRegistryName("oat_dough"),
            new OvenRecipe(IIngredient.of(ItemFoodTFC.get(Food.RICE_DOUGH)), new ItemStack(ItemFoodTFC.get(Food.RICE_BREAD)), 4 * hour).setRegistryName("rice_dough"),
            new OvenRecipe(IIngredient.of(ItemFoodTFC.get(Food.RYE_DOUGH)), new ItemStack(ItemFoodTFC.get(Food.RYE_BREAD)), 4 * hour).setRegistryName("rye_dough"),
            new OvenRecipe(IIngredient.of(ItemFoodTFC.get(Food.WHEAT_DOUGH)), new ItemStack(ItemFoodTFC.get(Food.WHEAT_BREAD)), 4 * hour).setRegistryName("wheat_dough"),

            new OvenRecipe(IIngredient.of("barley_flatbread_dough"), new ItemStack(ItemsFL.BARLEY_FLATBREAD), 4 * hour).setRegistryName("barley_flatbread_dough"),
            new OvenRecipe(IIngredient.of("corn_flatbread_dough"), new ItemStack(ItemsFL.CORN_FLATBREAD), 4 * hour).setRegistryName("corn_flatbread_dough"),
            new OvenRecipe(IIngredient.of("oat_flatbread_dough"), new ItemStack(ItemsFL.OAT_FLATBREAD), 4 * hour).setRegistryName("oat_flatbread_dough"),
            new OvenRecipe(IIngredient.of("rice_flatbread_dough"), new ItemStack(ItemsFL.RICE_FLATBREAD), 4 * hour).setRegistryName("rice_flatbread_dough"),
            new OvenRecipe(IIngredient.of("rye_flatbread_dough"), new ItemStack(ItemsFL.RYE_FLATBREAD), 4 * hour).setRegistryName("rye_flatbread_dough"),
            new OvenRecipe(IIngredient.of("wheat_flatbread_dough"), new ItemStack(ItemsFL.WHEAT_FLATBREAD), 4 * hour).setRegistryName("wheat_flatbread_dough")

            );

    }

    @SubscribeEvent
    public static void onRegisterDryingRecipeEvent(RegistryEvent.Register<DryingRecipe> event)
    {
        IForgeRegistry<DryingRecipe> r = event.getRegistry();
        int day = ICalendar.TICKS_IN_DAY;
        for (Fruit fruit : Fruit.values())
        {
            r.register(new DryingRecipe(IIngredient.of(fruit.getFruit()), new ItemStack(ItemsFL.getDriedFruit(fruit)), day / 2).setRegistryName(fruit.name().toLowerCase()));
        }
        r.registerAll(
            new DryingRecipe(IIngredient.of(new ItemStack(ItemsFL.CINNAMON_BARK)), new ItemStack(ItemsFL.CINNAMON), day).setRegistryName("cinnamon_bark"),
            new DryingRecipe(IIngredient.of(new ItemStack(ItemsFL.COCOA_BEANS)), new ItemStack(ItemsFL.DRIED_COCOA_BEANS), day / 2).setRegistryName("cocoa_beans")
        );
    }

    @SubscribeEvent
    public static void onRegisterPlanterQuadEvent(RegistryEvent.Register<PlanterRecipe> event)
    {
        IForgeRegistry<PlanterRecipe> r = event.getRegistry();
        r.registerAll(
            new PlanterRecipe(IIngredient.of(ItemSeedsTFC.get(Crop.BEET)), new ItemStack(ItemFoodTFC.get(Food.BEET)), 6).setRegistryName("beet"),
            new PlanterRecipe(IIngredient.of(ItemSeedsTFC.get(Crop.CABBAGE)), new ItemStack(ItemFoodTFC.get(Food.CABBAGE)), 5).setRegistryName("cabbage"),
            new PlanterRecipe(IIngredient.of(ItemSeedsTFC.get(Crop.CARROT)), new ItemStack(ItemFoodTFC.get(Food.CARROT)), 4).setRegistryName("carrot"),
            new PlanterRecipe(IIngredient.of(ItemSeedsTFC.get(Crop.GARLIC)), new ItemStack(ItemFoodTFC.get(Food.GARLIC)), 4).setRegistryName("garlic"),
            new PlanterRecipe(IIngredient.of(ItemSeedsTFC.get(Crop.ONION)), new ItemStack(ItemFoodTFC.get(Food.ONION)), 6).setRegistryName("onion"),
            new PlanterRecipe(IIngredient.of(ItemSeedsTFC.get(Crop.POTATO)), new ItemStack(ItemFoodTFC.get(Food.POTATO)), 6).setRegistryName("potato"),
            new PlanterRecipe(IIngredient.of(ItemSeedsTFC.get(Crop.SOYBEAN)), new ItemStack(ItemFoodTFC.get(Food.SOYBEAN)), 6).setRegistryName("soybean")
        );
    }

    @SubscribeEvent
    @SuppressWarnings("ConstantConditions")
    public static void onRegisterNutTreeEvent(RegistryEvent.Register<NutRecipe> event)
    {
        IForgeRegistry<NutRecipe> r = event.getRegistry();
        Tree chestnut = TFCRegistries.TREES.getValue(DefaultTrees.CHESTNUT);
        Tree oak = TFCRegistries.TREES.getValue(DefaultTrees.OAK);
        Tree hickory = TFCRegistries.TREES.getValue(DefaultTrees.HICKORY);
        Tree pine = TFCRegistries.TREES.getValue(DefaultTrees.PINE);
        Tree palm = TFCRegistries.TREES.getValue(DefaultTrees.PALM);
        r.registerAll(
            new NutRecipe(BlockLogTFC.get(chestnut), BlockLeavesTFC.get(chestnut), new ItemStack(ItemsFL.CHESTNUTS)).setRegistryName("chestnut"),
            new NutRecipe(BlockLogTFC.get(oak), BlockLeavesTFC.get(oak), new ItemStack(ItemsFL.ACORNS)).setRegistryName("oak"),
            new NutRecipe(BlockLogTFC.get(hickory), BlockLeavesTFC.get(hickory), new ItemStack(ItemsFL.PECAN_NUTS)).setRegistryName("hickory"),
            new NutRecipe(BlockLogTFC.get(pine), BlockLeavesTFC.get(pine), new ItemStack(ItemsFL.PINECONE)).setRegistryName("pine"),
            new NutRecipe(BlockLogTFC.get(palm), BlockLeavesTFC.get(palm), new ItemStack(ItemsFL.COCONUT)).setRegistryName("coconut")

        );
    }

    @SubscribeEvent
    public static void onRegisterCrackingRecipeEvent(RegistryEvent.Register<CrackingRecipe> event)
    {
        ItemStack filled_coconut = new ItemStack(ItemsFL.CRACKED_COCONUT);
        IFluidHandler fluidHandler = filled_coconut.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null);
        if (fluidHandler != null)
            fluidHandler.fill(new FluidStack(FluidsFL.COCONUT_MILK.get(), 1000), true);

        IForgeRegistry<CrackingRecipe> r = event.getRegistry();
        r.registerAll(
            new CrackingRecipe(IIngredient.of(ItemsFL.ACORNS), new ItemStack(ItemsFL.ACORN_FRUIT), 0.5f).setRegistryName("acorn_fruit"),
            new CrackingRecipe(IIngredient.of(ItemsFL.PINECONE), new ItemStack(ItemsFL.PINE_NUTS), 0.5f).setRegistryName("pine_nuts"),
            new CrackingRecipe(IIngredient.of(ItemsFL.PECAN_NUTS), new ItemStack(ItemsFL.PECANS), 0.5f).setRegistryName("pecans"),
            new CrackingRecipe(IIngredient.of(ItemsFL.COCONUT), filled_coconut, 0.5f).setRegistryName("coconut_milk")
        );
    }
}

