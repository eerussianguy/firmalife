package com.eerussianguy.firmalife.registry;

import com.eerussianguy.firmalife.blocks.BlockFruitDoor;
import com.eerussianguy.firmalife.init.FruitTreeFL;
import com.eerussianguy.firmalife.items.*;
import com.google.common.collect.ImmutableList;
import net.dries007.tfc.api.types.IFruitTree;
import net.dries007.tfc.util.OreDictionaryHelper;
import net.dries007.tfc.util.agriculture.FruitTree;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.registries.IForgeRegistry;

import com.eerussianguy.firmalife.init.FoodDataFL;
import com.eerussianguy.firmalife.init.StemCrop;
import com.eerussianguy.firmalife.items.ItemFoodFL;
import com.eerussianguy.firmalife.items.ItemGreenhouseDoor;
import com.eerussianguy.firmalife.items.ItemNutHammer;
import com.eerussianguy.firmalife.items.ItemRoastedCocoaBeans;

import net.dries007.tfc.api.capability.size.Size;
import net.dries007.tfc.api.capability.size.Weight;
import net.dries007.tfc.objects.items.ItemMisc;
import net.dries007.tfc.objects.items.ItemSeedsTFC;
import net.dries007.tfc.objects.items.wood.ItemWoodenBucket;
import net.dries007.tfc.util.Helpers;

import static com.eerussianguy.firmalife.FirmaLife.MOD_ID;
import static net.dries007.tfc.objects.CreativeTabsTFC.*;

@Mod.EventBusSubscriber(modid = MOD_ID)
@GameRegistry.ObjectHolder(MOD_ID)
public class ItemsFL
{
    @GameRegistry.ObjectHolder("cocoa_beans")
    public static final ItemFoodFL COCOA_BEANS = Helpers.getNull();
    @GameRegistry.ObjectHolder("dried_cocoa_beans")
    public static final ItemFoodFL DRIED_BANANA = Helpers.getNull();
    @GameRegistry.ObjectHolder("dried_blackberry")
    public static final ItemFoodFL DRIED_BLACKBERRY = Helpers.getNull();
    @GameRegistry.ObjectHolder("dried_blueberry")
    public static final ItemFoodFL DRIED_BLUEBERRY = Helpers.getNull();
    @GameRegistry.ObjectHolder("dried_bunch_berry")
    public static final ItemFoodFL DRIED_BUNCH_BERRY = Helpers.getNull();
    @GameRegistry.ObjectHolder("dried_cherry")
    public static final ItemFoodFL DRIED_CHERRY = Helpers.getNull();
    @GameRegistry.ObjectHolder("dried_cloud_berry")
    public static final ItemFoodFL DRIED_CLOUD_BERRY = Helpers.getNull();
    @GameRegistry.ObjectHolder("dried_cranberry")
    public static final ItemFoodFL DRIED_COCOA_BEANS = Helpers.getNull();
    @GameRegistry.ObjectHolder("dried_banana")
    public static final ItemFoodFL DRIED_CRANBERRY = Helpers.getNull();
    @GameRegistry.ObjectHolder("dried_elderberry")
    public static final ItemFoodFL DRIED_ELDERBERRY = Helpers.getNull();
    @GameRegistry.ObjectHolder("dried_gooseberry")
    public static final ItemFoodFL DRIED_GOOSEBERRY = Helpers.getNull();
    @GameRegistry.ObjectHolder("dried_green_apple")
    public static final ItemFoodFL DRIED_GREEN_APPLE = Helpers.getNull();
    @GameRegistry.ObjectHolder("dried_lemon")
    public static final ItemFoodFL DRIED_LEMON = Helpers.getNull();
    @GameRegistry.ObjectHolder("dried_olive")
    public static final ItemFoodFL DRIED_OLIVE = Helpers.getNull();
    @GameRegistry.ObjectHolder("dried_orange")
    public static final ItemFoodFL DRIED_ORANGE = Helpers.getNull();
    @GameRegistry.ObjectHolder("dried_peach")
    public static final ItemFoodFL DRIED_PEACH = Helpers.getNull();
    @GameRegistry.ObjectHolder("dried_plum")
    public static final ItemFoodFL DRIED_PLUM = Helpers.getNull();
    @GameRegistry.ObjectHolder("dried_raspberry")
    public static final ItemFoodFL DRIED_RASPBERRY = Helpers.getNull();
    @GameRegistry.ObjectHolder("dried_red_apple")
    public static final ItemFoodFL DRIED_RED_APPLE = Helpers.getNull();
    @GameRegistry.ObjectHolder("dried_snow_berry")
    public static final ItemFoodFL DRIED_SNOW_BERRY = Helpers.getNull();
    @GameRegistry.ObjectHolder("dried_strawberry")
    public static final ItemFoodFL DRIED_STRAWBERRY = Helpers.getNull();
    @GameRegistry.ObjectHolder("dried_wintergreen_berry")
    public static final ItemFoodFL DRIED_WINTERGREEN_BERRY = Helpers.getNull();
    @GameRegistry.ObjectHolder("chestnuts")
    public static final ItemFoodFL CHESTNUTS = Helpers.getNull();
    @GameRegistry.ObjectHolder("acorns")
    public static final ItemFoodFL ACORNS = Helpers.getNull();
    @GameRegistry.ObjectHolder("pinecone")
    public static final ItemFoodFL PINECONE = Helpers.getNull();
    @GameRegistry.ObjectHolder("pecan_nuts")
    public static final ItemFoodFL PECAN_NUTS = Helpers.getNull();
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
    @GameRegistry.ObjectHolder("nut_hammer_head")
    public static final ItemMisc NUT_HAMMER_HEAD = Helpers.getNull();
    @GameRegistry.ObjectHolder("pumpkin_scooped")
    public static final ItemFoodFL PUMPKIN_SCOOPED = Helpers.getNull();
    @GameRegistry.ObjectHolder("pumpkin_chunks")
    public static final ItemFoodFL PUMPKIN_CHUNKS = Helpers.getNull();

