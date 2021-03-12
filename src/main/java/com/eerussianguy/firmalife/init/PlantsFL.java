package com.eerussianguy.firmalife.init;

import net.minecraft.util.ResourceLocation;

import net.dries007.tfc.TerraFirmaCraft;
import net.dries007.tfc.api.types.Plant;
import net.dries007.tfc.api.types.Tree;
import net.dries007.tfc.world.classic.worldgen.trees.TreeGenSequoia;

import static net.dries007.tfc.TerraFirmaCraft.MOD_ID;

public class PlantsFL
{
    public static final Tree CINNAMON_TREE = new Tree(new ResourceLocation(TerraFirmaCraft.MOD_ID, "cinnamon"), new TreeGenSequoia(), 28, 35, 280, 400, 0f, 1f, 0, 4, 15, 4, false, null, false, 15, 0, 0);
}
