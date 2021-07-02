package com.eerussianguy.firmalife.registry;

import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryModifiable;

import com.eerussianguy.firmalife.ConfigFL;
import com.eerussianguy.firmalife.FirmaLife;
import com.eerussianguy.firmalife.init.FoodFL;
import com.eerussianguy.firmalife.init.KnappingFL;
import com.eerussianguy.firmalife.init.PlantsFL;
import com.eerussianguy.firmalife.init.StemCrop;
import com.eerussianguy.firmalife.recipe.KnappingRecipeFood;
import net.dries007.tfc.TerraFirmaCraft;
import net.dries007.tfc.api.capability.food.FoodTrait;
import net.dries007.tfc.api.recipes.LoomRecipe;
import net.dries007.tfc.api.recipes.anvil.AnvilRecipe;
import net.dries007.tfc.api.recipes.barrel.BarrelRecipe;
import net.dries007.tfc.api.recipes.barrel.BarrelRecipeFoodPreservation;
import net.dries007.tfc.api.recipes.heat.HeatRecipe;
import net.dries007.tfc.api.recipes.heat.HeatRecipeSimple;
import net.dries007.tfc.api.recipes.knapping.KnappingRecipe;
import net.dries007.tfc.api.recipes.knapping.KnappingRecipeSimple;
import net.dries007.tfc.api.recipes.knapping.KnappingType;
import net.dries007.tfc.api.recipes.quern.QuernRecipe;
import net.dries007.tfc.api.registries.TFCRegistries;
import net.dries007.tfc.api.registries.TFCRegistryEvent;
import net.dries007.tfc.api.types.Metal;
import net.dries007.tfc.api.types.Ore;
import net.dries007.tfc.api.types.Plant;
import net.dries007.tfc.objects.Powder;
import net.dries007.tfc.objects.fluids.FluidsTFC;
import net.dries007.tfc.objects.inventory.ingredient.IIngredient;
import net.dries007.tfc.objects.inventory.ingredient.IngredientItemFood;
import net.dries007.tfc.objects.items.ItemPowder;
import net.dries007.tfc.objects.items.ItemSeedsTFC;
import net.dries007.tfc.objects.items.food.ItemFoodTFC;
import net.dries007.tfc.objects.items.metal.ItemMetal;
import net.dries007.tfc.util.agriculture.Food;
import net.dries007.tfc.util.calendar.ICalendar;

import static com.eerussianguy.firmalife.FirmaLife.MOD_ID;
import static net.dries007.tfc.util.forge.ForgeRule.*;
import static net.dries007.tfc.util.skills.SmithingSkill.Type.GENERAL;
import static net.dries007.tfc.util.skills.SmithingSkill.Type.TOOLS;

@Mod.EventBusSubscriber(modid = MOD_ID)
public class TFCRegistry
{
    public static final ResourceLocation HALITE = new ResourceLocation(TerraFirmaCraft.MOD_ID, "halite");

    @SubscribeEvent
    public static void onPreRegisterOre(TFCRegistryEvent.RegisterPreBlock<Ore> event)
    {
        IForgeRegistry<Ore> r = event.getRegistry();
        r.registerAll(
            new Ore(HALITE)
        );
    }

    @SubscribeEvent
    public static void onPreRegisterPlant(TFCRegistryEvent.RegisterPreBlock<Plant> event)
    {
        event.getRegistry().registerAll(PlantsFL.WRAPPERS.toArray(new Plant[0]));
    }

    @SubscribeEvent
    public static void onRegisterLoomRecipeEvent(RegistryEvent.Register<LoomRecipe> event)
    {
        IForgeRegistry<LoomRecipe> r = event.getRegistry();

        r.register(new LoomRecipe(new ResourceLocation(MOD_ID, "pineapple_yarn"), IIngredient.of(ItemsFL.PINEAPPLE_YARN, 8), new ItemStack(ItemsFL.PINEAPPLE_LEATHER), 8, new ResourceLocation(MOD_ID, "textures/blocks/pineapple.png")));
    }

