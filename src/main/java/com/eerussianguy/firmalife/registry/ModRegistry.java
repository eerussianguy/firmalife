package com.eerussianguy.firmalife.registry;

import com.google.common.collect.ImmutableList;
import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;
import net.minecraftforge.registries.RegistryBuilder;

import com.eerussianguy.firmalife.blocks.*;
import com.eerussianguy.firmalife.init.*;
import com.eerussianguy.firmalife.items.ItemFoodFL;
import com.eerussianguy.firmalife.items.ItemGreenhouseDoor;
import com.eerussianguy.firmalife.items.ItemRoastedCocoaBeans;
import com.eerussianguy.firmalife.te.TELeafMat;
import com.eerussianguy.firmalife.te.TEOven;
import com.eerussianguy.firmalife.te.TEQuadPlanter;
import net.dries007.tfc.api.capability.size.Size;
import net.dries007.tfc.api.capability.size.Weight;
import net.dries007.tfc.objects.blocks.agriculture.BlockFruitTreeBranch;
import net.dries007.tfc.objects.blocks.agriculture.BlockFruitTreeLeaves;
import net.dries007.tfc.objects.blocks.agriculture.BlockFruitTreeSapling;
import net.dries007.tfc.objects.blocks.agriculture.BlockFruitTreeTrunk;
import net.dries007.tfc.objects.items.ItemMisc;
import net.dries007.tfc.objects.items.itemblock.ItemBlockTFC;
import net.dries007.tfc.util.Helpers;

import static com.eerussianguy.firmalife.FirmaLife.MOD_ID;
import static net.dries007.tfc.objects.CreativeTabsTFC.*;

@Mod.EventBusSubscriber(modid = MOD_ID)
@GameRegistry.ObjectHolder(MOD_ID)
public class ModRegistry
{
    @GameRegistry.ObjectHolder("cocoa_beans")
    public static final ItemFoodFL COCOA_BEANS = Helpers.getNull();
    @GameRegistry.ObjectHolder("dried_cocoa_beans")
    public static final ItemMisc DRIED_COCOA_BEANS = Helpers.getNull();
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
    @GameRegistry.ObjectHolder("planter")
    public static final ItemMisc PLANTER = Helpers.getNull();
    @GameRegistry.ObjectHolder("greenhouse_door")
    public static final ItemGreenhouseDoor ITEM_GREENHOUSE_DOOR = Helpers.getNull();

    @GameRegistry.ObjectHolder("oven")
    public static final BlockOven OVEN = Helpers.getNull();
    @GameRegistry.ObjectHolder("oven_wall")
    public static final BlockOvenWall OVEN_WALL = Helpers.getNull();
    @GameRegistry.ObjectHolder("oven_chimney")
    public static final BlockOvenChimney OVEN_CHIMNEY = Helpers.getNull();
    @GameRegistry.ObjectHolder("leaf_mat")
    public static final BlockLeafMat LEAF_MAT = Helpers.getNull();
    @GameRegistry.ObjectHolder("cinnamon_log")
    public static final BlockCinnamonLog CINNAMON_LOG = Helpers.getNull();
    @GameRegistry.ObjectHolder("cinnamon_leaves")
    public static final BlockCinnamonLeaves CINNAMON_LEAVES = Helpers.getNull();
    @GameRegistry.ObjectHolder("cinnamon_sapling")
    public static final BlockCinnamonSapling CINNAMON_SAPLING = Helpers.getNull();

    private static ImmutableList<Item> allEasyItems;
    private static ImmutableList<ItemBlock> allIBs;

    private static ImmutableList<Block> allNormalIBs = Helpers.getNull();
    private static ImmutableList<BlockFruitTreeLeaves> allFruitLeaves = Helpers.getNull();
    private static ImmutableList<BlockFruitTreeSapling> allFruitSaps = Helpers.getNull();
    private static ImmutableList<BlockPlanter> allPlanters = Helpers.getNull();
    private static ImmutableList<BlockGreenhouseDoor> allGreenhouseDoors = Helpers.getNull();

    public static ImmutableList<Item> getAllEasyItems()
    {
        return allEasyItems;
    }

    public static ImmutableList<ItemBlock> getAllIBs()
    {
        return allIBs;
    }

    public static ImmutableList<Block> getAllNormalIBs() { return allNormalIBs; }

