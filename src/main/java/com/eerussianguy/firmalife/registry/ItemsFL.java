package com.eerussianguy.firmalife.registry;

import java.util.HashMap;
import java.util.Map;

import com.google.common.base.CaseFormat;
import com.google.common.collect.ImmutableList;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.registries.IForgeRegistry;

import com.eerussianguy.firmalife.blocks.BlockFruitDoor;
import com.eerussianguy.firmalife.init.*;
import com.eerussianguy.firmalife.items.*;
import net.dries007.tfc.api.capability.food.FoodData;
import net.dries007.tfc.api.capability.size.Size;
import net.dries007.tfc.api.capability.size.Weight;
import net.dries007.tfc.api.registries.TFCRegistries;
import net.dries007.tfc.api.types.IFruitTree;
import net.dries007.tfc.api.types.Metal;
import net.dries007.tfc.objects.items.ItemMisc;
import net.dries007.tfc.objects.items.ItemSeedsTFC;
import net.dries007.tfc.objects.items.ceramics.ItemPottery;
import net.dries007.tfc.objects.items.wood.ItemWoodenBucket;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.agriculture.FruitTree;

import static com.eerussianguy.firmalife.FirmaLife.MOD_ID;
import static net.dries007.tfc.objects.CreativeTabsTFC.*;

