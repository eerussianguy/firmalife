package com.eerussianguy.firmalife.registry;

import java.util.HashMap;
import java.util.Map;

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
import com.eerussianguy.firmalife.init.FoodDataFL;
import com.eerussianguy.firmalife.init.Fruit;
import com.eerussianguy.firmalife.init.FruitTreeFL;
import com.eerussianguy.firmalife.init.StemCrop;
import com.eerussianguy.firmalife.items.*;
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
    @GameRegistry.ObjectHolder("cocoa_beans")
    public static final ItemFoodFL COCOA_BEANS = Helpers.getNull();
    @GameRegistry.ObjectHolder("curd")
    public static final ItemFoodFL CURD = Helpers.getNull();
    @GameRegistry.ObjectHolder("goat_curd")
    public static final ItemFoodFL GOAT_CURD = Helpers.getNull();
    @GameRegistry.ObjectHolder("yak_curd")
    public static final ItemFoodFL YAK_CURD = Helpers.getNull();
    @GameRegistry.ObjectHolder("zebu_curd")
    public static final ItemFoodFL ZEBU_CURD = Helpers.getNull();
    @GameRegistry.ObjectHolder("chestnuts")
    public static final ItemFoodFL CHESTNUTS = Helpers.getNull();
    @GameRegistry.ObjectHolder("roasted_chestnuts")
    public static final ItemFoodFL ROASTED_CHESTNUTS = Helpers.getNull();
    @GameRegistry.ObjectHolder("acorn_fruit")
    public static final ItemFoodFL ACORN_FRUIT = Helpers.getNull();
    @GameRegistry.ObjectHolder("acorns")
    public static final ItemFoodFL ACORNS = Helpers.getNull();
    @GameRegistry.ObjectHolder("pinecone")
    public static final ItemFoodFL PINECONE = Helpers.getNull();
    @GameRegistry.ObjectHolder("pine_nuts")
    public static final ItemFoodFL PINE_NUTS = Helpers.getNull();
    @GameRegistry.ObjectHolder("pecan_nuts")
    public static final ItemFoodFL PECAN_NUTS = Helpers.getNull();
    @GameRegistry.ObjectHolder("pecans")
    public static final ItemFoodFL PECANS = Helpers.getNull();
    @GameRegistry.ObjectHolder("rennet")
    public static final ItemMisc RENNET = Helpers.getNull();
    @GameRegistry.ObjectHolder("cheesecloth")
    public static final ItemMisc CHEESECLOTH = Helpers.getNull();
    @GameRegistry.ObjectHolder("curd_cloth")
    public static final ItemCheesecloth CURD_CLOTH = Helpers.getNull();
    @GameRegistry.ObjectHolder("yak_curd_cloth")
    public static final ItemCheesecloth YAK_CURD_CLOTH = Helpers.getNull();
    @GameRegistry.ObjectHolder("goat_curd_cloth")
    public static final ItemCheesecloth GOAT_CURD_CLOTH = Helpers.getNull();
    @GameRegistry.ObjectHolder("zebu_curd_cloth")
    public static final ItemCheesecloth ZEBU_CURD_CLOTH = Helpers.getNull();
    @GameRegistry.ObjectHolder("dirty_cheesecloth")
    public static final ItemMisc DIRTY_CHEESECLOTH = Helpers.getNull();
    @GameRegistry.ObjectHolder("roasted_cocoa_beans")
    public static final ItemMisc ROASTED_COCOA_BEANS = Helpers.getNull();
    @GameRegistry.ObjectHolder("peel")
    public static final ItemMisc PEEL = Helpers.getNull();
    @GameRegistry.ObjectHolder("fruit_leaf")
    public static final ItemMisc FRUIT_LEAF = Helpers.getNull();
    @GameRegistry.ObjectHolder("cocoa_powder")
    public static final ItemMisc COCOA_POWDER = Helpers.getNull();
    @GameRegistry.ObjectHolder("vanilla")
    public static final ItemMisc VANILLA = Helpers.getNull();
    @GameRegistry.ObjectHolder("cinnamon")
    public static final ItemMisc CINNAMON = Helpers.getNull();
    @GameRegistry.ObjectHolder("cinnamon_bark")
    public static final ItemMisc CINNAMON_BARK = Helpers.getNull();
    @GameRegistry.ObjectHolder("ground_cinnamon")
    public static final ItemMisc GROUND_CINNAMON = Helpers.getNull();
    @GameRegistry.ObjectHolder("cinnamon_pole")
    public static final ItemMisc CINNAMON_POLE = Helpers.getNull();
    @GameRegistry.ObjectHolder("planter")
    public static final ItemMisc PLANTER = Helpers.getNull();
    @GameRegistry.ObjectHolder("greenhouse_door")
    public static final ItemGreenhouseDoor ITEM_GREENHOUSE_DOOR = Helpers.getNull();
    @GameRegistry.ObjectHolder("cracked_coconut")
    public static final ItemWoodenBucket CRACKED_COCONUT = Helpers.getNull();
    @GameRegistry.ObjectHolder("coconut")
    public static final ItemFoodFL COCONUT = Helpers.getNull();
    @GameRegistry.ObjectHolder("pumpkin_scooped")
    public static final ItemFoodFL PUMPKIN_SCOOPED = Helpers.getNull();
    @GameRegistry.ObjectHolder("pumpkin_chunks")
    public static final ItemFoodFL PUMPKIN_CHUNKS = Helpers.getNull();
    @GameRegistry.ObjectHolder("dried_cocoa_beans")
    public static final ItemFoodFL DRIED_COCOA_BEANS = Helpers.getNull();
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

    private static ImmutableList<Item> allEasyItems;

    public static ImmutableList<Item> getAllEasyItems()
    {
        return allEasyItems;
    }

    private static ImmutableList<ItemFruitDoor> allFruitDoors;

    public static ImmutableList<ItemFruitDoor> getAllFruitDoors() { return allFruitDoors; }

    private static Map<Metal, ItemMetalMalletHead> malletHeads = new HashMap<>();

    public static ItemMetalMalletHead getMetalMalletHead(Metal metal) { return malletHeads.get(metal); }

    private static Map<Fruit, Item> driedFruits = new HashMap<>();

    public static Item getDriedFruit(Fruit fruit)
    {
        return driedFruits.get(fruit);
    }

    public static ItemMetalMalletMold malletMold;


    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Item> event)
    {
        IForgeRegistry<Item> r = event.getRegistry();

        ImmutableList.Builder<Item> easyItems = ImmutableList.builder();
        ImmutableList.Builder<ItemFruitDoor> fruitDoors = ImmutableList.builder();
        //Foods
        easyItems.add(register(r, "dark_chocolate", new ItemFoodFL(FoodDataFL.CHOCOLATE), CT_FOOD));
        easyItems.add(register(r, "milk_chocolate", new ItemFoodFL(FoodDataFL.CHOCOLATE), CT_FOOD));
        easyItems.add(register(r, "white_chocolate", new ItemFoodFL(FoodDataFL.CHOCOLATE), CT_FOOD));
        easyItems.add(register(r, "cocoa_beans", new ItemFoodFL(FoodDataFL.COCOA_BEANS), CT_FOOD));
        easyItems.add(register(r, "pumpkin_scooped", new ItemFoodFL(FoodDataFL.PUMPKIN), CT_FOOD));
        easyItems.add(register(r, "pumpkin_chunks", new ItemFoodFL(FoodDataFL.PUMPKIN), CT_FOOD));

        easyItems.add(register(r, "curd", new ItemFoodFL(FoodDataFL.MILK_CURD), CT_FOOD));
        easyItems.add(register(r, "goat_curd", new ItemFoodFL(FoodDataFL.MILK_CURD), CT_FOOD));
        easyItems.add(register(r, "yak_curd", new ItemFoodFL(FoodDataFL.MILK_CURD), CT_FOOD));
        easyItems.add(register(r, "zebu_curd", new ItemFoodFL(FoodDataFL.MILK_CURD), CT_FOOD));
        easyItems.add(register(r, "cheddar", new ItemFoodFL(FoodDataFL.CHEESE_SALTED), CT_FOOD));
        easyItems.add(register(r, "chevre", new ItemFoodFL(FoodDataFL.CHEESE_SALTED), CT_FOOD));
        easyItems.add(register(r, "rajya_metok", new ItemFoodFL(FoodDataFL.CHEESE_SALTED), CT_FOOD));
        easyItems.add(register(r, "gouda", new ItemFoodFL(FoodDataFL.CHEESE_BRINED), CT_FOOD));
        easyItems.add(register(r, "feta", new ItemFoodFL(FoodDataFL.CHEESE_BRINED), CT_FOOD));
        easyItems.add(register(r, "shosha", new ItemFoodFL(FoodDataFL.CHEESE_BRINED), CT_FOOD));

        easyItems.add(register(r, "unfired_mallet_mold", new ItemPottery(), CT_POTTERY));
        malletMold = register(r, "mallet_mold", new ItemMetalMalletMold("mallet"), CT_POTTERY);

        for(Metal metal : TFCRegistries.METALS.getValuesCollection())
            if(metal.isToolMetal())
            {
                easyItems.add(register(r, metal.toString() + "_mallet", new ItemMetalMallet(metal), CT_METAL));
                ItemMetalMalletHead head = new ItemMetalMalletHead(metal);
                easyItems.add(register(r, metal.toString() + "_mallet_head", head, CT_METAL));
                malletHeads.put(metal, head);
            }

        easyItems.add(register(r, "dried_cocoa_beans", new ItemFoodFL(FoodDataFL.DRIED_COCOA_BEANS), CT_FOOD));

        //Dried Berries
        for (Fruit fruit : Fruit.values())
        {
            if (fruit.canDry())
            {
                ItemFoodFL dried = new ItemFoodFL(fruit.getDriedData());
                easyItems.add(register(r, "dried_" + fruit.name().toLowerCase(), dried, CT_FOOD));
                OreDictionary.registerOre("dried_" + fruit.name().toLowerCase(), dried);
                OreDictionary.registerOre("fruitDry", dried);
                driedFruits.put(fruit, dried);
            }
        }

        easyItems.add(register(r, "acorn_fruit", new ItemFoodFL(FoodDataFL.UNCRACKED_NUT), CT_FOOD));
        easyItems.add(register(r, "acorns", new ItemFoodFL(FoodDataFL.NUT), CT_FOOD));
        easyItems.add(register(r, "chestnuts", new ItemFoodFL(FoodDataFL.UNCRACKED_NUT), CT_FOOD));
        easyItems.add(register(r, "roasted_chestnuts", new ItemFoodFL(FoodDataFL.ROASTED_NUT), CT_FOOD));
        easyItems.add(register(r, "chestnut_dough", new ItemFoodFL(FoodDataFL.DOUGH), CT_FOOD));
        easyItems.add(register(r, "chestnut_flour", new ItemFoodFL(FoodDataFL.FLOUR), CT_FOOD));
        easyItems.add(register(r, "pecan_nuts", new ItemFoodFL(FoodDataFL.UNCRACKED_NUT), CT_FOOD));
        easyItems.add(register(r, "pecans", new ItemFoodFL(FoodDataFL.NUT), CT_FOOD));
        easyItems.add(register(r, "pinecone", new ItemFoodFL(FoodDataFL.UNCRACKED_NUT), CT_FOOD));
        easyItems.add(register(r, "pine_nuts", new ItemFoodFL(FoodDataFL.NUT), CT_FOOD));
        easyItems.add(register(r, "coconut", new ItemFoodFL(FoodDataFL.NUT), CT_FOOD));
        for (String grain : new String[] {"barley", "corn", "oat", "rice", "rye", "wheat"})
        {
            ItemFoodFL flatbread_dough = new ItemFoodFL(FoodDataFL.DOUGH);
            easyItems.add(register(r, grain + "_flatbread_dough", flatbread_dough, CT_FOOD));
            OreDictionary.registerOre(grain + "_flatbread_dough", flatbread_dough);
            OreDictionary.registerOre("doughFlat", flatbread_dough);

            ItemFoodFL flatbread = new ItemFoodFL(FoodDataFL.FLATBREAD);
            easyItems.add(register(r, grain + "_flatbread", flatbread, CT_FOOD));
            OreDictionary.registerOre("flatbread", flatbread);
        }

        //Misc Items
        easyItems.add(register(r, "rennet", new ItemMisc(Size.SMALL, Weight.LIGHT), CT_MISC));
        easyItems.add(register(r, "cheesecloth", new ItemMisc(Size.NORMAL, Weight.VERY_LIGHT), CT_MISC));
        easyItems.add(register(r, "curd_cloth", new ItemCheesecloth(), CT_MISC));
        easyItems.add(register(r, "yak_curd_cloth", new ItemCheesecloth(), CT_MISC));
        easyItems.add(register(r, "goat_curd_cloth", new ItemCheesecloth(), CT_MISC));
        easyItems.add(register(r, "zebu_curd_cloth", new ItemCheesecloth(), CT_MISC));
        easyItems.add(register(r, "dirty_cheesecloth", new ItemMisc(Size.NORMAL, Weight.MEDIUM), CT_MISC));
        easyItems.add(register(r, "roasted_cocoa_beans", new ItemRoastedCocoaBeans(), CT_MISC));
        easyItems.add(register(r, "cocoa_butter", new ItemMisc(Size.SMALL, Weight.LIGHT), CT_MISC));
        easyItems.add(register(r, "cocoa_powder", new ItemMisc(Size.SMALL, Weight.LIGHT), CT_MISC));
        easyItems.add(register(r, "dark_chocolate_blend", new ItemMisc(Size.SMALL, Weight.LIGHT), CT_MISC));
        easyItems.add(register(r, "milk_chocolate_blend", new ItemMisc(Size.SMALL, Weight.LIGHT), CT_MISC));
        easyItems.add(register(r, "white_chocolate_blend", new ItemMisc(Size.SMALL, Weight.LIGHT), CT_MISC));
        easyItems.add(register(r, "peel", new ItemMisc(Size.LARGE, Weight.VERY_HEAVY), CT_MISC));

        ItemMisc fruit_leaf = new ItemMisc(Size.VERY_SMALL, Weight.VERY_LIGHT);
        easyItems.add(register(r, "fruit_leaf", fruit_leaf, CT_MISC));
        OreDictionary.registerOre("fruitLeaf", fruit_leaf); //todo: Use our OreDict helper

        easyItems.add(register(r, "planter", new ItemMisc(Size.NORMAL, Weight.MEDIUM), CT_MISC));
        easyItems.add(register(r, "vanilla", new ItemMisc(Size.VERY_SMALL, Weight.VERY_LIGHT), CT_MISC));
        easyItems.add(register(r, "cinnamon_bark", new ItemMisc(Size.VERY_SMALL, Weight.VERY_LIGHT), CT_MISC));
        easyItems.add(register(r, "cinnamon", new ItemMisc(Size.VERY_SMALL, Weight.VERY_LIGHT), CT_MISC));
        easyItems.add(register(r, "ground_cinnamon", new ItemMisc(Size.VERY_SMALL, Weight.VERY_LIGHT), CT_MISC));
        easyItems.add(register(r, "greenhouse_door", new ItemGreenhouseDoor(BlocksFL.BLOCK_GREENHOUSE_DOOR), CT_DECORATIONS));

        ItemMisc cpole = new ItemMisc(Size.SMALL, Weight.MEDIUM);
        easyItems.add(register(r, "cinnamon_pole", cpole, CT_MISC));
        OreDictionary.registerOre("poleCinnamon", cpole);

        for (FruitTreeFL fruitTree : FruitTreeFL.values())
        {
            String name = fruitTree.getName().toLowerCase();
            ItemMisc pole = new ItemMisc(Size.SMALL, Weight.MEDIUM);
            easyItems.add(register(r, name + "_pole", pole, CT_MISC));
            //todo: Use our OreDict helper
            OreDictionary.registerOre("pole" + name.substring(0,1).toUpperCase() + name.substring(1).toLowerCase(), pole);
        }

        for (IFruitTree fruitTree : FruitTree.values())
        {
            String name = fruitTree.getName().toLowerCase();
            ItemMisc pole = new ItemMisc(Size.SMALL, Weight.MEDIUM);
            easyItems.add(register(r, name + "_pole", pole, CT_MISC));
            //todo: Use our OreDict helper
            OreDictionary.registerOre("pole" + name.substring(0,1).toUpperCase() + name.substring(1).toLowerCase(), pole);
        }
        for (BlockFruitDoor door : BlocksFL.getAllFruitDoors())
            fruitDoors.add(register(r, door.getRegistryName().getPath(), new ItemFruitDoor(door), CT_DECORATIONS));

        //uses a separate model loader
        register(r, "cracked_coconut", new ItemWoodenBucket(), CT_MISC);

        BlocksFL.getAllIBs().forEach((x) -> {
            registerIB(r, x);
        });

        for (StemCrop crop : StemCrop.values())
        {
            easyItems.add(register(r, "crop/seeds/" + crop.name().toLowerCase(), new ItemSeedsTFC(crop), CT_FOOD));
        }
        allEasyItems = easyItems.build();
        allFruitDoors = fruitDoors.build();
    }

    private static <T extends Item> T register(IForgeRegistry<Item> r, String name, T item, CreativeTabs ct)
    {
        item.setRegistryName(MOD_ID, name);
        item.setCreativeTab(ct);
        item.setTranslationKey(MOD_ID + "." + name.replace('/', '.'));
        r.register(item);
        return item;
    }

    private static void registerIB(IForgeRegistry<Item> r, ItemBlock item)
    {
        item.setRegistryName(item.getBlock().getRegistryName());
        item.setCreativeTab(item.getBlock().getCreativeTab());
        r.register(item);
    }
}
