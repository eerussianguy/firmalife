package com.eerussianguy.firmalife.blocks;

import java.util.HashMap;
import java.util.Map;

import net.dries007.tfc.api.types.IFruitTree;
import net.minecraft.block.BlockFence;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;

public class BlockFruitFence extends BlockFence {

    private static final Map<IFruitTree, BlockFruitFence> MAP = new HashMap<>();

    public static BlockFruitFence get(IFruitTree tree)
    {
        return MAP.get(tree);
    }

    public final IFruitTree tree;

    public BlockFruitFence(IFruitTree tree)
    {
        super(Material.WOOD, Material.WOOD.getMaterialMapColor());
        if (MAP.put(tree, this) != null) throw new IllegalStateException("There can only be one.");
        this.tree = tree;
        setHarvestLevel("axe", 0);
        setHardness(2.0F);
        setResistance(15.0F);
        setSoundType(SoundType.WOOD);
    }
}