    public static ImmutableList<BlockFruitTreeLeaves> getAllFruitLeaves()
    {
        return allFruitLeaves;
    }

    public static ImmutableList<BlockFruitTreeSapling> getAllFruitSaps()
    {
        return allFruitSaps;
    }

    public static ImmutableList<BlockPlanter> getAllPlanters()
    {
        return allPlanters;
    }

    public static ImmutableList<BlockGreenhouseDoor> getAllGreenhouseDoors()
    {
        return allGreenhouseDoors;
    }

    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Item> event) {
        IForgeRegistry<Item> r = event.getRegistry();

        ImmutableList.Builder<Item> easyItems = ImmutableList.builder();
        //Foods
        easyItems.add(register(r, "dark_chocolate", new ItemFoodFL(FoodDataFL.CHOCOLATE), CT_FOOD));
        easyItems.add(register(r, "milk_chocolate", new ItemFoodFL(FoodDataFL.CHOCOLATE), CT_FOOD));
        easyItems.add(register(r, "white_chocolate", new ItemFoodFL(FoodDataFL.CHOCOLATE), CT_FOOD));
        easyItems.add(register(r, "cocoa_beans", new ItemFoodFL(FoodDataFL.COCOA_BEANS), CT_FOOD));
        //Misc Items
        easyItems.add(register(r, "dried_cocoa_beans", new ItemMisc(Size.SMALL, Weight.LIGHT), CT_MISC));
        easyItems.add(register(r, "roasted_cocoa_beans", new ItemRoastedCocoaBeans(), CT_MISC));
        easyItems.add(register(r, "cocoa_butter", new ItemMisc(Size.SMALL, Weight.LIGHT), CT_MISC));
        easyItems.add(register(r, "cocoa_powder", new ItemMisc(Size.SMALL, Weight.LIGHT), CT_MISC));
        easyItems.add(register(r, "dark_chocolate_blend", new ItemMisc(Size.SMALL, Weight.LIGHT), CT_MISC));
        easyItems.add(register(r, "milk_chocolate_blend", new ItemMisc(Size.SMALL, Weight.LIGHT), CT_MISC));
        easyItems.add(register(r, "white_chocolate_blend", new ItemMisc(Size.SMALL, Weight.LIGHT), CT_MISC));
        easyItems.add(register(r, "peel", new ItemMisc(Size.LARGE, Weight.VERY_HEAVY), CT_MISC));
        easyItems.add(register(r, "fruit_leaf", new ItemMisc(Size.VERY_SMALL, Weight.VERY_LIGHT), CT_MISC));
        easyItems.add(register(r, "planter", new ItemMisc(Size.NORMAL, Weight.MEDIUM), CT_MISC));
        easyItems.add(register(r, "vanilla", new ItemMisc(Size.VERY_SMALL, Weight.VERY_LIGHT), CT_MISC));
        easyItems.add(register(r, "cinnamon_bark", new ItemMisc(Size.VERY_SMALL, Weight.VERY_LIGHT), CT_MISC));
        easyItems.add(register(r, "cinnamon", new ItemMisc(Size.VERY_SMALL, Weight.VERY_LIGHT), CT_MISC));
        easyItems.add(register(r, "ground_cinnamon", new ItemMisc(Size.VERY_SMALL, Weight.VERY_LIGHT), CT_MISC));

        allGreenhouseDoors.forEach((x) -> {
            easyItems.add(register(r, "greenhouse_door", new ItemGreenhouseDoor(x), CT_DECORATIONS));
        });
        allEasyItems = easyItems.build();

        ModRegistry.getAllIBs().forEach((x) -> {
            registerIB(r, x);
        });
    }

    @SubscribeEvent
    public static void registerBlocks(RegistryEvent.Register<Block> event)
    {
        IForgeRegistry<Block> r = event.getRegistry();

        ImmutableList.Builder<ItemBlock> IBs = ImmutableList.builder();
        ImmutableList.Builder<Block> NormalIBs = ImmutableList.builder();
        ImmutableList.Builder<BlockFruitTreeLeaves> fruitLeaves = ImmutableList.builder();
        ImmutableList.Builder<BlockFruitTreeSapling> fruitSaps = ImmutableList.builder();
        ImmutableList.Builder<BlockPlanter> planters = ImmutableList.builder();
        ImmutableList.Builder<BlockGreenhouseDoor> greenhouseDoor = ImmutableList.builder();
        for (FruitTreeFL fruitTree : FruitTreeFL.values())
        {
            String name = fruitTree.getName().toLowerCase();
            register(r, name + "_branch", new BlockFruitTreeBranch(fruitTree));
            fruitLeaves.add(register(r, name + "_leaves", new BlockFruitTreeLeaves(fruitTree), CT_WOOD));
            fruitSaps.add(register(r, name + "_sapling", new BlockFruitTreeSapling(fruitTree), CT_WOOD));
            register(r, name + "_trunk", new BlockFruitTreeTrunk(fruitTree));
        }
        NormalIBs.add(register(r, "oven", new BlockOven(), CT_DECORATIONS));
        NormalIBs.add(register(r, "oven_wall", new BlockOvenWall(), CT_DECORATIONS));
        NormalIBs.add(register(r, "oven_chimney", new BlockOvenChimney(), CT_DECORATIONS));
        NormalIBs.add(register(r, "leaf_mat", new BlockLeafMat(), CT_DECORATIONS));
        NormalIBs.add(register(r, "cinnamon_log", new BlockCinnamonLog(), CT_WOOD));
        NormalIBs.add(register(r, "cinnamon_leaves", new BlockCinnamonLeaves(), CT_WOOD));
        NormalIBs.add(register(r, "cinnamon_sapling", new BlockCinnamonSapling(), CT_WOOD));
        NormalIBs.add(register(r, "greenhouse_wall", new BlockGreenhouseWall(), CT_DECORATIONS));
        NormalIBs.add(register(r, "greenhouse_roof", new BlockGreenhouseRoof(), CT_DECORATIONS));
        NormalIBs.add(register(r, "climate_station", new BlockClimateStation(), CT_DECORATIONS));
        NormalIBs.add(register(r, "quad_planter", new BlockQuadPlanter(), CT_DECORATIONS));
        planters.add(register(r, "vanilla_planter", new BlockPlanter(() -> ModRegistry.VANILLA, PlantsFL.VANILLA_PLANT, 1), CT_FLORA));
        greenhouseDoor.add(register(r, "greenhouse_door", new BlockGreenhouseDoor(), CT_DECORATIONS));

        register(TEOven.class, "oven");
        register(TEQuadPlanter.class, "quad_planter");
        register(TELeafMat.class, "leaf_mat");

        allNormalIBs = NormalIBs.build();
        allNormalIBs.forEach((x) -> {
            IBs.add(new ItemBlockTFC(x));
        });
        allFruitLeaves = fruitLeaves.build();
        allFruitLeaves.forEach((x) -> {
            IBs.add(new ItemBlockTFC(x));
        });
        allFruitSaps = fruitSaps.build();
        allFruitSaps.forEach((x) -> {
            IBs.add(new ItemBlockTFC(x));
        });
        allPlanters = planters.build();
        allPlanters.forEach((x) -> {
            IBs.add(new ItemBlockTFC(x));
        });
        allGreenhouseDoors = greenhouseDoor.build();

        allIBs = IBs.build();
    }

    @SubscribeEvent
    public static void onNewRegistryEvent(RegistryEvent.NewRegistry event)
    {
        newRegistry(RegNames.OVEN_RECIPE, OvenRecipe.class);
        newRegistry(RegNames.DRYING_RECIPE, DryingRecipe.class);
        newRegistry(RegNames.PLANTER_QUAD_REGISTRY, PlanterRegistry.class);
    }

    private static <T extends Block> T register(IForgeRegistry<Block> r, String name, T block, CreativeTabs ct)
    {
        block.setCreativeTab(ct);
        return register(r, name, block);
    }

    private static <T extends Block> T register(IForgeRegistry<Block> r, String name, T block)
    {
        block.setRegistryName(MOD_ID, name);
        block.setTranslationKey(MOD_ID + "." + name.replace('/', '.'));
        r.register(block);
        return block;
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

    private static <T extends TileEntity> void register(Class<T> te, String name)
    {
        TileEntity.register(MOD_ID + ":" + name, te);
    }

    private static <T extends IForgeRegistryEntry<T>> void newRegistry(ResourceLocation name, Class<T> tClass)
    {
        IForgeRegistry<T> r = new RegistryBuilder<T>().setName(name).allowModification().setType(tClass).create();
    }
}
