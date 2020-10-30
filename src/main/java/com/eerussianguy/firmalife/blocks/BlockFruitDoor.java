package com.eerussianguy.firmalife.blocks;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import com.eerussianguy.firmalife.items.ItemFruitDoor;
import net.dries007.tfc.api.types.IFruitTree;
import net.minecraft.block.BlockDoor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

public class BlockFruitDoor extends BlockDoor {

    private static final Map<IFruitTree, BlockFruitDoor> MAP = new HashMap<>();

    public static BlockFruitDoor get(IFruitTree tree) { return MAP.get(tree); }

    public final IFruitTree tree;

    public BlockFruitDoor(IFruitTree tree)
    {
        super(Material.WOOD);
        if (MAP.put(tree, this) != null) throw new IllegalStateException("There can only be one.");
        this.tree = tree;
        setHardness(3.0F);
        disableStats();
    }

    public Item getItem() { return ItemFruitDoor.get(tree); }

    @Override
    public Item getItemDropped(IBlockState state, Random rand, int fortune)
    {
        return state.getValue(HALF) == EnumDoorHalf.UPPER ? Items.AIR : getItem();
    }

    @Override
    public ItemStack getPickBlock(IBlockState state, RayTraceResult target, World world, BlockPos pos, EntityPlayer player)
    {
        return new ItemStack(getItem());
    }
}