    private static ImmutableList<Item> allEasyItems;

    public static ImmutableList<Item> getAllEasyItems()
    {
        return allEasyItems;
    }

    private static ImmutableList<ItemFruitDoor> allFruitDoors;

    public static ImmutableList<ItemFruitDoor> getAllFruitDoors() { return allFruitDoors; }

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
        //Dried Berries
        easyItems.add(register(r, "dried_banana", new ItemFoodFL(FoodDataFL.DRIED_FRUIT_DECAY), CT_FOOD));
        easyItems.add(register(r, "dried_blackberry", new ItemFoodFL(FoodDataFL.DRIED_FRUIT_CATEGORY), CT_FOOD));
        easyItems.add(register(r, "dried_blueberry", new ItemFoodFL(FoodDataFL.DRIED_FRUIT_CATEGORY), CT_FOOD));
        easyItems.add(register(r, "dried_bunch_berry", new ItemFoodFL(FoodDataFL.DRIED_FRUIT_SATURATION), CT_FOOD));
        easyItems.add(register(r, "dried_cherry", new ItemFoodFL(FoodDataFL.DRIED_FRUIT_CATEGORY), CT_FOOD));
        easyItems.add(register(r, "dried_cloud_berry", new ItemFoodFL(FoodDataFL.DRIED_FRUIT_SATURATION), CT_FOOD));
        easyItems.add(register(r, "dried_cocoa_beans", new ItemFoodFL(FoodDataFL.DRIED_COCOA_BEANS), CT_FOOD));
        easyItems.add(register(r, "dried_cranberry", new ItemFoodFL(FoodDataFL.DRIED_FRUIT_DECAY), CT_FOOD));
        easyItems.add(register(r, "dried_elderberry", new ItemFoodFL(FoodDataFL.DRIED_FRUIT_CATEGORY), CT_FOOD));
        easyItems.add(register(r, "dried_gooseberry", new ItemFoodFL(FoodDataFL.DRIED_FRUIT_SATURATION), CT_FOOD));
        easyItems.add(register(r, "dried_green_apple", new ItemFoodFL(FoodDataFL.DRIED_FRUIT_DECAY), CT_FOOD));
        easyItems.add(register(r, "dried_lemon", new ItemFoodFL(FoodDataFL.DRIED_FRUIT_DECAY), CT_FOOD));
        easyItems.add(register(r, "dried_olive", new ItemFoodFL(FoodDataFL.DRIED_FRUIT_DECAY), CT_FOOD));
        easyItems.add(register(r, "dried_orange", new ItemFoodFL(FoodDataFL.DRIED_FRUIT_DECAY), CT_FOOD));
        easyItems.add(register(r, "dried_peach", new ItemFoodFL(FoodDataFL.DRIED_FRUIT_SATURATION), CT_FOOD));
        easyItems.add(register(r, "dried_plum", new ItemFoodFL(FoodDataFL.DRIED_FRUIT_SATURATION), CT_FOOD));
        easyItems.add(register(r, "dried_raspberry", new ItemFoodFL(FoodDataFL.DRIED_FRUIT_SATURATION), CT_FOOD));
        easyItems.add(register(r, "dried_red_apple", new ItemFoodFL(FoodDataFL.DRIED_FRUIT_DECAY), CT_FOOD));
        easyItems.add(register(r, "dried_snow_berry", new ItemFoodFL(FoodDataFL.DRIED_FRUIT_CATEGORY), CT_FOOD));
        easyItems.add(register(r, "dried_strawberry", new ItemFoodFL(FoodDataFL.DRIED_FRUIT_SATURATION), CT_FOOD));
        easyItems.add(register(r, "dried_wintergreen_berry", new ItemFoodFL(FoodDataFL.DRIED_FRUIT_CATEGORY), CT_FOOD));

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

