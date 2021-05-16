package com.eerussianguy.firmalife.util;

import net.minecraft.init.Items;
import net.minecraftforge.oredict.OreDictionary;

import com.eerussianguy.firmalife.init.FoodFL;
import com.eerussianguy.firmalife.items.ItemFoodFL;
import com.eerussianguy.firmalife.registry.BlocksFL;
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
        OreDictionary.registerOre("cheese", ItemsFL.CHEDDAR);
        OreDictionary.registerOre("cheese", ItemsFL.CHEVRE);
        OreDictionary.registerOre("cheese", ItemsFL.RAJYA_METOK);
        OreDictionary.registerOre("cheese", ItemsFL.GOUDA);
        OreDictionary.registerOre("cheese", ItemsFL.FETA);
        OreDictionary.registerOre("cheese", ItemsFL.SHOSHA);
        OreDictionary.registerOre("cheese", ItemFoodTFC.get(Food.CHEESE)); // for compat, I guess
        OreDictionary.registerOre("categoryBread", ItemsFL.CHESTNUT_SLICE);
        OreDictionary.registerOre("slice", ItemsFL.CHESTNUT_SLICE);
        OreDictionary.registerOre("categoryGrain", ItemsFL.ROASTED_COCOA_BEANS);
        OreDictionary.registerOre("leather", ItemsFL.PINEAPPLE_LEATHER);
        OreDictionary.registerOre("string", ItemsFL.PINEAPPLE_YARN);
        OreDictionary.registerOre("tool", ItemsFL.PEEL);
        OreDictionary.registerOre("greenhouse", BlocksFL.GREENHOUSE_ROOF);
        OreDictionary.registerOre("greenhouse", BlocksFL.GREENHOUSE_WALL);
        //OreDictionary.registerOre("greenhouse", BlocksFL.GREENHOUSE_DOOR); broken?

    }
}
