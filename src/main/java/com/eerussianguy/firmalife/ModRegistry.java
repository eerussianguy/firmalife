package com.eerussianguy.firmalife;

import com.google.common.collect.ImmutableList;
import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.registries.IForgeRegistry;

import com.eerussianguy.firmalife.init.FoodDataFL;
import com.eerussianguy.firmalife.init.FruitTreeFL;
import com.eerussianguy.firmalife.items.ItemFoodFL;
import net.dries007.tfc.objects.blocks.agriculture.BlockFruitTreeBranch;
import net.dries007.tfc.objects.blocks.agriculture.BlockFruitTreeLeaves;
import net.dries007.tfc.objects.blocks.agriculture.BlockFruitTreeSapling;
import net.dries007.tfc.objects.blocks.agriculture.BlockFruitTreeTrunk;
import net.dries007.tfc.objects.items.itemblock.ItemBlockTFC;
import net.dries007.tfc.util.Helpers;

import static com.eerussianguy.firmalife.FirmaLife.MOD_ID;
import static net.dries007.tfc.objects.CreativeTabsTFC.*;

@Mod.EventBusSubscriber(modid = MOD_ID)
@GameRegistry.ObjectHolder(MOD_ID)
public class ModRegistry
{
    public static final ItemFoodFL CHOCOLATE = Helpers.getNull();
    @GameRegistry.ObjectHolder("cocoa_beans")
    public static final ItemFoodFL COCOA_BEANS = Helpers.getNull();

    private static ImmutableList<Item> allEasyItems;
    private static ImmutableList<ItemBlock> allIBs;

    public static final BlockFruitTreeSapling COCOA_SAPLING = Helpers.getNull();
    public static final BlockFruitTreeLeaves COCOA_LEAVES = Helpers.getNull();

    private static ImmutableList<BlockFruitTreeLeaves> allFruitLeaves = Helpers.getNull();

    public static ImmutableList<Item> getAllEasyItems()
    {
        return allEasyItems;
    }

    public static ImmutableList<ItemBlock> getAllIBs()
    {
        return allIBs;
    }

    public static ImmutableList<BlockFruitTreeLeaves> getAllFruitLeaves()
    {
        return allFruitLeaves;
    }

    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Item> event) {
        IForgeRegistry<Item> r = event.getRegistry();

        ImmutableList.Builder<Item> easyItems = ImmutableList.builder();
        easyItems.add(register(r, "chocolate", new ItemFoodFL(FoodDataFL.CHOCOLATE), CT_FOOD));
        easyItems.add(register(r, "cocoa_beans", new ItemFoodFL(FoodDataFL.COCOA_BEANS), CT_FOOD));
        allEasyItems = easyItems.build();

        ImmutableList.Builder<ItemBlock> IBs = ImmutableList.builder();
        for (FruitTreeFL fruitTree : FruitTreeFL.values())
        {
            registerIB(r, new ItemBlockTFC(COCOA_LEAVES));
            registerIB(r, new ItemBlockTFC(COCOA_SAPLING));
        }

        allIBs = IBs.build();
    }

    @SubscribeEvent
    public static void registerBlocks(RegistryEvent.Register<Block> event)
    {
        IForgeRegistry<Block> r = event.getRegistry();

        ImmutableList.Builder<BlockFruitTreeLeaves> fruitLeaves = ImmutableList.builder();
        for (FruitTreeFL fruitTree : FruitTreeFL.values())
        {
            register(r, "cocoa_branch", new BlockFruitTreeBranch(FruitTreeFL.COCOA), CT_WOOD);
            fruitLeaves.add(register(r, "cocoa_leaves", new BlockFruitTreeLeaves(FruitTreeFL.COCOA)));
            register(r, "cocoa_sapling", new BlockFruitTreeSapling(FruitTreeFL.COCOA), CT_WOOD);
            register(r, "cocoa_trunk", new BlockFruitTreeTrunk(FruitTreeFL.COCOA));
        }
        allFruitLeaves = fruitLeaves.build();
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
}
