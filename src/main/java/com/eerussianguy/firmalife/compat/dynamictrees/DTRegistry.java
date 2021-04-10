package com.eerussianguy.firmalife.compat.dynamictrees;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraftforge.registries.IForgeRegistry;

import com.eerussianguy.firmalife.compat.CompatibleRecipeRegistry;
import com.eerussianguy.firmalife.init.FoodFL;
import com.eerussianguy.firmalife.recipe.NutRecipe;
import com.eerussianguy.firmalife.registry.ItemsFL;
import com.ferreusveritas.dynamictrees.blocks.BlockBranch;
import org.labellum.mc.dynamictreestfc.ModBlocks;
import org.labellum.mc.dynamictreestfc.ModTrees;

public class DTRegistry extends CompatibleRecipeRegistry
{
    public DTRegistry()
    {
        super("dynamic_trees_tfc_registry");
    }

    @Override
    public void registerNutRecipes(IForgeRegistry<NutRecipe> r)
    {
        ModTrees.tfcTrees.forEach(tree -> {
            String treeName = tree.getName().getPath();
            BlockBranch log = tree.getDynamicBranch();
            Block leaves = ModBlocks.leafMap.get(treeName).getDynamicLeavesState().getBlock();

            if (treeName.contains("chestnut"))
            {
                r.register(new NutRecipe(log, leaves, new ItemStack(ItemsFL.getFood(FoodFL.CHESTNUTS))).setRegistryName("dt_chestnut"));
            }
            else if (treeName.contains("pine"))
            {
                r.register(new NutRecipe(log, leaves, new ItemStack(ItemsFL.getFood(FoodFL.PINE_NUTS))).setRegistryName("dt_pine"));
            }
            else if (treeName.contains("oak"))
            {
                r.register(new NutRecipe(log, leaves, new ItemStack(ItemsFL.getFood(FoodFL.ACORNS))).setRegistryName("dt_oak"));
            }
            else if (treeName.contains("hickory"))
            {
                r.register(new NutRecipe(log, leaves, new ItemStack(ItemsFL.getFood(FoodFL.PECAN_NUTS))).setRegistryName("dt_hickory"));
            }
            else if (treeName.contains("palm"))
            {
                r.register(new NutRecipe(log, leaves, new ItemStack(ItemsFL.getFood(FoodFL.COCONUT))).setRegistryName("dt_palm"));
            }
        });
    }
}