    @SubscribeEvent
    public static void onRegisterQuernRecipeEvent(RegistryEvent.Register<QuernRecipe> event)
    {
        IForgeRegistry<QuernRecipe> r = event.getRegistry();

        r.register(new QuernRecipe(IIngredient.of("gemHalite"), new ItemStack(ItemPowder.get(Powder.SALT), 2)).setRegistryName("halite"));
        r.register(new QuernRecipe(IIngredient.of(ItemsFL.CINNAMON), new ItemStack(ItemsFL.GROUND_CINNAMON, 2)).setRegistryName("cinnamon"));
        r.register(new QuernRecipe(IIngredient.of(ItemsFL.getFood(FoodFL.ROASTED_CHESTNUTS)), new ItemStack(ItemsFL.getFood(FoodFL.CHESTNUT_FLOUR))).setRegistryName("roasted_chestnuts"));
        r.register(new QuernRecipe(IIngredient.of(ItemFoodTFC.get(Food.SOYBEAN)), new ItemStack(ItemsFL.getFood(FoodFL.GROUND_SOYBEANS))).setRegistryName("soybean"));
        r.register(new QuernRecipe(IIngredient.of(ItemFoodTFC.get(Food.TOMATO)), new ItemStack(ItemsFL.getFood(FoodFL.TOMATO_SAUCE))).setRegistryName("tomato"));
    }

    @SubscribeEvent
    public static void onRegisterKnappingRecipeEvent(RegistryEvent.Register<KnappingRecipe> event)
    {
        event.getRegistry().registerAll(
            new KnappingRecipeSimple(KnappingType.CLAY, true, new ItemStack(BlocksFL.OVEN), "XXXXX", "XX XX", "X   X", "X   X", "XXXXX").setRegistryName("clay_oven"),
            new KnappingRecipeSimple(KnappingType.CLAY, true, new ItemStack(BlocksFL.OVEN_CHIMNEY), "XX XX", "X   X", "X   X", "X   X", "X   X").setRegistryName("clay_oven_chimney"),
            new KnappingRecipeSimple(KnappingType.CLAY, true, new ItemStack(BlocksFL.OVEN_WALL), "    X", "   XX", "   XX", "  XXX", "  XXX").setRegistryName("clay_oven_wall"),
            new KnappingRecipeSimple(KnappingType.CLAY, true, new ItemStack(ItemsFL.UNFIRED_MALLET_MOLD), "XXXXX", "     ", "   X ", "XXXXX", "XXXXX").setRegistryName("unfired_mallet_mold"),

            new KnappingRecipeFood(KnappingFL.PUMPKIN, true, new ItemStack(ItemSeedsTFC.get(StemCrop.PUMPKIN)), "XXXXX", "X   X", "X   X", "X   X", "XXXXX").setRegistryName("pumpkin_scoop"),
            new KnappingRecipeFood(KnappingFL.PUMPKIN, true, new ItemStack(ItemsFL.getFood(FoodFL.PUMPKIN_CHUNKS), 4), "XX XX", "XX XX", "     ", "XX XX", "XX XX").setRegistryName("pumpkin_chunk")
        );

        event.getRegistry().registerAll(BlocksFL.getAllJackOLanterns().stream().map(j -> new KnappingRecipeSimple(KnappingFL.PUMPKIN, true, new ItemStack(Item.getItemFromBlock(j)), j.getCarving().getCraftPattern()).setRegistryName("pumpkin_carve_" + j.getCarving().getName())).toArray(KnappingRecipe[]::new));
    }

