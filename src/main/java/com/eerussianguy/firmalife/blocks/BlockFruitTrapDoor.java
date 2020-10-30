package com.eerussianguy.firmalife.blocks;

import java.util.HashMap;
import java.util.Map;

import net.dries007.tfc.api.types.IFruitTree;
import net.minecraft.block.BlockTrapDoor;
import net.minecraft.block.material.Material;

public class BlockFruitTrapDoor extends BlockTrapDoor {

    private static final Map<IFruitTree, BlockFruitTrapDoor> MAP = new HashMap<>();

    public static BlockFruitTrapDoor get(IFruitTree tree) { return MAP.get(tree); }

    public final IFruitTree tree;

    public BlockFruitTrapDoor(IFruitTree tree)
    {
        super(Material.WOOD);
        this.tree = tree;
        if (MAP.put(tree, this) != null) throw new IllegalStateException("There can only be one.");
        setHardness(0.5F);
    }
}
