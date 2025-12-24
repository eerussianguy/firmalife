package com.eerussianguy.firmalife.providers;

import java.util.concurrent.CompletableFuture;
import com.eerussianguy.firmalife.common.FLTags;
import com.eerussianguy.firmalife.common.items.FLItems;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;

import net.dries007.tfc.TerraFirmaCraft;
import net.dries007.tfc.common.component.size.ItemSizeDefinition;
import net.dries007.tfc.common.component.size.ItemSizeManager;
import net.dries007.tfc.common.component.size.Size;
import net.dries007.tfc.common.component.size.Weight;

public class BuiltinItemSizes extends DataManagerProvider<ItemSizeDefinition>
{
    public BuiltinItemSizes(PackOutput output, CompletableFuture<HolderLookup.Provider> lookup)
    {
        super(ItemSizeManager.MANAGER, output, lookup, TerraFirmaCraft.MOD_ID);
    }

    @Override
    protected void addData(HolderLookup.Provider provider)
    {
        add("beehive_frame", Ingredient.of(FLItems.BEEHIVE_FRAME, FLItems.FILLED_BEEHIVE_FRAME, FLItems.SUGARED_BEEHIVE_FRAME), Size.VERY_SMALL, Weight.VERY_HEAVY);
        add("cheese_wheels", FLTags.Items.CHEESE_WHEELS, Size.VERY_LARGE, Weight.VERY_HEAVY);
        add("dynamic_foods", FLTags.Items.DYNAMIC_FOODS, Size.VERY_SMALL, Weight.VERY_HEAVY);
        add("peel", FLItems.PEEL, Size.VERY_LARGE, Weight.HEAVY);
        add("kegs", FLTags.Items.KEGS, Size.VERY_LARGE, Weight.VERY_HEAVY);
        add("wine", FLTags.Items.FILLED_WINE_BOTTLES, Size.VERY_LARGE, Weight.VERY_HEAVY);
        add("empty_wine", FLTags.Items.EMPTY_WINE_BOTTLES, Size.NORMAL, Weight.MEDIUM);
        add("empty_jar", FLItems.EMPTY_JAR_WITH_STAINLESS_STEEL_LID, Size.TINY, Weight.MEDIUM);
    }

    private void add(String name, TagKey<Item> item, Size size, Weight weight)
    {
        add(name, new ItemSizeDefinition(Ingredient.of(item), size, weight));
    }

    private void add(String name, ItemLike item, Size size, Weight weight)
    {
        add(name, new ItemSizeDefinition(Ingredient.of(item), size, weight));
    }

    private void add(String name, Ingredient item, Size size, Weight weight)
    {
        add(name, new ItemSizeDefinition(item, size, weight));
    }
}