    @SuppressWarnings("rawtypes")
    @SubscribeEvent
    public static void onRegisterBarrelRecipeEvent(RegistryEvent.Register<BarrelRecipe> event)
    {
        if (ConfigFL.General.COMPAT.removeTFC)
        {
            IForgeRegistryModifiable modRegistry = (IForgeRegistryModifiable) TFCRegistries.BARREL;
            String[] regNames = {"milk_vinegar", "curdled_milk", "cheese"};
            for (String name : regNames)
            {
                BarrelRecipe recipe = TFCRegistries.BARREL.getValue(new ResourceLocation("tfc", name));
                if (recipe != null)
                {
                    modRegistry.remove(recipe.getRegistryName());
                    if (ConfigFL.General.COMPAT.logging)
                    {
                        FirmaLife.logger.info("Removed barrel recipe from tfc:{}", name);
                    }
                }
            }
        }
        int hour = ICalendar.TICKS_IN_HOUR;
        event.getRegistry().registerAll(
            new BarrelRecipe(IIngredient.of(FluidsTFC.FRESH_WATER.get(), 100), IIngredient.of("fruitDry"), new FluidStack(FluidsFL.YEAST_STARTER.get(), 100), ItemStack.EMPTY, hour * 96).setRegistryName("yeast_from_fruit"),
            new BarrelRecipe(IIngredient.of(FluidsFL.YEAST_STARTER.get(), 100), IIngredient.of("flour"), new FluidStack(FluidsFL.YEAST_STARTER.get(), 600), ItemStack.EMPTY, hour * 12).setRegistryName("yeast_multiplication"),
            new BarrelRecipe(IIngredient.of(FluidsTFC.RUM.get(), 1000), IIngredient.of(ItemsFL.FROTHY_COCONUT), new FluidStack(FluidsFL.PINA_COLADA.get(), 1000), ItemStack.EMPTY, hour).setRegistryName("pina_colada"),
            new BarrelRecipe(IIngredient.of(FluidsTFC.HOT_WATER.get(), 250), new IngredientItemFood(IIngredient.of(ItemFoodTFC.get(Food.BEET), 8)), null, new ItemStack(Items.SUGAR), hour * 8).setRegistryName("beet_sugar"),
            new BarrelRecipe(IIngredient.of(FluidsTFC.FRESH_WATER.get(), 250), new IngredientItemFood(IIngredient.of(ItemsFL.getFood(FoodFL.GROUND_SOYBEANS), 1)), null, new ItemStack(ItemsFL.getFood(FoodFL.TOFU)), hour * 8).setRegistryName("tofu"),
            new BarrelRecipeFoodPreservation(IIngredient.of(FluidsTFC.LIMEWATER.get(), 125), IIngredient.of(new ItemStack(ItemsFL.getFood(FoodFL.PICKLED_EGG))), FoodTrait.PRESERVED, "barrel_recipe_lime").setRegistryName("pickle_egg"),
            new BarrelRecipe(IIngredient.of(FluidsTFC.MILK.get(), 2000), IIngredient.of(ItemsFL.RENNET), new FluidStack(FluidsTFC.CURDLED_MILK.get(), 2000), ItemStack.EMPTY, hour * 4).setRegistryName("curdled_milk"),
            new BarrelRecipe(IIngredient.of(FluidsFL.YAK_MILK.get(), 2000), IIngredient.of(ItemsFL.RENNET), new FluidStack(FluidsFL.CURDLED_YAK_MILK.get(), 2000), ItemStack.EMPTY, hour * 4).setRegistryName("curdled_yak_milk"),
            new BarrelRecipe(IIngredient.of(FluidsFL.GOAT_MILK.get(), 2000), IIngredient.of(ItemsFL.RENNET), new FluidStack(FluidsFL.CURDLED_GOAT_MILK.get(), 2000), ItemStack.EMPTY, hour * 4).setRegistryName("curdled_goat_milk"),
            new BarrelRecipe(IIngredient.of(FluidsFL.ZEBU_MILK.get(), 2000), IIngredient.of(ItemsFL.RENNET), new FluidStack(FluidsTFC.CURDLED_MILK.get(), 2000), ItemStack.EMPTY, hour * 4).setRegistryName("curdled_zebu_milk"),
            new BarrelRecipe(IIngredient.of(FluidsTFC.SALT_WATER.get(), 750), new IngredientItemFood(IIngredient.of(ItemsFL.getFood(FoodFL.YAK_CURD), 3)), null, new ItemStack(BlocksFL.SHOSHA_WHEEL), hour * 16).setRegistryName("shosha_wheel"),
            new BarrelRecipe(IIngredient.of(FluidsTFC.SALT_WATER.get(), 750), new IngredientItemFood(IIngredient.of(ItemsFL.getFood(FoodFL.GOAT_CURD), 3)), null, new ItemStack(BlocksFL.FETA_WHEEL), hour * 16).setRegistryName("feta_wheel"),
            new BarrelRecipe(IIngredient.of(FluidsTFC.SALT_WATER.get(), 750), new IngredientItemFood(IIngredient.of(ItemsFL.getFood(FoodFL.MILK_CURD), 3)), null, new ItemStack(BlocksFL.GOUDA_WHEEL), hour * 16).setRegistryName("gouda_wheel"),
            new BarrelRecipe(IIngredient.of(FluidsTFC.OLIVE_OIL.get(), 500), IIngredient.of("lumber"), null, new ItemStack(ItemsFL.TREATED_LUMBER), hour * 8).setRegistryName("treat_lumber")
        );
    }

