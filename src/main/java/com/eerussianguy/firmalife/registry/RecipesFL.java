package com.eerussianguy.firmalife.registry;

import net.minecraft.item.ItemStack;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.registries.IForgeRegistry;

import com.eerussianguy.firmalife.init.FoodFL;
import com.eerussianguy.firmalife.init.Fruit;
import com.eerussianguy.firmalife.init.PlantsFL;
import com.eerussianguy.firmalife.recipe.*;
import net.dries007.tfc.api.registries.TFCRegistries;
import net.dries007.tfc.api.types.Tree;
import net.dries007.tfc.objects.blocks.plants.BlockPlantTFC;
import net.dries007.tfc.objects.blocks.wood.BlockLeavesTFC;
import net.dries007.tfc.objects.blocks.wood.BlockLogTFC;
import net.dries007.tfc.objects.fluids.FluidsTFC;
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

            new OvenRecipe(IIngredient.of(new ItemStack(ItemsFL.getFood(FoodFL.DRIED_COCOA_BEANS))), new ItemStack(ItemsFL.ROASTED_COCOA_BEANS), 2 * hour).setRegistryName("dried_cocoa_beans"),
            new OvenRecipe(IIngredient.of(new ItemStack(ItemsFL.getFood(FoodFL.CHESTNUTS))), new ItemStack(ItemsFL.getFood(FoodFL.ROASTED_CHESTNUTS)), 2 * hour).setRegistryName("chestnuts"),
            new OvenRecipe(IIngredient.of(new ItemStack(ItemsFL.getFood(FoodFL.CHESTNUT_DOUGH))), new ItemStack(ItemsFL.getFood(FoodFL.CHESTNUT_BREAD)), 2 * hour).setRegistryName("chestnut_dough"),
            new OvenRecipe(IIngredient.of(new ItemStack(ItemsFL.getFood(FoodFL.PIZZA_DOUGH))), new ItemStack(ItemsFL.getFood(FoodFL.COOKED_PIZZA)), hour).setRegistryName("pizza_dough"),

            new OvenRecipe(IIngredient.of(ItemFoodTFC.get(Food.BARLEY_DOUGH)), new ItemStack(ItemFoodTFC.get(Food.BARLEY_BREAD)), 2 * hour).setRegistryName("barley_dough"),
            new OvenRecipe(IIngredient.of(ItemFoodTFC.get(Food.CORNMEAL_DOUGH)), new ItemStack(ItemFoodTFC.get(Food.CORNBREAD)), 2 * hour).setRegistryName("corn_dough"),
            new OvenRecipe(IIngredient.of(ItemFoodTFC.get(Food.OAT_DOUGH)), new ItemStack(ItemFoodTFC.get(Food.OAT_BREAD)), 2 * hour).setRegistryName("oat_dough"),
            new OvenRecipe(IIngredient.of(ItemFoodTFC.get(Food.RICE_DOUGH)), new ItemStack(ItemFoodTFC.get(Food.RICE_BREAD)), 2 * hour).setRegistryName("rice_dough"),
            new OvenRecipe(IIngredient.of(ItemFoodTFC.get(Food.RYE_DOUGH)), new ItemStack(ItemFoodTFC.get(Food.RYE_BREAD)), 2 * hour).setRegistryName("rye_dough"),
            new OvenRecipe(IIngredient.of(ItemFoodTFC.get(Food.WHEAT_DOUGH)), new ItemStack(ItemFoodTFC.get(Food.WHEAT_BREAD)), 2 * hour).setRegistryName("wheat_dough"),

            new OvenRecipe(IIngredient.of("barley_flatbread_dough"), new ItemStack(ItemsFL.BARLEY_FLATBREAD), 2 * hour).setRegistryName("barley_flatbread_dough"),
            new OvenRecipe(IIngredient.of("corn_flatbread_dough"), new ItemStack(ItemsFL.CORN_FLATBREAD), 2 * hour).setRegistryName("corn_flatbread_dough"),
            new OvenRecipe(IIngredient.of("oat_flatbread_dough"), new ItemStack(ItemsFL.OAT_FLATBREAD), 2 * hour).setRegistryName("oat_flatbread_dough"),
            new OvenRecipe(IIngredient.of("rice_flatbread_dough"), new ItemStack(ItemsFL.RICE_FLATBREAD), 2 * hour).setRegistryName("rice_flatbread_dough"),
            new OvenRecipe(IIngredient.of("rye_flatbread_dough"), new ItemStack(ItemsFL.RYE_FLATBREAD), 2 * hour).setRegistryName("rye_flatbread_dough"),
            new OvenRecipe(IIngredient.of("wheat_flatbread_dough"), new ItemStack(ItemsFL.WHEAT_FLATBREAD), 2 * hour).setRegistryName("wheat_flatbread_dough"),

            new OvenRecipe(IIngredient.of(new ItemStack(ItemsFL.WHITE_BLEND)), new ItemStack(ItemsFL.getFood(FoodFL.WHITE_CHOCOLATE)), 2 * hour).setRegistryName("white_blend"),
            new OvenRecipe(IIngredient.of(new ItemStack(ItemsFL.DARK_BLEND)), new ItemStack(ItemsFL.getFood(FoodFL.DARK_CHOCOLATE)), 2 * hour).setRegistryName("dark_blend"),
            new OvenRecipe(IIngredient.of(new ItemStack(ItemsFL.MILK_BLEND)), new ItemStack(ItemsFL.getFood(FoodFL.MILK_CHOCOLATE)), 2 * hour).setRegistryName("milk_blend")
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
            new DryingRecipe(IIngredient.of(new ItemStack(ItemsFL.getFood(FoodFL.COCOA_BEANS))), new ItemStack(ItemsFL.getFood(FoodFL.DRIED_COCOA_BEANS)), day / 2).setRegistryName("cocoa_beans"),
            new DryingRecipe(IIngredient.of(new ItemStack(ItemsFL.getFood(FoodFL.PINEAPPLE))), new ItemStack(ItemsFL.DRIED_PINEAPPLE), day / 2).setRegistryName("pineapple")
        );
    }

    @SubscribeEvent
    public static void onRegisterPlanterEvent(RegistryEvent.Register<PlanterRecipe> event)
    {
        IForgeRegistry<PlanterRecipe> r = event.getRegistry();
        r.registerAll(
            new PlanterRecipe(IIngredient.of(ItemSeedsTFC.get(Crop.BEET)), new ItemStack(ItemFoodTFC.get(Food.BEET)), 6, false).setRegistryName("beet"),
            new PlanterRecipe(IIngredient.of(ItemSeedsTFC.get(Crop.CABBAGE)), new ItemStack(ItemFoodTFC.get(Food.CABBAGE)), 5, false).setRegistryName("cabbage"),
            new PlanterRecipe(IIngredient.of(ItemSeedsTFC.get(Crop.CARROT)), new ItemStack(ItemFoodTFC.get(Food.CARROT)), 4, false).setRegistryName("carrot"),
            new PlanterRecipe(IIngredient.of(ItemSeedsTFC.get(Crop.GARLIC)), new ItemStack(ItemFoodTFC.get(Food.GARLIC)), 4, false).setRegistryName("garlic"),
            new PlanterRecipe(IIngredient.of(ItemSeedsTFC.get(Crop.ONION)), new ItemStack(ItemFoodTFC.get(Food.ONION)), 6, false).setRegistryName("onion"),
            new PlanterRecipe(IIngredient.of(ItemSeedsTFC.get(Crop.POTATO)), new ItemStack(ItemFoodTFC.get(Food.POTATO)), 6, false).setRegistryName("potato"),
            new PlanterRecipe(IIngredient.of(ItemSeedsTFC.get(Crop.SOYBEAN)), new ItemStack(ItemFoodTFC.get(Food.SOYBEAN)), 6, false).setRegistryName("soybean"),

            new PlanterRecipe(IIngredient.of(ItemSeedsTFC.get(Crop.YELLOW_BELL_PEPPER)), new ItemStack(ItemFoodTFC.get(Food.YELLOW_BELL_PEPPER)), 5, true).setRegistryName("yellow_bell_pepper"),
            new PlanterRecipe(IIngredient.of(ItemSeedsTFC.get(Crop.SUGARCANE)), new ItemStack(ItemFoodTFC.get(Food.SUGARCANE)), 7, true).setRegistryName("sugarcane"),
            new PlanterRecipe(IIngredient.of(ItemSeedsTFC.get(Crop.TOMATO)), new ItemStack(ItemFoodTFC.get(Food.TOMATO)), 7, true).setRegistryName("tomato"),
            new PlanterRecipe(IIngredient.of(ItemSeedsTFC.get(Crop.RED_BELL_PEPPER)), new ItemStack(ItemFoodTFC.get(Food.RED_BELL_PEPPER)), 5, true).setRegistryName("red_bell_pepper"),
            new PlanterRecipe(IIngredient.of(ItemSeedsTFC.get(Crop.JUTE)), new ItemStack(ItemsTFC.JUTE), 5, true).setRegistryName("jute"),
            new PlanterRecipe(IIngredient.of(ItemSeedsTFC.get(Crop.GREEN_BEAN)), new ItemStack(ItemFoodTFC.get(Food.GREEN_BEAN)), 6, true).setRegistryName("green_bean"),

            new PlanterRecipe(IIngredient.of(ItemSeedsTFC.get(Crop.MAIZE)), new ItemStack(ItemFoodTFC.get(Food.MAIZE)), 5, true, 3).setRegistryName("maize"),
            new PlanterRecipe(IIngredient.of(ItemSeedsTFC.get(Crop.BARLEY)), new ItemStack(ItemFoodTFC.get(Food.BARLEY)), 7, true, 3).setRegistryName("barley"),
            new PlanterRecipe(IIngredient.of(ItemSeedsTFC.get(Crop.OAT)), new ItemStack(ItemFoodTFC.get(Food.OAT)), 7, true, 3).setRegistryName("oat"),
            new PlanterRecipe(IIngredient.of(ItemSeedsTFC.get(Crop.RICE)), new ItemStack(ItemFoodTFC.get(Food.RICE)), 7, true, 3).setRegistryName("rice"),
            new PlanterRecipe(IIngredient.of(ItemSeedsTFC.get(Crop.RYE)), new ItemStack(ItemFoodTFC.get(Food.RYE)), 7, true, 3).setRegistryName("rye"),
            new PlanterRecipe(IIngredient.of(ItemSeedsTFC.get(Crop.WHEAT)), new ItemStack(ItemFoodTFC.get(Food.WHEAT)), 7, true, 3).setRegistryName("wheat")
            );

        // this makes me laugh for some reason...
        PlantsFL.WRAPPERS.forEach(plant -> {
            BlockPlantTFC block = BlockPlantTFC.get(plant);
            r.register(new PlanterRecipe(IIngredient.of(block), new ItemStack(block), 1, false).setRegistryName(plant.getRegistryName().getPath()));
        });
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
            new NutRecipe(BlockLogTFC.get(chestnut), BlockLeavesTFC.get(chestnut), new ItemStack(ItemsFL.getFood(FoodFL.CHESTNUTS))).setRegistryName("chestnut"),
            new NutRecipe(BlockLogTFC.get(oak), BlockLeavesTFC.get(oak), new ItemStack(ItemsFL.getFood(FoodFL.ACORNS))).setRegistryName("oak"),
            new NutRecipe(BlockLogTFC.get(hickory), BlockLeavesTFC.get(hickory), new ItemStack(ItemsFL.getFood(FoodFL.PECAN_NUTS))).setRegistryName("hickory"),
            new NutRecipe(BlockLogTFC.get(pine), BlockLeavesTFC.get(pine), new ItemStack(ItemsFL.getFood(FoodFL.PINECONE))).setRegistryName("pine"),
            new NutRecipe(BlockLogTFC.get(palm), BlockLeavesTFC.get(palm), new ItemStack(ItemsFL.getFood(FoodFL.COCONUT))).setRegistryName("coconut")
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
            new CrackingRecipe(IIngredient.of(ItemsFL.getFood(FoodFL.ACORNS)), new ItemStack(ItemsFL.getFood(FoodFL.ACORN_FRUIT)), 0.5f).setRegistryName("acorn_fruit"),
            new CrackingRecipe(IIngredient.of(ItemsFL.getFood(FoodFL.PINECONE)), new ItemStack(ItemsFL.getFood(FoodFL.PINE_NUTS)), 0.5f).setRegistryName("pine_nuts"),
            new CrackingRecipe(IIngredient.of(ItemsFL.getFood(FoodFL.PECAN_NUTS)), new ItemStack(ItemsFL.getFood(FoodFL.PECANS)), 0.5f).setRegistryName("pecans"),
            new CrackingRecipe(IIngredient.of(ItemsFL.getFood(FoodFL.COCONUT)), filled_coconut.copy(), 0.5f).setRegistryName("coconut_milk")
        );
    }

    @SubscribeEvent
    public static void inRegisterStrainingRecipeEvent(RegistryEvent.Register<StrainingRecipe> event)
    {
        IForgeRegistry<StrainingRecipe> r = event.getRegistry();
        r.registerAll(
            new StrainingRecipe(IIngredient.of(FluidsTFC.CURDLED_MILK.get(), 500), new ItemStack(ItemsFL.MILK_CURD), null).setRegistryName("milk_curd"),
            new StrainingRecipe(IIngredient.of(FluidsFL.CURDLED_GOAT_MILK.get(), 500), new ItemStack(ItemsFL.GOAT_CURD), null).setRegistryName("goat_curd"),
            new StrainingRecipe(IIngredient.of(FluidsFL.CURDLED_YAK_MILK.get(), 500), new ItemStack(ItemsFL.YAK_CURD), null).setRegistryName("yak_curd")
        );
    }
}