@Mod.EventBusSubscriber(modid = MOD_ID)
@GameRegistry.ObjectHolder(MOD_ID)
public class ItemsFL
{
    @GameRegistry.ObjectHolder("unfired_mallet_mold")
    public static final ItemPottery UNFIRED_MALLET_MOLD = Helpers.getNull();
    @GameRegistry.ObjectHolder("mallet_mold")
    public static final ItemMetalMalletMold MALLET_MOLD = Helpers.getNull();
    @GameRegistry.ObjectHolder("chestnut_slice")
    public static final ItemHeatableFoodFL CHESTNUT_SLICE = Helpers.getNull();
    @GameRegistry.ObjectHolder("roasted_cocoa_beans")
    public static final ItemMisc ROASTED_COCOA_BEANS = Helpers.getNull();
    @GameRegistry.ObjectHolder("peel")
    public static final ItemMisc PEEL = Helpers.getNull();
    @GameRegistry.ObjectHolder("fruit_leaf")
    public static final ItemMisc FRUIT_LEAF = Helpers.getNull();
    @GameRegistry.ObjectHolder("cocoa_powder")
    public static final ItemMisc COCOA_POWDER = Helpers.getNull();
    @GameRegistry.ObjectHolder("cinnamon")
    public static final ItemMisc CINNAMON = Helpers.getNull();
    @GameRegistry.ObjectHolder("cinnamon_bark")
    public static final ItemMisc CINNAMON_BARK = Helpers.getNull();
    @GameRegistry.ObjectHolder("ground_cinnamon")
    public static final ItemMisc GROUND_CINNAMON = Helpers.getNull();
    @GameRegistry.ObjectHolder("cinnamon_pole")
    public static final ItemMisc CINNAMON_POLE = Helpers.getNull();
    @GameRegistry.ObjectHolder("frothy_coconut")
    public static final ItemMisc FROTHY_COCONUT = Helpers.getNull();
    @GameRegistry.ObjectHolder("greenhouse_door")
    public static final ItemGreenhouseDoor ITEM_GREENHOUSE_DOOR = Helpers.getNull();
    @GameRegistry.ObjectHolder("cracked_coconut")
    public static final ItemWoodenBucket CRACKED_COCONUT = Helpers.getNull();
    @GameRegistry.ObjectHolder("barley_flatbread")
    public static final ItemFoodFL BARLEY_FLATBREAD = Helpers.getNull();
    @GameRegistry.ObjectHolder("corn_flatbread")
    public static final ItemFoodFL CORN_FLATBREAD = Helpers.getNull();
    @GameRegistry.ObjectHolder("oat_flatbread")
    public static final ItemFoodFL OAT_FLATBREAD = Helpers.getNull();
    @GameRegistry.ObjectHolder("rye_flatbread")
    public static final ItemFoodFL RYE_FLATBREAD = Helpers.getNull();
    @GameRegistry.ObjectHolder("rice_flatbread")
    public static final ItemFoodFL RICE_FLATBREAD = Helpers.getNull();
    @GameRegistry.ObjectHolder("wheat_flatbread")
    public static final ItemFoodFL WHEAT_FLATBREAD = Helpers.getNull();
    @GameRegistry.ObjectHolder("white_chocolate_blend")
    public static final ItemMisc WHITE_BLEND = Helpers.getNull();
    @GameRegistry.ObjectHolder("dark_chocolate_blend")
    public static final ItemMisc DARK_BLEND = Helpers.getNull();
    @GameRegistry.ObjectHolder("milk_chocolate_blend")
    public static final ItemMisc MILK_BLEND = Helpers.getNull();
    @GameRegistry.ObjectHolder("dried_pineapple")
    public static final ItemDriedPineapple DRIED_PINEAPPLE = Helpers.getNull();
    @GameRegistry.ObjectHolder("pineapple_leather")
    public static final ItemMisc PINEAPPLE_LEATHER = Helpers.getNull();
    @GameRegistry.ObjectHolder("pineapple_fiber")
    public static final ItemMisc PINEAPPLE_FIBER = Helpers.getNull();
    @GameRegistry.ObjectHolder("pineapple_yarn")
    public static final ItemMisc PINEAPPLE_YARN = Helpers.getNull();
    @GameRegistry.ObjectHolder("quad_planter")
    public static final ItemBlock ITEM_QUAD_PLANTER = Helpers.getNull();
    @GameRegistry.ObjectHolder("large_planter")
    public static final ItemBlock ITEM_LARGE_PLANTER = Helpers.getNull();
    @GameRegistry.ObjectHolder("cinnamon_sapling")
    public static final ItemBlock ITEM_CINNAMON_SAPLING = Helpers.getNull();
    @GameRegistry.ObjectHolder("honeycomb")
    public static final ItemMisc HONEYCOMB = Helpers.getNull();
    @GameRegistry.ObjectHolder("honey_jar")
    public static final ItemBlockJar HONEY_JAR = Helpers.getNull();
    @GameRegistry.ObjectHolder("milk_curd")
    public static final ItemFoodFL MILK_CURD = Helpers.getNull();
    @GameRegistry.ObjectHolder("goat_curd")
    public static final ItemFoodFL GOAT_CURD = Helpers.getNull();
    @GameRegistry.ObjectHolder("yak_curd")
    public static final ItemFoodFL YAK_CURD = Helpers.getNull();
    @GameRegistry.ObjectHolder("rennet")
    public static final ItemMisc RENNET = Helpers.getNull();
    @GameRegistry.ObjectHolder("cheesecloth")
    public static final ItemCheesecloth CHEESECLOTH = Helpers.getNull();
    @GameRegistry.ObjectHolder("cheddar")
    public static final ItemFoodFL CHEDDAR = Helpers.getNull();
    @GameRegistry.ObjectHolder("chevre")
    public static final ItemFoodFL CHEVRE = Helpers.getNull();
    @GameRegistry.ObjectHolder("rajya_metok")
    public static final ItemFoodFL RAJYA_METOK = Helpers.getNull();
    @GameRegistry.ObjectHolder("gouda")
    public static final ItemFoodFL GOUDA = Helpers.getNull();
    @GameRegistry.ObjectHolder("feta")
    public static final ItemFoodFL FETA = Helpers.getNull();
    @GameRegistry.ObjectHolder("shosha")
    public static final ItemFoodFL SHOSHA = Helpers.getNull();
    @GameRegistry.ObjectHolder("jar")
    public static final ItemMisc JAR = Helpers.getNull();
    @GameRegistry.ObjectHolder("treated_lumber")
    public static final ItemMisc TREATED_LUMBER = Helpers.getNull();
    @GameRegistry.ObjectHolder("beeswax")
    public static final ItemMisc BEESWAX = Helpers.getNull();

    private static final Map<Metal, ItemMetalMalletHead> malletHeads = new HashMap<>();
    private static final Map<Fruit, Item> driedFruits = new HashMap<>();
    private static final Map<FoodFL, ItemFoodFL> foods = new HashMap<>();
    public static ItemMetalMalletMold malletMold;
    private static ImmutableList<Item> allEasyItems;
    private static ImmutableList<ItemFruitDoor> allFruitDoors;
    private static ImmutableList<Item> unused;

    public static ImmutableList<Item> getAllEasyItems()
    {
        return allEasyItems;
    }

