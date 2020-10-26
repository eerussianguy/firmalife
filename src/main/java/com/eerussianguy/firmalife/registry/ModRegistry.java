package com.eerussianguy.firmalife.registry;

import com.eerussianguy.firmalife.blocks.*;
import com.eerussianguy.firmalife.items.ItemBlockRot;
import com.eerussianguy.firmalife.te.TEStemCrop;
import com.eerussianguy.firmalife.util.CropFL;
import com.google.common.collect.ImmutableList;
import net.dries007.tfc.objects.blocks.agriculture.*;
import net.dries007.tfc.objects.items.ItemSeedsTFC;
import net.minecraft.block.Block;
import net.minecraft.block.material.MapColor;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
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

import com.eerussianguy.firmalife.init.FoodDataFL;
import com.eerussianguy.firmalife.init.FruitTreeFL;
import com.eerussianguy.firmalife.init.OvenRecipe;
import com.eerussianguy.firmalife.items.ItemFoodFL;
import com.eerussianguy.firmalife.te.TEOven;
import net.dries007.tfc.api.capability.size.Size;
import net.dries007.tfc.api.capability.size.Weight;
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
    @GameRegistry.ObjectHolder("oven")
    public static final BlockOven OVEN = Helpers.getNull();
    @GameRegistry.ObjectHolder("oven_wall")
    public static final BlockOvenWall OVEN_WALL = Helpers.getNull();
    @GameRegistry.ObjectHolder("oven_chimney")
    public static final BlockOvenChimney OVEN_CHIMNEY = Helpers.getNull();
    @GameRegistry.ObjectHolder("pumpkin_fruit")
    public static final BlockStemFruit PUMPKIN_FRUIT = Helpers.getNull();
    @GameRegistry.ObjectHolder("melon_fruit")
    public static final BlockStemFruit MELON_FRUIT = Helpers.getNull();

    private static ImmutableList<Item> allEasyItems;
    private static ImmutableList<ItemBlock> allIBs;
    private static ImmutableList<ItemSeedsTFC> allSeeds;

    private static ImmutableList<Block> allNormalIBs = Helpers.getNull();
    private static ImmutableList<Block> allFoodIBs = Helpers.getNull();
    private static ImmutableList<BlockFruitTreeLeaves> allFruitLeaves = Helpers.getNull();
    private static ImmutableList<BlockFruitTreeSapling> allFruitSaps = Helpers.getNull();

    private static ImmutableList<BlockCropDead> allDeadCrops = Helpers.getNull();
    private static ImmutableList<BlockStemCrop> allCropBlocks = Helpers.getNull();

    public static ImmutableList<Item> getAllEasyItems()
    {
        return allEasyItems;
    }

    public static ImmutableList<ItemBlock> getAllIBs()
    {
        return allIBs;
    }

    public static ImmutableList<Block> getAllNormalIBs() { return allNormalIBs; }

    public static ImmutableList<Block> getAllFoodIBs() { return allFoodIBs; }

    public static ImmutableList<BlockFruitTreeLeaves> getAllFruitLeaves()
    {
        return allFruitLeaves;
    }

    public static ImmutableList<BlockFruitTreeSapling> getAllFruitSaps()
    {
        return allFruitSaps;
    }

    public static ImmutableList<BlockCropDead> getAllDeadCrops() { return allDeadCrops; }

    public static ImmutableList<BlockStemCrop> getAllCropBlocks() { return allCropBlocks; }

    public static BlockStemFruit getPumpkin() { return PUMPKIN_FRUIT; }

    public static BlockStemFruit getMelon() { return MELON_FRUIT; }

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
        easyItems.add(register(r, "roasted_cocoa_beans", new ItemMisc(Size.SMALL, Weight.LIGHT), CT_MISC));
        easyItems.add(register(r, "cocoa_butter", new ItemMisc(Size.SMALL, Weight.LIGHT), CT_MISC));
        easyItems.add(register(r, "cocoa_powder", new ItemMisc(Size.SMALL, Weight.LIGHT), CT_MISC));
        easyItems.add(register(r, "dark_chocolate_blend", new ItemMisc(Size.SMALL, Weight.LIGHT), CT_MISC));
        easyItems.add(register(r, "milk_chocolate_blend", new ItemMisc(Size.SMALL, Weight.LIGHT), CT_MISC));
        easyItems.add(register(r, "white_chocolate_blend", new ItemMisc(Size.SMALL, Weight.LIGHT), CT_MISC));
        easyItems.add(register(r, "peel", new ItemMisc(Size.LARGE, Weight.VERY_HEAVY), CT_MISC));

        ModRegistry.getAllIBs().forEach((x) -> {
            registerIB(r, x);
        });

        for(CropFL crop : CropFL.values())
        {
            ItemSeedsTFC item = new ItemSeedsTFC(crop);
            String name = "crop/seeds/" + crop.name().toLowerCase();
            item.setRegistryName(MOD_ID, name);
            item.setCreativeTab(CT_FOOD);
            item.setTranslationKey(MOD_ID + "." + name.replace('/', '.'));
            r.register(item);
            easyItems.add(item);
        }
        allEasyItems = easyItems.build();

    }

    @SubscribeEvent
    public static void registerBlocks(RegistryEvent.Register<Block> event)
    {
        IForgeRegistry<Block> r = event.getRegistry();

        ImmutableList.Builder<ItemBlock> IBs = ImmutableList.builder();
        ImmutableList.Builder<Block> NormalIBs = ImmutableList.builder();
        ImmutableList.Builder<Block> FoodIBs = ImmutableList.builder();
        ImmutableList.Builder<BlockFruitTreeLeaves> fruitLeaves = ImmutableList.builder();
        ImmutableList.Builder<BlockFruitTreeSapling> fruitSaps = ImmutableList.builder();
        ImmutableList.Builder<BlockCropDead> deadCrops = ImmutableList.builder();
        ImmutableList.Builder<BlockStemCrop> cropBlocks = ImmutableList.builder();
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

        FoodIBs.add(register(r, "pumpkin_fruit", new BlockStemFruit(), CT_FLORA));
        FoodIBs.add(register(r, "melon_fruit", new BlockStemFruit(), CT_FLORA));

        for(CropFL crop : CropFL.values())
        {
            String name = "crop/" + crop.name().toLowerCase();
            String nameDead = "dead_crop/" + crop.name().toLowerCase();

            BlockStemCrop block = BlockStemCrop.create(crop);
            BlockCropDead blockDead = new BlockCropDead(crop);

            block.setRegistryName(MOD_ID, name);
            blockDead.setRegistryName(MOD_ID,nameDead);
            block.setTranslationKey(MOD_ID + "." + name.replace('/', '.'));
            blockDead.setTranslationKey(MOD_ID + "." + nameDead.replace('/', '.'));
            r.register(block);
            r.register(blockDead);
            deadCrops.add(blockDead);
            cropBlocks.add(block);
        }

        register(TEOven.class, "oven");

        allNormalIBs = NormalIBs.build();
        allNormalIBs.forEach((x) -> {
            IBs.add(new ItemBlockTFC(x));
        });
        allFoodIBs = FoodIBs.build();
        allFoodIBs.forEach((x) -> {
            IBs.add(new ItemBlockRot(x));
        });
        allFruitLeaves = fruitLeaves.build();
        allFruitLeaves.forEach((x) -> {
            IBs.add(new ItemBlockTFC(x));
        });
        allFruitSaps = fruitSaps.build();
        allFruitSaps.forEach((x) -> {
            IBs.add(new ItemBlockTFC(x));
        });


        allIBs = IBs.build();

        allDeadCrops = deadCrops.build();
        allCropBlocks = cropBlocks.build();


        register(TEStemCrop.class, "stem_crop");
    }

    @SubscribeEvent
    public static void onNewRegistryEvent(RegistryEvent.NewRegistry event)
    {
        newRegistry(RegNames.OVEN_RECIPE, OvenRecipe.class);
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
