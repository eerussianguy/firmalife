package com.eerussianguy.firmalife.registry;

import net.minecraft.item.ItemStack;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.registries.IForgeRegistry;

import com.eerussianguy.firmalife.recipe.DryingRecipe;
import com.eerussianguy.firmalife.recipe.NutRecipe;
import com.eerussianguy.firmalife.recipe.OvenRecipe;
import com.eerussianguy.firmalife.recipe.PlanterRecipe;
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
            new DryingRecipe(IIngredient.of(new ItemStack(ItemsFL.COCOA_BEANS)), new ItemStack(ItemsFL.DRIED_COCOA_BEANS), day * 5).setRegistryName("cocoa_beans"),
            new DryingRecipe(IIngredient.of(new ItemStack(ItemsFL.CINNAMON_BARK)), new ItemStack(ItemsFL.CINNAMON), day).setRegistryName("cinnamon_bark"),
            new DryingRecipe(IIngredient.of(new ItemStack(ItemFoodTFC.get(Food.BANANA))), new ItemStack(ItemsFL.DRIED_BANANA), day / 2).setRegistryName("banana"),
            new DryingRecipe(IIngredient.of(new ItemStack(ItemFoodTFC.get(Food.BLACKBERRY))), new ItemStack(ItemsFL.DRIED_BLACKBERRY), day / 2).setRegistryName("blackberry"),
            new DryingRecipe(IIngredient.of(new ItemStack(ItemFoodTFC.get(Food.BLUEBERRY))), new ItemStack(ItemsFL.DRIED_BLUEBERRY), day / 2).setRegistryName("blueberry"),
            new DryingRecipe(IIngredient.of(new ItemStack(ItemFoodTFC.get(Food.BUNCH_BERRY))), new ItemStack(ItemsFL.DRIED_BUNCH_BERRY), day / 2).setRegistryName("bunch_berry"),
            new DryingRecipe(IIngredient.of(new ItemStack(ItemFoodTFC.get(Food.CHERRY))), new ItemStack(ItemsFL.DRIED_CHERRY), day / 2).setRegistryName("cherry"),
            new DryingRecipe(IIngredient.of(new ItemStack(ItemFoodTFC.get(Food.CLOUD_BERRY))), new ItemStack(ItemsFL.DRIED_CLOUD_BERRY), day / 2).setRegistryName("cloud_berry"),
            new DryingRecipe(IIngredient.of(new ItemStack(ItemFoodTFC.get(Food.CRANBERRY))), new ItemStack(ItemsFL.DRIED_CRANBERRY), day / 2).setRegistryName("cranberry"),
            new DryingRecipe(IIngredient.of(new ItemStack(ItemFoodTFC.get(Food.ELDERBERRY))), new ItemStack(ItemsFL.DRIED_ELDERBERRY), day / 2).setRegistryName("elderberry"),
            new DryingRecipe(IIngredient.of(new ItemStack(ItemFoodTFC.get(Food.GOOSEBERRY))), new ItemStack(ItemsFL.DRIED_GOOSEBERRY), day / 2).setRegistryName("gooseberry"),
            new DryingRecipe(IIngredient.of(new ItemStack(ItemFoodTFC.get(Food.GREEN_APPLE))), new ItemStack(ItemsFL.DRIED_GREEN_APPLE), day / 2).setRegistryName("green_apple"),
            new DryingRecipe(IIngredient.of(new ItemStack(ItemFoodTFC.get(Food.LEMON))), new ItemStack(ItemsFL.DRIED_LEMON), day / 2).setRegistryName("lemon"),
            new DryingRecipe(IIngredient.of(new ItemStack(ItemFoodTFC.get(Food.OLIVE))), new ItemStack(ItemsFL.DRIED_OLIVE), day / 2).setRegistryName("olive"),
            new DryingRecipe(IIngredient.of(new ItemStack(ItemFoodTFC.get(Food.ORANGE))), new ItemStack(ItemsFL.DRIED_ORANGE), day / 2).setRegistryName("orange"),
            new DryingRecipe(IIngredient.of(new ItemStack(ItemFoodTFC.get(Food.PEACH))), new ItemStack(ItemsFL.DRIED_PEACH), day / 2).setRegistryName("peach"),
            new DryingRecipe(IIngredient.of(new ItemStack(ItemFoodTFC.get(Food.PLUM))), new ItemStack(ItemsFL.DRIED_PLUM), day / 2).setRegistryName("plum"),
            new DryingRecipe(IIngredient.of(new ItemStack(ItemFoodTFC.get(Food.RASPBERRY))), new ItemStack(ItemsFL.DRIED_RASPBERRY), day / 2).setRegistryName("raspberry"),
            new DryingRecipe(IIngredient.of(new ItemStack(ItemFoodTFC.get(Food.RED_APPLE))), new ItemStack(ItemsFL.DRIED_RED_APPLE), day / 2).setRegistryName("red_apple"),
            new DryingRecipe(IIngredient.of(new ItemStack(ItemFoodTFC.get(Food.SNOW_BERRY))), new ItemStack(ItemsFL.DRIED_SNOW_BERRY), day / 2).setRegistryName("snow_berry"),
            new DryingRecipe(IIngredient.of(new ItemStack(ItemFoodTFC.get(Food.STRAWBERRY))), new ItemStack(ItemsFL.DRIED_STRAWBERRY), day / 2).setRegistryName("strawberry"),
            new DryingRecipe(IIngredient.of(new ItemStack(ItemFoodTFC.get(Food.WINTERGREEN_BERRY))), new ItemStack(ItemsFL.DRIED_WINTERGREEN_BERRY), day / 2).setRegistryName("wintergreen_berry")
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
}