        //Misc Items
        easyItems.add(register(r, "roasted_cocoa_beans", new ItemRoastedCocoaBeans(), CT_MISC));
        easyItems.add(register(r, "cocoa_butter", new ItemMisc(Size.SMALL, Weight.LIGHT), CT_MISC));
        easyItems.add(register(r, "cocoa_powder", new ItemMisc(Size.SMALL, Weight.LIGHT), CT_MISC));
        easyItems.add(register(r, "dark_chocolate_blend", new ItemMisc(Size.SMALL, Weight.LIGHT), CT_MISC));
        easyItems.add(register(r, "milk_chocolate_blend", new ItemMisc(Size.SMALL, Weight.LIGHT), CT_MISC));
        easyItems.add(register(r, "white_chocolate_blend", new ItemMisc(Size.SMALL, Weight.LIGHT), CT_MISC));
        easyItems.add(register(r, "peel", new ItemMisc(Size.LARGE, Weight.VERY_HEAVY), CT_MISC));
        easyItems.add(register(r, "nut_hammer", new ItemNutHammer(), CT_MISC));
        easyItems.add(register(r, "nut_hammer_head", new ItemMisc(Size.LARGE, Weight.VERY_HEAVY), CT_MISC));

        ItemMisc fruit_leaf = new ItemMisc(Size.VERY_SMALL, Weight.VERY_LIGHT);
        easyItems.add(register(r, "fruit_leaf", fruit_leaf, CT_MISC));
        OreDictionary.registerOre("fruitLeaf", fruit_leaf); //todo: Use our OreDict helper

        easyItems.add(register(r, "planter", new ItemMisc(Size.NORMAL, Weight.MEDIUM), CT_MISC));
        easyItems.add(register(r, "vanilla", new ItemMisc(Size.VERY_SMALL, Weight.VERY_LIGHT), CT_MISC));
        easyItems.add(register(r, "cinnamon_bark", new ItemMisc(Size.VERY_SMALL, Weight.VERY_LIGHT), CT_MISC));
        easyItems.add(register(r, "cinnamon", new ItemMisc(Size.VERY_SMALL, Weight.VERY_LIGHT), CT_MISC));
        easyItems.add(register(r, "ground_cinnamon", new ItemMisc(Size.VERY_SMALL, Weight.VERY_LIGHT), CT_MISC));
        easyItems.add(register(r, "cinnamon_pole", new ItemMisc(Size.SMALL, Weight.MEDIUM), CT_MISC));
        easyItems.add(register(r, "greenhouse_door", new ItemGreenhouseDoor(BlocksFL.BLOCK_GREENHOUSE_DOOR), CT_DECORATIONS));

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
