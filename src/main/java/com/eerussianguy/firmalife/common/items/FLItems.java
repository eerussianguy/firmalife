package com.eerussianguy.firmalife.common.items;

import java.util.Locale;
import java.util.function.Function;
import java.util.function.Supplier;

import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import com.eerussianguy.firmalife.common.blocks.AbstractOvenBlock;
import com.eerussianguy.firmalife.common.blocks.FLBlocks;
import com.eerussianguy.firmalife.common.blocks.FLStateProperties;

import static net.dries007.tfc.common.TFCItemGroup.DECORATIONS;
import static net.dries007.tfc.common.TFCItemGroup.MISC;

@SuppressWarnings("unused")
public class FLItems
{
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, com.eerussianguy.firmalife.Firmalife.MOD_ID);

    public static final RegistryObject<Item> CURED_OVEN_BOTTOM = register("cured_oven_bottom", () -> new BlockStateItem(FLBlocks.OVEN_BOTTOM.get(), FLItems::cure, new Item.Properties().tab(DECORATIONS)));
    public static final RegistryObject<Item> CURED_OVEN_TOP = register("cured_oven_top", () -> new BlockStateItem(FLBlocks.OVEN_TOP.get(), FLItems::cure, new Item.Properties().tab(DECORATIONS)));
    public static final RegistryObject<Item> CURED_OVEN_CHIMNEY = register("cured_oven_chimney", () -> new BlockStateItem(FLBlocks.OVEN_CHIMNEY.get(), FLItems::cure, new Item.Properties().tab(DECORATIONS)));

    public static final RegistryObject<Item> CINNAMON = register("cinnamon", MISC);
    public static final RegistryObject<Item> CINNAMON_BARK = register("cinnamon_bark", MISC);
    public static final RegistryObject<Item> GROUND_CINNAMON = register("ground_cinnamon", MISC);

    public static final RegistryObject<Item> BEESWAX = register("beeswax", MISC);
    public static final RegistryObject<Item> FROTHY_COCONUT = register("frothy_coconut", MISC);
    public static final RegistryObject<Item> FRUIT_LEAF = register("fruit_leaf", MISC);
    public static final RegistryObject<Item> PEEL = register("peel", MISC);
    public static final RegistryObject<Item> PINEAPPLE_LEATHER = register("pineapple_leather", MISC);
    public static final RegistryObject<Item> PINEAPPLE_YARN = register("pineapple_yarn", MISC);
    public static final RegistryObject<Item> RENNET = register("rennet", MISC);

    public static final RegistryObject<Item> WHITE_CHOCOLATE_BLEND = register("white_chocolate_blend", MISC);
    public static final RegistryObject<Item> DARK_CHOCOLATE_BLEND = register("dark_chocolate_blend", MISC);
    public static final RegistryObject<Item> MILK_CHOCOLATE_BLEND = register("milk_chocolate_blend", MISC);

    private static BlockState cure(BlockState state)
    {
        return state.setValue(FLStateProperties.CURED, true);
    }

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
