package com.eerussianguy.firmalife.blocks;

import java.util.HashMap;
import java.util.Map;

import net.dries007.tfc.api.types.IFruitTree;
import net.minecraft.block.BlockFenceGate;
import net.minecraft.block.BlockPlanks;

public class BlockFruitFenceGate extends BlockFenceGate {

    private static final Map<IFruitTree, BlockFruitFenceGate> MAP = new HashMap<>();

    public static BlockFruitFenceGate get(IFruitTree tree)
    {
        return MAP.get(tree);
    }

    public final IFruitTree tree;

    public BlockFruitFenceGate(IFruitTree tree)
    {
        super(BlockPlanks.EnumType.OAK);
        if (MAP.put(tree, this) != null) throw new IllegalStateException("There can only be one.");
        this.tree = tree;
        setHarvestLevel("axe", 0);
        setHardness(2.0F);
        setResistance(15.0F);
    }
}
