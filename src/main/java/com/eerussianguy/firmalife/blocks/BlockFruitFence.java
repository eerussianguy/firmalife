package com.eerussianguy.firmalife.blocks;

import net.minecraft.block.BlockFence;
import net.minecraft.block.material.Material;

public class BlockFruitFence extends BlockFence
{

    public BlockFruitFence()
    {
        super(Material.WOOD, Material.WOOD.getMaterialMapColor());
        setHarvestLevel("axe", 0);
        setHardness(2.0F);
        setResistance(15.0F);
    }
}