    public static ImmutableList<ItemFruitDoor> getAllFruitDoors() { return allFruitDoors; }

    public static ItemMetalMalletHead getMetalMalletHead(Metal metal) { return malletHeads.get(metal); }

    public static Item getDriedFruit(Fruit fruit)
    {
        return driedFruits.get(fruit);
    }

    public static ItemFoodFL getFood(FoodFL food)
    {
        return foods.get(food);
    }

    public static Item getUnused(int idx)
    {
        return unused.get(idx);
    }


    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Item> event)
    {
        IForgeRegistry<Item> r = event.getRegistry();

        ImmutableList.Builder<Item> easyItems = ImmutableList.builder();
        ImmutableList.Builder<ItemFruitDoor> fruitDoors = ImmutableList.builder();
        ImmutableList.Builder<Item> unu = ImmutableList.builder();

        for (FoodFL food : FoodFL.values())
        {
            FoodData data = food.getData();
            ItemFoodFL foodItem = new ItemFoodFL(data);
            easyItems.add(register(r, food.name().toLowerCase(), foodItem, CT_FOOD));
            OreDictionary.registerOre(convert(food.name()), foodItem);
            String[] nameOverrides = food.getNameOverrides();
            if (nameOverrides != null)
            {
                for (String name : nameOverrides)
                    OreDictionary.registerOre(name, foodItem);
            }
            else if (!food.isReplacingOres())
            {
                addCategories(foodItem, data);
            }
            foods.put(food, foodItem);
        }

        easyItems.add(register(r, "unfired_mallet_mold", new ItemPottery(), CT_POTTERY));
        malletMold = register(r, "mallet_mold", new ItemMetalMalletMold("mallet"), CT_POTTERY);

        ItemDriedPineapple dryPineapple = new ItemDriedPineapple();
        easyItems.add(register(r, "dried_pineapple", dryPineapple, CT_FOOD));
        OreDictionary.registerOre("dried_pineapple", dryPineapple);

        ItemTrailMix trailMix = new ItemTrailMix();
        easyItems.add(register(r, "trail_mix", trailMix, CT_FOOD));
        OreDictionary.registerOre("categoryMeal", trailMix);

        ItemPizza pizza = new ItemPizza();
        easyItems.add(register(r, "finished_pizza", pizza, CT_FOOD));
        OreDictionary.registerOre("categoryMeal", pizza);

        //Dried Berries
        for (Fruit fruit : Fruit.values())
        {
            if (fruit.canDry())
            {
                ItemFoodFL dried = new ItemFoodFL(fruit.getDriedData());
                easyItems.add(register(r, "dried_" + fruit.name().toLowerCase(), dried, CT_FOOD));
                OreDictionary.registerOre("dried_" + fruit.name().toLowerCase(), dried);
                OreDictionary.registerOre("fruitDry", dried);
                OreDictionary.registerOre("categoryFruit", dried);
                driedFruits.put(fruit, dried);
            }
        }

        for (String grain : new String[] {"barley", "corn", "oat", "rice", "rye", "wheat"})
        {
            ItemFoodFL flatbread_dough = new ItemFoodFL(FoodDataFL.DOUGH);
            easyItems.add(register(r, grain + "_flatbread_dough", flatbread_dough, CT_FOOD));
            OreDictionary.registerOre(grain + "_flatbread_dough", flatbread_dough);
            OreDictionary.registerOre("doughFlat", flatbread_dough);

            ItemFoodFL flatbread = new ItemFoodFL(FoodDataFL.FLATBREAD);
            easyItems.add(register(r, grain + "_flatbread", flatbread, CT_FOOD));
            OreDictionary.registerOre("flatbread", flatbread);
            OreDictionary.registerOre("categoryBread", flatbread);

            ItemHeatableFoodFL slice = new ItemHeatableFoodFL(FoodDataFL.SLICE);
            easyItems.add(register(r, grain + "_slice", slice, CT_FOOD));
            OreDictionary.registerOre("slice", slice);
            OreDictionary.registerOre("categoryBread", slice);
        }
        easyItems.add(register(r, "chestnut_slice", new ItemHeatableFoodFL(FoodDataFL.SLICE), CT_FOOD));
        ItemSandwichFL sandwich = new ItemSandwichFL(FoodDataFL.SANDWICH);
        easyItems.add(register(r, "chestnut_sandwich", sandwich, CT_FOOD));
        OreDictionary.registerOre("sandwich", sandwich);
        OreDictionary.registerOre("categoryMeal", sandwich);

        //Misc Items
        easyItems.add(register(r, "rennet", new ItemMisc(Size.SMALL, Weight.LIGHT), CT_MISC));
        easyItems.add(register(r, "roasted_cocoa_beans", new ItemRoastedCocoaBeans(), CT_MISC));
        easyItems.add(register(r, "cocoa_butter", new ItemMisc(Size.SMALL, Weight.LIGHT), CT_MISC));
        easyItems.add(register(r, "cocoa_powder", new ItemMisc(Size.SMALL, Weight.LIGHT), CT_MISC));
        easyItems.add(register(r, "dark_chocolate_blend", new ItemMisc(Size.SMALL, Weight.LIGHT), CT_MISC));
        easyItems.add(register(r, "milk_chocolate_blend", new ItemMisc(Size.SMALL, Weight.LIGHT), CT_MISC));
        easyItems.add(register(r, "white_chocolate_blend", new ItemMisc(Size.SMALL, Weight.LIGHT), CT_MISC));
        easyItems.add(register(r, "peel", new ItemMisc(Size.LARGE, Weight.VERY_HEAVY), CT_MISC));
        easyItems.add(register(r, "frothy_coconut", new ItemMisc(Size.SMALL, Weight.LIGHT), CT_MISC));

        ItemMisc fruit_leaf = new ItemMisc(Size.VERY_SMALL, Weight.VERY_LIGHT);
        easyItems.add(register(r, "fruit_leaf", fruit_leaf, CT_MISC));
        OreDictionary.registerOre("fruitLeaf", fruit_leaf);

        easyItems.add(register(r, "watering_can", new ItemWateringCan(), CT_MISC));

        easyItems.add(register(r, "vanilla", new ItemMisc(Size.VERY_SMALL, Weight.VERY_LIGHT), CT_MISC));
        easyItems.add(register(r, "cinnamon_bark", new ItemMisc(Size.VERY_SMALL, Weight.VERY_LIGHT), CT_MISC));
        easyItems.add(register(r, "cinnamon", new ItemMisc(Size.VERY_SMALL, Weight.VERY_LIGHT), CT_MISC));
        easyItems.add(register(r, "ground_cinnamon", new ItemMisc(Size.VERY_SMALL, Weight.VERY_LIGHT), CT_MISC));
        easyItems.add(register(r, "greenhouse_door", new ItemGreenhouseDoor(BlocksFL.GREENHOUSE_DOOR), CT_DECORATIONS));
        easyItems.add(register(r, "quad_planter", new ItemBlock(BlocksFL.QUAD_PLANTER), CT_DECORATIONS));
        easyItems.add(register(r, "large_planter", new ItemBlock(BlocksFL.LARGE_PLANTER), CT_DECORATIONS));
        easyItems.add(register(r, "cinnamon_sapling", new ItemBlock(BlocksFL.CINNAMON_SAPLING), CT_DECORATIONS));

        easyItems.add(register(r, "pineapple_leather", new ItemMisc(Size.VERY_SMALL, Weight.VERY_LIGHT), CT_MISC));
        easyItems.add(register(r, "pineapple_fiber", new ItemMisc(Size.VERY_SMALL, Weight.VERY_LIGHT), CT_MISC));
        easyItems.add(register(r, "pineapple_yarn", new ItemMisc(Size.VERY_SMALL, Weight.VERY_LIGHT), CT_MISC));
        easyItems.add(register(r, "honeycomb", new ItemMisc(Size.VERY_SMALL, Weight.VERY_LIGHT), CT_MISC));
        easyItems.add(register(r, "honey_jar", new ItemBlockJar(BlocksFL.HONEY_JAR), CT_FOOD));
        easyItems.add(register(r, "jar", new ItemMisc(Size.VERY_SMALL, Weight.VERY_LIGHT), CT_FOOD));
        easyItems.add(register(r, "treated_lumber", new ItemMisc(Size.SMALL, Weight.MEDIUM), CT_MISC));
        easyItems.add(register(r, "beeswax", new ItemMisc(Size.VERY_SMALL, Weight.VERY_LIGHT), CT_FOOD));

        for (int i = 0; i < 5; i++)
        {
            ItemMisc item = register(r, "unused" + i, new ItemMisc(Size.VERY_SMALL, Weight.VERY_LIGHT));
            easyItems.add(item);
            unu.add(item);
        }

        ItemMisc cpole = new ItemMisc(Size.SMALL, Weight.MEDIUM);
        easyItems.add(register(r, "cinnamon_pole", cpole, CT_MISC));
        OreDictionary.registerOre("poleWooden", cpole);

        for (FruitTreeFL fruitTree : FruitTreeFL.values())
            easyItems.add(registerPole(r, fruitTree));
        for (IFruitTree fruitTree : FruitTree.values())
            easyItems.add(registerPole(r, fruitTree));
        for (BlockFruitDoor door : BlocksFL.getAllFruitDoors())
            fruitDoors.add(register(r, door.getRegistryName().getPath(), new ItemFruitDoor(door), CT_DECORATIONS));

        for (Metal metal : TFCRegistries.METALS.getValuesCollection())
            if (metal.isToolMetal())
            {
                ItemMetalMallet mallet = register(r, metal.toString() + "_mallet", new ItemMetalMallet(metal), CT_METAL);
                OreDictionary.registerOre("tool", mallet);
                easyItems.add(mallet);
                ItemMetalMalletHead head = new ItemMetalMalletHead(metal);
                easyItems.add(register(r, metal.toString() + "_mallet_head", head, CT_METAL));
                OreDictionary.registerOre("tool", head);
                malletHeads.put(metal, head);
            }

        //uses a separate model loader
        register(r, "cracked_coconut", new ItemWoodenBucket(), CT_MISC);
        register(r, "cheesecloth", new ItemCheesecloth(), CT_MISC);

        BlocksFL.getAllIBs().forEach((x) -> registerIB(r, x));
        BlocksFL.getAllInventoryIBs().forEach((x) -> easyItems.add(registerIB(r, new ItemBlock(x))));

        for (StemCrop crop : StemCrop.values())
        {
            easyItems.add(register(r, "crop/seeds/" + crop.name().toLowerCase(), new ItemSeedsTFC(crop), CT_FOOD));
        }
        allEasyItems = easyItems.build();
        allFruitDoors = fruitDoors.build();
        unused = unu.build();
    }

    private static String convert(String in)
    {
        return CaseFormat.UPPER_UNDERSCORE.converterTo(CaseFormat.LOWER_CAMEL).convert(in);
    }

    private static void addCategories(Item foodItem, FoodData data)
    {
        float[] nutrients = data.getNutrients();
        if (nutrients[0] > 0)
            OreDictionary.registerOre("categoryGrain", foodItem);
        if (nutrients[1] > 0)
            OreDictionary.registerOre("categoryFruit", foodItem);
        if (nutrients[2] > 0)
            OreDictionary.registerOre("categoryVegetable", foodItem);
        if (nutrients[4] > 0)
            OreDictionary.registerOre("categoryDairy", foodItem);
    }

    private static ItemFruitPole registerPole(IForgeRegistry<Item> r, IFruitTree fruitTree)
    {
        String name = fruitTree.getName().toLowerCase();
        ItemFruitPole pole = new ItemFruitPole(fruitTree);
        ItemFruitPole entry = register(r, name + "_pole", pole, CT_MISC);
        OreDictionary.registerOre("poleWooden", pole);
        return entry;
    }

    private static <T extends Item> T register(IForgeRegistry<Item> r, String name, T item, CreativeTabs ct)
    {
        item.setRegistryName(MOD_ID, name);
        item.setCreativeTab(ct);
        item.setTranslationKey(MOD_ID + "." + name.replace('/', '.'));
        r.register(item);
        return item;
    }

    private static <T extends Item> T register(IForgeRegistry<Item> r, String name, T item)
    {
        item.setRegistryName(MOD_ID, name);
        item.setTranslationKey(MOD_ID + "." + name.replace('/', '.'));
        r.register(item);
        return item;
    }

    private static <T extends ItemBlock> T registerIB(IForgeRegistry<Item> r, T item)
    {
        item.setRegistryName(item.getBlock().getRegistryName());
        item.setCreativeTab(item.getBlock().getCreativeTab());
        r.register(item);
        return item;
    }
}
