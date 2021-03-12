package com.eerussianguy.firmalife.util;

import net.minecraft.init.Items;
import net.minecraftforge.oredict.OreDictionary;

import com.eerussianguy.firmalife.registry.ItemsFL;
import net.dries007.tfc.objects.items.food.ItemFoodTFC;
import net.dries007.tfc.util.agriculture.Food;

public class OreDictsFL
{
    public static void addStaticOres()
    {
        // I don't care enough to do this to everything right now, but
        // this should be used instead of checking for a held item
        // because it's better mod compat
        // Also anything that is an IIngredient input should eventually be moved to ore dicts
        // Requires some more thinking
        OreDictionary.registerOre("peel", ItemsFL.PEEL);
        OreDictionary.registerOre("sweetener", Items.SUGAR);
        OreDictionary.registerOre("doughYeast", ItemFoodTFC.get(Food.BARLEY_DOUGH));
        OreDictionary.registerOre("doughYeast", ItemFoodTFC.get(Food.CORNMEAL_DOUGH));
        OreDictionary.registerOre("doughYeast", ItemFoodTFC.get(Food.OAT_DOUGH));
        OreDictionary.registerOre("doughYeast", ItemFoodTFC.get(Food.RYE_DOUGH));
        OreDictionary.registerOre("doughYeast", ItemFoodTFC.get(Food.WHEAT_DOUGH));
        OreDictionary.registerOre("doughYeast", ItemFoodTFC.get(Food.RICE_DOUGH));
        OreDictionary.registerOre("cheese", ItemFoodTFC.get(Food.CHEESE)); //todo: remove this
        OreDictionary.registerOre("categoryBread", ItemsFL.CHESTNUT_BREAD);
        OreDictionary.registerOre("categoryBread", ItemsFL.CHESTNUT_SLICE);
        OreDictionary.registerOre("slice", ItemsFL.CHESTNUT_SLICE);
        OreDictionary.registerOre("categoryGrain", ItemsFL.DRIED_COCOA_BEANS);
        OreDictionary.registerOre("categoryGrain", ItemsFL.ROASTED_COCOA_BEANS);
        OreDictionary.registerOre("categoryFruit", ItemsFL.PUMPKIN_CHUNKS);
        OreDictionary.registerOre("categoryFruit", ItemsFL.PUMPKIN_SCOOPED);
        OreDictionary.registerOre("categoryGrain", ItemsFL.ACORN_FRUIT);
        OreDictionary.registerOre("categoryGrain", ItemsFL.PINE_NUTS);
        OreDictionary.registerOre("categoryGrain", ItemsFL.PECANS);
        OreDictionary.registerOre("categoryMeat", ItemsFL.ACORN_FRUIT);
        OreDictionary.registerOre("categoryMeat", ItemsFL.PINE_NUTS);
        OreDictionary.registerOre("categoryMeat", ItemsFL.PECANS);
        OreDictionary.registerOre("categoryMeat", ItemsFL.ROASTED_CHESTNUTS);

    }
}
