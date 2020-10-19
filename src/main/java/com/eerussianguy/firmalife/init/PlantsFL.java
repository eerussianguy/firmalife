package com.eerussianguy.firmalife.init;

import net.minecraft.util.ResourceLocation;

import net.dries007.tfc.TerraFirmaCraft;
import net.dries007.tfc.api.types.Plant;
import net.dries007.tfc.api.types.Tree;
import net.dries007.tfc.world.classic.worldgen.trees.TreeGenSequoia;

import static net.dries007.tfc.TerraFirmaCraft.MOD_ID;

public class PlantsFL
{
    public static final ResourceLocation VANILLA = new ResourceLocation(MOD_ID, "vanilla");

    public static final Plant VANILLA_PLANT = new Plant(VANILLA, Plant.PlantType.STANDARD, new int[] {1, 1, 1, 0, 0, 1, 1, 1, 1, 1, 1, 1}, false, false, 15f, 31f, 18f, 35f, 200f, 500f, 12, 15, 1, 0.9D, "vanilla_wild");

    public static final Tree CINNAMON_TREE = new Tree(new ResourceLocation(TerraFirmaCraft.MOD_ID, "cinnamon"), new TreeGenSequoia(), 28, 35, 280, 400, 0, 0, 0, 4, 15, 4, false, null, false, 15, 0, 0);
}
