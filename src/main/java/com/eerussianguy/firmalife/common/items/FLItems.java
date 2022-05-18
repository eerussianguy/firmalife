package com.eerussianguy.firmalife.common.items;

import java.util.Locale;
import java.util.function.Supplier;

import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;


import static net.dries007.tfc.common.TFCItemGroup.MISC;

@SuppressWarnings("unused")
public class FLItems
{
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, com.eerussianguy.firmalife.Firmalife.MOD_ID);

    public static final RegistryObject<Item> CINNAMON = register("spice/cinnamon", MISC);
    public static final RegistryObject<Item> CINNAMON_BARK = register("cinnamon_bark", MISC);
    public static final RegistryObject<Item> GROUND_CINNAMON = register("spice/ground_cinnamon", MISC);

    public static final RegistryObject<Item> BEESWAX = register("beeswax", MISC);
    public static final RegistryObject<Item> FROTHY_COCONUT = register("food/frothy_coconut", MISC);
    public static final RegistryObject<Item> FRUIT_LEAF = register("fruit_leaf", MISC);
    public static final RegistryObject<Item> PEEL = register("peel", MISC);
    public static final RegistryObject<Item> PINEAPPLE_LEATHER = register("pineapple_leather", MISC);
    public static final RegistryObject<Item> PINEAPPLE_YARN = register("pineapple_yarn", MISC);
    public static final RegistryObject<Item> RENNET = register("rennet", MISC);

    public static final RegistryObject<Item> WHITE_CHOCOLATE_BLEND = register("food/white_chocolate_blend", MISC);
    public static final RegistryObject<Item> DARK_CHOCOLATE_BLEND = register("food/dark_chocolate_blend", MISC);
    public static final RegistryObject<Item> MILK_CHOCOLATE_BLEND = register("food/milk_chocolate_blend", MISC);

    private static Item.Properties prop()
    {
        return new Item.Properties().tab(MISC);
    }

    private static RegistryObject<Item> register(String name, CreativeModeTab group)
    {
        return register(name, () -> new Item(new Item.Properties().tab(group)));
    }

    private static <T extends Item> RegistryObject<T> register(String name, Supplier<T> item)
    {
        return ITEMS.register(name.toLowerCase(Locale.ROOT), item);
    }
}