    @SuppressWarnings("rawtypes")
    @SubscribeEvent
    public static void onRegisterHeatRecipeEvent(RegistryEvent.Register<HeatRecipe> event)
    {
        IForgeRegistry<HeatRecipe> r = event.getRegistry();

        r.registerAll(
            new HeatRecipeSimple(IIngredient.of(ItemsFL.UNFIRED_MALLET_MOLD), new ItemStack(ItemsFL.MALLET_MOLD), 1599.0F, Metal.Tier.TIER_I).setRegistryName("mallet_mold"),
            new HeatRecipeSimple(IIngredient.of("slice"), new ItemStack(ItemsFL.getFood(FoodFL.TOAST)), 150, 400).setRegistryName("slice"),
            new HeatRecipeSimple(IIngredient.of(ItemsFL.HONEYCOMB), new ItemStack(ItemsFL.BEESWAX), 150, 400).setRegistryName("honeycomb")
        );

        //Remove recipes
        if (ConfigFL.General.COMPAT.removeTFC)
        {
            IForgeRegistryModifiable modRegistry = (IForgeRegistryModifiable) TFCRegistries.HEAT;
            String[] regNames = {"barley_bread", "cornbread", "oat_bread", "rice_bread", "rye_bread", "wheat_bread"};
            for (String name : regNames)
            {
                HeatRecipe recipe = TFCRegistries.HEAT.getValue(new ResourceLocation("tfc", name));
                if (recipe != null)
                {
                    modRegistry.remove(recipe.getRegistryName());
                    if (ConfigFL.General.COMPAT.logging)
                        FirmaLife.logger.info("Removed heating recipe tfc:{}", name);
                }
            }
        }

    }

    @SubscribeEvent
    public static void onRecipeRegister(RegistryEvent.Register<IRecipe> event)
    {
        if (ConfigFL.General.COMPAT.removeTFC)
        {
            IForgeRegistryModifiable<IRecipe> registry = (IForgeRegistryModifiable<IRecipe>) event.getRegistry();
            String[] regNames = {"food/barley/barley_dough", "food/cornmeal/cornmeal_dough", "food/oat/oat_dough", "food/rice/rice_dough", "food/rye/rye_dough", "food/wheat/wheat_dough",
                "food/barley/barley_bread_sandwich", "food/cornmeal/cornbread_sandwich", "food/oat/oat_bread_sandwich", "food/rice/rice_bread_sandwich", "food/rye/rye_bread_sandwich", "food/wheat/wheat_bread_sandwich"};
            for (String name : regNames)
            {
                IRecipe recipe = registry.getValue(new ResourceLocation("tfc", name));
                if (recipe != null)
                {
                    registry.remove(recipe.getRegistryName());
                    if (ConfigFL.General.COMPAT.logging)
                        FirmaLife.logger.info("Removed crafting recipe tfc:{}", name);
                }

            }
        }
    }

    @SubscribeEvent
    public static void onRegisterAnvilRecipes(RegistryEvent.Register<AnvilRecipe> event)
    {
        IForgeRegistry<AnvilRecipe> r = event.getRegistry();
        for (Metal metal : TFCRegistries.METALS.getValuesCollection())
        {
            if (metal.isToolMetal())
                r.register(new AnvilRecipe(new ResourceLocation(MOD_ID, metal.toString() + "_mallet_head"), IIngredient.of(new ItemStack(ItemMetal.get(metal, Metal.ItemType.INGOT))),
                    new ItemStack(ItemsFL.getMetalMalletHead(metal)), metal.getTier(), TOOLS, PUNCH_LAST, PUNCH_SECOND_LAST, SHRINK_THIRD_LAST));
        }
        r.registerAll(new AnvilRecipe(new ResourceLocation(MOD_ID, "greenhouse_wall"), IIngredient.of(ItemMetal.get(Metal.WROUGHT_IRON, Metal.ItemType.SHEET)),
            new ItemStack(BlocksFL.GREENHOUSE_WALL, 2), Metal.WROUGHT_IRON.getTier(), GENERAL, HIT_NOT_LAST, PUNCH_NOT_LAST, SHRINK_LAST));
        r.registerAll(new AnvilRecipe(new ResourceLocation(MOD_ID, "greenhouse_roof"), IIngredient.of(ItemMetal.get(Metal.WROUGHT_IRON, Metal.ItemType.SHEET)),
            new ItemStack(BlocksFL.GREENHOUSE_ROOF, 2), Metal.WROUGHT_IRON.getTier(), GENERAL, HIT_THIRD_LAST, PUNCH_SECOND_LAST, PUNCH_LAST));
        r.registerAll(new AnvilRecipe(new ResourceLocation(MOD_ID, "greenhouse_door"), IIngredient.of(ItemMetal.get(Metal.WROUGHT_IRON, Metal.ItemType.SHEET)),
            new ItemStack(ItemsFL.ITEM_GREENHOUSE_DOOR), Metal.WROUGHT_IRON.getTier(), GENERAL, HIT_NOT_LAST, HIT_NOT_LAST, PUNCH_LAST));
        r.registerAll(new AnvilRecipe(new ResourceLocation(MOD_ID, "spout"), IIngredient.of("ingotBlackSteel"),
            new ItemStack(BlocksFL.SPOUT), Metal.WROUGHT_IRON.getTier(), GENERAL, PUNCH_THIRD_LAST, SHRINK_SECOND_LAST, HIT_LAST));
    }
}
